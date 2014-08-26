package dk.statsbiblioteket.medieplatform.newspaper.ninestars;

import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import dk.statsbiblioteket.medieplatform.autonomous.ConfigConstants;
import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.medieplatform.autonomous.RunnableComponent;
import dk.statsbiblioteket.newspaper.BatchStructureCheckerComponent;
import dk.statsbiblioteket.newspaper.metadatachecker.MetadataCheckerComponent;
import dk.statsbiblioteket.newspaper.metadatachecker.MetadataChecksFactory;
import dk.statsbiblioteket.newspaper.mfpakintegration.batchcontext.BatchContextUtils;
import dk.statsbiblioteket.newspaper.mfpakintegration.configuration.MfPakConfiguration;
import dk.statsbiblioteket.newspaper.mfpakintegration.database.MfPakDAO;
import dk.statsbiblioteket.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

/** This is the main class of the Ninestars QA suite */
public class NinestarsBatchQA {

    private static Logger log = LoggerFactory.getLogger(NinestarsBatchQA.class);

    public static void main(String... args) throws Exception {
        int returnCode = doMain(args);
        System.exit(returnCode);
    }

    protected static int doMain(String... args) {
        log.info("Entered " + NinestarsBatchQA.class);
        Properties properties;
        Batch batch;
        try {
            //Get the batch (id) from the command line
            batch = getBatch(args[0]);
            //Create the properties that need to be passed into the components
            properties = createProperties(args[0], args[1]);
        } catch (Exception e) {
            usage();
            System.err.println(e.getMessage());
            return 2;
        }
        MfPakDAO mfPakDao;
        try {
            MfPakConfiguration mfPakConfiguration = new MfPakConfiguration();
            mfPakConfiguration.setDatabaseUrl(properties.getProperty(ConfigConstants.MFPAK_URL));
            mfPakConfiguration.setDatabaseUser(properties.getProperty(ConfigConstants.MFPAK_USER));
            mfPakConfiguration.setDatabasePassword(properties.getProperty(ConfigConstants.MFPAK_PASSWORD));
            mfPakDao = new MfPakDAO(mfPakConfiguration);
            // Force caching of batch context.
            BatchContextUtils.buildBatchContext(mfPakDao, batch);
        } catch (Exception e) {
            log.warn("Unable to initialize MFPAK", e);
            usage();
            System.err.println("Unable to initialize MFPAK");
            return 3;
        }

        Set<MetadataChecksFactory.Checks> disabledChecks = parseDisabledChecks(args);
        //This is the list of results so far
        ArrayList<ResultCollector> resultList = new ArrayList<>();
        try {
            //Make the components
            RunnableComponent batchStructureCheckerComponent = new BatchStructureCheckerComponent(properties, mfPakDao);
            RunnableComponent metadataCheckerComponent = new MetadataCheckerComponent(properties, mfPakDao,disabledChecks);
            //Run the batch structure checker component, where the result is added to the resultlist
            runComponent(batch, resultList, batchStructureCheckerComponent);
            //Run the batch metadata checker component, where the result is added to the resultlist
            runComponent(batch, resultList, metadataCheckerComponent);
            //Add more components as needed
        } catch (WorkException e) {
            //Do nothing, as the failure has already been reported
        } finally {
            mfPakDao.close();
        }
        ResultCollector mergedResult = NinestarsUtils.mergeResults(resultList);
        String result = NinestarsUtils.convertResult(mergedResult,batch.getFullID(),disabledChecks);
        System.out.println(result);
        if (!mergedResult.isSuccess()) {
            return 1;
        } else {
            return 0;
        }
    }

    private static Set<MetadataChecksFactory.Checks> parseDisabledChecks(String... args) {
        HashSet<MetadataChecksFactory.Checks> result = new HashSet<>();
        for (int i = 2; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("--disable=")){

                try {
                    String checkName = arg.split("=")[1];
                    try {
                        MetadataChecksFactory.Checks check = MetadataChecksFactory.Checks.valueOf(checkName);
                        result.add(check);
                    } catch (IllegalArgumentException e){
                        throw new RuntimeException("Failed to parse '"+checkName+"' as a check. The allowed values are "+ Arrays.deepToString(
                                MetadataChecksFactory.Checks.values()));
                    }
                } catch (IndexOutOfBoundsException e){
                    throw new RuntimeException("Malformed argument '"+arg+"', should be of the form --disable=<CHECK>");
                }
            } else {
                throw new RuntimeException("Malformed argument '" + arg + "', should be of the form --disable=<CHECK>");
            }
        }
        return result;
    }

    private static boolean argsContainFlag(String[] args, String flag) {
        for (String arg : args) {
            if (arg.equals(flag)) {
                return true;
            }
        }
        return false;
    }



    /**
     * Create a properties construct with just one property, "scratch". Scratch denotes the folder where the batches
     * reside. It is takes as the parent of the first argument, which should be the path to the batch
     *
     * @param batchPath the path to the batch
     * @param sqlString the Sql connect string
     *
     * @return a properties construct
     * @throws RuntimeException on trouble parsing arguments.
     */
    private static Properties createProperties(String batchPath,String sqlString) throws IOException {
        Properties properties = new Properties(System.getProperties());
        File batchFile = new File(batchPath);
        setIfNotSet(properties, ConfigConstants.ITERATOR_FILESYSTEM_BATCHES_FOLDER, batchFile.getParent());
        setIfNotSet(properties, ConfigConstants.JPYLYZER_PATH, NinestarsUtils.getJpylyzerPath());
        setIfNotSet(properties, ConfigConstants.AT_NINESTARS, Boolean.TRUE.toString());
        setIfNotSet(properties, ConfigConstants.MFPAK_URL, sqlString);
        setIfNotSet(properties,
                ConfigConstants.AUTONOMOUS_BATCH_STRUCTURE_STORAGE_DIR,
                createTempDir().getAbsolutePath());
        setIfNotSet(properties, ConfigConstants.THREADS_PER_BATCH, Runtime.getRuntime().availableProcessors() + "");

        return properties;
    }

    private static File createTempDir() throws IOException {
        File temp = File.createTempFile("ninestarsQA", "");
        temp.delete();
        temp.mkdir();
        temp.deleteOnExit();
        return temp;
    }

    private static void setIfNotSet(Properties properties, String key, String value) {
        if (properties.getProperty(key) == null) {
            properties.setProperty(key, value);
        } else {
            System.out.println(properties.getProperty(key));
        }
    }

    private static void runComponent(Batch batch, ArrayList<ResultCollector> resultList,
                                     RunnableComponent component1) throws WorkException {
        log.info("Preparing to run component {}", component1.getComponentName());
        ResultCollector result1 = new ResultCollector(component1.getComponentName(), component1.getComponentVersion());
        resultList.add(result1);
        doWork(batch, component1, result1);
        log.info("Completed run of component {}", component1.getComponentName());
    }

    /**
     * Parse the batch and round trip id from the first argument to the script
     *
     * @param arg0 the first command line argument
     *
     * @return the batch id as a batch with no events
     */
    protected static Batch getBatch(String arg0) {
        File batchPath = new File(arg0);
        if (!batchPath.isDirectory()) {
            throw new RuntimeException("Must have first argument as existing directory");
        }
        String batchFullId = batchPath.getName();
        String[] splits = batchFullId.split(Pattern.quote("-RT"));
        Batch batch = new Batch(splits[0].replaceAll("[^0-9]", "").trim());
        batch.setRoundTripNumber(Integer.parseInt(splits[1].trim()));
        return batch;
    }

    /**
     * Call the doWork method on the runnable component, and add a failure to the result collector is the
     * method throws
     *
     * @param batch           the batch to work on
     * @param component       the component doing the work
     * @param resultCollector the result collector
     *
     * @return the resultcollector
     * @throws WorkException if the component threw an exception
     */
    protected static ResultCollector doWork(Batch batch, RunnableComponent component,
                                            ResultCollector resultCollector) throws WorkException {
        try {
            component.doWorkOnBatch(batch, resultCollector);
        } catch (Exception e) {
            log.error("Failed to do work on component {}", component.getComponentName(), e);
            resultCollector.addFailure(batch.getFullID(),
                    "exception",
                    component.getClass().getSimpleName(),
                    "Unexpected error in component: " + e.toString(),
                    Strings.getStackTrace(e));
            throw new WorkException(e);
        }
        return resultCollector;
    }

    /**
     * Print usage.
     */
    private static void usage() {
        System.err.println("Usage: \n" + "java " + NinestarsFileQA.class.getName() + " <batchdirectory> <sqlconnectionstring>");
    }
}

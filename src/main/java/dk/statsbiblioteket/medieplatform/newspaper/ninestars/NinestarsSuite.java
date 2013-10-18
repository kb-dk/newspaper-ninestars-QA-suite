package dk.statsbiblioteket.medieplatform.newspaper.ninestars;

import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.medieplatform.autonomous.RunnableComponent;
import dk.statsbiblioteket.newspaper.md5checker.MD5CheckerComponent;
import dk.statsbiblioteket.util.xml.XSLT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/** This is the main class of the Ninestars QA suite */
public class NinestarsSuite {


    private static Logger log = LoggerFactory.getLogger(NinestarsSuite.class);

    public static void main(String[] args)
            throws
            Exception {
        log.info("Entered " + NinestarsSuite.class);

        //Create the properties that needs to be passed into the components
        Properties properties = createProperties(args);

        //Get the batch (id) from the command line
        Batch batch = getBatch(args);
        if (batch == null) {
            System.err.println("No batch given");
            return;
        }

        //Get the sql connect string from the command line
        String sqlConnectString = getSQLString(args);


        //This is the list of results so far
        ArrayList<ResultCollector> resultList = new ArrayList<>();
        try {
            //Make the component
            RunnableComponent component1 = new MD5CheckerComponent(properties);
            //Run the component, where the result is added to the resultlist
            runComponent(batch, resultList, component1);

            RunnableComponent component2 = new MockComponent(properties);
            runComponent(batch, resultList, component2);
            //Add more components as neeeded

        } catch (WorkException e) {
            //do nothing, as the failure have already been reported
        } finally {
            ResultCollector mergedResult = mergeResults(resultList);
            String result = convertResult(mergedResult);
            System.out.println(result);
            if (!mergedResult.isSuccess()) {
                System.exit(1);
            }
        }
    }

    /**
     * Get the sql connect string, which should be the second command line parameter. Returns null if there is no
     * second parameter
     * @param args the command line args
     * @return the sql connect string
     */
    private static String getSQLString(String[] args) {
        if (args.length > 1) {
            return args[1];
        } else {
            return null;
        }
    }

    /**
     * Create a properties construct with just one property, "scratch". Scratch denotes the folder where the batches
     * reside. It is takes as the parent of the first argument, which should be the path to the batch
     * @param args the args
     * @return a properties construct
     */
    private static Properties createProperties(String[] args) {
        Properties properties = new Properties();
        File batchPath = new File(args[0]);
        properties.setProperty("scratch", batchPath.getParent());
        return properties;
    }


    private static void runComponent(Batch batch,
                                     ArrayList<ResultCollector> resultList,
                                     RunnableComponent component1)
            throws
            WorkException {
        log.info("Preparing to run component {}", component1.getComponentName());
        ResultCollector result1 = getResultCollector(component1);
        resultList.add(result1);
        doWork(batch, component1, result1);
        log.info("Completed run of component {}", component1.getComponentName());
    }

    /**
     * Converts the result from the resultCollector to the ninestars qa format
     * @param result the result to convert
     * @return the ninestars xml
     */
    protected static String convertResult(ResultCollector result) {
        try {
            return XSLT.transform(Thread.currentThread().getContextClassLoader().getResource("converter.xslt"),
                                  result.toReport());
        } catch (TransformerException e) {
            throw new RuntimeException("Failed to transform");
        }
    }

    /**
     * Merge the list of resultcollecots into one resultcollector
     * @param resultCollectors the result collectors
     * @return a single merged resultcollector
     */
    protected static ResultCollector mergeResults(List<ResultCollector> resultCollectors) {
        ResultCollector finalresult = new ResultCollector("batch", getVersion());
        for (ResultCollector resultCollector : resultCollectors) {
            finalresult = resultCollector.mergeInto(finalresult);
        }
        return finalresult;
    }

    /**
     * Get the version of this Suite
     * @return the version
     */
    private static String getVersion() {
        return NinestarsSuite.class.getPackage().getImplementationVersion();
    }

    /**
     * Parse the batch and round trip id from the first argument to the script
     * @param args the command line arguments
     * @return the batch id as a batch with no events
     */
    protected static Batch getBatch(String[] args) {
        String batchFullId = new File(args[0]).getName();
        String[] splits = batchFullId.split(Pattern.quote("-RT"));
        Batch batch = new Batch(splits[0].replaceAll("[^0-9]", "").trim());
        batch.setRoundTripNumber(Integer.parseInt(splits[1].trim()));
        return batch;
    }

    /**
     * Call the doWork method on the runnable component, and add a failure to the result collector is the
     * method throws
     * @param batch the batch to work on
     * @param component the component doing the work
     * @param resultCollector the result collector
     * @return the resultcollector
     * @throws WorkException if the component threw an exception
     */
    protected static ResultCollector doWork(Batch batch,
                                            RunnableComponent component,
                                            ResultCollector resultCollector)
            throws
            WorkException {
        try {
            component.doWorkOnBatch(batch, resultCollector);
        } catch (Exception e) {
            log.error("Failed to do work on component {}", component.getComponentName(), e);
            resultCollector.addFailure(batch.getFullID(),
                                       "Exception",
                                       component.getComponentName(),
                                       "Unexpected error in component",
                                       e.toString());
            throw new WorkException(e);
        }
        return resultCollector;

    }

    /**
     * Utility method to get a new initialised result collector
     * @param component the component to get name and version from
     * @return a result collector
     */
    private static ResultCollector getResultCollector(RunnableComponent component) {
        return new ResultCollector(component.getComponentName(), component.getComponentVersion());
    }

}

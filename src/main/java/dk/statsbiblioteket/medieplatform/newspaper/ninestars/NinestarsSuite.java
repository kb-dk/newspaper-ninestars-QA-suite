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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

public class NinestarsSuite {


    private static Logger log = LoggerFactory.getLogger(NinestarsSuite.class);
    private static String[] requiredProperties =
            new String[]{"useFileSystem", "scratch"}; //etc.

    public static void main(String[] args)
            throws
            Exception {
        log.info("Entered " + NinestarsSuite.class);
        Properties properties = readProperties(args);
        Batch batch = getBatch(args);
        if (batch == null){
            System.err.println("No batch given");
            return;
        }
        ArrayList<ResultCollector> resultList = new ArrayList<>();

        try {

            RunnableComponent component1 = new MD5CheckerComponent(properties);
            runComponent(batch, resultList, component1);

            RunnableComponent component2 = new MockComponent(properties);
            runComponent(batch, resultList, component2);
        } catch (WorkException e){
            //do nothing
        } finally {
            ResultCollector mergedResult = mergeResults(resultList);
            String result = convertResult(mergedResult);
            System.out.println(result);
        }
    }

    private static void runComponent(Batch batch,
                                     ArrayList<ResultCollector> resultList,
                                     RunnableComponent component1)
            throws
            WorkException {
        log.info("Preparing to run component {}",component1.getComponentName());
        ResultCollector result1 = getResultCollector(component1);
        resultList.add(result1);
        doWork(batch, component1, result1);
        log.info("Completed run of component {}",component1.getComponentName());
    }

    protected static String convertResult(ResultCollector mergedResult) {
        try {
            return XSLT.transform(Thread.currentThread().getContextClassLoader().getResource("converter.xslt"),
                                  mergedResult.toReport());
        } catch (TransformerException e) {
            throw new RuntimeException("Failed to transform");
        }
    }

    protected static ResultCollector mergeResults(List<ResultCollector> resultCollectors) {
        ResultCollector finalresult = new ResultCollector("batch", getVersion());
        for (ResultCollector resultCollector : resultCollectors) {
            finalresult = resultCollector.mergeInto(finalresult);
        }
        return finalresult;
    }

    private static String getVersion() {
        return "0.1"; //TODO
    }

    protected static Batch getBatch(String[] args) {
        for (int i = 0;
             i < args.length;
             i++) {
            String arg = args[i];
            if (arg.equals("-b")) {
                String batchFullId = args[i + 1];
                String[] splits = batchFullId.split(Pattern.quote("-RT"));
                Batch batch = new Batch(splits[0].replaceAll("[^0-9]", "").trim());
                batch.setRoundTripNumber(Integer.parseInt(splits[1].trim()));
                return batch;
            }
        }
        return null;

    }

    protected static ResultCollector doWork(Batch batch,
                                            RunnableComponent component,
                                            ResultCollector resultCollector)
            throws
            WorkException {
        try {
            component.doWorkOnBatch(batch, resultCollector);
        } catch (Exception e) {
            //todo better stack trace print
            resultCollector.addFailure(batch.getFullID(),
                                       e.getClass().getName(),
                                       component.getComponentName(),
                                       e.getMessage(),
                                       e.toString());
            throw new WorkException(e);
        }
        return resultCollector;

    }

    private static ResultCollector getResultCollector(RunnableComponent component) {
        return new ResultCollector(component.getComponentName(), component.getComponentVersion());
    }

    /**
     * Reads the properties from the arguments or system properties. Either the first argument must be a path to a
     * properties file, or, if not, the system property "newspaper.component.properties.file" must denote such a path.
     * If neither, then a runtime exception is set
     *
     * @param args the command line arguments
     *
     * @return a properties object parsed from the properties file
     * @throws java.io.IOException if the file could not be read
     * @throws RuntimeException    if no path could be determined
     */
    private static Properties readProperties(String[] args)
            throws
            IOException,
            RuntimeException {
        Properties properties = new Properties();
        String propsFileString;
        if (args.length >= 1) {
            propsFileString = args[0];
        } else {
            propsFileString = System.getProperty("newspaper.component.properties.file");
        }
        if (propsFileString == null) {
            throw new RuntimeException("Properties file must be defined either as command-line parameter or as system"
                                       + "property newspaper.component.properties .");
        }
        log.info("Reading properties from " + propsFileString);
        File propsFile = new File(propsFileString);
        if (!propsFile.exists()) {
            throw new FileNotFoundException("No such file: " + propsFile.getAbsolutePath());
        }
        properties.load(new FileReader(propsFile));
        checkProperties(properties, requiredProperties);
        return properties;
    }

    /**
     * Check that all the required properties are set
     *
     * @param props     the properties to check
     * @param propnames the names that must be found in the properties
     *
     * @throws RuntimeException if any name could not be found
     */
    private static void checkProperties(Properties props,
                                        String[] propnames)
            throws
            RuntimeException {
        for (String prop : propnames) {
            if (props.getProperty(prop) == null) {
                throw new RuntimeException("Property not found: " + prop);
            }
        }
    }

}

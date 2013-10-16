package dk.statsbiblioteket.newspaper.ninestars;

import dk.statsbiblioteket.autonomous.ResultCollector;
import dk.statsbiblioteket.autonomous.RunnableComponent;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.Batch;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.EventID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class NinestarsSuite {


    private static Logger log = LoggerFactory.getLogger(NinestarsSuite.class);
    private static String[] requiredProperties =
            new String[]{"fedora.admin.username", "fedora.admin.password", "doms.server", "pidgenerator.location",
                         "newspaper.batch.superdir", "lockserver", "summa"}; //etc.

    public static void main(String[] args)
            throws
            Exception {
        log.info("Entered " + NinestarsSuite.class);
        Properties properties = readProperties(args);
        Batch batch = getBatch(args);


        ResultCollector md5result = runMD5Checker(batch);

        ResultCollector md5result2 = runMD5Checker(batch);

        ResultCollector mergedResult = mergeResults(md5result,md5result2);

        String result = convertResult(mergedResult);

        System.out.println(result);

    }

    private static String convertResult(ResultCollector mergedResult) {
        return mergedResult.toReport();
    }

    private static ResultCollector mergeResults(ResultCollector... result) {
        return result[0];
    }

    private static Batch getBatch(String[] args) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }


    private static ResultCollector runMD5Checker(Batch batch) {
        RunnableComponent component = new RunnableComponent() {

            @Override
            public String getComponentName() {
                return "MD5SumChecker";
            }

            @Override
            public String getComponentVersion() {
                return "0.1";
            }

            @Override
            public EventID getEventID() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void doWorkOnBatch(Batch batch,
                                      ResultCollector resultCollector)
                    throws
                    Exception {
                return;
            }
        };
        ResultCollector resultCollector =
                new ResultCollector(component.getComponentName(), component.getComponentVersion());
        try {
            component.doWorkOnBatch(batch,resultCollector);
        } catch (Exception e) {
            resultCollector.setSuccess(false);
            //todo better stack trace print
            resultCollector.addFailure(batch.getFullID(),e.getClass().getName(),component.getComponentName(),e.getMessage(),e.toString());
        }
        return resultCollector;

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

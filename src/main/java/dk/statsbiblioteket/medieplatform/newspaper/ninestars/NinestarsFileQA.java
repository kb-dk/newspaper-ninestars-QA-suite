package dk.statsbiblioteket.medieplatform.newspaper.ninestars;

import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.DataFileNodeBeginsParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.DataFileNodeEndsParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.filesystem.FileAttributeParsingEvent;
import dk.statsbiblioteket.newspaper.metadatachecker.jpylyzer.JpylyzerValidatorEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Exception;
import java.lang.String;

public class NinestarsFileQA {


    private static Logger log = LoggerFactory.getLogger(NinestarsFileQA.class);

    public static void main(String[] args)
            throws
            Exception {
        int result = doMain(args);
        System.exit(result);
    }

    protected static int doMain(String... args)
            throws
            FileNotFoundException {
        log.info("Entered " + NinestarsFileQA.class);

        // Parse arguments
        File file;
        try {
            file = getFile(args);
        } catch (Exception e) {
            usage();
            return 2;
        }



        String controlPoliciesPath = NinestarsUtils.getControlPolicies();

        String jpylyzerPath = NinestarsUtils.getJpylyzerPath();

        return runValidation(file, controlPoliciesPath, jpylyzerPath);

    }

    protected static int runValidation(File file,
                                     String controlPoliciesPath,
                                     String jpylyzerPath)
            throws
            FileNotFoundException {
        ResultCollector resultCollector = new ResultCollector("file", NinestarsUtils.getVersion());
        JpylyzerValidatorEventHandler eventHandler =
                new JpylyzerValidatorEventHandler(file.getParentFile().getAbsolutePath(),
                                                  resultCollector,
                                                  controlPoliciesPath,
                                                  jpylyzerPath,
                                                  true);


        //simulate a tree iteration
        eventHandler.handleNodeBegin(new DataFileNodeBeginsParsingEvent(file.getName()));
        eventHandler.handleAttribute(new FileAttributeParsingEvent(file.getName() + JpylyzerValidatorEventHandler.CONTENTS,
                                                                   file));
        eventHandler.handleNodeEnd(new DataFileNodeEndsParsingEvent(file.getName()));
        eventHandler.handleFinish();

        String result = NinestarsUtils.convertResult(resultCollector);
        System.out.println(result);

        if (!resultCollector.isSuccess()) {
            return 1;
        }

        return 0;
    }

    /**
     * Print usage.
     */
    private static void usage() {
        System.err.println("Usage: \n" + "java " + NinestarsFileQA.class.getName() + " <jp2file>");
    }

    /**
     * Get file parameter from arguments
     * @param args Will read first argument as a file name
     * @return The file from first argument
     * @throws RuntimeException on trouble parsing argument.
     */
    private static File getFile(String[] args) {
        File file = new File(args[0]);
        if (!file.isFile()) {
            throw new RuntimeException("Must have first argument as existing file");
        }
        return file;

    }
}

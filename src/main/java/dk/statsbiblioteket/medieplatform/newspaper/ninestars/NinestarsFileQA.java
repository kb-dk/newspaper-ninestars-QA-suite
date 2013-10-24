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

    //TODO usage and args parsing
    protected static int doMain(String... args)
            throws
            FileNotFoundException {
        log.info("Entered " + NinestarsFileQA.class);

        File file = getFile(args);

        ResultCollector resultCollector = new ResultCollector("file", NinestarsUtils.getVersion());

        String controlPoliciesPath = NinestarsUtils.getControlPolicies();

        String jpylyzerPath = NinestarsUtils.getJpylyzerPath();

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


    private static File getFile(String[] args) {
        return new File(args[0]);
    }
}

package dk.statsbiblioteket.medieplatform.newspaper.ninestars;

import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.DataFileNodeBeginsParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.DataFileNodeEndsParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.filesystem.FileAttributeParsingEvent;
import dk.statsbiblioteket.newspaper.metadatachecker.jpylyzer.JpylyzerValidatorEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.Exception;
import java.lang.String;

public class NinestarsFileQA {


    private static Logger log = LoggerFactory.getLogger(NinestarsFileQA.class);

    public static void main(String[] args)
            throws
            Exception {
        log.info("Entered " + NinestarsFileQA.class);

        File file = getFile(args);

        ResultCollector resultCollector = createResultCollector();

        String controlPoliciesPath = getControlPolicies(args);

        String jpylyzerPath = getJpylyzerPath(args);

        JpylyzerValidatorEventHandler eventHandler =
                new JpylyzerValidatorEventHandler(file.getParentFile().getAbsolutePath(),
                                                  resultCollector,
                                                  controlPoliciesPath,
                                                  jpylyzerPath,
                                                  true);


        //simulate a tree iteration
        String prefix = file.getParentFile().getAbsolutePath();


        eventHandler.handleNodeBegin(new DataFileNodeBeginsParsingEvent(file.getName()));
        eventHandler.handleAttribute(new FileAttributeParsingEvent(file.getName() + JpylyzerValidatorEventHandler.CONTENTS,
                                                                   file));
        eventHandler.handleNodeEnd(new DataFileNodeEndsParsingEvent(file.getName()));
        eventHandler.handleFinish();

        String result = NinestarsUtils.convertResult(resultCollector);
        System.out.println(result);
        if (!resultCollector.isSuccess()) {
            System.exit(1);
        }

    }

    private static String getJpylyzerPath(String[] args) {
        return new File("extras/jpylyzer/jpylyzer.py").getAbsolutePath();
    }

    private static String getControlPolicies(String[] args) {
        return null; //Null means use default control policies
    }

    private static ResultCollector createResultCollector() {
        return new ResultCollector("file",NinestarsUtils.getVersion());
    }

    private static File getFile(String[] args) {
        return new File(args[0]);
    }
}

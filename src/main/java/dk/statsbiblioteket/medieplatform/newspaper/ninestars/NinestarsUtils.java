package dk.statsbiblioteket.medieplatform.newspaper.ninestars;

import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.util.xml.XSLT;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.util.List;

public class NinestarsUtils {
    /**
     * Converts the result from the resultCollector to the ninestars qa format
     *
     * @param result the result to convert
     *
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
     *
     * @param resultCollectors the result collectors
     *
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
     *
     * @return the version
     */
    public static String getVersion() {
        return NinestarsUtils.class.getPackage().getImplementationVersion();
    }

    public static String getJpylyzerPath() {
        return "jpylyzer.py";
    }

    public static String getControlPolicies() {
        return null; //Null means use default control policies
    }

}

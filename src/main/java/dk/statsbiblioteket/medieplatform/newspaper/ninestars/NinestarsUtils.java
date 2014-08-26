package dk.statsbiblioteket.medieplatform.newspaper.ninestars;

import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.newspaper.metadatachecker.MetadataChecksFactory;
import dk.statsbiblioteket.util.xml.XSLT;

import javax.xml.transform.TransformerException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NinestarsUtils {
    /**
     * Converts the result from the resultCollector to the ninestars qa format
     *
     * @param result the result to convert
     *
     * @param disabledChecks
     * @return the ninestars xml
     */
    protected static String convertResult(ResultCollector result, Set<MetadataChecksFactory.Checks> disabledChecks) {
        try {
            return XSLT.transform(Thread.currentThread().getContextClassLoader().getResource("converter.xslt"),
                                  result.toReport());
        } catch (TransformerException e) {
            throw new RuntimeException("Failed to transform");
        }
    }

    private static Map<String, String> checksAsParams(Set<MetadataChecksFactory.Checks> disabledChecks) {
        Map<String,String> result = new HashMap<>();
        for (MetadataChecksFactory.Checks disabledCheck : disabledChecks) {
            result.put(disabledCheck.name(),Boolean.TRUE.toString());
        }
        return result;
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

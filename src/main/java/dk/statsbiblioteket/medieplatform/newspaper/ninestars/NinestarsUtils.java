package dk.statsbiblioteket.medieplatform.newspaper.ninestars;

import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.newspaper.metadatachecker.MetadataChecksFactory;
import dk.statsbiblioteket.util.xml.XSLT;

import javax.xml.transform.TransformerException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class NinestarsUtils {
    /**
     * Converts the result from the resultCollector to the ninestars qa format
     *
     * @param result the result to convert
     *
     * @param batchFullID The batch ID to put in the report
     * @param disabledChecks  a set of enums detailing the checks to be disabled
     * @return the ninestars xml
     */
    protected static String convertResult(ResultCollector result, String batchFullID,
                                          Set<MetadataChecksFactory.Checks> disabledChecks) {
        Map<String, String> params = new HashMap<>();
        params.put("batchID",batchFullID);
        StringBuilder disabledChecksString = wrapDisabledChecks(disabledChecks);
        params.put("disabledChecks",disabledChecksString.toString());
        try {
            return XSLT.transform(Thread.currentThread().getContextClassLoader().getResource("converter.xslt"),
                                  result.toReport(),params);
        } catch (TransformerException e) {
            throw new RuntimeException("Failed to transform");
        }
    }

    private static StringBuilder wrapDisabledChecks(Set<MetadataChecksFactory.Checks> disabledChecks) {
        StringBuilder disabledChecksString = new StringBuilder();
        boolean first = true;
        for (MetadataChecksFactory.Checks disabledCheck : new TreeSet<>(disabledChecks)) {
            if (!first){
                disabledChecksString.append(",");
            } else {
                first = false;
            }
            disabledChecksString.append(disabledCheck.name());
        }
        return disabledChecksString;
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

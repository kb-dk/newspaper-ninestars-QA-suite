package dk.statsbiblioteket.medieplatform.newspaper.ninestars;

import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.medieplatform.autonomous.RunnableComponent;
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

    /**
     * Utility method to get a new initialised result collector
     *
     * @param component the component to get name and version from
     *
     * @return a result collector
     */
    public static ResultCollector getResultCollector(RunnableComponent component) {
        return new ResultCollector(component.getComponentName(), component.getComponentVersion());
    }

    public static String getJpylyzerPath(String[] args) {
        return new File("extras/jpylyzer/jpylyzer.py").getAbsolutePath();
    }

    public static String getControlPolicies(String[] args) {
        return null; //Null means use default control policies
    }

}

package dk.statsbiblioteket.medieplatform.newspaper.ninestars;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

public class NinestarsFileQATest {


    @Test(groups = "integrationTest")
    public void testValidate()
            throws
            Exception {

        File jp2File = getJP2File();
        String jpylyzyrPath = getJpylyzerPath();
        int result = NinestarsFileQA.runValidation(jp2File, null, jpylyzyrPath);
        Assert.assertEquals(result, 0, "Failed to validate jp2file");
    }

    @Test(groups = "integrationTest")
    public void testInvalidate()
            throws
            Exception {

        File invalidJp2File = getInvalidJP2File();
        String jpylyzyrPath = getJpylyzerPath();
        int result = NinestarsFileQA.runValidation(invalidJp2File, null, jpylyzyrPath);
        Assert.assertEquals(result, 1, "Failed to validate jp2file");
    }

    private String getJpylyzerPath()
            throws
            IOException {
        Properties props = new Properties(System.getProperties());
        props.load(Thread.currentThread().getContextClassLoader()
                         .getResourceAsStream("getJpylyzerPath.properties" + ""));
        return props.getProperty("jpylyzerPath");

    }

    private File getJP2File() {
        try {
            return new File(Thread.currentThread().getContextClassLoader().getResource("valid.jp2")
                                  .toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to resolve valid jp2 file for tests", e);
        }
    }

    private File getInvalidJP2File() {
        try {
            return new File(Thread.currentThread().getContextClassLoader().getResource("invalid.jp2")
                                  .toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to resolve valid jp2 file for tests", e);
        }
    }
}

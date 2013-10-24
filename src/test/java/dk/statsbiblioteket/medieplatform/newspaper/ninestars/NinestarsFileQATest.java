package dk.statsbiblioteket.medieplatform.newspaper.ninestars;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URISyntaxException;

public class NinestarsFileQATest {

    /**
     * Test the invocation of the MD5CheckerComponent
     *
     * @throws WorkException       if the work failed
     * @throws java.io.IOException if the propertiesfile could not be read
     */
    @Test(groups = "integrationTest", enabled = true)
    public void testMain()
            throws
            Exception {

        File jp2File = getJP2File();
        int result = NinestarsFileQA.doMain(jp2File.getAbsolutePath());
        Assert.assertEquals(result,0,"Failed to validate jp2file");
    }

    private File getJP2File() {
        try {
            return new File(Thread.currentThread().getContextClassLoader().getResource("valid.jp2").toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to resolve valid jp2 file for tests",e);
        }
    }

}

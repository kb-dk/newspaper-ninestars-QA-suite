package dk.statsbiblioteket.medieplatform.newspaper.ninestars;

import dk.statsbiblioteket.medieplatform.autonomous.ConfigConstants;
import dk.statsbiblioteket.newspaper.metadatachecker.MetadataChecksFactory;
import dk.statsbiblioteket.util.console.ProcessRunner;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.util.Properties;

public class NinestarsPostPackageTest {

    @Test(groups = "integrationTest", enabled = true)
    public void testMain() throws Exception {
        System.out.println("Running postPackageTest");
        String batchFolder
                = System.getProperty("integration.test.newspaper.testdata") + "/small-test-batch/" + "B400022028241-RT1";
        Properties properties = new Properties(System.getProperties());
        properties.load(new FileInputStream(System.getProperty("integration.test.newspaper.properties")));
        String jdbcURL = properties.getProperty(ConfigConstants.MFPAK_URL);
        jdbcURL = jdbcURL + "?user=" + properties.getProperty(ConfigConstants.MFPAK_USER);
        jdbcURL = jdbcURL + "&password=" + properties.getProperty(ConfigConstants.MFPAK_PASSWORD);
        ProcessRunner runner = new ProcessRunner("bash",
                Thread.currentThread().getContextClassLoader().getResource("integrationTest.sh").getFile(),
                batchFolder,
                jdbcURL);
        runner.run();
        System.out.println(runner.getProcessOutputAsString());
        if (runner.getReturnCode() != 0) {
            System.err.println(runner.getProcessErrorAsString());
        }
        Assert.assertEquals(runner.getReturnCode(), 0);
    }


    @Test(groups = "integrationTest", enabled = true)
    public void testMainWithDisabledTests() throws Exception {
        System.out.println("Running postPackageTestWithDisabledChecksum");
        String batchFolder
                = System.getProperty("integration.test.newspaper.testdata") + "/bad-bad-batch/" + "B400022028241-RT1";
        Properties properties = new Properties(System.getProperties());
        properties.load(new FileInputStream(System.getProperty("integration.test.newspaper.properties")));
        String jdbcURL = properties.getProperty(ConfigConstants.MFPAK_URL);
        jdbcURL = jdbcURL + "?user=" + properties.getProperty(ConfigConstants.MFPAK_USER);
        jdbcURL = jdbcURL + "&password=" + properties.getProperty(ConfigConstants.MFPAK_PASSWORD);
        ProcessRunner runner = new ProcessRunner("bash",
                Thread.currentThread().getContextClassLoader().getResource("integrationTest.sh").getFile(),
                batchFolder,
                jdbcURL,
                "--disable=" + MetadataChecksFactory.Checks.FILM_XML,
                "--disable=" + MetadataChecksFactory.Checks.MODS_XPATH,
                "--disable=" + MetadataChecksFactory.Checks.ALTO_MIX,
                "--disable=" + MetadataChecksFactory.Checks.MIX_XML,
                "--disable=" + MetadataChecksFactory.Checks.ALTO_XPATH,
                "--disable=" + MetadataChecksFactory.Checks.SCHEMATRON,
                "--disable=" + MetadataChecksFactory.Checks.EDITION_MODS,
                "--disable=" + MetadataChecksFactory.Checks.CHECKSUM);
        runner.setOutputCollectionByteSize(Integer.MAX_VALUE);
        runner.run();
        String output = runner.getProcessOutputAsString();
        Assert.assertTrue(output.contains(
                "disabledChecks=\"ALTO_XPATH,ALTO_MIX,CHECKSUM,EDITION_MODS,MODS_XPATH,SCHEMATRON,MIX_XML,FILM_XML\""),
                output);
        Assert.assertFalse(output.contains("<qa:type>metadata</qa:type>"));
        Assert.assertEquals(runner.getReturnCode(), 1);
    }
}

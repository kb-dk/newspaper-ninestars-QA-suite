package dk.statsbiblioteket.medieplatform.newspaper.ninestars;

import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.util.xml.DOM;
import dk.statsbiblioteket.util.xml.XPathSelector;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Date;

public class NinestarsSuiteTest {

    @Test
    public void testConvert() {

        ResultCollector resultCollector = new ResultCollector("batch", "0.1");
        String reference = "reference";
        String type = "type";
        resultCollector.addFailure(reference, type, "component", "description", "details1\n", "details2\n");

        String converted = NinestarsSuite.convertResult(resultCollector);
        //System.out.println(converted);

        URL schemaFile = Thread.currentThread().getContextClassLoader().getResource("xsd/qaresult.xsd");
        try {
            checkSchema(converted, schemaFile);
        } catch (SAXException | IOException e) {
            Assert.fail();
        }
        Document convertedAsDom = DOM.stringToDOM(converted, true);
        XPathSelector xpath = DOM.createXPathSelector("qa", "http://schemas.statsbiblioteket.dk/qaresult/");
        String typeSelected = xpath.selectString(convertedAsDom,
                                                 "/qa:qaresult/qa:qafailures/qa:qafailure[qa:filereference = '"
                                                 + reference + "']/qa:type");
        Assert.assertEquals(typeSelected, type);

    }

    @Test
    public void testMerger() {
        ResultCollector resultCollector = new ResultCollector("check1", "0.1");
        String reference = "reference";
        String type = "type";
        resultCollector.addFailure(reference, type, "check1", "description", "details1\n", "details2\n");


        ResultCollector resultCollector2 = new ResultCollector("check2", "0.2");

        String type2 = "type2";
        resultCollector2.addFailure(reference, type2, "check2", "description2", "details1\n", "details2\n");

        //test identity
        ResultCollector resultCollectorIdentity = new ResultCollector("check1", "0.1");
        resultCollector.mergeInto(resultCollectorIdentity);
        Date now = new Date();
        resultCollector.setTimestamp(now);
        resultCollectorIdentity.setTimestamp(now);
        Assert.assertEquals(resultCollectorIdentity.toReport(),resultCollector.toReport());

        ResultCollector result = new ResultCollector("batch", "0.1");

        Assert.assertTrue(result.isSuccess());

        resultCollector.mergeInto(result);
        Assert.assertFalse(result.isSuccess());
        String firstMerger = result.toReport();


        resultCollector2.mergeInto(result);
        Assert.assertFalse(result.isSuccess());
        String secondMerger = result.toReport();

        Assert.assertNotEquals(firstMerger, secondMerger, "The second merger changed nothing");

        resultCollector.mergeInto(result);
        Assert.assertFalse(result.isSuccess());
        String thirdMerger = result.toReport();

        Assert.assertNotEquals(secondMerger, thirdMerger, "merging twice is not idempotent");



    }

    private void checkSchema(String converted,
                             URL schemaFile)
            throws
            SAXException,
            IOException {
        Source xmlFile = new StreamSource(new StringReader(converted));
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(schemaFile);
        Validator validator = schema.newValidator();
        validator.validate(xmlFile);
    }

}

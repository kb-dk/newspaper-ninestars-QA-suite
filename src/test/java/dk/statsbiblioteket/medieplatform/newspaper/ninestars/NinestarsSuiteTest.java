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

public class NinestarsSuiteTest {

    @Test
    public void testConvert() {

        ResultCollector resultCollector = new ResultCollector("batch", "0.1");
        String reference = "reference";
        String type = "type";
        resultCollector.addFailure(reference, type, "component", "description", "details1\n","details2\n");

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
       Assert.assertEquals(typeSelected,type);

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
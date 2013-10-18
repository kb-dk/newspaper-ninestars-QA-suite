package dk.statsbiblioteket.medieplatform.newspaper.ninestars;


import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.medieplatform.autonomous.RunnableComponent;

import java.util.Properties;

public class MockComponent implements RunnableComponent {

    public MockComponent(Properties properties) {


    }

    @Override
    public String getComponentName() {
        return "MD5SumChecker";
    }

    @Override
    public String getComponentVersion() {
        return "0.1";
    }

    @Override
    public String getEventID() {
        return "Mock_Event";
    }

    @Override
    public void doWorkOnBatch(Batch batch,
                              ResultCollector resultCollector)
            throws
            Exception {
        return;
    }
}

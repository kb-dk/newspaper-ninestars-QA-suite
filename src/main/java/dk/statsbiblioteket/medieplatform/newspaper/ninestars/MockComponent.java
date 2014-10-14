package dk.statsbiblioteket.medieplatform.newspaper.ninestars;


import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import dk.statsbiblioteket.medieplatform.autonomous.EventStorer;
import dk.statsbiblioteket.medieplatform.autonomous.EventTrigger;
import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.medieplatform.autonomous.RunnableComponent;

import java.util.Properties;

public class MockComponent implements RunnableComponent<Batch> {

    public MockComponent(Properties properties) {


    }

    @Override
    public String getComponentName() {
        return "MockComponent";
    }

    @Override
    public String getComponentVersion() {
        return getClass().getPackage().getImplementationVersion();
    }

    @Override
    public String getEventID() {
        return "Mock_Event";
    }

    @Override
    public void doWorkOnItem(Batch batch,
                              ResultCollector resultCollector)
            throws
            Exception {
        return;
    }
}

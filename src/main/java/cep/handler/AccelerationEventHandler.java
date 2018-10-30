package cep.handler;

import cep.correlator.CorrelationService;
import cep.event.AccelerationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cep.subscriber.StatementSubscriber;
import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

/**
 * This class handles incoming Acceleration Events. It processes them through the EPService, to which
 * it has attached the query.
 */
@Component
@Scope(value = "singleton")
public class AccelerationEventHandler implements InitializingBean{

    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(AccelerationEventHandler.class);

    /** Esper service */
    private EPServiceProvider epService;
    private EPStatement shakeEventStatement;
    private CorrelationService correlator;
    @Autowired
    @Qualifier("shakeEventSubscriber")
    private StatementSubscriber shakeEventSubscriber;


    /**
     * Configure Esper Statement(s).
     */
    public void initService() {

        LOG.debug("Initializing Service ..");
        Configuration config = new Configuration();
        config.addEventTypeAutoName("cep.event");
        epService = EPServiceProviderManager.getDefaultProvider(config);
        this.correlator = null;
        createShakeCheckExpression();

    }

    /**
     *
     */
    private void createShakeCheckExpression() {

        LOG.debug("create Shake Check Expression");
        shakeEventStatement = epService.getEPAdministrator().createEPL(shakeEventSubscriber.getStatement());
        shakeEventStatement.setSubscriber(shakeEventSubscriber);
    }

    /**
     * Handle the incoming AccelerationEvent.
     */
    public void handle(AccelerationEvent event) {

        LOG.debug(event.toString());
        epService.getEPRuntime().sendEvent(event);

    }

    @Override
    public void afterPropertiesSet() {

        LOG.debug("Configuring..");
        initService();
    }
}

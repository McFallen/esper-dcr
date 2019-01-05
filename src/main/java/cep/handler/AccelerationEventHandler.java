package cep.handler;

import cep.event.AccelerationEvent;
import cep.event.AccelerationShakeEvent;
import cep.subscriber.ShakeEventSubscriber;
import cep.subscriber.ShakeHighEventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
    private EPStatement shakeHighEventStatement;

    @Autowired
    private ShakeEventSubscriber orderingSubscriber;

    @Autowired
    private ShakeEventSubscriber shakeEventSubscriber;

    @Autowired
    private ShakeHighEventSubscriber shakeHighEventSubscriber;


    /**
     * Setup Esper environment.
     */
    public void initService() {

        LOG.debug("Initializing Service ..");
        Configuration config = new Configuration();
        config.addEventTypeAutoName("cep.event");
        epService = EPServiceProviderManager.getDefaultProvider(config);

        createShakeCheckExpression();
        createShakeHighCheckExpression();

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
     *
     */
    private void createShakeHighCheckExpression() {

        LOG.debug("create Shake High Check Expression");
        shakeHighEventStatement = epService.getEPAdministrator().createEPL(shakeHighEventSubscriber.getStatement());
        shakeHighEventStatement.setSubscriber(shakeHighEventSubscriber);
    }

    /**
     * Handle the incoming AccelerationEvent.
     */
    public void handle(AccelerationEvent event) {

        //LOG.debug(event.toString());
        epService.getEPRuntime().sendEvent(event);

    }

    /**
     * Handle the incoming AccelerationHighEvent.
     */
    public void handle(AccelerationShakeEvent event) {

//        LOG.debug(event.toString());
        epService.getEPRuntime().sendEvent(event);

    }


    @Override
    public void afterPropertiesSet() {

        LOG.debug("Configuring..");
        initService();
    }
}

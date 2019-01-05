package cep.subscriber;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cep.correlator.CorrelationService;
import cep.event.AccelerationEvent;
import cep.event.AccelerationShakeEvent;
import cep.handler.AccelerationEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Wraps Esper Statement and Listener. No dependency on Esper libraries.
 */
@Component
public class ShakeEventSubscriber {

    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(ShakeEventSubscriber.class);

    /** If shake is over  1 m/s^2*/
    private static final String SHAKE_EVENT_THRESHOLD = "80";
    private static HashMap<Integer,Long> newestTimeStamp = new HashMap<Integer, Long>();
    @Autowired
    @Qualifier("accelerationEventHandler")
    private AccelerationEventHandler accelerationEventHandler;

    /**
     */
    public String getStatement() {

        // Statement does not take time order into consideration
        String shakeEventExpression =
                "select * " +
                "from AccelerationEvent.std:groupwin(shelfID).ext:time_order(timeOfReading, 1 seconds) as incEvent, " +
                        "AccelerationEvent.std:groupwin(shelfID).ext:time_order(timeOfReading, 1 seconds).std:lastevent() as currentLatest " +
                "WHERE incEvent.acceleration > " + SHAKE_EVENT_THRESHOLD + " AND " + "currentLatest.timeOfReading <= incEvent.timeOfReading";

        return shakeEventExpression;
    }

    /**
     * Listener method called when Esper has detected a pattern match.
     */
    public void update(Map<String, AccelerationEvent> eventMap) {


        AccelerationEvent highLevelEvent = (AccelerationEvent) eventMap.get("incEvent");

        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------------------------------");
        sb.append("\n- [WARNING] : ACCELERATION SPIKE DETECTED = ");
        sb.append("\n- " + highLevelEvent);
        sb.append("\n- Incoming time: " + new Date(System.currentTimeMillis()));
        sb.append("\n--------------------------------------------------");

        LOG.debug(sb.toString());

        // Send event to correlator, to be processed
        // CorrelationService.shakeDetected(highLevelEvent);

        AccelerationShakeEvent rawEvent =
                new AccelerationShakeEvent(
                        highLevelEvent.getAcceleration(),
                        new Date(highLevelEvent.getTimeOfReading()),
                        highLevelEvent.getShelfID(),
                        highLevelEvent.getUnit(),
                        highLevelEvent
                );

        accelerationEventHandler.handle(rawEvent);

    }
}

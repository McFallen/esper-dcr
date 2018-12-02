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
    private static final String SHAKE_EVENT_THRESHOLD = "800";
    private static HashMap<Integer,Long> newestTimeStamp = new HashMap<Integer, Long>();
    @Autowired
    @Qualifier("accelerationEventHandler")
    private AccelerationEventHandler accelerationEventHandler;

    /**
     */
    public String getStatement() {

/*        String shakeEventExpression =
                "INSERT rstream into ArrivalTimeOrderedStream " +
                        "SELECT rstream * " +
                        "FROM AccelerationEvent.ext:time_order(timeOfReading, 1 msec) myacc " +
                        "WHERE acceleration > " + SHAKE_EVENT_THRESHOLD;
*/
        // Statement does not take time order into consideration
        String shakeEventExpression =
                "select * " +
                "from AccelerationEvent.std:groupwin(shelfID).ext:time_order(timeOfReading, 1 seconds) as highLevel " +
                "WHERE acceleration > " + SHAKE_EVENT_THRESHOLD;

        return shakeEventExpression;
    }

    /**
     * Listener method called when Esper has detected a pattern match.
     */
    public void update(Map<String, AccelerationEvent> eventMap) {

        // 1st Temperature in the Warning Sequence
        AccelerationEvent highLevelEvent = (AccelerationEvent) eventMap.get("highLevel");

        if (newestTimeStamp.containsKey(highLevelEvent.getShelfID())) {
            System.out.println("Do we even get here");
            if (newestTimeStamp.get(highLevelEvent.getShelfID()) < highLevelEvent.getTimeOfReading()) {
                newestTimeStamp.put(highLevelEvent.getShelfID(), highLevelEvent.getTimeOfReading());

                StringBuilder sb = new StringBuilder();
                sb.append("--------------------------------------------------");
                sb.append("\n- [WARNING] : ACCELARATION SPIKE DETECTED = " + highLevelEvent);
                sb.append("\n- Incoming time: " + new Date(System.currentTimeMillis()));
                sb.append("\n--------------------------------------------------");

                LOG.debug(sb.toString());

                // Send event to correlator, to be processed
                // CorrelationService.shakeDetected(highLevelEvent);

                AccelerationShakeEvent rawEvent =
                        new AccelerationShakeEvent(
                                highLevelEvent.getAcceleration(),
                                new Date(highLevelEvent.getTimeOfReading()),
                                highLevelEvent.getShelfID()
                        );

                accelerationEventHandler.handle(rawEvent);

            }
        } else {
            newestTimeStamp.put(highLevelEvent.getShelfID(), highLevelEvent.getTimeOfReading());
        }
    }
}

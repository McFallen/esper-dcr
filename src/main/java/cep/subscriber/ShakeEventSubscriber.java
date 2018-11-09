package cep.subscriber;

import java.util.Map;

import cep.correlator.CorrelationService;
import cep.event.AccelerationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Wraps Esper Statement and Listener. No dependency on Esper libraries.
 */
@Component
public class ShakeEventSubscriber implements StatementSubscriber {

    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(ShakeEventSubscriber.class);

    /** If shake is over  1 m/s^2*/
    private static final String SHAKE_EVENT_THRESHOLD = "80";


    /**
     * {@inheritDoc}
     */
    public String getStatement() {

        // Example using 'Match Recognise' syntax.
        String shakeEventExpression =
                "SELECT * " +
                "FROM AccelerationEvent myacc " +
                "WHERE acceleration > " + SHAKE_EVENT_THRESHOLD +
                "group by shelfID";

        return shakeEventExpression;
    }

    /**
     * Listener method called when Esper has detected a pattern match.
     */
    public void update(Map<String, AccelerationEvent> eventMap) {

        // 1st Temperature in the Warning Sequence
        AccelerationEvent highLevelEvent = (AccelerationEvent) eventMap.get("myacc");

        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------------------------------");
        sb.append("\n- [WARNING] : TEMPERATURE SPIKE DETECTED = " + highLevelEvent);
        sb.append("\n--------------------------------------------------");

        LOG.debug(sb.toString());

        // Send event to correlator, to be processed
        CorrelationService.notifyDesignatedRobotAboutShake(highLevelEvent);
    }
}

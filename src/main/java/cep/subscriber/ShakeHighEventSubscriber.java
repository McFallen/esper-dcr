package cep.subscriber;

import java.util.Map;

import cep.correlator.CorrelationService;
import cep.event.AccelerationShakeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Wraps Esper Statement and Listener. No dependency on Esper libraries.
 */
@Component
public class ShakeHighEventSubscriber {

    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(ShakeHighEventSubscriber.class);

    /** If shake is over  1 m/s^2*/
    private static final String SHAKE_EVENT_THRESHOLD = "80";


    /**
     * {@inheritDoc}
     */
    public String getStatement() {

        // Statement does not take time order into consideration
        String shakeHighEventExpression =
                "SELECT * FROM AccelerationShakeEvent as highLevelEvent " +
                "WHERE acceleration > " + SHAKE_EVENT_THRESHOLD;

        return shakeHighEventExpression;
    }

    /**
     * Listener method called when Esper has detected a pattern match.
     */
    public void update(Map<String, AccelerationShakeEvent> eventMap) {

        // 1st Temperature in the Warning Sequence
        System.out.println(eventMap);
        AccelerationShakeEvent event = (AccelerationShakeEvent) eventMap.get("highLevelEvent");

        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------------------------------");
        sb.append("\n- THIS IS THE HIGHHIGH LEVEL EVENT");
        sb.append("\n--------------------------------------------------");

        LOG.debug(sb.toString());

        // Send event to correlator, to be processed
        CorrelationService.shakeDetected(event);
    }
}

package cep.subscriber;

import cep.event.AccelerationEvent;

import java.util.Map;

/**
 * Just a convenience interface to let us easily contain the Esper statements with the Subscribers -
 * just for clarity so it's easy to see the statements the subscribers are registered against.
 */
public interface StatementSubscriber {

    /**
     * Get the EPL Stamement the Subscriber will listen to.
     * @return EPL Statement
     */
    public String getStatement();

    /**
     * Method to handle events that fires the statement above.
     */
    public void update(Map<String, AccelerationEvent> eventMap);

}

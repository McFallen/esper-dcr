package cep.util;

import cep.event.AccelerationEvent;
import cep.handler.AccelerationEventHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Just a simple class to create a number of Random AccelerationEvents and pass them off to the
 * AccelerationEventHandler.
 */
@Component
public class RandomAcceleratorEventGenerator {

    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(cep.util.RandomAcceleratorEventGenerator.class);

    /**
     * The AccelerationEventHandler - wraps the Esper engine and processes the Events
     */
    @Autowired
    private AccelerationEventHandler accelerationEventHandler;

    /**
     * Creates simple random Acceleration events and lets the implementation class handle them.
     */
    public void startSendingReadings(final long noOfAccelerationEvents) {

        ExecutorService xrayExecutor = Executors.newSingleThreadExecutor();

        xrayExecutor.submit(new Runnable() {
            public void run() {

                LOG.debug(getStartingMessage());

                int count = 0;
                while (count < noOfAccelerationEvents) {
                    AccelerationEvent ve =
                            new AccelerationEvent(
                                    new Random().nextInt(100),
                                    new Date(System.currentTimeMillis() - Integer.toUnsignedLong((new Random().nextInt(1000)))),
                                    count % 5,
                                    "mm/s"
                            );
                    accelerationEventHandler.handle(ve);
                    count++;
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        LOG.error("Thread Interrupted", e);
                    }
                    // Prints progress for amount of produced acceleration events
                    if (count % 10 == 0) { System.out.println(count); }
                }

            }
        });
    }


    private String getStartingMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n************************************************************");
        sb.append("\n* STARTING - ");
        sb.append("\n* PLEASE WAIT - ACCELERATION ARE RANDOM SO MAY TAKE");
        sb.append("\n* A WHILE TO SEE WARNING AND CRITICAL EVENTS!");
        sb.append("\n************************************************************\n");
        return sb.toString();
    }
}

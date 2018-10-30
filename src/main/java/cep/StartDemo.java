package cep;

import cep.communicator.DCRGraphCommunicator;
import cep.correlator.CorrelationService;
import cep.util.RandomAcceleratorEventGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Entry point for the Demo. Run this from your IDE, or from the command line using 'mvn exec:java'.
 */
public class StartDemo {

    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(StartDemo.class);

    
    /**
     * Main method - start the Demo!
     */
    public static void main(String[] args){

        int graphID = 8349;
        LOG.debug("Starting...");
        LOG.info("Using graph ID: " + graphID);

        long noOfAcceleratorEvents = 100;

        if (args.length != 1) {
            LOG.debug("No override of number of events detected - defaulting to " + noOfAcceleratorEvents + " events.");
        } else {
            noOfAcceleratorEvents = Long.valueOf(args[0]);
        }
        // Set up DCR communicator
        DCRGraphCommunicator.setGraphID(graphID);

        // Set up Correlator, load current sims.
        CorrelationService.initCorrelationService();



        // Load simulations
        // Load spring config
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(new String[] { "application-context.xml" });
        BeanFactory factory = (BeanFactory) appContext;

        // Start Demo
        RandomAcceleratorEventGenerator generator = (RandomAcceleratorEventGenerator) factory.getBean("accelerationEventGenerator");
        generator.startSendingReadings(noOfAcceleratorEvents);

    }

}

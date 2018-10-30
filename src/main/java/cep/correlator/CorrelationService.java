package cep.correlator;

import cep.communicator.DCRGraphCommunicator;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Correlates the process to given events
 */
public class CorrelationService {
    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(CorrelationService.class);

    private static HashMap simHash;
    private static HashMap activityHash;

    public static void initCorrelationService() {
        simHash = DCRGraphCommunicator.fetchSimulationsIndexed();
        initActivityHash();
    }

    private static void initActivityHash() {
        HashMap derp = new HashMap();
        Iterator iterSims = simHash.keySet().iterator();
        while (iterSims.hasNext()) {
            Integer simId = (Integer) iterSims.next();
            derp.put(simId, DCRGraphCommunicator.fetchSimulationActivities(simId));
        }

        System.out.println(derp);
    }

}

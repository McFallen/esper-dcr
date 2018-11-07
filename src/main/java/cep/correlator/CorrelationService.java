package cep.correlator;

import cep.communicator.DCRGraphCommunicator;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Correlates the process to given events
 */
public class CorrelationService {
    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(CorrelationService.class);

    private static HashMap simHash;
    private static HashMap activityHash;

    // Activities with simulations belonging to them, should make it easier if there is multiple listeners as they are bundled
    private static HashMap<String, List<Integer>> variableHash;

    // No pretty way to do bidirectional map. Can be optimized with a tool, for now we leave hardcoded
    private static HashMap<String, String> activityIDtoVariable = new HashMap<String, String>(){ { put("robotID", "Activity14");put("shelfID", "Activity10"); } };
    private static HashMap<String, String> variableToActivityID = new HashMap<String, String>(){ { put("Activity14", "robotID");put("Activity10", "shelfID"); } };

    public static void initCorrelationService() {
        simHash = DCRGraphCommunicator.fetchSimulationsIndexed();
        initActivityAndVariableHash();
    }

    private static void initActivityAndVariableHash() {
        HashMap<Integer, JSONObject> activityHash = new HashMap<Integer, JSONObject>();
        HashMap<String, Integer> variableHash = new HashMap<String, Integer>();
        for (Object o : simHash.keySet()) {
            Integer simId = (Integer) o;
            activityHash.put(simId, DCRGraphCommunicator.fetchSimulationActivities(simId));
            JSONArray variables = DCRGraphCommunicator.fetchSimulationData(simId);

            if (variables != null) {
                for (Object var : variables) {
                    ((JSONObject) var).get("id");
                    ((JSONObject) var).get("value");
                }
            }
            //            System.out.println(variables);
//            variableHash.put(simId, );

        }
    }

    private static void initDataHash(){


    }
}

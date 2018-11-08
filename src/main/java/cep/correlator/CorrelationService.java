package cep.correlator;

import cep.communicator.DCRGraphCommunicator;

import cep.event.AccelerationEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.lang.System.exit;

/**
 * Correlates the process to given events
 */
public class CorrelationService {
    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(CorrelationService.class);

    private static HashMap simHash;
    private static HashMap activityHash;

    // Activities with simulations belonging to them, should make it easier if there is multiple listeners as they are bundled
    private static HashMap<String, Integer> variableHash;
    private static HashMap<String, HashMap<String, List<Integer>>> simVarHash;
    // No pretty way to do bidirectional map. Can possibly be optimized with a tool, for now we leave hardcoded
    private static HashMap<String, String> activityIDtoVariable = new HashMap<String, String>(){ { put("robotID", "Activity2");put("shelfID", "Activity10"); } };
    private static HashMap<String, String> variableToActivityID = new HashMap<String, String>(){ { put("Activity2", "robotID");put("Activity10", "shelfID"); } };

    // Which activity is enabled for simulations
    // Simulations can be found and notified/published to
    // Shelf-ID, to Robot-ID ot sim-ID
    private static HashMap<String, List<String>> pubToSub = new HashMap<String, List<String>>();

    public static void initCorrelationService() {
        simHash = DCRGraphCommunicator.fetchSimulationsIndexed();
        initActivityAndVariableHash();
    }

    private static void initActivityAndVariableHash() {
        HashMap<Integer, JSONObject> activityHash = new HashMap<Integer, JSONObject>();

        for (Object key : simHash.keySet()) {
            Integer simId = (Integer) key;
            activityHash.put(simId, DCRGraphCommunicator.fetchSimulationActivities(simId));
            JSONArray variables = DCRGraphCommunicator.fetchSimulationData(simId);
            //Object robot = null;
            if (variables != null) {
                for (Object var : variables) {
                    // Locate the relevant IDs
                    String activityID = (String) ((JSONObject) var).get("id");
                    if (activityID.equals(activityIDtoVariable.get("shelfID"))) {

                        // build/update entries for pub/sub
                        if (pubToSub.containsKey(((JSONObject) var).get("value"))) {
                            List<String> oldSubs = pubToSub.get(((JSONObject) var).get("value"));
                            oldSubs.add((String) key);
                            List<String> updatedSubs = oldSubs;
                            pubToSub.put(((JSONObject) var).get("value").toString(), updatedSubs);

                        } else {
                            List<String> entry = new ArrayList<String>();
                            entry.add(simId.toString());
                            pubToSub.put(((JSONObject) var).get("value").toString(), entry);
                        }
                        // We don't care for other ID's, so we can just break out of the loop
                        break;
                    }

                }


            }



        }
    }

    // Find which simulations that needs to be notified and pass it to the communicator
    public static void notifyDesignatedRobotAboutShake(AccelerationEvent event){
        List<String> listeners = pubToSub.get(Integer.toString(event.getShelfID()));

        // Notify each simulation
        for (String listener : listeners) {
            DCRGraphCommunicator.executeActivityWithData(Integer.parseInt(listener), "Activity14", Integer.toString((int) event.getAcceleration()));
        }
    }
}

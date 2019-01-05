package cep.correlator;

import cep.communicator.DCRGraphCommunicator;

import cep.event.AccelerationEvent;
import cep.event.AccelerationShakeEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Correlates the process to given events
 */
public class CorrelationService {
    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(CorrelationService.class);

    private static HashMap simHash;
    private static HashMap<Integer, JSONObject> activityHash = new HashMap<Integer, JSONObject>();

    private static HashMap<String, HashMap<String, List<Integer>>> simVarHash;
    // No pretty way to do bidirectional map. Can possibly be optimized with a tool, for now we leave hardcoded
    private static HashMap<String, String> activityIDtoVariable = new HashMap<String, String>(){ { put("robotID", "Activity2");put("shelfID", "Activity10"); } };
    private static HashMap<String, String> variableToActivityID = new HashMap<String, String>(){ { put("Activity2", "robotID");put("Activity10", "shelfID"); } };

    // Which activity is enabled for simulations
    // Simulations can be found and notified/published to
    // Shelf-ID, to Robot-ID ot sim-ID
    private static HashMap<String, HashMap<String, Set<String>>> pubToSub = new HashMap<String, HashMap<String, Set<String>>>(){ { put("Activity10", new HashMap<String, Set<String>>()); } };

    public static void initCorrelationService() {
        simHash = DCRGraphCommunicator.fetchSimulationsIndexed();

        initCorrelations();
    }

    private static void initCorrelations() {

        for (Object key : simHash.keySet()) {
            Integer simId = (Integer) key;
            activityHash.put(simId, DCRGraphCommunicator.fetchSimulationActivities(simId));
            JSONArray variables = DCRGraphCommunicator.fetchSimulationData(simId);
            //Object robot = null;
            if (variables != null) {
                for (Object var : variables) {
                    // Locate the relevant IDs
                    JSONObject varr = (JSONObject) var;
                    String activityID = (String) ((JSONObject) var).get("id");
                    if (pubToSub.keySet().contains(activityID)) {
                        // build/update entries for pub/sub for shelves
                        if (!pubToSub.get(activityID).isEmpty() &&
                            pubToSub.get(activityID).containsKey(varr.get("value").toString())) {
                            Set<String> oldSubs = pubToSub.get(activityID).get(varr.get("value").toString());
                            oldSubs.add(key.toString());
                            Set<String> updatedSubs = oldSubs;
                            pubToSub.get(activityID).put(((JSONObject) var).get("value").toString(), updatedSubs);
                        } else {

                            Set<String> emptySimHash = new HashSet<String>();
                            emptySimHash.add(simId.toString());
                            HashMap<String, Set<String>> entry = new HashMap<String, Set<String>>();

                            pubToSub.get(activityID).put(((JSONObject) var).get("value").toString(), emptySimHash);
                        }
                        // We don't care for other ID's, so we can just break out of the loop
                        break;
                    } // Add here additional clauses for building pub/sub

                }


            }



        }
    }

    public static void updateActivities(Integer simID, JSONObject activities){
        if (activities != null) {
            activityHash.put(simID, activities);
            LOG.debug("Activities updated for simulation: " + simID);
        } else {
            LOG.info("No activities executed for process instance");
        }
    }


    public static HashMap<Integer, JSONObject> getActivityHash(){
        return activityHash;
    }
    // Find which simulations that needs to be notified and pass it to the communicator
    public static void shakeDetected(AccelerationEvent event){
        Set<String> listeners = pubToSub.get("shelfID").get(Integer.toString(event.getShelfID()));

        // Notify each simulation
        for (String listener : listeners) {
            DCRGraphCommunicator.executeActivityWithData(Integer.parseInt(listener), "Activity14", Integer.toString((int) event.getAcceleration()));
        }
    }
    public static void shakeDetected(AccelerationShakeEvent event){
        String activity = "Activity14";

        // Get the node for which the data is contained
        Set<String> listeners = pubToSub.get("Activity10").get(Integer.toString(event.getShelfID()));


        // Notify each simulation

        if(listeners != null) {
            for (String listener : listeners) {

                // Check if activity is executable
                if (checkActivityEnabled(Integer.parseInt(listener), activity)) {
                    DCRGraphCommunicator.executeActivityWithData(Integer.parseInt(listener), "Activity14", Integer.toString((int) event.getAcceleration()));
                }
            }
        } else {
            LOG.info("No correlated process instance found for the High-Level event");
        }
    }

    private static boolean checkActivityEnabled(Integer simID, String activity) {

        for (Object activityEntry:
            CorrelationService.getActivityHash().get(simID).getJSONObject("events").getJSONArray("event")) {
            activityEntry = (JSONObject) activityEntry;

            if (((JSONObject) activityEntry).get("id").toString().equals(activity) &&
                ((JSONObject) activityEntry).get("enabled").toString().equals("true")) {
                //System.out.println("We actually get here, who'd thunk");
                return true;
            }

        }
        return false;
    }
}

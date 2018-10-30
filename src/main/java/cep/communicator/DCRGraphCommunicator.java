package cep.communicator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;
import org.json.XML;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;

public final class DCRGraphCommunicator {

    private static int graphID;
    private static String user = "AllanNielsenDTU";
    private static String password = "oeJP?d";
    private static String baseUrl = "http://dcrgraphs.net/api/graphs/";


    public static int createSimulation() {
        return 0;
    }

    private static JSONObject fetchSimulationsMixed(){
        String url = baseUrl + graphID + "/sims/";
        HttpResponse resp = sendRequest(url, "GET");
        System.out.println((String)resp.getBody());
        return convertToJSON((String)resp.getBody());
    }

    public static HashMap<Integer, JSONObject> fetchSimulationsIndexed()  {
        Iterator mixedSims = fetchSimulationsMixed().getJSONObject("simulations").getJSONArray("simulation").iterator();

        HashMap<Integer, JSONObject> indexedSims = new HashMap<Integer, JSONObject>();
        while (mixedSims.hasNext()) {
            JSONObject entry = (JSONObject) mixedSims.next();
            indexedSims.put((Integer) entry.get("id"), entry);
        }

        return indexedSims;
    }

    public static JSONObject fetchSimulationActivities(int simulationID){
        String url = getBaseUrl() + getGraphID() + "/sims/" + Integer.toString(simulationID) + "/events?filter=enabled-or-pending";
        HttpResponse sims = sendRequest(url, "GET");

        JSONObject simActivities = convertToJSON(((String) sims.getBody()).replace("\\\"", "\""));

        return simActivities;
    }



    public static void executeActivity(int graphID, int simulationID) {

        String url = baseUrl + graphID + "/sims/" + simulationID;
        //
    }

    @org.jetbrains.annotations.Nullable
    private static HttpResponse sendRequest(String URL, String requestType){

        try {
            HttpResponse jsonResp;
            if (requestType.equals("GET") || requestType.equals("get")) {
                jsonResp = Unirest.get(URL).basicAuth(user, password).asString();

            } else if (requestType.equals("POST") || requestType.equals("post")) {
                jsonResp = Unirest.post(URL).basicAuth(user, password).asString();
            } else {
                throw new Exception("Unknown request type. Please specify GET, POST or DELETE request types.");
            }

            if (jsonResp.getStatus() == 200 || jsonResp.getStatus() == 201) {
                return jsonResp;
            } else if(jsonResp.getStatus() == 405 || jsonResp.getStatus() == 407) {
                throw new Exception("Bad authentication, please check username and password");
            }
        } catch(UnirestException UnirestEX) {
            System.out.println(UnirestEX.getMessage());
            System.out.println(Arrays.toString(UnirestEX.getStackTrace()));
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
            //System.out.println(ex.getStackTrace().toString());
        }
        return null;
    }

    private static JSONObject convertToJSON(String strWithXML) {
        String bodyWithHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + strWithXML;
        System.out.println(bodyWithHeader);

        return XML.toJSONObject(bodyWithHeader);
    }

    public static int getGraphID() {
        return graphID;
    }

    public static void setGraphID(int graphId) {
        DCRGraphCommunicator.graphID = graphId;
    }

    public static String getUser() {
        return user;
    }

    public static void setUser(String user) {
        DCRGraphCommunicator.user = user;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        DCRGraphCommunicator.password = password;
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static void setBaseUrl(String baseUrl) {
        DCRGraphCommunicator.baseUrl = baseUrl;
    }
}

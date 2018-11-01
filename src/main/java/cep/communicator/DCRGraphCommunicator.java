package cep.communicator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import com.google.common.base.CharMatcher;
import org.json.JSONObject;
import org.json.XML;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;

public final class DCRGraphCommunicator {

    private static int graphID;
    private static String user = "AllanNielsenDTU";
    private static String password = "oeJP?d";
    private static String baseUrl = "http://www.dcrgraphs.net/api/graphs/";


    public static int createSimulation() {
        return 0;
    }

    private static JSONObject fetchSimulationsMixed(){
        String url = baseUrl + graphID + "/sims/";
        HttpResponse resp = sendGetRequest(url);

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
        HttpResponse sims = sendGetRequest(url);

        JSONObject simActivities = convertToJSON(((String) sims.getBody()).replace("\\\"", "\""));

        return simActivities;
    }

    public static JSONObject fetchSimulationData(int simulationID){
        String url = getBaseUrl() + getGraphID() + "/sims/" + Integer.toString(simulationID);
        HttpResponse sims = sendGetRequest(url);
        System.out.println(sims.getBody());
        JSONObject simData = convertToJSON(((String) sims.getBody()).replace("\\\"", "\""));
        JSONObject dataVars;
        try {
            dataVars = simData.getJSONObject("executionResult").getJSONObject("dcrgraph").getJSONObject("runtime").getJSONObject("marking").getJSONObject("globalStore");
            System.out.println(dataVars.getJSONArray("variable"));
        } catch (Exception ex) {
            dataVars = null;
            System.out.println("No data found for simulation with ID: " + simulationID);
        }

        return dataVars;
    }

    public static void executeActivityWithData(int simulationID, String activityID, String data){

        String dataJson = null;
        if (!data.isEmpty()) {
            dataJson = "{DataXML:\'<globalStore><variable id=\"" + activityID + "\" value=\"" + data+ "\" isNull=\"false\" type=\"int\"/></globalStore>\'}";
        }

        String url = baseUrl + graphID + "/sims/" + simulationID + "/events/" + activityID;

        HttpResponse resp = sendPostRequest(url, dataJson);

    }

    public static void executeActivity(int simulationID, String activityID) {
        executeActivityWithData(simulationID, activityID, null);
    }

    private static HttpResponse sendGetRequest(String URL) {
        return sendRequest(URL, "GET", null);
    }

    private static HttpResponse sendPostRequest(String URL, String data) {
        return sendRequest(URL, "POST", data);
    }

    @org.jetbrains.annotations.Nullable
    private static HttpResponse sendRequest(String url, String requestType, String data){

        try {
            HttpResponse jsonResp;
            if (requestType.equals("GET") || requestType.equals("get")) {
                jsonResp = Unirest.get(url).basicAuth(user, password).asString();

            } else if (requestType.equals("POST") || requestType.equals("post")) {
                if (data.isEmpty()) {
                    jsonResp = Unirest
                            .post(url)
                            .basicAuth(user, password)
                            .asString();
                } else {
                    jsonResp = Unirest.
                            post(url).
                            basicAuth(user, password).
                            header("Content-Type", "application/json").
                            body(data).
                            asString();
                }
            } else {
                throw new Exception("Unknown request type. Please specify GET, POST or DELETE request types.");
            }

            if (jsonResp.getStatus() == 200 || jsonResp.getStatus() == 201 || jsonResp.getStatus() == 204) {
                return jsonResp;
            } else if(jsonResp.getStatus() == 405 || jsonResp.getStatus() == 407) {
                throw new Exception("Bad authentication, please check username and password");
            } else if(jsonResp.getStatus() == 500 || jsonResp.getStatus() == 501 || jsonResp.getStatus() == 504) {
                throw new Exception("Internal error, maybe wrong request? please verify. Request: " + url);
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
        String bodyWithHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + CharMatcher.is('\"').trimFrom(strWithXML);

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

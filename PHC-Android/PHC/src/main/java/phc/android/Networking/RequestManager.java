package phc.android.Networking;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by tonywu on 2/24/15.
 * Contains abstractions for doing network requests.
 * Should be instantiated by any class that needs to do network requests
 */

public class RequestManager {

    private static final String BASE_URL = "http://phc-production.herokuapp.com";
    private static final String LOGIN_ENDPOINT = "/login";
    private static final String SEARCH_ENDPOINT = "/api/v1/search";
    private static final String SEARCH_REG_ENDPOINT = "/api/v1/event_registrations/search";
    private static final String UPDATE_SERVICE_ENDPOINT = "/api/v1/event_registrations/update_service";
    private static final String CREATE_EVENT_REG_ENDPOINT = "/api/v1/event_registrations/create";
    private static final String USER_INFO_ENDPOINT = "/api/v1/accounts";
    private static final String SERVICES_ENDPOINT = "/services";
    private static final String UPDATE_FEEDBACK_ENDPOINT = "/api/v1/event_registrations/update_feedback";

    private static RequestQueue sRequestQueue;
    private static String sTAG;

    public RequestManager(String TAG, RequestQueue requestQueue) {
        sRequestQueue = requestQueue;
        sTAG = TAG;
    }

    /**
     * Used to login a user and receive a user_id and auth_token
     * @param email user email
     * @param password user password
     * @param responseListener listener that is called when response received
     * @param errorListener listener that is called when error
     */
    public void requestLogin(final String email,
                             final String password,
                             Response.Listener<JSONObject> responseListener,
                             Response.ErrorListener errorListener) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);
        params.put("Accept", "*/*");

        JsonObjectRequest loginRequest = new JsonObjectRequest(
                BASE_URL + LOGIN_ENDPOINT,
                new JSONObject(params),
                responseListener,
                errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "*/*");
                return headers;
            }
        };

        loginRequest.setTag(sTAG);
        setRetryPolicy(loginRequest);
        sRequestQueue.add(loginRequest);
    }

    /**
     * Used to search for a specific user
     * @param firstName first name of search
     * @param lastName last name of search
     * @param userId user_id of logged in user
     * @param authToken auth_token of logged in user
     * @param responseListener listener that is called when response received
     * @param errorListener listener that is called when error
     */
    public void requestSearch(String firstName,
                                     String lastName,
                                     final String userId,
                                     final String authToken,
                                     final int cursor,
                                     Response.Listener<JSONArray> responseListener,
                                     Response.ErrorListener errorListener) {
        // Strip whitespace from first and last name;
        firstName = firstName.replaceAll("\\s+","");
        lastName = lastName.replaceAll("\\s+","");

        StringBuilder buildUrl = new StringBuilder(BASE_URL);
        buildUrl.append(SEARCH_ENDPOINT);
        buildUrl.append("?");
        buildUrl.append("FirstName=");
        buildUrl.append(firstName);
        buildUrl.append("&");
        buildUrl.append("LastName=");
        buildUrl.append(lastName);
        buildUrl.append("&");
        buildUrl.append("cursor=");
        buildUrl.append(cursor);

        JsonArrayRequest searchRequest = new JsonArrayRequest(buildUrl.toString(),
                responseListener,
                errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("user_id", userId);
                params.put("auth_token", authToken);
                params.put("Accept", "*/*");
                return params;
            }
        };
        searchRequest.setTag(sTAG);
        setRetryPolicy(searchRequest);
        sRequestQueue.add(searchRequest);
    }

    /**
     * Used to search for a specific user
     * @param sfID salesforce Id of the user we want info from
     * @param userId user_id of logged in user
     * @param authToken auth_token of logged in user
     * @param responseListener listener that is called when response received
     * @param errorListener listener that is called when error
     */
    public void requestUserInfo(final String sfID,
                              final String userId,
                              final String authToken,
                              Response.Listener<JSONObject> responseListener,
                              Response.ErrorListener errorListener) {

        StringBuilder buildUrl = new StringBuilder(BASE_URL);
        buildUrl.append(USER_INFO_ENDPOINT);
        buildUrl.append("/");
        buildUrl.append(sfID);

        JsonObjectRequest userInfoRequest = new JsonObjectRequest(buildUrl.toString(),
                null,
                responseListener,
                errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("user_id", userId);
                params.put("auth_token", authToken);
                params.put("Accept", "*/*");
                return params;
            }
        };
        userInfoRequest.setTag(sTAG);
        setRetryPolicy(userInfoRequest);
        sRequestQueue.add(userInfoRequest);
    }

    /**
     * Used to search for a person receiving services
     * @param qrCode qrCode of person seeking services
     * @param userId user_id of logged in user
     * @param authToken auth_token of logged in user
     */
    public void requestSearchByCode(final String qrCode,
                                    final String userId,
                                    final String authToken,
                                    Response.Listener<JSONObject> responseListener,
                                    Response.ErrorListener errorListener){

        JsonObjectRequest searchRequest = new JsonObjectRequest(BASE_URL + SEARCH_REG_ENDPOINT,
                null,
                responseListener,
                errorListener) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("user_id", userId);
                params.put("auth_token", authToken);
                params.put("Accept", "*/*");
                params.put("Number__c", qrCode);
                return params;
            }
        };
        searchRequest.setTag(sTAG);
        setRetryPolicy(searchRequest);
        sRequestQueue.add(searchRequest);
    }

    /**
     * Used to create a new Event Registration object in the database
     * @param params key values that descrive the event registration object being created
     * @param userId user_id of logged in user
     * @param authToken auth_token of logged in user
     * @param responseListener listener that is called when response received
     * @param errorListener listener that is called when error
     */
    public void requestCreateEventReg(HashMap<String, Object> params,
                                     final String userId,
                                     final String authToken,
                                     Response.Listener<JSONObject> responseListener,
                                     Response.ErrorListener errorListener) {

        JsonObjectRequest createRequest = new JsonObjectRequest(BASE_URL + CREATE_EVENT_REG_ENDPOINT,
                new JSONObject(params),
                responseListener,
                errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("user_id", userId);
                headers.put("auth_token", authToken);
                headers.put("Accept", "*/*");
                return headers;
            }
        };
        createRequest.setTag(sTAG);
        setRetryPolicy(createRequest);
        sRequestQueue.add(createRequest);
    }

    /**
     * Used to mark a client as checked-in to a service.
     * @param userId user_id of logged in user
     * @param authToken auth_token of logged in user
     * @param responseListener listener that is called when response received
     * @param errorListener listener that is called when error
     */
    public void requestUpdateService(final String qrCode,
                                     final String serviceName,
                                     final String userId,
                                     final String authToken,
                                     Response.Listener<JSONObject> responseListener,
                                     Response.ErrorListener errorListener) {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("Number__c", qrCode);
        params.put("service_name", serviceName);

        JsonObjectRequest updateRequest = new JsonObjectRequest(BASE_URL + UPDATE_SERVICE_ENDPOINT,
                new JSONObject(params),
                responseListener,
                errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("user_id", userId);
                params.put("auth_token", authToken);
                params.put("Accept", "*/*");
                return params;
            }
        };
        updateRequest.setTag(sTAG);
        setRetryPolicy(updateRequest);
        sRequestQueue.add(updateRequest);
    }

    public void requestServices(final String userId,
                                final String authToken,
                                Response.Listener<JSONArray> responseListener,
                                Response.ErrorListener errorListener) {
        JsonArrayRequest serviceRequest = new JsonArrayRequest(BASE_URL + SERVICES_ENDPOINT,
                responseListener,
                errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("user_id", userId);
                params.put("auth_token", authToken);
                params.put("Accept", "*/*");
                return params;
            }
        };
        serviceRequest.setTag(sTAG);
        setRetryPolicy(serviceRequest);
        sRequestQueue.add(serviceRequest);
    }

    /**
     * Used to fetch all the services. need endpoint
     *
     */
  public void requestGetApplied(final String qrCode,
                                final String userId,
                                final String authToken,
                                Response.Listener<JSONObject> responseListener,
                                Response.ErrorListener errorListener){

          StringBuilder buildUrl = new StringBuilder(BASE_URL);
          buildUrl.append("/api/v1/event_registrations/get_applied");
          buildUrl.append("/");
          buildUrl.append("?");
          buildUrl.append("Number__c=");
          buildUrl.append(qrCode);

          JsonObjectRequest searchRequest = new JsonObjectRequest(buildUrl.toString(),
                  null,
                  responseListener,
                  errorListener){
          @Override
          public Map<String, String> getHeaders(){
              HashMap<String, String> header = new HashMap<String, String>();
              header.put("user_id", userId);
              header.put("auth_token", authToken);
              header.put("Accept", "*/*");
              return header;
          }
        };
      searchRequest.setTag(sTAG);
      setRetryPolicy(searchRequest);
      sRequestQueue.add(searchRequest);
  }

    /**
     * Used to create a new object with checkout information. Similar to requestUpdateService
     */
    public void requestUpdateFeedback(final String comments,
                                      final int experience,
                                      final JSONArray servicesNotReceived,
                                      final String scanResult,
                                      final String userId,
                                      final String authToken,
                                      Response.Listener<JSONObject> responseListener,
                                      Response.ErrorListener errorListener){

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("Feedback__c", comments);
        params.put("Experience__c", experience);
        params.put("Services_Needed__c", servicesNotReceived);
        params.put("Number__c", scanResult);

        JsonObjectRequest createRequest = new JsonObjectRequest(BASE_URL + UPDATE_FEEDBACK_ENDPOINT,
                new JSONObject(params),
                responseListener,
                errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("user_id", userId);
                params.put("auth_token", authToken);
                params.put("Accept", "*/*");
                params.put("content-type", "application/json");
                return params;
            }
        };
        createRequest.setTag(sTAG);
        setRetryPolicy(createRequest);
        sRequestQueue.add(createRequest);
    }

    /**
     * Volley by default sends a second retry request if there is no response for the first after
     * 2.5 seconds. Because many of our requests can take more than 2.5 seconds, Volley ends up
     * sending a lot of double request, which causes issues for POSTs. setRetryPolicy()
     * increase the time before the second retry to 10 seconds, matching the time it takes for a
     * retry alertdialog to appear.
     */
    private void setRetryPolicy(JsonObjectRequest request){
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(10), 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void setRetryPolicy(JsonArrayRequest request){
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(10), 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
    
}

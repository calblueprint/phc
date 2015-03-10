package phc.android.Networking;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tonywu on 2/24/15.
 * Contains abstractions for doing network requests.
 * Should be instantiated by any class that needs to do network requests
 */
public class RequestManager {

    //TODO: Change to heroku url when rails code pushed to heroku
    private static final String BASE_URL = "http://phc-staging.herokuapp.com";
    private static final String LOGIN_ENDPOINT = "/login";
    private static final String SEARCH_ENDPOINT = "/api/v1/search";
    private static final String CREATE_EVENT_REG_ENDPOINT = "/api/v1/event_registrations/create";
    private static final String USER_INFO_ENDPOINT = "/api/v1/accounts";
    private static final String SERVICES_ENDPOINT = "/services";

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
    public void requestSearch(final String firstName,
                                     final String lastName,
                                     final String userId,
                                     final String authToken,
                                     Response.Listener<JSONArray> responseListener,
                                     Response.ErrorListener errorListener) {

        StringBuilder buildUrl = new StringBuilder(BASE_URL);
        buildUrl.append(SEARCH_ENDPOINT);
        buildUrl.append("?");
        buildUrl.append("FirstName=");
        buildUrl.append(firstName);
        buildUrl.append("&");
        buildUrl.append("LastName=");
        buildUrl.append(lastName);

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
        sRequestQueue.add(userInfoRequest);
    }

    /**
     * Used to search for a person receiving services
     * @param qrCode qrCode of person seeking services
     * @param userId user_id of logged in user
     * @param authToken auth_token of logged in user
     */
    public void requestSearchByCode(String qrCode,
                                    String authToken,
                                    String userId,
                                    Response.Listener<JSONObject> responseListener,
                                    Response.ErrorListener errorListener){
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("qr_code", qrCode);
        params.put("user_id", userId);
        params.put("auth_token", authToken);

        JsonObjectRequest searchRequest = new JsonObjectRequest(BASE_URL + SEARCH_ENDPOINT,
                new JSONObject(params),
                responseListener,
                errorListener);
        searchRequest.setTag(sTAG);
        sRequestQueue.add(searchRequest);
    }

    /**
     * Used to create a new Event Registration object in the databse
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
        sRequestQueue.add(createRequest);
    }

    public void requestServices(final String userId,
                                final String authToken,
                                Response.Listener<JSONArray> responseListener,
                                Response.ErrorListener errorListener) {
        JsonArrayRequest searchRequest = new JsonArrayRequest(BASE_URL + SERVICES_ENDPOINT,
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
        sRequestQueue.add(searchRequest);

    }
}

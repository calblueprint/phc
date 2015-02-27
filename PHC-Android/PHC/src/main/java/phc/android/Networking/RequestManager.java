package phc.android.Networking;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by tonywu on 2/24/15.
 */
public class RequestManager {

    //TODO: Change to heroku url when rails code pushed to heroku
    private static final String BASE_URL = "localhost:3000";

    private static final String LOGIN_ENDPOINT = "/login";

    private static final String SEARCH_ENDPOINT = "/api/v1/search";

    private static final String CREATE_ENDPOINT = "/api/v1/create";

    private static RequestQueue sRequestQueue;

    private static String sTAG;

    public RequestManager(String TAG, RequestQueue requestQueue) {
        sRequestQueue = requestQueue;
        sTAG = TAG;
    }

    public static void requestLogin(String email,
                                    String password,
                                    Response.Listener<JSONObject> responseListener,
                                    Response.ErrorListener errorListener) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);

        JsonObjectRequest loginRequest = new JsonObjectRequest(BASE_URL + LOGIN_ENDPOINT,
                new JSONObject(params),
                responseListener,
                errorListener);
        loginRequest.setTag(sTAG);
        sRequestQueue.add(loginRequest);
    }

    public static void requestSearch(String firstName,
                                     String lastName,
                                     String userId,
                                     String authToken,
                                     Response.Listener<JSONObject> responseListener,
                                     Response.ErrorListener errorListener) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("first_name", firstName);
        params.put("last_name", lastName);
        params.put("user_id", userId);
        params.put("auth_token", authToken);

        JsonObjectRequest searchRequest = new JsonObjectRequest(BASE_URL + SEARCH_ENDPOINT,
                new JSONObject(params),
                responseListener,
                errorListener);
        searchRequest.setTag(sTAG);
        sRequestQueue.add(searchRequest);
    }

    public static void requestCreate(String firstName,
                                     String lastName,
                                     String userId,
                                     String authToken,
                                     Response.Listener<JSONObject> responseListener,
                                     Response.ErrorListener errorListener) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("first_name", firstName);
        params.put("last_name", lastName);
        params.put("user_id", userId);
        params.put("auth_token", authToken);

        JsonObjectRequest createRequest = new JsonObjectRequest(BASE_URL + CREATE_ENDPOINT,
                new JSONObject(params),
                responseListener,
                errorListener);
        createRequest.setTag(sTAG);
        sRequestQueue.add(createRequest);
    }
}

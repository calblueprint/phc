package phc.android.Networking;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import phc.android.Helpers.SearchResult;

/**
 * Created by tonywu on 2/24/15.
 */
public class RequestManager {

    //TODO: Change to heroku url when rails code pushed to heroku
    //Change this to 10.0.2.2 if you are using the build in android emulator/phone
    private static final String BASE_URL = "http://10.0.2.2:3000";

    private static final String LOGIN_ENDPOINT = "/login";

    private static final String SEARCH_ENDPOINT = "/api/v1/search";

    private static final String CREATE_ENDPOINT = "/api/v1/create";

    private static RequestQueue sRequestQueue;

    private static String sTAG;

    public RequestManager(String TAG, RequestQueue requestQueue) {
        sRequestQueue = requestQueue;
        sTAG = TAG;
    }

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

    public void requestSearch(String firstName,
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

    public void requestCreate(String firstName,
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

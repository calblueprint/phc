package phc.android.Main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;

import phc.android.Networking.RequestManager;
import phc.android.R;

public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";
    // Key for user shared preferences
    private static final String USER_AUTH_PREFS_NAME = "UserKey";
    // Timeout for login request
    private static final int REQUEST_TIMEOUT = 10000;

    private static RequestManager sRequestManager;
    private static RequestQueue sRequestQueue;

    // Shared Preferences
    private SharedPreferences mUserPreferences;
    // SharedPreference editor object
    private SharedPreferences.Editor mUserPreferencesEditor;
    private EditText mEmailText;
    private EditText mPasswordText;
    private Button mLoginButton;
    private ProgressDialog mProgressDialog;
    // Timer used to schedule the progress dialog to disappear
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailText = (EditText) findViewById(R.id.login_email_edittext);
        mPasswordText = (EditText) findViewById(R.id.login_password_edittext);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new OnLoginClickListener());

        //Set up Volley request framework
        sRequestQueue = Volley.newRequestQueue(this);
        sRequestManager = new RequestManager(TAG, sRequestQueue);
    }

    /**
     * OnClickListener for the Login button
     */
    private class OnLoginClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String email = mEmailText.getText().toString();
            String password = mPasswordText.getText().toString();

            showProgressDialog();
            sRequestManager.requestLogin(
                     email,
                     password,
                     new LoginResponseListener(),
                     new LoginErrorListener());
        }
    }

    private class LoginResponseListener implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject jsonObject) {
            try {
                // Get the user_id and auth_token and save into SharedPreferences
                String userId = jsonObject.getString("user_id");
                String authToken = jsonObject.getString("auth_token");
                mUserPreferences = getSharedPreferences(USER_AUTH_PREFS_NAME,
                        Context.MODE_PRIVATE);
                mUserPreferencesEditor = mUserPreferences.edit();
                mUserPreferencesEditor.putString("user_id", userId);
                mUserPreferencesEditor.putString("auth_token", authToken);
                mUserPreferencesEditor.apply();

                // Dismiss the progress dialog and stop the timed task
                mProgressDialog.dismiss();
                mTimer.cancel();
                mTimer.purge();

                // Start MainActivity
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);

                // Close this activity
                finish();
            } catch(JSONException e) {
                Log.e(TAG, "Error parsing JSON");
                Log.e(TAG, e.toString());
            }
        }
    }

    private class LoginErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            String errorMessage = "";
            if (volleyError.getLocalizedMessage() != null) {
                Log.e(TAG, volleyError.toString());
            }
            if (volleyError instanceof NoConnectionError){
                errorMessage = "No internet connection. Please reconnect or try another network.";
            }
            else if (volleyError instanceof TimeoutError){
                errorMessage = "Slow internet. Please try again or connect to another network.";
            }
            else if (volleyError instanceof NetworkError){
                errorMessage = "Server error. Please ask for assistance.";
            }
            else if (volleyError instanceof ParseError){
                errorMessage = "Error parsing the server's response. Please ask for assistance.";
            }
            else if (volleyError.networkResponse != null){
                if (volleyError.networkResponse.statusCode == 401) {
                    errorMessage = "Incorrect e-mail or password.";
                }
            }
            else { errorMessage = "Unknown login error. Please ask for assistance."; }

            // Dismiss the progress dialog and stop the timed task
            mProgressDialog.dismiss();
            mTimer.cancel();
            mTimer.purge();

            // Display the error message
            Toast toast = Toast.makeText(getApplicationContext(), errorMessage,
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void showProgressDialog() {
        mProgressDialog =
                ProgressDialog.show(LoginActivity.this, "Please wait...", "Logging In", true);

        // Creates a timer that closes the progress dialog and displays a toast after timeout
        mTimer = new Timer();
        mTimer.schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressDialog.dismiss();
                                String errorMessage = "Request timed out. Please try again.";
                                Toast toast = Toast.makeText(getApplicationContext(), errorMessage,
                                        Toast.LENGTH_LONG);
                                toast.show();
                            }
                        });
                    }
                },
                REQUEST_TIMEOUT
        );
    }


}

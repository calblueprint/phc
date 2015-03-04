package phc.android.Main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import phc.android.Networking.RequestManager;
import phc.android.R;

public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";
    // Key for user shared preferences
    private static final String USER_PREFS_NAME = "UserKey";

    private static RequestManager sRequestManager;
    private static RequestQueue sRequestQueue;

    // Shared Preferences
    private SharedPreferences mUserPreferences;
    // SharedPreference editor object
    private SharedPreferences.Editor mUserPreferencesEditor;
    private EditText mEmailText;
    private EditText mPasswordText;
    private Button mLoginButton;

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

            // Used to bypass login, Testing only
            // TODO: Take out before publishing app
            if (email.equals("email") && password.equals("pass")) {
                // Start MainActivity
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);

                // Close this activity
                finish();
            } else {
                sRequestManager.requestLogin(email,
                        password,
                        new LoginResponseListener(),
                        new LoginErrorListener());
            }
        }
    }

    private class LoginResponseListener implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject jsonObject) {
            try {
                // Get the user_id and auth_token and save into SharedPreferences
                String userId = jsonObject.getString("user_id");
                String authToken = jsonObject.getString("auth_token");
                mUserPreferences = getSharedPreferences(USER_PREFS_NAME,
                        Context.MODE_PRIVATE);
                mUserPreferencesEditor = mUserPreferences.edit();
                mUserPreferencesEditor.putString("user_id", userId);
                mUserPreferencesEditor.putString("auth_token", authToken);
                mUserPreferencesEditor.apply();

                // Start MainActivity
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);

                // Close this activity
                finish();
            } catch(JSONException e){
                Log.e(TAG, "Error parsing JSON");
                Log.e(TAG, e.toString());
            }
        }
    }

    private class LoginErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            if (volleyError.getLocalizedMessage() != null) {
                Log.e(TAG, volleyError.toString());
            }

            Toast toast = Toast.makeText(getApplicationContext(), "Error during login", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
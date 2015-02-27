package phc.android.Main;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import phc.android.Networking.RequestManager;
import phc.android.R;

public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";

    private static RequestManager sRequestManager;
    private static RequestQueue sRequestQueue;

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
        mLoginButton.setOnClickListener(new onLoginClickListener());

        //Set up Volley request framework
        sRequestQueue = Volley.newRequestQueue(this);
        sRequestManager = new RequestManager(TAG, sRequestQueue);
    }

    /**
     * OnClickListener for the Login button
     */
    private class onLoginClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String email = mEmailText.getText().toString();
            String password = mPasswordText.getText().toString();
            sRequestManager.requestLogin(email, password,);
        }
    }

    private class loginResponseListener implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject jsonObject) {

        }
    }

    private class loginErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError volleyError) {

        }
    }

}

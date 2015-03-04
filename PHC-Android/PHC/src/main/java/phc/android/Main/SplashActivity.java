package phc.android.Main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import phc.android.R;

public class SplashActivity extends Activity {

    // Show splash screen for 2 seconds
    private static final int SPLASH_TIME_OUT = 2000;
    // Key for user shared prefenreces
    private static final String USER_PREFS_NAME = "UserKey";
    // Shared Preferences
    SharedPreferences mUserPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mUserPreferences = getSharedPreferences(USER_PREFS_NAME,
                        Context.MODE_PRIVATE);
                String userId = mUserPreferences.getString("user_id", null);
                String auth_token = mUserPreferences.getString("auth_token", null);

                if (userId != null && auth_token != null) {
                    // Go to MainActivity, skip LoginActivity
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);

                    // Close this activity
                    finish();
                } else {
                    // Go to LoginActivity
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);

                    // Close this activity
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }
}

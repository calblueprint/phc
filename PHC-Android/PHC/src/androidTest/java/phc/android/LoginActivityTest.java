package phc.android;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

import phc.android.Main.LoginActivity;

/**
 * Created by Byronium on 8/11/15.
 * TODO: change when login page is replaced by a password prompt page
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private LoginActivity mLoginActivity;
    private EditText mEmailEditText, mPasswordEditText;
    private Button mLoginButton;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(true);

        mLoginActivity = getActivity();
        mEmailEditText = (EditText) mLoginActivity.findViewById(R.id.login_email_edittext);
        mPasswordEditText = (EditText) mLoginActivity.findViewById(R.id.login_password_edittext);
        mLoginButton = (Button) mLoginActivity.findViewById(R.id.login_button);
    }

    public void testPreconditions() {
        assertNotNull("mLoginActivity is null", mLoginActivity);
        assertNotNull("mEmailEditText is null", mEmailEditText);
        assertNotNull("mPasswordEditText is null", mPasswordEditText);
        assertNotNull("mLoginButton is null", mLoginButton);
    }

    public void testmLoginButton_noEmail() {
        final String email = "test@test.com";
        final String pass = "password";
    }

    public void testmLoginButton_noPass() {
        final String email = "test@test.com";
        final String pass = "password";
    }

    public void testmLoginButton_incorrectEmail() {
        final String email = "test@test.com";
        final String pass = "password";
    }

    public void testmLoginButton_incorrectPass() {
        final String email = "test@test.com";
        final String pass = "password";
    }

    public void testmLoginButton_correct() {
        final String email = "test@test.com";
        final String pass = "password";

    }

//    @MediumTest
//    public void testClickMeButton_clickButtonAndExpectInfoText() {
//        String expectedInfoText = mClickFunActivity.getString(R.string.info_text);
//        TouchUtils.clickView(this, mClickMeButton);
//        assertTrue(View.VISIBLE == mInfoTextView.getVisibility());
//        assertEquals(expectedInfoText, mInfoTextView.getText());
//    }
//
//    @MediumTest
//    public void testNextActivityWasLaunchedWithIntent() {
//        startActivity(mLaunchIntent, null, null);
//        final Button launchNextButton =
//                (Button) getActivity()
//                        .findViewById(R.id.launch_next_activity_button);
//        launchNextButton.performClick();
//
//        final Intent launchIntent = getStartedActivityIntent();
//        assertNotNull("Intent was null", launchIntent);
//        assertTrue(isFinishCalled());
//
//        final String payload =
//                launchIntent.getStringExtra(NextActivity.EXTRAS_PAYLOAD_KEY);
//        assertEquals("Payload is empty", LaunchActivity.STRING_PAYLOAD, payload);
//    }
}

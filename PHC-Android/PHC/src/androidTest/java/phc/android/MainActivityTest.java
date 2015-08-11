package phc.android;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

import junit.framework.TestCase;

import org.junit.Test;

import java.lang.Override;

import phc.android.Main.MainActivity;
import phc.android.R;

/**
 * Created by Byronium on 8/11/15.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mMainActivity;
    private Button mCheckinButton, mServicesButton, mCheckoutButton;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        //prevents UI elements from taking focus when programmatically pressed
        setActivityInitialTouchMode(true);

        mMainActivity = getActivity();
        mCheckinButton = (Button) mMainActivity.findViewById(R.id.button_checkin);
        mServicesButton = (Button) mMainActivity.findViewById(R.id.button_services);
        mCheckoutButton = (Button) mMainActivity.findViewById(R.id.button_checkout);
    }

    public void testPreconditions() {
        assertNotNull("mMainActivity is null", mMainActivity);
        assertNotNull("mCheckinButton is null", mCheckinButton);
        assertNotNull("mServicesButton is null", mServicesButton);
        assertNotNull("mCheckoutButton is null", mCheckoutButton);
    }

    public void testmCheckinButton_buttonPress() {

    }

}

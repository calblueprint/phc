package phc.android.Checkout;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

import phc.android.R;

/**
 * An activity for when a client exits an event and wants someone to follow up
 * on them.
 *
 * Created by howardchen on 12/1/14.
 */
public class CheckoutActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        //move this activity to a fragment
        //remove this fragment.
        transaction.add(R.id.checkout_activity_container, new CheckoutScannerFragment());
        transaction.commit();
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    protected void recordResult(String code) {
        //getServiceStatus(getKeyByValue(sOfferedServices, mServiceSelected), code);
    }
}

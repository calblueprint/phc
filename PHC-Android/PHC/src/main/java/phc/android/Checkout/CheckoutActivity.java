package phc.android.Checkout;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

import phc.android.R;
import phc.android.SharedFragments.ScannerFragment;

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
        ScannerFragment checkoutScannerFragment = new CheckoutScannerFragment();
        checkoutScannerFragment.setType(CheckoutScannerFragment.FlowType.CHECKOUT);
        transaction.add(R.id.checkout_activity_container, checkoutScannerFragment);
        transaction.commit();

        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

}

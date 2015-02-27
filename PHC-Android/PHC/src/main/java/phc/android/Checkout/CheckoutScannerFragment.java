package phc.android.Checkout;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import phc.android.R;
import phc.android.Services.ScannerFragment;


/**
 * User enters or scans code, leading to CheckoutFormFragment
 */
public class CheckoutScannerFragment extends ScannerFragment {
    /* Name for logs and fragment transaction code */
    public final static String TAG = "CheckoutScannerFragment";

    /**
     * Sets up the view for the user to confirm
     * the scanned code.
     */
    @Override
    protected void confirmScan(CharSequence scanResult, boolean manualInput) {
        /* Can use bundle passed in, or must create new? */
        Bundle args = new Bundle();
        args.putCharSequence("scan_result", scanResult);
        args.putBoolean("manual_input", manualInput);

        Fragment confFrag = new CheckoutFormFragment();
        confFrag.setArguments(args);
        displayNextFragment(confFrag, CheckoutFormFragment.TAG);
    }

    /**
     * Brings up another fragment when this fragment
     * is complete
     * @param nextFrag Fragment to display next
     * @param fragName String fragment names
     */
    @Override
    protected void displayNextFragment(Fragment nextFrag, String fragName) {
        FragmentTransaction transaction =
                (getActivity()).getFragmentManager().beginTransaction();
        transaction.replace(R.id.checkout_activity_container, nextFrag, fragName);
        transaction.addToBackStack(fragName);
        transaction.commit();
    }

    // Can remove when sidebar is added
    @Override
    protected void resumeHelper(){
        ;
    }


}

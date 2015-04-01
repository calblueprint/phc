package phc.android.Checkout;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import phc.android.Helpers.OnSubmitClickListener;
import phc.android.R;
import phc.android.SharedFragments.ScannerConfirmationFragment;
import phc.android.SharedFragments.SuccessFragment;

/**
 * Created by Byronium on 3/9/15.
 */
public class CheckoutScannerConfirmationFragment extends ScannerConfirmationFragment{

    /* Tag for logs and fragment code */
    public final static String TAG = "CHECKOUT_SCANNER_CONF";



    /**
     * Returns to scanner fragment and displays a
     * failure toast.
     */
    protected void retry() {
        showFailureToast();
        FragmentManager manager = getFragmentManager();
        manager.popBackStack(CheckoutScannerConfirmationFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }



    // Can remove when sidebar is added
    @Override
    protected void resumeHelper(){
        ;
    }

}

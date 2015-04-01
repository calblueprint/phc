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

    @Override
    protected View setupView(LayoutInflater inflater, ViewGroup container) {
        View view = super.setupView(inflater, container);
        mConfirmButton.setOnClickListener(new SubmitListener(getActivity()));
        //mPreferenceEditor = new PreferenceEditor(getActivity().getApplicationContext());
        return view;
    }

    /**
     * Used to confirm the scan result.
     * Uses OnSubmit
     */
    protected class SubmitListener extends OnSubmitClickListener implements View.OnClickListener {


        public SubmitListener(Context context) {
            super(context);
        }

        @Override
        public void onClick(View view) {
            //mPreferenceEditor.storeScanResult(mScanResult);
            //registerPerson();
            // clear previous information here
            super.onClick(view);

        }
    }






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

package phc.android.Checkin;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import phc.android.R;
import phc.android.SharedFragments.ScannerConfirmationFragment;
import phc.android.SharedFragments.ScannerFragment;

public class CheckinScannerFragment extends ScannerFragment {


    /* Tag for logs and fragment code */
    public final static String TAG = "CheckinScannerFragment";

    /**
     * Sets up the view for the user to confirm
     * the scanned code.
     */
    @Override
    protected void confirmScan(CharSequence scanResult, boolean manualInput) {
        Bundle args = new Bundle();
        args.putCharSequence("scan_result", scanResult);
        args.putBoolean("manual_input", manualInput);
        ScannerConfirmationFragment confFrag = new CheckinScannerConfirmationFragment();
        confFrag.setArguments(args);
        displayNextFragment(confFrag, CheckinScannerConfirmationFragment.TAG);
    }

    /**
     * Brings up another fragment when this fragment
     * is complete. Override to use
     * registration_fragment_container
     * @param nextFrag Fragment to display next
     * @param fragName String fragment name
     */
    @Override
    protected void displayNextFragment(Fragment nextFrag, String fragName) {
        FragmentTransaction transaction =
                getActivity().getFragmentManager().beginTransaction();
        transaction.replace(R.id.checkin_fragment_container, nextFrag, fragName);
        transaction.addToBackStack(fragName);
        transaction.commit();
    }

    /**
     * Override to use sidebar view and string ids
     */
    @Override
    protected void resumeHelper() {
        LinearLayout sidebarList = (LinearLayout) getActivity().findViewById(R.id.sidebar_list);
        for (int i = 0; i < sidebarList.getChildCount(); i++) {
            View v = sidebarList.getChildAt(i);
            Object vTag = v.getTag();
            if ((vTag != null) && (vTag.equals(getResources().getString(R.string.sidebar_scan_code)))) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.BOLD);
            } else if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.NORMAL);
            }
        }
    }

}

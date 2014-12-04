package phc.android;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class RegistrationScannerFragment extends ScannerFragment {

    /**
     * Sets up the view for the user to confirm
     * the scanned code.
     */
    protected void confirmScan(CharSequence scanResult) {
        Bundle args = new Bundle();
        args.putCharSequence("scan_result", scanResult);
        ScannerConfirmationFragment confFrag = new RegistrationScannerConfirmationFragment();
        confFrag.setArguments(args);
        displayNextFragment(confFrag, RegistrationScannerConfirmationFragment.TAG);
    }

    /**
     * Separate method for setting up view so that this
     * functionality can be overriden by a subclass.
     * @param view is passed in by onCreateView()
     */
    @Override
    protected View setupView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_registration_scanner, container, false);

        mScanButton = (Button) view.findViewById(R.id.start_scan);
        mScanButton.setOnClickListener(new ScanListener());
        return view;
    }



    @Override
    protected void displayNextFragment(Fragment nextFrag, String fragName) {
        FragmentTransaction transaction =
                getActivity().getFragmentManager().beginTransaction();
        transaction.replace(R.id.registration_fragment_container, nextFrag, fragName);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Called when activity is re opened.
     * Camera must be acquired again, and
     * the preview's camera handle should
     * be updated as well.
     */
    @Override
    public void onResume() {
        super.onResume();
    }

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

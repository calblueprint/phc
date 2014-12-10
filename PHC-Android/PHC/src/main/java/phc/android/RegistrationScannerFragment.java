package phc.android;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RegistrationScannerFragment extends ScannerFragment {


    /* Tag for logs and fragment code */
    public final static String TAG = "RegistrationScannerFragment";

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
        transaction.replace(R.id.registration_fragment_container, nextFrag, fragName);
        transaction.addToBackStack(null);
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

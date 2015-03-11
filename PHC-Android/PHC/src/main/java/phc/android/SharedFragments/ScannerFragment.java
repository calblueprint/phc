package phc.android.SharedFragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import phc.android.Checkin.CheckinScannerConfirmationFragment;
import phc.android.Main.MainActivity;
import phc.android.R;
import phc.android.Services.ServicesScannerConfirmationFragment;


public class ScannerFragment extends Fragment {

    public final static String TAG = "SCANNER_FRAGMENT";

    /* Different flows this scanner fragment can be in. */
    public static enum FlowType { CHECKIN, SERVICES, CHECKOUT };
    /* The flow this scanner fragment is in. */
    private FlowType mCurrentFlowType;
    /* The correct ScannerConfirmationFragment to load next (depends on flow type). */
    private ScannerConfirmationFragment mConfFrag;
    /* The tag of the the ScannerConfirmationFragment to load next. */
    private String mConfFragTag;
    /* The correct fragment container to load the next fragment into. */
    private int mFragContainer;
    /* The correct sidebar to use (depends on flow type). */
    private int mSidebarId;

    /* Button to start BarcodeScanner app. */
    protected Button mScanButton;
    /* Field and submit button for manual code input. */
    protected EditText mCodeInput;
    protected Button mCodeInputSubmitButton;

    /* Toast that tells the user when input is invalid.
     * An array is used to simulate a pointer and pass by reference instead of by value. */
    protected Toast[] mInvalidInputToast = { null };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and set up view
        View view = setupView(inflater, container);
        mCodeInputSubmitButton = (Button) view.findViewById(R.id.submit_input);
        mCodeInputSubmitButton.setOnClickListener(new InputSubmitListener());

        return view;
    }

    /**
     * Called from each Activity to specify which sidebar to use
     * and which fragment to load next.
     */
    public void setType(FlowType type) {
        mCurrentFlowType = type;

        switch (mCurrentFlowType){
            case CHECKIN:
                mConfFrag = new CheckinScannerConfirmationFragment();
                mConfFragTag = ((CheckinScannerConfirmationFragment)mConfFrag).TAG;
                mFragContainer = R.id.checkin_fragment_container;
                mSidebarId = R.id.checkin_sidebar_list;
                break;
            case SERVICES:
                mConfFrag = new ServicesScannerConfirmationFragment();
                mConfFragTag = ((ServicesScannerConfirmationFragment)mConfFrag).TAG;
                mFragContainer = R.id.service_fragment_container;
                mSidebarId = R.id.services_sidebar_list;
                break;
//            case CHECKOUT: //TODO: ADD ONCE CHECKOUT IS IMPLEMENTED
//                mConfFrag = new CheckoutScannerConfirmationFragment();
//                mFragContainer = R.id.checkout_fragment_container;
//                break;
        }
    }

    /**
     * Separate method for setting up view so that this
     * functionality can be overriden by a subclass.
     * @param inflater LayoutInflater passed in from onCreateView()
     * @param container ViewGroup passed in from onCreateView()
     */
    protected View setupView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);

        mScanButton = (Button) view.findViewById(R.id.start_scan);
        mScanButton.setOnClickListener(new ScanListener());

        setupButtons(view);

        return view;
    }

    protected void setupButtons(View view) {
        mCodeInput = (EditText) view.findViewById(R.id.code_input);
        mCodeInput.addTextChangedListener(new InputTextWatcher());

        mCodeInputSubmitButton = (Button) view.findViewById(R.id.submit_input);
        mCodeInputSubmitButton.setOnClickListener(new InputSubmitListener());

        setInputSubmitButton();
    }

    /**
     * This class is used by the input submit button to
     * pass the correct arguments to the next fragment,
     * as well as validate the input.
     */
    public class InputSubmitListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            CharSequence result = mCodeInput.getText();
            if (isValidInput(mCodeInput.getText())) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mCodeInput.getWindowToken(), 0);
                CharSequence trimmedResult = (result.toString()).trim();
                confirmScan(trimmedResult, true);
            } else {
                displayInvalidInputToast();
            }
        }
    }

    /**
     * Used to watch for input and update the submit button
     * when the user enters or removes text.
     */
    public class InputTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            setInputSubmitButton();
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            setInputSubmitButton();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            setInputSubmitButton();
        }
    }

    /**
     * Updates the input submit button state and
     * changes the text color depending on whether
     * the user has entered text or not.
     */
    protected void setInputSubmitButton() {
        if (inputBoxEmpty()) {
            mCodeInputSubmitButton.setEnabled(false);
            mCodeInputSubmitButton.setTextColor(Color.GRAY);
            mCodeInputSubmitButton.setBackgroundResource(R.drawable.disabled_button);
        } else {
            mCodeInputSubmitButton.setEnabled(true);
            mCodeInputSubmitButton.setTextColor(getResources().getColor(R.color.green));
            mCodeInputSubmitButton.setBackgroundResource(R.drawable.submit_button);

        }
    }

    /**
     * Validates mCodeInput to make sure
     * at least a single character is present.
     * Does not validate for content.
     * @return True if mCodeInput is empty,
     * False otherwise
     */
    protected boolean inputBoxEmpty() {
        String text = mCodeInput.getText().toString();
        if (text.trim().equals("")) {
            return true;
        }
        return false;
    }

    /**
     * Used when the user wants to send an intent
     * to the scanner app.
     */
    public class ScanListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            startScan();
        }
    }

    /**
     * Validates EditText input to make sure only
     * numbers and/or spaces are present.
     * Does not validate for empty input. Spaces
     * may be removed later, but this method
     * does not remove them.
     * @param input CharSequence input to validate
     * @return True if valid input, False otherwise
     */
    protected boolean isValidInput(CharSequence input) {
        /** Will not remove whitespace between digits! **/
        String inputStr = input.toString().trim();
        try {
            Integer.parseInt(inputStr);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    protected void displayInvalidInputToast() {
        MainActivity.maybeShowToast(getString(R.string.toast_invalid_input),
                mInvalidInputToast, Toast.LENGTH_SHORT, getActivity());
    }

    /**
     * Displays a failure message at the bottom
     * of the screen.
     */
    protected void showFailureToast() {
        Context c = getActivity().getApplicationContext();
        CharSequence message = getResources().getString(R.string.toast_scan_failure);
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(c, message, duration);
        toast.show();
    }

    /**
     * Starts the BarcodeScanner app.
     */
    protected void startScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    /**
     * Used to retrieve the result from the
     * BarcodeScanner app.
     * @param reqCode int request code
     * @param resCode int result code
     * @param data Intent containing the result data
     */
    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(reqCode, resCode, data);
        CharSequence scanResult = result.getContents();
        if (scanResult == null) {
            showFailureToast();
        } else {
            confirmScan(scanResult, false);
        }
    }

    /**
     * Sets up the view for the user to confirm the scanned code.
     * @param scanResult QR code
     * @param manualInput false if user used scanner, true if user entered manually
     */
    protected void confirmScan(CharSequence scanResult, boolean manualInput) {
        Bundle args = new Bundle();
        args.putCharSequence("scan_result", scanResult);
        args.putBoolean("manual_input", manualInput);
        mConfFrag.setArguments(args);

        FragmentTransaction transaction =
                getActivity().getFragmentManager().beginTransaction();
        transaction.replace(mFragContainer, mConfFrag, mConfFragTag);
        transaction.addToBackStack(mConfFragTag);
        transaction.commit();
    }

    @Override
    public void onResume() {
        resumeHelper();
        super.onResume();
    }

    /**
     * Used so that a subclass can implement their own
     * resumeHelper() so method calls to onResume() will
     * execute this class's super.onResume()
     */
    protected void resumeHelper() {
        Log.d("Sidebar Resumed", "RESUMED");
        LinearLayout sidebarList = (LinearLayout) getActivity().findViewById(mSidebarId);
        for (int i = 0; i < sidebarList.getChildCount(); i++) {
            View v = sidebarList.getChildAt(i);
            Object vTag = v.getTag();
            if ((vTag != null) && (vTag.equals(getResources().getString(R.string.sidebar_scan_code))
            )) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.BOLD);
            } else if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.NORMAL);
            }
        }
    }
}
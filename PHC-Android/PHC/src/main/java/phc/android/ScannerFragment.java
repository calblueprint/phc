package phc.android;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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


public class ScannerFragment extends Fragment {

    public final static String TAG = "ScannerFragment";

    /** Button to start BarcodeScanner app. **/
    protected Button mScanButton;

    /** Field and submit button for manual code input. **/
    protected EditText mCodeInput;
    protected Button mCodeInputSubmitButton;

    /** Toast that tells the user when input is
     *  invalid. An array is used to simulate a
     *  pointer and pass by reference instead
     *  of by value.
     */
    protected Toast[] mInvalidInputToast = { null };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Inflate the layout for this fragment and set up view*/
        View view = setupView(inflater, container);
        return view;
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
    protected class InputSubmitListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            CharSequence result = mCodeInput.getText();
            if (isValidInput(mCodeInput.getText())) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mCodeInput.getWindowToken(), 0);
                confirmScan(result, true);
            } else {
                displayInvalidInputToast();
            }
        }
    }

    /**
     * Used to watch for input and update the submit button
     * when the user enters or removes text.
     */
    protected class InputTextWatcher implements TextWatcher {
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
        } else {
            mCodeInputSubmitButton.setEnabled(true);
            mCodeInputSubmitButton.setTextColor(getResources().getColor(R.color.button_text_color));

        }
    }

    /**
     * Used when the user wants to send an intent
     * to the scanner app.
     */
    protected class ScanListener implements View.OnClickListener{
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

    protected void displayInvalidInputToast() {
        MainActivity.maybeShowToast(getString(R.string.invalid_input_toast),
                mInvalidInputToast, Toast.LENGTH_SHORT, getActivity());
    }

    /**
     * Displays a failure message at the bottom
     * of the screen.
     */
    protected void showFailureToast() {
        Context c = getActivity().getApplicationContext();
        CharSequence message = getResources().getString(R.string.scan_failure);
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
     * Sets up the view for the user to confirm
     * the scanned code.
     */
    protected void confirmScan(CharSequence scanResult, boolean manualInput) {
        Bundle args = new Bundle();
        args.putCharSequence("scan_result", scanResult);
        args.putBoolean("manual_input", manualInput);
        ScannerConfirmationFragment confFrag = new ScannerConfirmationFragment();
        confFrag.setArguments(args);
        displayNextFragment(confFrag, ScannerConfirmationFragment.TAG);
    }

    /**
     * Brings up another fragment when this fragment
     * is complete
     * @param nextFrag Fragment to display next
     * @param fragName String fragment name
     */
    protected void displayNextFragment(Fragment nextFrag, String fragName) {
        FragmentTransaction transaction =
                getActivity().getFragmentManager().beginTransaction();
        transaction.replace(R.id.service_fragment_container, nextFrag, fragName);
        transaction.addToBackStack(fragName);
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
        LinearLayout sidebarList = (LinearLayout) getActivity().findViewById(R.id.services_sidebar_list);
        for (int i = 0; i < sidebarList.getChildCount(); i++) {
            View v = sidebarList.getChildAt(i);
            Object vTag = v.getTag();
            if ((vTag != null) && (vTag.equals(getResources().getString(R.string.sidebar_scan)))) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.BOLD);
            } else if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.NORMAL);
            }
        }
    }
}


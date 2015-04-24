package phc.android.Checkout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import phc.android.Helpers.OnSubmitClickListener;
import phc.android.Helpers.Utils;
import phc.android.R;
import phc.android.Services.ServicesActivity;
import phc.android.SharedFragments.ScannerConfirmationFragment;
import phc.android.SharedFragments.SuccessFragment;

/**
 * Created by Byronium on 3/9/15.
 */
public class CheckoutScannerConfirmationFragment extends ScannerConfirmationFragment{

    /* Tag for logs and fragment code */
    public final static String TAG = "CHECKOUT_SCANNER_CONF";

    /* Holds the response from requestSearchByCode (true if qr code matches). */
    private boolean mRegistrationFound = false;

    private JSONArray serviceArray;

    /* Holds the status boolean from requestGetApplied (true qr code maps to existing person) */

    @Override
    protected void confirm(){

        Log.d ("looking up code: ", mScanResult);
        sRequestManager.requestGetApplied(mScanResult,
                mUserId,
                mAuthToken,
                new SearchByCodeResponseListener(),
                new SearchByCodeErrorListener());
    }

    /**
     * Brings up another fragment when this fragment
     * is complete
     * @param nextFrag Fragment to display next
     * @param fragName String fragment names
     */
    protected void displayNextFragment(Fragment nextFrag, String fragName) {
        FragmentTransaction transaction =
                (getActivity()).getFragmentManager().beginTransaction();
        transaction.replace(R.id.checkout_activity_container, nextFrag, fragName);
        transaction.addToBackStack(fragName);
        transaction.commit();
    }

    /**
     * Used to confirm the scan result
     * Uses OnSubmit
     */
    // Why can't this be protected? wat
    // this code is exactly like servicesscannerconfirmationfragmnent

    class SearchByCodeResponseListener implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject jsonObject) {
            try {
                mRegistrationFound = jsonObject.getBoolean("status");
                Log.d("found?", Boolean.toString(mRegistrationFound));
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing JSON");
                Log.e(TAG, e.toString());
            }

            if (mRegistrationFound) {
                // Get services here
                Log.d("found the user","");
                try {
                    serviceArray = jsonObject.getJSONArray("services");

                    Bundle args = new Bundle();

                    // Convert JSONArray to ArrayList
                    ArrayList<String> mServices = new ArrayList<String>();
                    if (serviceArray != null){
                        for (int i=0; i < serviceArray.length(); i++){
                            try {
                                mServices.add(serviceArray.get(i).toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    args.putStringArrayList("services",mServices);
                    args.putCharSequence("scan_result", mScanResult);
                    args.putBoolean("manual_input", mManualInput);

                    CheckoutFormFragment formFrag = new CheckoutFormFragment();
                    formFrag.setArguments(args);
                    displayNextFragment(formFrag, CheckoutConfirmationFragment.TAG);
                } catch (JSONException e) {
                    Log.d(TAG,"error getting services");
                    e.printStackTrace();
                }

            } else{
                notFoundDialogue();
            }

        }
    }

    private class SearchByCodeErrorListener implements Response.ErrorListener{

            @Override
            public void onErrorResponse(VolleyError volleyError){
                if (volleyError.getLocalizedMessage() != null){
                    Log.e(TAG, volleyError.toString());
                }
                Toast toast = Toast.makeText(getActivity(), "Error looking up the QR Code",
                        Toast.LENGTH_SHORT);
                toast.show();
        }
    }
    /**
     * Creates an alert dialogue to tell the user that the qr code is not found,
     * along with a button to try another.
     */
    public void notFoundDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                retry();
            }
        });
        builder.setTitle("Code not recognized");
        builder.setMessage("Either entered incorrectly OR person has not checked in");

        AlertDialog dialog = builder.create();
        dialog.show();

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

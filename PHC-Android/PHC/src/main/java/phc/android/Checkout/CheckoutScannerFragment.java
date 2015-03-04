package phc.android.Checkout;


import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.salesforce.androidsdk.util.JSTestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import phc.android.Checkin.SearchResultsFragment;
import phc.android.Networking.RequestManager;
import phc.android.R;
import phc.android.SharedFragments.ScannerFragment;


/**
 * User enters or scans code, leading to CheckoutFormFragment
 */
public class CheckoutScannerFragment extends ScannerFragment {
    /* Name for logs and fragment transaction code */
    public final static String TAG = "CheckoutScannerFragment";
    private static final String USER_PREFS_NAME = "UserKey";
    private static RequestManager sRequestManager;
    private static RequestQueue sRequestQueue;
    // Shared Preferences needs getActivity?
    private SharedPreferences mUserPreferences;
    // SharedPreference editor object
    private ArrayList<String> mServicesNotReceived = new ArrayList<String>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Inflate the layout for this fragment and set up view*/
        View view = setupView(inflater, container);
        mCodeInputSubmitButton = (Button) view.findViewById(R.id.submit_input);
        mCodeInputSubmitButton.setOnClickListener(new InputSubmitListener());

        // Set up Volley request framework
        sRequestQueue = Volley.newRequestQueue(this.getActivity());
        sRequestManager = new RequestManager(TAG, sRequestQueue);

        // Needs to create requestmanager for the scanner button too

        return view;
    }

// Dont need for service population. Only need confirm scan?
    /**
     * This class is used by the input submit button to pass in the
     * correct arguments and the queried services from the given code to the
     * next fragment, as well as validate the input
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
     * Sets up the view for the user to confirm
     * the scanned code.
     */
    @Override
    protected void confirmScan(CharSequence scanResult, boolean manualInput) {
        /* Can use bundle passed in, or must create new? */
        mUserPreferences = getActivity().getSharedPreferences(USER_PREFS_NAME,
                Context.MODE_PRIVATE);
        String userId = mUserPreferences.getString("user_id", null);
        String authToken = mUserPreferences.getString("auth_token", null);


        sRequestManager.requestSearchByCode(scanResult.toString(),
                authToken,
                userId,
                new LoginResponseListener(),
                new LoginErrorListener());


        Bundle args = new Bundle();
        args.putCharSequence("scan_result", scanResult);
        args.putBoolean("manual_input", manualInput);
        args.putStringArrayList("services_not_received", mServicesNotReceived);

        Fragment confFrag = new CheckoutFormFragment();
        confFrag.setArguments(args);
        displayNextFragment(confFrag, CheckoutFormFragment.TAG);
    }



    private class LoginResponseListener implements Response.Listener<JSONObject> {


        @Override
        public void onResponse(JSONObject jsonObject) {
            try{
                // find elements in services applied not in checked in services
                JSONArray appliedServices = jsonObject.getJSONArray("services_applied");
                JSONArray checkedInServices = jsonObject.getJSONArray("checked_in_services");

                for(int i = 0; i < appliedServices.length(); i++){
                    boolean found = false;
                    for(int j = 0; j < checkedInServices.length(); j++){
                        if (appliedServices.get(i).equals(checkedInServices.get(j))){
                            found = true;
                        }
                    }
                    // after looking through the list of checked in services, if no duplicate values found
                    // put that applied service into servicesNotReceived
                    if(found == false){
                        // the strings in the array are "jsonobjects".... is this right?
                        mServicesNotReceived.add(appliedServices.get(i).toString());
                    }
                }

                // Convert String ArrayList to JSONArray
                JSONArray servicesNotReceivedJSONArray = new JSONArray(mServicesNotReceived);
                // Put list of services that were not received into the jsonObject
                jsonObject.put("services_not_received", servicesNotReceivedJSONArray);
                // ???????????????????????????????????/


            } catch (JSONException e){
                Log.e(TAG, "Error parsing JSON");
                Log.e(TAG, e.toString());
            }

        }
    }





    private class LoginErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            if (volleyError.getLocalizedMessage() != null) {
                Log.e(TAG, volleyError.toString());
            }

            Toast toast = Toast.makeText(getActivity(), "Error during submission", Toast.LENGTH_SHORT);
            toast.show();
        }
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

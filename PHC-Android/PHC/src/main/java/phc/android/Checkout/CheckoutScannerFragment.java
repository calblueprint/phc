package phc.android.Checkout;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import phc.android.SharedFragments.ScannerFragment;


/**
 * User enters or scans code, leading to CheckoutFormFragment
 */
public class CheckoutScannerFragment extends ScannerFragment {
    /* Name for logs and fragment transaction code */
    public final static String TAG = "CheckoutScannerFragment";
    private ArrayList<String> mServicesNotReceived = new ArrayList<String>();

    /**
     * Sets up the view for the user to confirm
     * the scanned code.
     */
    @Override
    protected void confirmScan(CharSequence scanResult, boolean manualInput) {
//        mUserPreferences = getActivity().getSharedPreferences(USER_PREFS_NAME,
//                Context.MODE_PRIVATE);
//        String userId = mUserPreferences.getString("user_id", null);
//        String authToken = mUserPreferences.getString("auth_token", null);

//        sRequestManager.requestSearchByCode(scanResult.toString(),
//                authToken,
//                userId,
//                new LoginResponseListener(),
//                new LoginErrorListener());

        Bundle args = new Bundle();
        args.putCharSequence("scan_result", scanResult);
        args.putBoolean("manual_input", manualInput);
//        args.putStringArrayList("services_not_received", mServicesNotReceived);

        Fragment confFrag = new CheckoutFormFragment();
        confFrag.setArguments(args);
//        displayNextFragment(confFrag, CheckoutFormFragment.TAG);
    }

    private class LoginResponseListener implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject jsonObject) {
            try {
                // find services with "applied" value
                // JSONArray checkedInServices = jsonObject.getJSONArray("checked_in_services");
                Iterator<String> keys = jsonObject.keys();

                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    if (jsonObject.get(key).toString().equals("Applied")) { // if found "applied", save the key to mServicesNotReceived
                        mServicesNotReceived.add(key.toString());
                    }
                }
            }catch(JSONException e){
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

    // Can remove when sidebar is added
    @Override
    protected void resumeHelper(){
        ;
    }

}

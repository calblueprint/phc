package phc.android.Checkout;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import phc.android.Helpers.Utils;
import phc.android.Networking.RequestManager;
import phc.android.R;
import phc.android.SharedFragments.SuccessFragment;

public class CheckoutFormFragment extends Fragment {
    /* Name for logs and fragment transaction code */
    public final static String TAG = "CheckoutFormFragment";

    /* Shared Preferences */
    private static final String USER_AUTH_PREFS_NAME = "UserKey";
    private SharedPreferences mUserPreferences;

    /* Network requests */
    private static RequestManager sRequestManager;
    private static RequestQueue sRequestQueue;

    /* Scan results */
    protected CharSequence mScanResult;
    protected Boolean mManualInput;

    /* Experience rating 0-5 */
    private RadioGroup mExperienceRadioGroup;
    private int mExperience;
    
    /* Services applied but not received */
    private ViewGroup mLayout;
    private ArrayList<CheckBox> mCheckBoxArray;
    private ArrayList<String> mServices;
    private JSONArray mCheckedServices;

    /* Comments */
    private EditText mCommentView;
    private String mComment = null;
    
    private Button mSubmitButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_checkout_form, container, false);

        if (savedInstanceState != null) {
            mScanResult = savedInstanceState.getCharSequence("scan_result");
            mManualInput = savedInstanceState.getBoolean("manual_input");
            mServices = savedInstanceState.getStringArrayList("services");
        } else {
            mScanResult =  getArguments().getCharSequence("scan_result");
            mManualInput = getArguments().getBoolean("manual_input");
            mServices = getArguments().getStringArrayList("services");
        }

        setServices(view);
        setExperience(view);
        mCommentView = (EditText) view.findViewById(R.id.checkout_comment);
        mSubmitButton = (Button) view.findViewById(R.id.button_submit);
        mSubmitButton.setOnClickListener(new SubmitListener());

        sRequestQueue = Volley.newRequestQueue(getActivity());
        sRequestManager = new RequestManager(TAG, sRequestQueue);

        return view;
    }

    /**
     * Dynamically populates layout with checkboxes for each applied but not received service. 
     * Note: the current ID assignment method is rather hacky, but it's the only way that seems to
     * work. Android is not able to save and load dynamically created views even when their ID is
     * set, unless the views are created again with the same previous ID.
     */
    private void setServices(View view){
        mLayout = (LinearLayout) view.findViewById(R.id.services_list);
        mCheckBoxArray = new ArrayList<CheckBox>();

        for(int i = 0; i < mServices.size(); i++){
            CheckBox cb = new CheckBox(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            cb.setLayoutParams(params);
            cb.setId(i);
            cb.setText(Utils.fieldNameHelper(mServices.get(i)));
            mLayout.addView(cb);

            mCheckBoxArray.add(cb);
        }
    }

    private void setExperience(View view){
        mExperienceRadioGroup = (RadioGroup) view.findViewById(R.id.checkout_radiogroup);
        mExperienceRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId){
                //Resource ids are not constants -> Can't use switch statements
                if (checkedId==R.id.checkout_radio_0){
                    mExperience = 0;
                }
                else if (checkedId==R.id.checkout_radio_1){
                    mExperience = 1;
                }
                else if (checkedId==R.id.checkout_radio_2){
                    mExperience = 2;
                }
                else if (checkedId==R.id.checkout_radio_3){
                    mExperience = 3;
                }
                else if (checkedId==R.id.checkout_radio_4){
                    mExperience = 4;
                }
                else if (checkedId==R.id.checkout_radio_5){
                    mExperience = 5;
                }
            }
        });
    }

    private void getComment(){
        if (mCommentView != null && mCommentView.getText() != null) {
            mComment = mCommentView.getText().toString();
        }
    }

    private void getServices(){
        mCheckedServices = new JSONArray();
        for (int i = 0 ;  i < mCheckBoxArray.size(); i++) {
            CheckBox checkBox = mCheckBoxArray.get(i);
            if (checkBox.isChecked()) {
                mCheckedServices.put(mServices.get(i));
            }
        }
    }

    protected class SubmitListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            mUserPreferences = getActivity().getSharedPreferences(USER_AUTH_PREFS_NAME,
                    Context.MODE_PRIVATE);
            String userId = mUserPreferences.getString("user_id", null);
            String authToken = mUserPreferences.getString("auth_token", null);

            getComment();
            getServices();

            sRequestManager.requestUpdateFeedback(
                    mComment,
                    mExperience,
                    mCheckedServices,
                    mScanResult.toString(),
                    userId,
                    authToken,
                    new UpdateResponseListener(),
                    new UpdateErrorListener());
        }
    }

    private class UpdateResponseListener implements Response.Listener<JSONObject>{
        @Override
        public void onResponse(JSONObject jsonObject){
            FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            SuccessFragment successFragment = new SuccessFragment();
            successFragment.setType(SuccessFragment.SuccessType.CHECKOUT_SUCCESS);
            transaction.replace(R.id.checkout_activity_container, successFragment);
            transaction.commit();
        }
    }
    private class UpdateErrorListener implements Response.ErrorListener{
        @Override
        public void onErrorResponse(VolleyError volleyError){
            if (volleyError != null && volleyError.getLocalizedMessage()!=null) {
                volleyError.printStackTrace();
            }
            Toast toast = Toast.makeText(getActivity(), "Error checking out user", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}

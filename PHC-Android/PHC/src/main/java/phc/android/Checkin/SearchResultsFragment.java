package phc.android.Checkin;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.salesforce.androidsdk.rest.RestClient.AsyncRequestCallback;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phc.android.Main.MainActivity;
import phc.android.Networking.RequestManager;
import phc.android.R;
import phc.android.Helpers.SearchResult;

/**
 * SearchFragment is launched on successful submission of a client's form data,
 * and allows the user to go back to activity_checkin another client.
 */
public class SearchResultsFragment extends Fragment implements ListView.OnItemClickListener {
    private static RequestQueue requestQueue;
    private static final String TAG = "Search";
    private static final String SEARCH_PATH = "/api/v1/search";
    private static final String AUTH_TOKEN = "phcplusplus";
    public static final String SEARCH_RESULT = "SEARCH_RESULT";
    public static final String CACHED_RESULTS = "CACHED_RESULTS";
    // Key for user shared preferences
    private static final String USER_AUTH_PREFS_NAME = "UserKey";

    private static RequestManager sRequestManager;
    private static RequestQueue sRequestQueue;

    // Shared Preferences
    private SharedPreferences mUserPreferences;
    private ProgressDialog mProgressDialog;
    // Parent Activity *
    private CheckinActivity mParent;
    // Caching the search results
    private SearchResult[] mSearchResults = new SearchResult[0];
    // ListView for results and its adapter *
    private ListView mListView;
    // TextView holding the "No Results Found" message.
    private TextView mTextView;

    private SearchResultAdapter mAdapter;
    // Button to try search again.
    private Button mSearchAgainButton;
    // Button to register client as a new user.
    private Button mRegisterAsNewButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);
        mListView = (ListView) view.findViewById(R.id.search_result_list);
        mTextView = (TextView) view.findViewById(R.id.search_text_no_results);

        setupButtons(view);

        if (savedInstanceState != null) {
            mSearchResults = (SearchResult[]) savedInstanceState.get(CACHED_RESULTS);
            if (mSearchResults.length == 0) {
                setNoResultsMessage();
            }
            mAdapter = new SearchResultAdapter(getActivity(), mSearchResults);
            mListView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        } else {
            // Initialize search results to be empty
            mSearchResults = new SearchResult[0];
        }

        // Create a new progress dialog if no cached search results
        if (mSearchResults.length == 0) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setTitle("Search Results");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setCancelable(true);
            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    requestQueue.cancelAll(TAG);
                    dialog.dismiss();
                }
            });
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        //Set up Volley request framework
        sRequestQueue = Volley.newRequestQueue(getActivity());
        sRequestManager = new RequestManager(TAG, sRequestQueue);

        return view;
    }


    public void setupButtons(View view) {
        mSearchAgainButton = (Button) view.findViewById(R.id.button_search_again);
        mSearchAgainButton.setOnClickListener(new View.OnClickListener() {
            //returns to search fragment
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        mRegisterAsNewButton = (Button) view.findViewById(R.id.button_register_as_new);
        mRegisterAsNewButton.setOnClickListener(new View.OnClickListener() {
            //opens personal info fragment
            public void onClick(View v) {
                PersonalInfoFragment newFragment = new PersonalInfoFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.checkin_fragment_container, newFragment,
                        getResources().getString(R.string.sidebar_personal_info));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    /**
     * Sets the TextView's text to "No Results Found"
     */
    private void setNoResultsMessage() {
        mTextView.setText(R.string.search_no_results);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        requestQueue = Volley.newRequestQueue(getActivity());
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save the cached results
        outState.putParcelableArray(CACHED_RESULTS, mSearchResults);

        // Remove the progress dialog on orientation change
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        mParent = (CheckinActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        // Get the search parameters
        Bundle args = getArguments();
        String firstName = args.getString("firstName");
        String lastName = args.getString("lastName");

        // Get userId and authToken
        mUserPreferences = getActivity().getSharedPreferences(USER_AUTH_PREFS_NAME,
                Context.MODE_PRIVATE);
        final String userId = mUserPreferences.getString("user_id", null);
        final String authToken = mUserPreferences.getString("auth_token", null);

        //If there are search parameters,
        if (firstName != null && lastName != null && userId != null && authToken != null) {
            mListView.setOnItemClickListener(this);
            sRequestManager.requestSearch(firstName,
                    lastName,
                    userId,
                    authToken,
                    new SearchResultResponseListener(),
                    new SearchResultErrorListener());
        }
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
        // Get userId and authToken
        mUserPreferences = getActivity().getSharedPreferences(USER_AUTH_PREFS_NAME,
                Context.MODE_PRIVATE);
        final String userId = mUserPreferences.getString("user_id", null);
        final String authToken = mUserPreferences.getString("auth_token", null);

        SearchResult searchResult = (SearchResult) parent.getItemAtPosition(position);
        String sfID = searchResult.getSalesForceId();
        sRequestManager.requestUserInfo(
                sfID,
                userId,
                authToken,
                new UserInfoResponseListener(),
                new UserInfoErrorListener());
    }

    class SearchResultAdapter extends ArrayAdapter<SearchResult> {

        private SearchResult[] data;
        private Context context;
        /**
         * A Simple Date Formatter to make date strings more readable.
         */
        private SimpleDateFormat sdf;

        public SearchResultAdapter(Context context, SearchResult[] data) {
            super(context, R.layout.row_search_result, data);
            sdf = new SimpleDateFormat("MMM d, ''yy");
            this.data = data;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View rowView;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.row_search_result, parent, false);
            } else {
                rowView = convertView;
            }

            SearchResult searchResult = data[position];

            ((TextView) rowView.findViewById(R.id.first_name)).setText(searchResult.getFirstName());
            ((TextView) rowView.findViewById(R.id.last_name)).setText(searchResult.getLastName());

            if (searchResult.getBirthday() != null) {
                String birthday = sdf.format(searchResult.getBirthday());
                ((TextView) rowView.findViewById(R.id.birthday)).setText(birthday);
            } else {
                ((TextView) rowView.findViewById(R.id.birthday)).setText("None");
            }
            return rowView;
        }
    }

    /**
     * Response Listener for initiating a search request
     */
    private class SearchResultResponseListener implements Response.Listener<JSONArray> {

        @Override
        public void onResponse(JSONArray jsonArray) {
            try {
                final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                mSearchResults = new SearchResult[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    SearchResult result = new SearchResult();
                    result.setFirstName(json.getString("FirstName"));
                    result.setLastName(json.getString("LastName"));

                    if (!json.getString("Birthdate__c").equals("")) {
                        result.setBirthday(df.parse(json.getString("Birthdate__c")));
                    }

                    result.setSalesForceId(json.getString("sf_id"));
                    mSearchResults[i] = result;
                }

                // if there are no results, show the no results message
                if (mSearchResults.length == 0) {
                    setNoResultsMessage();
                }
                // otherwise, populate the list view with the results
                else {
                    mAdapter = new SearchResultAdapter(mParent, mSearchResults);
                    mListView.setAdapter(mAdapter);
                }
                // Check if progress dialog is showing before dismissing
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing JSON");
                Log.e(TAG, e.toString());
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing Birthday");
                Log.e(TAG, e.toString());
            }
        }
    }

    /**
     * Error listener for initiating a search request
     */
    private class SearchResultErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            if (volleyError.getLocalizedMessage() != null) {
                Log.e(TAG, volleyError.toString());
            }

            // Check if progress dialog is showing before dismissing
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            Toast toast = Toast.makeText(getActivity(), "Error during Search", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * Response Listener for user info request
     */
    private class UserInfoResponseListener implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject jsonObject) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SEARCH_RESULT, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            try {
                editor.putString("SS_Num", jsonObject.getString("SS_Num__c"));
                editor.putString("FirstName", jsonObject.getString("FirstName"));
                editor.putString("LastName", jsonObject.getString("LastName"));
                editor.putString("Phone", jsonObject.getString("Phone"));
                editor.putString("Birthdate", jsonObject.getString("Birthdate__c"));
                editor.putString("Email", jsonObject.getString("PersonEmail"));
                editor.putString("Gender", jsonObject.getString("Gender__c"));
                editor.putString("Language", jsonObject.getString("Primary_Language__c"));
                editor.putString("Ethnicity", jsonObject.getString("Race__c"));
                editor.putString("SFID", jsonObject.getString("sf_id"));
            } catch (JSONException e2) {
                Log.e(TAG, e2.toString());
            } finally {
                editor.putBoolean("Searched", true);
                editor.apply();
            }
            // If we successfully load a user, we change the state to returning user
            mParent.setCurrentState(CheckinActivity.RegistrationState.RETURNING_USER);

            PersonalInfoFragment newFragment = new PersonalInfoFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.checkin_fragment_container, newFragment, getResources().getString(R.string.sidebar_personal_info));
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    /**
     * Error Listener for user info request
     */
    private class UserInfoErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            if (volleyError.getLocalizedMessage() != null) {
                Log.e(TAG, volleyError.toString());
            }

            Toast toast = Toast.makeText(getActivity(), "Error getting user info", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}

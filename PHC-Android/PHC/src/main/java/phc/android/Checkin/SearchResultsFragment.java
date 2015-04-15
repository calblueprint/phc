package phc.android.Checkin;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import phc.android.Helpers.SearchResult;
import phc.android.Networking.RequestManager;
import phc.android.R;

/**
 * SearchFragment is launched on successful submission of a client's form data,
 * and allows the user to go back to activity_checkin another client.
 */
public class SearchResultsFragment extends Fragment implements ListView.OnItemClickListener {
    private static final String TAG = "Search";
    public static final String SEARCH_RESULT = "SEARCH_RESULT";
    public static final String CACHED_RESULTS = "CACHED_RESULTS";
    public static final String SAVED_CURSOR = "CURSOR";

    // Key for user shared preferences
    private static final String USER_AUTH_PREFS_NAME = "UserKey";

    private static final int RESULTS_PER_PAGE = 20;

    private static RequestManager sRequestManager;
    private static RequestQueue sRequestQueue;

    // Shared Preferences
    private SharedPreferences mUserPreferences;

    private ProgressDialog mProgressDialog;

    // Parent Activity
    private CheckinActivity mParent;
    // Caching the search results
    private SearchResult[] mSearchResults = new SearchResult[0];
    // ListView for results and its adapter *
    private ListView mListView;
    // TextView holding the "No Results Found" message.
    private TextView mTextView;
    // Cursor for search pagination
    private int mCursor;


    private SearchResultAdapter mAdapter;
    // Button to try search again.
    private Button mSearchAgainButton;
    // Button to register client as a new user.
    private Button mRegisterAsNewButton;
    // Button to get next page of results
    private Button mNextResultsButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);
        mListView = (ListView) view.findViewById(R.id.search_result_list);
        mTextView = (TextView) view.findViewById(R.id.search_text_no_results);

        setupButtons(view);

        if (savedInstanceState != null) {
            mCursor = savedInstanceState.getInt(SAVED_CURSOR);
            mSearchResults = (SearchResult[]) savedInstanceState.get(CACHED_RESULTS);
            if (mSearchResults.length == 0) {
                setNoResultsMessage();
            }
            mAdapter = new SearchResultAdapter(getActivity(), mSearchResults);
            mListView.setAdapter(mAdapter);

            mAdapter.notifyDataSetChanged();
        } else {
            mCursor = 0;
            // Initialize search results to be empty
            mSearchResults = new SearchResult[0];
        }

        //Set up Volley request framework
        sRequestQueue = Volley.newRequestQueue(getActivity());
        sRequestManager = new RequestManager(TAG, sRequestQueue);

        return view;
    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("Search Results");
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(true);
        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sRequestQueue.cancelAll(TAG);
                dialog.dismiss();
            }
        });
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
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

        mNextResultsButton = (Button) view.findViewById(R.id.button_search_next);
        mNextResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCursor += RESULTS_PER_PAGE;
                getPage(mCursor);
            }
        });
    }

    /**
     * Sets the TextView's text to "No Results Found"
     */
    private void setNoResultsMessage() {
        mTextView.setText(R.string.search_no_results);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save the cached results
        outState.putParcelableArray(CACHED_RESULTS, mSearchResults);
        outState.putInt(SAVED_CURSOR, mCursor);

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
        getPage(mCursor);
        super.onResume();
    }

    private void getPage(int cursor) {
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
                    cursor,
                    new SearchResultResponseListener(),
                    new SearchResultErrorListener());
            showProgressDialog();
        }
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
                    mListView.setVisibility(View.INVISIBLE);
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
                editor.putBoolean("GLBT", jsonObject.getBoolean("Identify_as_GLBT__c"));
                editor.putBoolean("Foster", jsonObject.getBoolean("Foster_care__c"));
                editor.putBoolean("Veteran", jsonObject.getBoolean("Veteran__c"));
                editor.putString("SFID", jsonObject.getString("sf_id"));
            } catch (JSONException e2) {
                Log.e(TAG, e2.toString());
            } finally {
                editor.putBoolean("Searched", true);
                editor.apply();
            }
            // If we successfully load a user, we do not need to clear form data since we need to
            // populate it
            mParent.setCurrentState(CheckinActivity.FormDataState.SAVE_DATA);

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

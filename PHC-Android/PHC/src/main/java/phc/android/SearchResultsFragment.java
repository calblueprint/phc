package phc.android;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.RecoverySystem;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SearchFragment is launched on successful submission of a client's form data,
 * and allows the user to go back to activity_register another client.
 */
public class SearchResultsFragment extends Fragment implements RecoverySystem.ProgressListener {
    private static RequestQueue requestQueue;
    private static final String TAG = "Search";
    public static final String REQUEST_PATH = "/api/v1/search";
    private static final String AUTH_TOKEN = "phcplusplus";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);
        return view;
    }

    public void onActivityCreated (Bundle savedInstanceState) {
        requestQueue = Volley.newRequestQueue(getActivity());
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        LinearLayout sidebarList = (LinearLayout) getActivity().findViewById(R.id.sidebar_list);
        for (int i = 0; i < sidebarList.getChildCount(); i++) {
            View v = sidebarList.getChildAt(i);
            Object vTag = v.getTag();
            if ((vTag != null) && (vTag.equals(getResources().getText(R.string.sidebar_search)))) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.BOLD);
            } else if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTypeface(null, Typeface.NORMAL);
            }
        }

        SharedPreferences searchPreferences = getActivity().getSharedPreferences(SearchFragment.SEARCH_PARAMETERS, 0);
        final String firstName = searchPreferences.getString("firstName", null);
        final String lastName = searchPreferences.getString("lastName", null);
        final SimpleDateFormat df = new SimpleDateFormat();

        if(firstName != null && lastName != null) {
            String url = getActivity().getResources().getString(R.string.request_url);
            final ListView listView = (ListView) getView().findViewById(R.id.search_result_list);

            JsonArrayRequest searchResultsRequest = new JsonArrayRequest(url + REQUEST_PATH, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray jsonArray) {
                    SearchResult[] results = new SearchResult[jsonArray.length()];
                    try {
                        for (int i=0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            SearchResult result = new SearchResult();
                            result.setFirstName(json.getString("first_name"));
                            result.setLastName(json.getString("last_name"));

                            if (!json.getString("birthday").equals("null")) {
                                result.setBirthday(df.parse(json.getString("birthday")));
                            }

                            result.setSalesForceId(json.getString("sf_id"));
                            results[i] = result;
                        }
                    } catch (JSONException e1) {
                        Log.e("Search Results Parse Error", e1.toString());
                    } catch (ParseException e2) {
                        Log.e("Birthday Parse Error", e2.toString());
                    }

                    SearchResultAdapter adapter = new SearchResultAdapter(SearchResultsFragment.this.getActivity(), results);
                    Log.d("Adapter", "adapter created")
;                   listView.setAdapter(adapter);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Context context = getActivity();
                    String message;
                    if (volleyError instanceof NetworkError) {
                        message = "Network Error. Please try again later.";
                    } else {
                        try {
                            JSONObject response = new JSONObject(new String(volleyError.networkResponse.data));
                            message = response.toString();
                            Log.e("Volley Error", message);
                        } catch (Exception e) {
                            Log.e("Volley Error", "unknown");
                            message = "Unknown Error";
                        }
                    }
                    Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                    toast.show();

                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("FirstName", firstName);
                    headers.put("LastName", lastName);
                    headers.put("AuthToken", AUTH_TOKEN);
                    headers.put("Accept", "*/*");
                    return headers;
                }
            };

            requestQueue.add(searchResultsRequest);
        }


        super.onResume();
    }

    @Override
    public void onProgress(int progress) {
        // TODO: implement a progress bar that actually does something
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

            ((TextView)rowView.findViewById(R.id.first_name)).setText(searchResult.getFirstName());
            ((TextView)rowView.findViewById(R.id.last_name)).setText(searchResult.getLastName());

            if (searchResult.getBirthday() != null) {
                String birthday = sdf.format(searchResult.getBirthday());
                ((TextView)rowView.findViewById(R.id.birthday)).setText(birthday);
            } else {
                ((TextView)rowView.findViewById(R.id.birthday)).setText("None");
            }

            return rowView;
        }
    }

}

package jvvlives2005.c4q.nyc.jsondatahw_07_17;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by c4q-joshelynvivas on 7/18/15.

July 17, 2015
 Joshelyn Vivas HW- The JSON parsing at Data. Gov

 The idea is to use the url to get the data. I understand the set-up of it like create the xmls for the list,
 using the Service Handler, but I do not know the how to get the data or if I'm doing it correctly. For looking at the
 High School Graduation rate, I know that I am parsing objects.

 */


public class HSDataActivity extends ListActivity {

        private ProgressDialog pDialog;

        private static String url = "https://data.ok.gov/api/views/gwap-6qqd/rows.json?accessType=DOWNLOAD";

        //JSON node names
        private static final String TAG_TARGET = "target";
        private static final String TAG_HISTORICAL_COHORT = "historical cohort";
        private static final String TAG_YEAR = "year";

        //contact JSON Array
        JSONArray results = null;

        //Hashmap for ListView

        ArrayList<HashMap<String, String>> dataList;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            dataList = new ArrayList<HashMap<String, String>>();

            ListView resultView = getListView();

            // Calling async task to get json
            new GetResults().execute();

        }

        private class GetResults extends AsyncTask<Void, Void, Boolean> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // Showing progress dialog
                pDialog = new ProgressDialog(HSDataActivity.this);
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(false);
                pDialog.show();

            }

            @Override
            protected Boolean doInBackground(Void... params) {
                //Creating service handler class instance

                ServiceHandler sh = new ServiceHandler();

                // Making a request to url and getting response
                String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

                Log.d("Response: ", "> " + jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONArray results = new JSONArray(jsonStr);

                        // Getting JSON Array node
                        // results = jsonObj.getJSONArray(TAG_TARGET);

                        // looping through All Contacts
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject data = results.getJSONObject(i);

                            String year = data.getString(TAG_YEAR);
                            String historicalCohort = data.getString(TAG_HISTORICAL_COHORT);
                            String target = data.getString(TAG_TARGET);


                            // tmp hashmap for single contact
                            HashMap<String, String> HSData = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            HSData.put(TAG_YEAR, year);
                            HSData.put(TAG_HISTORICAL_COHORT, historicalCohort);
                            HSData.put(TAG_TARGET, target);

                            // adding contact to results list
                            dataList.add(HSData);
                        }
                        return true;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                }

                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);

                // Dismiss the progress dialog
                if (pDialog.isShowing())
                    pDialog.dismiss();

                /**
                 * Updating parsed JSON data into ListView
                 * */
                ListAdapter adapter = new SimpleAdapter(
                        HSDataActivity.this, dataList,
                        R.layout.list_row, new String[]{TAG_YEAR, TAG_HISTORICAL_COHORT, TAG_TARGET
                }, new int[]{R.id.year, R.id.historical_cohort,
                        R.id.target});

                setListAdapter(adapter);
            }

        }

    }



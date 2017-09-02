package edu.csulb.mediease;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ShoutActivity extends AppCompatActivity {

    public ShoutActivity CustomListView = null;
    public ArrayList<Message> CustomListViewValuesArr = new ArrayList<>();
    private ShoutAdapter adapter;
    private ListView listView;
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CustomListView = this;
        listView = (ListView) findViewById(R.id.listViewShout);
        setListData();
        adapter = new ShoutAdapter(this, CustomListViewValuesArr);
        listView.setAdapter(adapter);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("pushnotification")) {
                    handlePushNotification(intent);
                }
            }
        };
    }

    private void handlePushNotification(Intent intent) {
        //shared pref ->  get data -> add to message class object ->
        // then add to arraylist -> then notfiy data set changed

        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String hname = preferences.getString("hname", "");
        String uname = preferences.getString("uname", "");
        String msg = preferences.getString("message", "");

        Message message = new Message(hname, msg, uname);
        CustomListViewValuesArr.add(message);
        adapter.notifyDataSetChanged();
        listView.smoothScrollToPosition(adapter.getCount() - 1);
    }


    private void setListData() {
        // call asynctask => for json object length => add data to message class object => then to arraylist
        //-> after for loop ends => notifyDatasetchange

        //  HttpClient httpclient = new DefaultHttpClient();
        new SendShout().execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter("pushnotification"));

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);

        super.onPause();
    }

    private class SendShout extends AsyncTask<Void, Void, Void> {

        String uuid;
        private RequestQueue queue = Volley.newRequestQueue(ShoutActivity.this);
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(ShoutActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();

            SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            uuid = preferences.getString("uuid", "");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {

            String url = "http://ec2-54-215-133-230.us-west-1.compute.amazonaws.com/getChat.php?uuid=" + uuid;
//            String url = "http://ec2-54-215-133-230.us-west-1.compute.amazonaws.com/sendToAll.php";
            System.out.println("get chat url = " + url);
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("shout Response", response);

                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String status = jsonObject.getString("response");
                                System.out.println("status = " + status);

                                if (status.equals("Success")) {
                                    for (int i = 1; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        Message message = new Message(object.getString("hname"), object.getString("message")
                                                , object.getString("uname"));
                                        CustomListViewValuesArr.add(message);
                                    }
                                    adapter.notifyDataSetChanged();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.e("Error.Response", error.getMessage());
                        }
                    }
            ) /*{
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("uuid", uuid);
                    params.put("message", message);
                    params.put("hname", hname);
                    return params;
                }
            }*/;
            queue.add(postRequest);
            return null;
        }

    }
}

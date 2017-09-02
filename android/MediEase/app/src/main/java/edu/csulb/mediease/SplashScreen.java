package edu.csulb.mediease;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashScreen extends AppCompatActivity {

    private static final String MY_PREFS = "MyPrefs";
    private static final String SESSION = "session";
    private static final String UUID = "uuid";
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final boolean[] isLoggedIn = {false};

        new AsyncTask<Void, Void, Void>() {
            private RequestQueue queue = Volley.newRequestQueue(SplashScreen.this);
            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(SplashScreen.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Loading...");
                progressDialog.setIndeterminate(true);
                progressDialog.show();

                preferences = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
                if (preferences.contains(UUID) && preferences.contains(SESSION)) {
                    System.out.println("SESSION = " + preferences.getString(SESSION, ""));
                    if (preferences.getString(SESSION, "").equals("1")) {
                        isLoggedIn[0] = true;
                    }
                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                progressDialog.dismiss();
            }

            @Override
            protected Void doInBackground(Void... params) {

                if (!isLoggedIn[0]) {
                    startActivity(new Intent(SplashScreen.this, SignUpActivity.class));
                    finish();
                } else {

                    String uuid = preferences.getString(UUID, "");
                    System.out.println("uuid in splash= " + uuid);

                    String url = "http://ec2-54-215-133-230.us-west-1.compute.amazonaws.com/getSession.php?uuid=" + uuid;
                    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // response
                                    Log.d("Response", response);

                                    try {
                                        JSONArray jsonArray = new JSONArray(response);
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                                        String status = jsonObject.getString("response");
                                        String uuid = jsonObject.getString("uuid");
                                        String session = jsonObject.getString("session");
                                        System.out.println("status = " + status);
                                        System.out.println("uuid = " + uuid);
                                        System.out.println("session = " + session);
                                        if (status.equals("Success")) {
                                            if (session.equals("1")) {
                                                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                                                finish();
                                            } else {
                                                startActivity(new Intent(SplashScreen.this, SignUpActivity.class));
                                                finish();
                                            }
                                        } else {
                                            startActivity(new Intent(SplashScreen.this, SignUpActivity.class));
                                            finish();
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
                    );
                    queue.add(postRequest);

                }

                return null;
            }
        }.execute();

    }
}

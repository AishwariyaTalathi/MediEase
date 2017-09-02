package edu.csulb.mediease;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";
    private static final String MY_PREFS = "MyPrefs";
    private static final String TOKEN = "token";

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);

       // getApplicationContext().sendBroadcast(new Intent("myFCMTokenBroadcast"));
        //calling the method store token and passing token
        storeToken(refreshedToken);
    }

    private void storeToken(final String token) {
        //we will save the token in sharedpreferences later
        SharedPreferences preferences = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TOKEN, token);
        editor.apply();

    /*    RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://43.242.215.41/fcmTest.php?token="+token;
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
                            System.out.println("status = " + status);
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
        ); //{
            /*@Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", "m");
 //               params.put("password", );
   //             params.put("name", info.getUsername());
     //           params.put("number", info.getNumber());

                return params;
            }
         };*/

      //  queue.add(postRequest);

    }

}

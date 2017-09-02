package edu.csulb.mediease;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements View.OnClickListener {

    private GoogleMap mMap;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private EditText txtMessage;
    private Button btnSend;
    private String uuid, hname, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        txtMessage = (EditText) findViewById(R.id.editTextMessage);
        btnSend = (Button) findViewById(R.id.buttonSendMessage);
        btnSend.setOnClickListener(this);
        uuid = preferences.getString("uuid", "");

        Bundle bundle = getIntent().getExtras();
        final LatLng latLng = new LatLng(bundle.getDouble("lat"), bundle.getDouble("lng"));
        hname = bundle.getString("hname");
        hname = hname.replaceAll(" ", "%20");

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(14.0f).build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                mMap.moveCamera(cameraUpdate);
                mMap.addMarker(new MarkerOptions().position(latLng).title("Hospital"));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonSendMessage) {
            message = txtMessage.getText().toString().replaceAll(" ","%20");
            if (!message.equals("")) {
                new SendShout().execute();
                txtMessage.setText("");
                Toast.makeText(MapActivity.this, "Shout sent", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class SendShout extends AsyncTask<Void, Void, Void> {

        private RequestQueue queue = Volley.newRequestQueue(MapActivity.this);
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MapActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {

            String url = "http://ec2-54-215-133-230.us-west-1.compute.amazonaws.com/sendToAll.php?uuid="
                    + uuid + "&message=" + message + "&hname=" + hname;
//            String url = "http://ec2-54-215-133-230.us-west-1.compute.amazonaws.com/sendToAll.php";
            System.out.println("shout url = " + url);
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("shout Response", response);

                            /*try {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String status = jsonObject.getString("response");
                                String uuid = jsonObject.getString("uuid");
                                String session = jsonObject.getString("session");
                                System.out.println("status = " + status);
                                System.out.println("status = " + uuid);
                                System.out.println("status = " + session);

                                SharedPreferences preferences = getActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString(UUID, uuid);
                                editor.putString(SESSION, session);
                                editor.apply();

                                if (status.equals("Success")) {
                                    startActivity(new Intent(getActivity(), MainActivity.class));
                                    getActivity().finish();
                                } else {
                                    Toast.makeText(getActivity(), "User not found", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }*/
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

package edu.csulb.mediease;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1000;
    private ArrayList<String> placeIDs;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private FragmentManager fragmentManager;
    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;
    private GoogleMap mMap;
    private RequestQueue requestQueue;
    private LatLng position;
    private HomeFragment homeFragment, homeFragment1;
    private List<edu.csulb.mediease.Place> myPlaces = new ArrayList<>();
    private LatLng neededLatLng;
    private String uuid, token;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getFragmentManager();
        requestQueue = Volley.newRequestQueue(MainActivity.this);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        token = preferences.getString("token", "");
        uuid = preferences.getString("uuid", "");

        homeFragment = new HomeFragment();
        fragmentManager.beginTransaction().replace(R.id.navDrawerFragment, homeFragment, "homeFrag").commit();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(navigationView);

        navigationView.getMenu().getItem(0).setChecked(true);

        if (preferences.contains("fcmFlag") && preferences.getInt("fcmFlag", -1) == 1) {

        } else
            sendFCMToken();
    }

    private void sendFCMToken() {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://ec2-54-215-133-230.us-west-1.compute.amazonaws.com/insertToken.php?uuid=" + uuid + "&gcmtoken=" + token;
        System.out.println("url = " + url);
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
                            if (status.equals("Success")) {
                                editor = preferences.edit();
                                editor.putInt("fcmFlag", 1);
                                editor.apply();
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
                        Log.e("Error.Response", error.toString());
                    }
                }
        );
        queue.add(postRequest);
    }

    private void permissionchecking() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            /*case R.id.action_place_picker:
                permissionchecking();
                displayPlacePicker();
                break;*/
        }

        return super.onOptionsItemSelected(item);
    }

    public void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }

                    private void selectDrawerItem(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.navDrawerHome:
                                homeFragment = new HomeFragment();
                                fragmentManager.beginTransaction().replace(R.id.navDrawerFragment, homeFragment, "homeFrag").commit();
                                // Highlight the selected item has been done by NavigationView
                                menuItem.setChecked(true);
                                // Set action bar title
                                setTitle(menuItem.getTitle());
                                // Close the navigation drawer
                                drawerLayout.closeDrawers();
                                break;
                            case R.id.navDrawerOCR:
                                startActivity(new Intent(MainActivity.this, OCRActivity.class));
                                break;
                            case R.id.navDrawerAlarms:
                                startActivity(new Intent(MainActivity.this, AlarmsActivity.class));
                                break;
                            case R.id.navDrawerShout:
                                startActivity(new Intent(MainActivity.this, ShoutActivity.class));
                                break;
                            default:
                                homeFragment1 = new HomeFragment();
                                fragmentManager.beginTransaction().replace(R.id.navDrawerFragment, homeFragment1, "homeFrag").commit();
                                // Highlight the selected item has been done by NavigationView
                                menuItem.setChecked(true);
                                // Set action bar title
                                setTitle(menuItem.getTitle());
                                // Close the navigation drawer
                                drawerLayout.closeDrawers();
                                break;
                        }
                    }
                });
    }

    private void guessCurrentPlace() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {

            @Override
            public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    // String content = "";
                    // content= "Place" +placeLikelihood.getPlace().getName()+ "has ID:"+placeLikelihood.getPlace().getId()+"And Lat-Long:"+placeLikelihood.getPlace().getLatLng();
                    // mTextView.setText( content );
                    double current_latitude = placeLikelihood.getPlace().getLatLng().latitude;
                    double current_longitude = placeLikelihood.getPlace().getLatLng().longitude;

                    //Log.d("current_location",current_location);
                    System.out.println("current loc " + current_latitude + "" + current_longitude);
                }
                likelyPlaces.release();
                getPlaces();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new HospitalAsyncServer().execute();
                    }
                }, 1000);
            }
        });
    }

    private void getPlaces() {
        int radius = 8000;
        String types = "hospital";
        String key = "AIzaSyDIBE77Ok17o2r8ZjPFkfFpJgJ00zoRy78";
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                neededLatLng.latitude + "," + neededLatLng.longitude + "&radius=" + radius +
                "&types=" + types + "&key=" + key;
        System.out.println("url = " + url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            placeIDs = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String place_id = jsonObject.getString("place_id");
//                                Log.d("Place_ID", place_id);
                                placeIDs.add(place_id);
                            }
                            for (String placeid : placeIDs)
                                System.out.println("placeid = " + placeid);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                Log.e("Volley", "Error");
            }
        });
        requestQueue.add(request);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            displayPlace(PlacePicker.getPlace(data, this));
        }
    }

    private void displayPlace(Place place) {
        if (place == null)
            return;

        String content = "";
        if (!TextUtils.isEmpty(place.getName())) {
            content += "Name: " + place.getName() + "\n";
        }
        if (!TextUtils.isEmpty(place.getAddress())) {
            content += "Address: " + place.getAddress() + "\n";
        }
        if (!TextUtils.isEmpty(place.getPhoneNumber())) {
            content += "Phone: " + place.getPhoneNumber();
        }

        neededLatLng = place.getLatLng();
        System.out.println("needed lat lng = " + neededLatLng);

        System.out.println("content " + content);
        guessCurrentPlace();
    }

    private void displayPlacePicker() {
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected())
            return;

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            Log.d("PlacesAPI Demo", "GooglePlayServicesRepairableException thrown");
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.d("PlacesAPI Demo", "GooglePlayServicesNotAvailableException thrown");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void startMap(View view) {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.addMarker(new MarkerOptions().position(position).title("Hospital"));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(position));
            }
        });
    }

    public void locateMe(View view) {
        permissionchecking();
        displayPlacePicker();
    }

    /*private class HospitalAsync extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new HospitalAsyncServer().execute();
                }
            }, 2000);
        }

        @Override
        protected Void doInBackground(Void... params) {



            return null;
        }
    }*/

    private class HospitalAsyncServer extends AsyncTask<Void, Void, List<edu.csulb.mediease.Place>> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(List<edu.csulb.mediease.Place> places) {
            progressDialog.dismiss();
            System.out.println("in post exe");
            System.out.println("places = " + places);
            System.out.println("places count = " + places.size());
            homeFragment.loadList(places);
        }

        @Override
        protected List<edu.csulb.mediease.Place> doInBackground(Void... params) {
            String url2 = "http://ec2-54-215-133-230.us-west-1.compute.amazonaws.com/getplaces.php?" +
                    "id1=" + placeIDs.get(0) + "&id2=" + placeIDs.get(1) + "&id3="
                    + placeIDs.get(2) + "&id4=" + placeIDs.get(3) + "&id5=" + placeIDs.get(4);
            System.out.println("url2 = " + url2);
            System.out.println("placeids" + placeIDs.size());
            placeIDs.clear();
            /*String url2 = "http://ec2-54-215-133-230.us-west-1.compute.amazonaws.com/getplaces.php?" +
                    "id1=ChIJITRXWegz3YARH2qgix55GhE&id2=ChIJcbBjI-kz3YAROba_BNZo_cI" +
                    "&id3=ChIJX-Waw18u3YARv56efyTR89s&id4=ChIJdaTG0EMx3YARrrvgX5rFL84&id5=ChIJJzEPrukz3YARu09IvuIUrBY";*/
            // Create an array

            InputStream is = null;
            String result = "";

            // Retrieve JSON Objects from the given URL address

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url2);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();

            } catch (Exception e) {
                Log.e("log_tag", "Error in http connection " + e.toString());
            }

            // Convert response to string
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                result = sb.toString();
                System.out.println("result = " + result);
            } catch (Exception e) {

                Log.e("log_tag", "Error converting result " + e.toString());
            }

            try {
                JSONArray array = new JSONArray(result);
                if (myPlaces.size() > 0)
                    myPlaces.clear();
                for (int i = 1; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    String placeid = object.getString("place_id");
                    String hname = object.getString("hname");
                    double lat = object.getDouble("lat");
                    double lng = object.getDouble("lng");
                    String addr = object.getString("addr");
                    edu.csulb.mediease.Place place = new edu.csulb.mediease.Place(placeid, hname, lat, lng, addr);
                    System.out.println("Place = " + place.toString());
                    myPlaces.add(place);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }



			/*
             * try {
			 *
			 * jsonobject = new JSONObject(result); } catch (JSONException e) {
			 * Log.e("log_tag", "Error parsing data " + e.toString()); return
			 * e.toString(); }
			 */


            /*String url2 = "http://ec2-54-215-133-230.us-west-1.compute.amazonaws.com/getplaces.php?" +
                    "id1=" + placeIDs.get(0) + "&id2=" + placeIDs.get(1) + "&id3="
                    + placeIDs.get(2) + "&id4=" + placeIDs.get(3) + "&id5=" + placeIDs.get(4);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url2,
                    null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        if (response.getJSONObject(0).getString("response").equals("Success")) {
                            for (int i = 1; i < response.length(); i++) {
                                JSONObject object = response.getJSONObject(i);
                                String placeid = object.getString("place_id");
                                String hname = object.getString("hname");
                                double lat = object.getDouble("lat");
                                double lng = object.getDouble("lng");
                                String addr = object.getString("addr");
                                edu.csulb.mediease.Place place = new edu.csulb.mediease.Place(placeid, hname, lat, lng, addr);
                                System.out.println("Place = " + place.toString());
                                myPlaces.add(place);
                            }
                            HomeFragment fragment = (HomeFragment) getFragmentManager().findFragmentByTag("homeFrag");
                            fragment.loadList(myPlaces);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });*/

//            requestQueue.add(jsonArrayRequest);

            return myPlaces;
        }
    }

}

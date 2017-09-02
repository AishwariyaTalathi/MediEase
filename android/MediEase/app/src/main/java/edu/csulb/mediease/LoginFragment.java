package edu.csulb.mediease;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.regex.Pattern;

public class LoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private static final Integer RC_SIGN_IN = 100;
    private static final String MY_PREFS = "MyPrefs";
    private static final String SESSION = "session";
    private static final String UUID = "uuid";
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private Button buttonRegularSignIn;
    private TextView textViewSignUp;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton signInButton;
    private ProgressDialog mProgressDialog;
    private EditText txtEmail, txtPass;
    private String email_id, pass;
    private String fbEmail, fbName;
    private String gEmail, gName;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    private boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        txtEmail = (EditText) view.findViewById(R.id.editTextEmailLogin);
        txtPass = (EditText) view.findViewById(R.id.editTextPasswordLogin);
        textViewSignUp = (TextView) view.findViewById(R.id.textViewSignUp);
        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                SignUpFragment signupfragment = new SignUpFragment();
                fragmentTransaction.replace(R.id.signup_placeholder, signupfragment);
                fragmentTransaction.addToBackStack("signup");
                fragmentTransaction.commit();
            }
        });
        signInButton = (SignInButton) view.findViewById(R.id.signInGoogle);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        buttonRegularSignIn = (Button) view.findViewById(R.id.buttonSignIn);
        buttonRegularSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email_id = txtEmail.getText().toString();
                pass = txtPass.getText().toString();

                boolean bemail = false, bpass = false;

                if (isValidEmail(email_id)) {
                    bemail = true;
                } else if (email_id.equals("")) {
                    Toast.makeText(getActivity(), "Email Field is vacant", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Please Enter Valid Email Address", Toast.LENGTH_SHORT).show();
                }
                if (pass.equals("")) {
                    Toast.makeText(getActivity(), "Name Field is vacant", Toast.LENGTH_SHORT).show();
                } else {
                    bpass = true;
                }

                if (bpass && bemail)
                    new LoginAsync().execute();
            }
        });

        signInButton.setOnClickListener(this);

        loginButton = (LoginButton) view.findViewById(R.id.signInFacebook);
        loginButton.setFragment(this);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String accessToken = loginResult.getAccessToken().getToken();
                Log.i("accessToken", accessToken);
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("LoginActivity", response.toString());

                        String resp[] = response.toString().split("graphObject:");
                        try {
                            JSONObject jsonObject = new JSONObject(resp[1]);
                            fbName = jsonObject.getString("first_name") + "%20" + jsonObject.getString("last_name");
                            fbEmail = jsonObject.getString("email");

                            System.out.println("email = " + fbName);
                            System.out.println("name = " + fbEmail);
                            new LoginFBAsync().execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Get facebook data from login
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday");
                for (String key : parameters.keySet()) {
                    Log.d("Bundle Debug", key + " = \"" + parameters.get(key) + "\"");
                }
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d("Login Cancelled", "Login Cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Login Error", "Login Error");
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d("In On start::", "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideProgressDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signInGoogle:
                signIn();
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Loading..");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Connection Failed::", "onConnectionFailed:" + connectionResult);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("Success Tag::", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            // mStatusTextView.setText("Signed in user name: "+acct.getDisplayName());
            Log.d("Signing in as ::", acct.getDisplayName());
            Log.d("Email id is:", acct.getEmail());
            Log.d("id is:", acct.getId());
            gName = acct.getGivenName() + "%20" + acct.getFamilyName();
            gEmail = acct.getEmail();
            new LoginGoogleAsync().execute();
            // updateUI(true);
        } else {
            Log.d("Log In", "ERROR");
            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
    }

    private class LoginAsync extends AsyncTask<Void, Void, Void> {
        private RequestQueue queue = Volley.newRequestQueue(getActivity());
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
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

            String url = "http://ec2-54-215-133-230.us-west-1.compute.amazonaws.com/signin.php?email=" + email_id + "&password=" + pass + "&loginFlag=3";
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
            return null;
        }
    }

    private class LoginFBAsync extends AsyncTask<Void, Void, Void> {
        private RequestQueue queue = Volley.newRequestQueue(getActivity());

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
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

            String url = "http://ec2-54-215-133-230.us-west-1.compute.amazonaws.com/socialSignin.php?email=" + fbEmail + "&name=" + fbName + "&loginFlag=2";
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
            return null;
        }
    }

    private class LoginGoogleAsync extends AsyncTask<Void, Void, Void> {
        private RequestQueue queue = Volley.newRequestQueue(getActivity());

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
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

            String url = "http://ec2-54-215-133-230.us-west-1.compute.amazonaws.com/socialSignin.php?email=" + gEmail + "&name=" + gName + "&loginFlag=1";
            System.out.println("google url = " + url);
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
            return null;
        }
    }
}

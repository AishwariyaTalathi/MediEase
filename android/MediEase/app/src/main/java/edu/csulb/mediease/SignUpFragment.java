package edu.csulb.mediease;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class SignUpFragment extends Fragment implements View.OnClickListener {

    private static final String MY_PREFS = "MyPrefs";
    private static final String SESSION = "session";
    private static final String UUID = "uuid";
    private String name;
    private String pswd;
    private String number;
    private MaterialEditText txtName, txtPass, txtNum, txtEmail;
    private Context context;
    private String email_id;

    public SignUpFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        context = getActivity();
        Button btnSignUp = (Button) view.findViewById(R.id.buttonSignUp);
        txtEmail = (MaterialEditText) view.findViewById(R.id.editTextEmail);
        txtName = (MaterialEditText) view.findViewById(R.id.editTextName);
        txtNum = (MaterialEditText) view.findViewById(R.id.editTextNumber);
        txtPass = (MaterialEditText) view.findViewById(R.id.editTextPassword);
        btnSignUp.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonSignUp) {
            email_id = txtEmail.getText().toString();
            name = txtName.getText().toString();
            pswd = txtPass.getText().toString();
            number = txtNum.getText().toString();

            boolean bemail = false, bname = false, bpass = false, bnumber = false;

            if (isValidEmail(email_id)) {
                bemail = true;
            } else if (email_id.equals("")) {
                Toast.makeText(context, "Email Field is vacant", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Please Enter Valid Email Address", Toast.LENGTH_SHORT).show();
            }
            if (name.equals("")) {
                Toast.makeText(context, "Name Field is vacant", Toast.LENGTH_SHORT).show();
            } else {
                bname = true;
            }
            if (pswd.equals("")) {
                Toast.makeText(context, "Password Field is vacant", Toast.LENGTH_SHORT).show();
            } else {
                bpass = true;
            }
            if (number.equals("")) {
                Toast.makeText(context, "Number Field is vacant", Toast.LENGTH_SHORT).show();
            } else {
                bnumber = true;
            }

            if (bemail && bname && bnumber && bpass) {
                new SignUpAsync().execute();
                System.out.println("name = " + name);
                System.out.println("email = " + email_id);
                System.out.println("pass = " + pswd);
                System.out.println("number = " + number);
            }
        }
    }

    private boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private class SignUpAsync extends AsyncTask<Void, Void, Void> {

        private RequestQueue queue = Volley.newRequestQueue(context);
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
        protected Void doInBackground(Void... params) {

            String url = "http://ec2-54-215-133-230.us-west-1.compute.amazonaws.com/signup.php?email=" + email_id +
                    "&password=" + pswd + "&name=" + name +
                    "&number=" + number + "&loginFlag=3";
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

                                SharedPreferences preferences = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString(UUID, uuid);
                                editor.putString(SESSION, session);
                                editor.apply();

                                if (status.equals("Success")) {
                                    startActivity(new Intent(context, MainActivity.class));
                                    getActivity().finish();
                                } else {
                                    Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
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

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
        }
    }
}

package com.hair.fleek.learningcurve;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Registration extends AppCompatActivity {
    public Intent login;
    public Vibrator vb;
    public EditText firstname, lastname, username, email, password, confirmation;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        StrictMode.enableDefaults();
        firstname = (EditText) findViewById(R.id.input_name);
        lastname = (EditText) findViewById(R.id.last_name);
        email = (EditText) findViewById(R.id.input_email);
        username = (EditText) findViewById(R.id.input_username);
        password = (EditText) findViewById(R.id.input_password);
        confirmation = (EditText) findViewById(R.id.confirm_password);
        prefs = PreferenceManager.getDefaultSharedPreferences(Registration.this);
        editor = prefs.edit();

        login = new Intent(Registration.this, Login.class);
        vb = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        FloatingActionButton Registration = (FloatingActionButton) findViewById(R.id.Registration);
        Registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vb.vibrate(25);
                if (password.getText().toString().equals(confirmation.getText().toString())) {  //Password match, initiate HTTP requests
                    String url = "http://www.thekumpany.co.za/phpmyadmin/androidscripts/StylR/Registration.php";

if (EmailPatter(email.getText().toString())) {
    AccountRegistration(url, firstname.getText().toString().replace("'", "''").toString(),
            lastname.getText().toString().replace("'", "''").toString(),
            username.getText().toString().replace("'", "''").toString(),
            email.getText().toString().replace("'", "''").toString(),
            password.getText().toString().replace("'", "''").toString());
}
                    else
{
    Toast.makeText(getApplicationContext(), "Invalid Email Address", Toast.LENGTH_SHORT).show();
}

                } else {
                    Toast.makeText(getApplicationContext(), "Password's do not match", Toast.LENGTH_LONG).show();
                }


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean Passwordmatch(String password, String confirmation) {
        if (password.toString() == confirmation.toString()) {
            Toast.makeText(getApplicationContext(), password.toString() + " -- " + confirmation.toString(), Toast.LENGTH_LONG).show();
            return true;
        } else {
            return false;
        }
    }

    public void AccountRegistration(String url, String pfirstname, String plastname, String pusername,
                                    String pemail, String Password) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        //  paramsemaail = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("firstname", pfirstname));//  firstname.getText().toString().replace("'", "''")));
        params.add(new BasicNameValuePair("lastname", plastname));//lastname.getText().toString().replace("'", "''")));
        params.add(new BasicNameValuePair("password", Password));// password.getText().toString().replace("'", "''")));
        params.add(new BasicNameValuePair("email", pemail));// email.getText().toString().replace("'", "''")));
        params.add(new BasicNameValuePair("username", pusername));//username.getText().toString().replace("'","''")));
        params.add(new BasicNameValuePair("confirmationcode", CodeGenerate()));
       /* params.add(new BasicNameValuePair("sEmail", txtEmail.getText().toString().replace("'", "''")));
        params.add(new BasicNameValuePair("sAddress", txtAddress.getText().toString().replace("'", "''")));
        params.add(new BasicNameValuePair("ConfirmCode", code.toString()));*/

        String resultServer = getHttpPost(url, params);

        String strStatusID = "0";
        String strError = "Unknown Status!";
        String strEmail = "";

        JSONObject c;

        try {
            c = new JSONObject(resultServer);

            strStatusID = c.getString("StatusID");
            strError = c.getString("Error");
            strEmail = c.getString("Sent");


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (strStatusID.equals("0")) {
            Toast.makeText(getApplicationContext(), strError.toString(), Toast.LENGTH_LONG).show();
            Log.e("Not Saving", strError.toString());
        } else {
            if (strEmail.equals("Success")) {
                Toast.makeText(getApplicationContext(), "Account Created and Confirmation Email Sent.", Toast.LENGTH_LONG).show();

                editor.putString("StylRRegistered", "True").commit();

                startActivity(login); //to start and navigate to Login Activity
                finish();
            } else {

            }

        }
    }

    public String getHttpPost(String url, List<NameValuePair> params) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);


        try {
            httpPost.setHeader("Accept", "Application/x-www-form-urlencoded");
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = client.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Status OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download result..");
            }
        } catch (ClientProtocolException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return str.toString();
    }

    public String CodeGenerate() {
        Random r = new Random(System.currentTimeMillis());
        return "" + 5 + r.nextInt(10000) + "";
    }
    public boolean EmailPatter(String email) {
        if (email.toString().contains("@") && (email.toString().contains(".com") || email.toString().contains(".co.za") || email.toString().contains(".org"))) {
            if (email.toString().length() > 5) {
                Toast.makeText(getApplicationContext(), "Email Details Correct", Toast.LENGTH_SHORT).show();
                return true;
            } else {

                return false;
            }
        } else {

            return false;
        }
    }



}

package com.hair.fleek.learningcurve;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class recover_password extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    String splitdone;
    onSubmitListener mListener;
    Dialog dialog;
    Button canceldg,recover;
    TextView email,contact;
    private HttpResponse response;

    // TODO: Rename and change types and number of parameters
        public static recover_password newInstance(String param1, String param2) {
        recover_password fragment = new recover_password();
        Bundle args = new Bundle();


        return fragment;
    }

    public recover_password() {
        // Required empty public constructor
    }
    interface onSubmitListener {
        void setOnSubmitListener(String arg);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        StrictMode.enableDefaults();
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.custom_login_recovery);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        canceldg = (Button)dialog.findViewById(R.id.cancel_recover);
        recover = (Button)dialog.findViewById(R.id.recoverpassword);
        email = (TextView)dialog.findViewById(R.id.recovery_email);
        contact = (TextView)dialog.findViewById(R.id.recovery_contact);


        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        canceldg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        recover.setOnClickListener(new View.OnClickListener()
        {
            @Override
        public void onClick(View v)
            {
                if (email.getText().toString().isEmpty() || contact.getText().toString().isEmpty())
                {
                    Toast.makeText(getActivity().getApplicationContext(),"Please provide Details",Toast.LENGTH_SHORT).show();
                }
                else {
                    GetServerResponse(email.getText().toString(), contact.getText().toString());

                }
            }
        });

        return dialog;

    }

    public void GetServerResponse(String email,String phone)
    {
        boolean a=false;
        try {
            List<NameValuePair> nameValuePairs;

            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getConnectionManager().closeExpiredConnections();
            HttpPost httppost = new HttpPost("http://www.thekumpany.co.za/phpmyadmin/androidscripts/StylR/recover_password.php"); // make sure the url is correct.
            //add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);
            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,
            nameValuePairs.add(new BasicNameValuePair("email", email));  // $Edittext_value = $_POST['Edittext_value'];
            nameValuePairs.add(new BasicNameValuePair("phone", phone));

            httppost.setHeader("Accept", "Application/x-www-form-urlencoded");
            httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            //Execute HTTP Post Request
            response = httpclient.execute(httppost);


            //Passing the details of the current logged in user;
            InputStream isr = null;
            HttpEntity entity = response.getEntity();
            isr = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(isr, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + '\n');
            }

            isr.close();


            //end of reading Buffer

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);

          //  System.out.println("Response : " + response.toString()+ "Splitter Given" + Splitter(response.toString()));
if(Splitter(response.toString()).toString().equals("Success"))
{
   // Snackbar.make(getView(), "Please check your Mailbox for further instructions", Snackbar.LENGTH_LONG)
     //       .setAction("Action", null).show();
    mListener.setOnSubmitListener(splitdone);
}
            else
{
   // Snackbar.make(getView(), "Recovery Unsuccessful, please ensure that you proided correct details", Snackbar.LENGTH_LONG)
    //        .setAction("Action", null).show();
    mListener.setOnSubmitListener(splitdone);
}


        }catch(Exception e)
        {
            Log.e("Error",e.getMessage());
        }
    }

    public String Splitter(String response)
    {
        String status = response.toString().substring(response.toString().indexOf("Status"),response.toString().indexOf("!"));
        String finalstring = status.toString().substring(status.toString().indexOf(":")+2);
        splitdone = finalstring;
        return finalstring;
    }
}

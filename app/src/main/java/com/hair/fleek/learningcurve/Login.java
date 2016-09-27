package com.hair.fleek.learningcurve;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.hair.fleek.Introduction.PrefManager;
import com.hair.fleek.Introduction.WelcomeSliderActivity;
import com.hair.fleek.helper.MyPreferenceManager;
import com.linkedin.android.spyglass.mentions.MentionsEditable;
import com.linkedin.android.spyglass.suggestions.SuggestionsResult;
import com.linkedin.android.spyglass.suggestions.interfaces.Suggestible;
import com.linkedin.android.spyglass.suggestions.interfaces.SuggestionsResultListener;
import com.linkedin.android.spyglass.suggestions.interfaces.SuggestionsVisibilityManager;
import com.linkedin.android.spyglass.tokenization.QueryToken;
import com.linkedin.android.spyglass.tokenization.impl.WordTokenizer;
import com.linkedin.android.spyglass.tokenization.interfaces.QueryTokenReceiver;
import com.linkedin.android.spyglass.ui.MentionsEditText;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Calendar;

import data.InternetConnection;
import data.models.Person;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import android.util.Base64;
import android.util.Log;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login extends AppCompatActivity implements CustomStatusDialog.onSubmitListener, recover_password.onSubmitListener,
        QueryTokenReceiver,
        SuggestionsResultListener, SuggestionsVisibilityManager {
    public Intent done;
    public Vibrator vb;
    public String url, statusaccount;
    public String username, firstname, lastname, profilepic, confirmationcode, confirmed;
    private HttpResponse response;
    SharedPreferences prefs;
    private static final String BUCKET = "people-network";

    SharedPreferences.Editor editor;
    InternetConnection internet;
    MentionsEditText mentioneditor;
    TextView recoverpass;
    boolean recover = false;
    MixpanelAPI mixpanel;
    LoadingFragment localfrag = new LoadingFragment();


    private RecyclerView recyclerView;
    // private MentionsEditText editor;
    private PersonMentionAdapter adapter;
    private Person.PersonLoader people;
    CallbackManager callbackManager;

    interface onSubmitListener {
        void setOnSubmitListener(String arg);
    }
    protected void facebookSDKInitialize() {

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        facebookSDKInitialize();
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.login_main);
        StrictMode.enableDefaults();
        vb = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        done = new Intent(Login.this, MainDrawerActivity.class);
        Toolbar toolbar = (Toolbar) findViewById(R.id.LoginToolbar);
        setSupportActionBar(toolbar);
        internet = new InternetConnection();
        url = "http://www.thekumpany.co.za/phpmyadmin/androidscripts/StylR/Login.php";
        FloatingActionButton Loginfab = (FloatingActionButton) findViewById(R.id.LoginFab);
        FloatingActionButton CancelFab = (FloatingActionButton) findViewById(R.id.CancelFab);
        prefs = PreferenceManager.getDefaultSharedPreferences(Login.this);
        Button regbtn = (Button) findViewById(R.id.registerbutton);
        Button lg = (Button)findViewById(R.id.loginbutton);
        editor = prefs.edit();
        recoverpass = (TextView) findViewById(R.id.forgot_password);
        TextView passwordreset = (TextView) findViewById(R.id.forgot_password);
        final TextView username = (TextView) findViewById(R.id.loginusername);
        final TextView password = (TextView) findViewById(R.id.loginpassword);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button); //facebook_login
        recoverpass.setText(Html.fromHtml("<p><u>Forgot Password</u>?</p>"));
        mentioneditor = (MentionsEditText) findViewById(R.id.editor);
        recyclerView = (RecyclerView)findViewById(R.id.mentions_gridor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //specify total columns in Grid
      //   recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new PersonMentionAdapter(new ArrayList<Person>());
        recyclerView.setAdapter(adapter);
//getHash();
        loginButton.setReadPermissions("public_profile","email");
//        FirebaseInstanceId.getInstance().getToken();

        //Mix panel

        String projectToken = "6a686d1907246c6154486b33ce7176f5"; // e.g.: "1ef7e30d2a58d27f4b90c42e31d6d7ad"
        mixpanel = MixpanelAPI.getInstance(this, projectToken);
        // Always identify before initializing push notifications
        MixpanelAPI.People mxpeople = mixpanel.getPeople();
        mxpeople.identify("Obakeng");
        mxpeople.setPushRegistrationId("Obakeng");
        mxpeople.initPushHandling("284395923560");
        mxpeople.set("$first_name", "Obakeng");
        mxpeople.set("$last_name", "Moshane");
        mxpeople.set("$email","obk.moshane@gmail.com");



        //mixpanel.getPeople().identify("13793");
        //mixpanel.getPeople().initPushHandling("284395923560");
        //Mix panel
        try {
            JSONObject props = new JSONObject();
            props.put("Gender", "Male");
            props.put("Logged in", false);
            mixpanel.track("MainActivity - onCreate called", props);
        } catch (JSONException e) {
            Log.e("MYAPP", "Unable to add properties to JSONObject", e);
        }




        getLoginDetails(loginButton);

            regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefManager pf = new PrefManager(getApplicationContext());
                pf.setFirstTimeLaunch(true);
                startActivity(new Intent(Login.this, MainActivityPhone.class));
                /***
                 * Help provider
                 * http://code.tutsplus.com/tutorials/android-essentials-adding-events-to-the-users-calendar--mobile-8363
                 */

            }
        });
        mentioneditor.setTokenizer(new WordTokenizer());
        mentioneditor.setQueryTokenReceiver(this);
        mentioneditor.setSuggestionsVisibilityManager(this);
        mentioneditor.setHint("Post Comment");
       // people = new Person.PersonLoader(getResources());

        lg.setOnClickListener(new View.OnClickListener()
        {
            @Override
        public void onClick(View v)
            {
                MyPreferenceManager pf = new MyPreferenceManager(getApplicationContext());
                String response = pf.SendSMS("Hello There","0738413870");
                Log.d("Reported Response:" , response);
            }
        });
        LinearLayout mainLayout = (LinearLayout)findViewById(R.id.layout_main);
        TextView myLocation = new TextView(this);
       //myLocation.setText("436 Mayfield Ave, Stanford, CA");
        Linkify.addLinks(myLocation , Linkify.MAP_ADDRESSES);
        mainLayout.addView(myLocation);
        Loginfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vb.vibrate(25);
                recover = false;
                if (!(username.getText().toString().length() == 0) && !(password.getText().toString().length() == 0)) {
                   if (internet.hasActiveInternetConnection(getApplicationContext())) {
                        if (Login(username.getText().toString(), password.getText().toString())) {
//                          localfrag.dismiss();
                            if (confirmed.toString().equals("0")) {
                                CustomStatusDialog fragment1 = new CustomStatusDialog();
                                fragment1.mListener = Login.this;
                                fragment1.text = "";// mTextView.getText().toString();
                                fragment1.texttitle = "Enter Code";
                                fragment1.show(getFragmentManager(), "");
                            } else {
                                //localfrag.dismiss();
                                people = new Person.PersonLoader(getResources());
                                Toast.makeText(getApplicationContext(), "Account was confirmed, continue to start activity", Toast.LENGTH_LONG).show();
                                startActivity(done);
                                finish();
                            }
                        } else {
                            // localfrag.dismiss();
                            Snackbar.make(v, "Login details incorrect.", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    } else {

                        Snackbar.make(v, "No Active Internet Connection", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }


                } else {
                    Snackbar.make(v, "Please Provide Login Details", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }

        });
        CancelFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vb.vibrate(25);
                Snackbar.make(v, "User Closing Application, ask Confirmation", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        passwordreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recover_password fragment1 = new recover_password();
                fragment1.mListener = Login.this;
                //    fragment1.texttitle="Add Message?";
                recover = true;
                fragment1.show(getFragmentManager(), "");
            }
        });
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean Login(String username, String loginpassword) {
        boolean a = false;
        try {
            List<NameValuePair> nameValuePairs;
            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getConnectionManager().closeExpiredConnections();
            HttpPost httppost = new HttpPost("http://www.thekumpany.co.za/phpmyadmin/androidscripts/StylR/Login.php"); // make sure the url is correct.
            //add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);
            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,
            nameValuePairs.add(new BasicNameValuePair("username", username));  // $Edittext_value = $_POST['Edittext_value'];
            nameValuePairs.add(new BasicNameValuePair("password", loginpassword));
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
            // edited by James from coderzheaven.. from here....
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            System.out.println("Response : " + response.toString());
            if (response.contains("Login")) {
                Splitter(response);
                a = true;
            } else {
                a = false;
            }

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return a;
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

    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("Login");
            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);
                Log.i("ProPic", feedObj.getString("profilepic"));
                Log.i("firstname", feedObj.getString("firstname"));
                Log.i("lastname", feedObj.getString("lastname"));
                Log.i("username", feedObj.getString("username"));
            }
            // notify data changes to list adapater
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void Splitter(String fullresponse) {
        username = fullresponse.toString().substring(fullresponse.toString().indexOf("username") + 11,
                fullresponse.toString().indexOf(",") - 1);
        firstname = fullresponse.toString().substring(fullresponse.toString().indexOf("firstname") + 12,
                fullresponse.toString().indexOf("lastname") - 3);
        lastname = fullresponse.toString().substring(fullresponse.toString().indexOf("lastname") + 11,
                fullresponse.toString().indexOf("profilepic") - 3);
        profilepic = fullresponse.toString().substring(fullresponse.toString().indexOf("profilepic") + 13,
                fullresponse.toString().indexOf("email") - 3);

        String email;
        email = fullresponse.toString().substring(fullresponse.toString().indexOf("email") + 8,
                fullresponse.toString().indexOf("confirmationcode") - 3);
        confirmationcode =
                fullresponse.toString().substring(fullresponse.toString().indexOf("confirmationcode") + 19,
                        fullresponse.toString().indexOf("confirmed") - 3);
        confirmed =
                fullresponse.toString().substring(fullresponse.toString().indexOf("confirmed") + 12,
                        fullresponse.toString().lastIndexOf("]") - 2);
        editor.putString("username", username);
        editor.putString("firstname", firstname);
        editor.putString("lastname", lastname);
        editor.putString("profilepic", profilepic);
        editor.putString("email", email);
        String s = profilepic;
        s = s.replace("\\", "");
        editor.putString("profilepic", s.toString());
        editor.commit();
    }
    @Override
    public void setOnSubmitListener(String arg) {
        // generalStatus = arg.toString();
        if (recover) {
            //forgot password is clicked, act accordingly
            Toast.makeText(getApplicationContext(), "Hello now we on recovery mode", Toast.LENGTH_LONG).show();
        } else {
            if (arg.toString().equals(confirmationcode)) {
                Toast.makeText(getApplicationContext(), "Confirmation", Toast.LENGTH_LONG).show();
                //update the confirmed status via php and then continue to main screen
                ConfirmationCode();
            } else {
                Toast.makeText(getApplicationContext(), "Incorrect Confirmation Code", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events. for Facebook
        AppEventsLogger.activateApp(this);
    }
    @Override
    public void onPause()
    {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    public void ConfirmationCode() {
        try {
            List<NameValuePair> nameValuePairs;

            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getConnectionManager().closeExpiredConnections();
            HttpPost httppost = new HttpPost("http://www.thekumpany.co.za/phpmyadmin/androidscripts/StylR/updateconfirmation.php"); // make sure the url is correct.
            //add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);
            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,
            nameValuePairs.add(new BasicNameValuePair("username", username));  // $Edittext_value = $_POST['Edittext_value'];
            httppost.setHeader("Accept", "Application/x-www-form-urlencoded");
            httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            //Execute HTTP Post Request
            response = httpclient.execute(httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            Log.i("Response", "Response:->" + response.toString());
            String Message = response.toString().substring(response.toString().indexOf("Message") + 10,
                    response.toString().indexOf("!") + 1);
            Log.i("Response Message", Message.toString());
            if (Message.toString().equals("Data Updated!")) {
                startActivity(done);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public List<String> onQueryReceived(final @NonNull QueryToken queryToken) {
        List<String> buckets = Arrays.asList(BUCKET);
        List<Person> suggestions = people.getSuggestions(queryToken);
        SuggestionsResult result = new SuggestionsResult(queryToken, suggestions);
        // Have suggestions, now call the listener (which is this activity)
        onReceiveSuggestionsResult(result, BUCKET);
        return buckets;
    }
    // --------------------------------------------------
    // SuggestionsResultListener Implementation
    // --------------------------------------------------
    @Override
    public void onReceiveSuggestionsResult(@NonNull SuggestionsResult result, @NonNull String bucket) {
        List<? extends Suggestible> suggestions = result.getSuggestions();
        adapter = new PersonMentionAdapter(result.getSuggestions());
        recyclerView.swapAdapter(adapter, true);
        boolean display = suggestions != null && suggestions.size() > 0;
        displaySuggestions(display);
    }

    // --------------------------------------------------
    // SuggestionsManager Implementation
    // --------------------------------------------------

    @Override
    public void displaySuggestions(boolean display) {
        if (display) {
            recyclerView.setVisibility(RecyclerView.VISIBLE);
          //  commentlistview.setVisibility(r.GONE);
        } else {
            recyclerView.setVisibility(RecyclerView.GONE);
          //  commentlistview.setVisibility(r.VISIBLE);
        }
    }

    @Override
    public boolean isDisplayingSuggestions() {
        return recyclerView.getVisibility() == RecyclerView.VISIBLE;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView picture;
        public TextView handle;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.person_name);
            picture = (ImageView) itemView.findViewById(R.id.person_image);
            handle = (TextView) itemView.findViewById(R.id.person_handle);
        }
    }

    private class PersonMentionAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<? extends Suggestible> suggestions = new ArrayList<>();

        public PersonMentionAdapter(List<? extends Suggestible> people) {
            suggestions = people;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_mention_item, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            Suggestible suggestion = suggestions.get(i);
            if (!(suggestion instanceof Person)) {
                return;
            }

            final Person person = (Person) suggestion;
            viewHolder.name.setText(person.getFullName());
            Glide.with(viewHolder.picture.getContext())
                    .load(person.getPictureURL())
                    .crossFade()
                    .into(viewHolder.picture);
            viewHolder.handle.setText(person.getHandle());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Clicked " + person.getHandle().toString(), Toast.LENGTH_LONG).show();
                    mentioneditor.insertMention(person);

                    recyclerView.swapAdapter(new PersonMentionAdapter(new ArrayList<Person>()), true);
                    displaySuggestions(false);
                    mentioneditor.requestFocus();
                }
            });
        }
        @Override
        public int getItemCount() {
            return suggestions.size();
        }
    }
    protected void getLoginDetails(LoginButton login_button){

        // Callback registration
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result) {
                Profile profile = Profile.getCurrentProfile();
                Toast.makeText(getApplicationContext(),"ID " + login_result.getAccessToken().getUserId()
                      ,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Login.this,MainDrawerActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                // code for cancellation
            }

            @Override
            public void onError(FacebookException exception) {
                //  code to handle error
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e("data", data.toString() + "->" + resultCode);
    }
    public String geSpannable()
    {
        String ss = "@Obakeng @Moshane @Shai @Test";



        return ss;
    }

    public void getHash()
    {
        MessageDigest md = null;
        try {
            PackageInfo info = getApplicationContext().getPackageManager().getPackageInfo(
                    getApplicationContext().getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        Log.i("SecretKey = ",Base64.encodeToString(md.digest(), Base64.DEFAULT));
    }

}
package com.hair.fleek.learningcurve;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hair.fleek.activity.Main_Activity_Glide;
import com.hair.fleek.activity.Main_Activity_Glide_Category;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import data.DownloadImageTask;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.OnFragmentInteractionListener
          , SocialDeck_feed.OnFragmentInteractionListener,HairCalendarFragment.OnFragmentInteractionListener,
             ViewStylistProfile.OnFragmentInteractionListener,
            UpdateProfile.OnFragmentInteractionListener,Custom_dialog_booking_filter.onSubmitListener,
        custom_profile_crop.onSubmitListener,Main_Activity_Glide.OnFragmentInteractionListener
{
 //   Vibrator vb = (Vibrator)getSystemService(VIBRATOR_SERVICE);
    SharedPreferences prefs ;
    SharedPreferences.Editor editor;
    CircleImageView user_picture;
    Intent done;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView imgprofile = (ImageView)findViewById(R.id.profilePic);
        // TextView accountemail = (TextView)findViewById(R.id.accountemail);
         //TextView accountholdername = (TextView)findViewById(R.id.accountname);
        prefs =  PreferenceManager.getDefaultSharedPreferences(MainDrawerActivity.this);
         editor = prefs.edit();
         setNavigationHeader();

       // Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.profile);
      //   Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 100);
         done = new Intent(MainDrawerActivity.this, Main_Activity_Glide_Category.class);

         FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //             vb.vibrate(25);
                Snackbar.make(view, "Navigate", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

         View header = navigationView.getHeaderView(0);
         TextView name = (TextView)header.findViewById(R.id.accountname);
         TextView email = (TextView)header.findViewById(R.id.accountemail);
         user_picture = (CircleImageView)header.findViewById(R.id.accountprofile);
         name.setText(prefs.getString("firstname", "Error").toString() + " " + prefs.getString("lastname", "SharedPreferences").toString());
         email.setText(prefs.getString("email", "").toString());//"obk.moshane@gmail.com");
        try {
            URL url = new URL(prefs.getString("profilepic","http://thekumpany.co.za/StylRHair/PotentialModels/fleek_logo.jpg"));
            URLConnection urlConnection = url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());


        }
        catch(Exception e) {

        }

         new DownloadImageTask(user_picture)
                 .execute(prefs.getString("profilepic","http://thekumpany.co.za/StylRHair/ProfilePictures/Gloria12_dpic.JPG"));

         user_picture.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 custom_profile_crop fragment1 = new custom_profile_crop();
                 fragment1.mListener = MainDrawerActivity.this;
                 fragment1.show(getFragmentManager(), "");


             }
         });
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            Toast.makeText(getApplicationContext(),"Broadcast Message Received: Navigate to View Profile of Stylist " + message,Toast.LENGTH_SHORT).show();
            editor.putString("Caller", "Viewer");
            editor.commit(); //indicate that its mainly for viewing stylist from Feed.
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ViewStylistProfile hf = new ViewStylistProfile();
            FragmentManager frgManager = getFragmentManager();

            frgManager.beginTransaction().replace(R.id.container_body, hf).commit();


        }
    };

    private BroadcastReceiver mViewStylistReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
              Toast.makeText(getApplicationContext(), intent.toString() + "Hi there, received loud and clear -"
                        + intent.getStringExtra("link-explore_stylist"), Toast.LENGTH_SHORT).show();
                //now we need to call pull method.
                editor.putString("FeedCaller", "Clients").commit();
                SocialDeck_feed hf = new SocialDeck_feed();
                FragmentManager frgManager = getFragmentManager();
                frgManager.beginTransaction().replace(R.id.container_body, hf).commit();
            }


    };

    private BroadcastReceiver mGallery= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /*Toast.makeText(getApplicationContext(), "Hi there, received loud and clear -0000Gl" + intent.getStringExtra("Category").toString()
                    , Toast.LENGTH_SHORT).show();*/
            //now we need to call pull method.*/
            editor.putString("Zoom","True").commit(); //enable the zoom capability on the Image:Gallery
            editor.putString("LookBook","False").commit();  //to ensure that its only for viewing purposes
            editor.putString("Category",intent.getStringExtra("Category").toString()).commit();
            startActivity(done);
        }


    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            ///drawer.openDrawer(GravityCompat.START);
            super.onBackPressed();
        }
    }
    @Override
    public void onResume()
    {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("link-stylist"));
         LocalBroadcastManager.getInstance(this).registerReceiver(mViewStylistReceiver,
                                          new IntentFilter("link-explore_stylist"));


        LocalBroadcastManager.getInstance(this).registerReceiver(mGallery,
                new IntentFilter("ViewGallery"));

}
    @Override
    public void setOnSubmitListener(String arg) {
       // Toast.makeText(getApplicationContext(),"We calling this at theh end " + arg.toString(),Toast.LENGTH_LONG).show();
        new DownloadImageTask(user_picture).execute("http://thekumpany.co.za/StylRHair/ProfilePictures/" + arg.toString() + ".JPG");
            editor.putString("profilepic", "http://thekumpany.co.za/StylRHair/ProfilePictures/" + arg.toString() + ".JPG").commit();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_drawer, menu);
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
        if(id==R.id.uploadpics_settings)
        {
            Intent   done  = new Intent(MainDrawerActivity.this,CapturPic.class);
            startActivity(done);
            }

        if(id==R.id.action_upload)
        {

            Intent   done  = new Intent(MainDrawerActivity.this,CapturPic.class);
            startActivity(done);
        }

        return super.onOptionsItemSelected(item);
    }
    public void setActionBarTitle(String title)
    {
        getSupportActionBar().setTitle(title);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            HomeFragment hf = new HomeFragment();
            FragmentManager frgManager = getFragmentManager();
           // ft.replace(R.id.container_body,hf)
                    frgManager.beginTransaction().replace(R.id.container_body,hf).commit();
        } else if (id == R.id.nav_socialdeck) {
            editor.putString("FeedCaller","Default").commit();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            SocialDeck_feed hf = new SocialDeck_feed();
            FragmentManager frgManager = getFragmentManager();

            frgManager.beginTransaction().replace(R.id.container_body,hf).commit();

        } else if (id == R.id.nav_celeb_hair) {

        } else if (id == R.id.nav_nails) {



        } else if (id == R.id.nav_just_hair) {

        } else if (id == R.id.nav_stylists) {

            editor.putString("Caller","Default");
            editor.commit(); //Store to indicate that the caller is default
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ViewStylistProfile hf = new ViewStylistProfile();
            FragmentManager frgManager = getFragmentManager();

            frgManager.beginTransaction().replace(R.id.container_body,hf).commit();

        } else if (id==R.id.hair_appointment)
        {

            //Help with Application
            FragmentTransaction ftt = getSupportFragmentManager().beginTransaction();
            HairCalendarFragment hff = new HairCalendarFragment();
            FragmentManager frgManager = getFragmentManager();
            frgManager.beginTransaction().replace(R.id.container_body, hff).commit();

        }else if (id==R.id.nails_appointment)
        {

        } else if (id==R.id.nav_share)
        {
            //This will need to allow sharing on social media
        }
        else if (id == R.id.Help)
        {

        } else if (id == R.id.chat)
        {
            //TO allow chat functionality
        }
        else if (id == R.id.nav_tools)
        {
            //to allow navigation into application tools.
        }
        else if (id ==R.id.look_book)
        {
            //load the look book
            editor.putString("Zoom","False").commit();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Main_Activity_Glide hf = new Main_Activity_Glide();
            FragmentManager frgManager = getFragmentManager();
            // ft.replace(R.id.container_body,hf)
            frgManager.beginTransaction().replace(R.id.container_body,hf).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }



    public void setNavigationHeader()
    {
       NavigationView navigation_view = (NavigationView) findViewById(R.id.nav_view);

    }
}

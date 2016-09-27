package com.hair.fleek.learningcurve;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import app.AppController;
import data.FeedItem;
import android.widget.TabHost;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewStylistProfile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ViewStylistProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
    public class ViewStylistProfile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView image1,image2,image3;
    private String URL_FEED = "http://thekumpany.co.za/phpmyadmin/androidscripts/StylR/StylistProfileView.php?stylistID=KennyG";
    private TextView stylistname,stylisthandle,statistics;
    private Button viewclients, bookme;
    TabHost tabHost;

    private OnFragmentInteractionListener mListener;
    SharedPreferences prefs ;
    SharedPreferences.Editor editor;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewStylistProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewStylistProfile newInstance(String param1, String param2) {
        ViewStylistProfile fragment = new ViewStylistProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewStylistProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs =  PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = prefs.edit();



        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }

        if(!(prefs.getString("Caller","Default").toString().equals("Default")) && prefs.getString("Caller","Default").equals("Viewer"))
        {
            Toast.makeText(getActivity(),"Called ",Toast.LENGTH_SHORT).show();
            //method is called for viewing the stylistinfo:investigating
            AppController.getInstance().cancelPendingRequests(this);
            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(URL_FEED);
            if (entry != null) {
                // fetch the data from cache
                try {
                    String data = new String(entry.data, "UTF-8");
                    try {
                        parseJsonFeed(new JSONObject(data));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (UnsupportedEncodingException  e) {
                    e.printStackTrace();
                }

            } else {
              //  AppController.getInstance().cancelPendingRequests(this);
                // making fresh volley request and getting json
                ///----------------------------------------------------??This is the modified section,Request.Method.GET
                JsonObjectRequest jsonReq = new JsonObjectRequest(
                        URL_FEED,null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        VolleyLog.d("TAG", "Response: " + response.toString());
                        if (response != null) {
                            parseJsonFeed(response);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("TAG", "Error: " + error.getMessage());
                    }
                });

                // Adding request to volley request queue
                AppController.getInstance().addToRequestQueue(jsonReq);

            }
        }


    //    pull();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Stylist Profile");
        View v = inflater.inflate(R.layout.view_stylist_profile,container,false);
        image1 = (ImageView)v.findViewById(R.id.imageView2);
        //image2 = (ImageView)v.findViewById(R.id.imageView3);
        //image3 = (ImageView)v.findViewById(R.id.imageView4);
        stylistname = (TextView)v.findViewById(R.id.stylistname);
        stylisthandle =(TextView)v.findViewById(R.id.stylisthandle);
        statistics = (TextView)v.findViewById(R.id.statslabel);
        viewclients = (Button)v.findViewById(R.id.viewclients);
        bookme = (Button)v.findViewById(R.id.bookme);


//tab2

    viewclients.setOnClickListener(new View.OnClickListener()
    {
        @Override
    public void onClick(View v)
        {
           //Toast.makeText(getActivity().getApplicationContext(), "Navigate to Feed but filter by Current Stylist mentions", Toast.LENGTH_SHORT).show();
            editor.putString("FeedCaller","Clients").commit();
            Intent intent = new Intent("link-explore_stylist");
            // add data
            intent.putExtra("link-explore_stylist", stylisthandle.getText().toString());
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        }
    });
        bookme.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Initiate setting up an appointment", Toast.LENGTH_SHORT).show();
            }

        });

        tabHost = (TabHost) v.findViewById(android.R.id.tabhost);
        LocalActivityManager mlocalactivity = new LocalActivityManager(getActivity(),false);
        mlocalactivity.dispatchCreate(savedInstanceState);
        tabHost.setup(mlocalactivity);//very important to call this
        TabHost.TabSpec tab1 = tabHost.newTabSpec("Services Content");
        tab1.setIndicator("SERVICES");
        Intent services = new Intent(getActivity(), Services_Stylist.class);
        Intent reviews = new Intent(getActivity(), StylistView_BIO.class);
        Intent bio = new Intent(getActivity(), StylistView_BIO.class);
        tab1.setContent(services);

        TabHost.TabSpec tab2 = tabHost.newTabSpec("Reviews Content");

        tab2.setIndicator("REVIEWS");
        tab2.setContent(reviews);

        TabHost.TabSpec tab3 = tabHost.newTabSpec("BIO Content");
        tab3.setIndicator("BIO");
        tab3.setContent(bio);

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);
        tabHost.setCurrentTab(0);
        return v;
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }



    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("Stylists");
            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);
                stylistname.setText(feedObj.getString("StylistName"));
                stylisthandle.setText("@" + feedObj.getString("stylistID"));
                statistics.setText("Bookings: 33 \nNails Done: 22\nHair Done: 8");
                Log.i("Checking--", feedObj.getString("StylistName"));
            }
            // notify data changes to list adapater
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }
}

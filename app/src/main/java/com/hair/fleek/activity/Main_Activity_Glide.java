package com.hair.fleek.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hair.fleek.Introduction.WelcomeSliderActivity;
import com.hair.fleek.adapter.FeedListAdapter;
import com.hair.fleek.adapter.GalleryAdapter;
import com.hair.fleek.learningcurve.MainDrawerActivity;
import com.hair.fleek.learningcurve.R;
import com.hair.fleek.learningcurve.SocialDeck_feed;
import com.hair.fleek.learningcurve.ViewStylistProfile;
import com.hair.fleek.model.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.AppController;
import com.hair.fleek.Introduction.PrefManager;
import com.hair.fleek.activity.Main_Activity_Glide;
import com.hair.fleek.activity.Main_Activity_Glide_Category;
import data.FeedItem;

public class Main_Activity_Glide extends Fragment {
    private String TAG = Main_Activity_Glide.class.getSimpleName();
    private static final String endpoint = "http://api.androidhive.info/json/glide.json";
    private static final String endpoint2 ="http://www.thekumpany.co.za/phpmyadmin/androidscripts/StylR/Categories.php";
     ArrayList<Image> images;
     ProgressDialog pDialog;
     GalleryAdapter mAdapter;
     RecyclerView recyclerView;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    ProgressDialog mProgressDialog;

     static final String ARG_PARAM1 = "param1";
     static final String ARG_PARAM2 = "param2";
    private OnFragmentInteractionListener mListener;

    String mParam1;
     String mParam2;
     FragmentActivity  mycontext;


             Intent done;
    public static Main_Activity_Glide newInstance(String param1, String param2) {
        Main_Activity_Glide fragment = new Main_Activity_Glide();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public Main_Activity_Glide() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Look Book");
        View v = inflater.inflate(R.layout.activity_main__activity__glide,container,false);

        recyclerView = (RecyclerView)v.findViewById(R.id.recycler_view);

        mAdapter = new GalleryAdapter(getActivity().getApplicationContext(), images);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);
                editor.putString("LookBook", "True").commit(); //tell the fragment that, it must show the button
                FragmentTransaction ft = mycontext.getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
                // fetchImages();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return v;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        pDialog = new ProgressDialog(getActivity());
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getActivity(), images);
        prefs =  PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = prefs.edit();
        try {
      // pullCategories();
            new DownloadCategories().execute();
        }
        catch(Exception e)
        {
        }
    }
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
            mycontext=(FragmentActivity) activity;

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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }



    private BroadcastReceiver mViewStylistReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Toast.makeText(getActivity(),"Hi there, received loud and clear -"
                   // +  intent.getStringExtra("Category")
                    ,Toast.LENGTH_SHORT).show();


        }
    };

    private BroadcastReceiver mGalleryReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {





        }
    };

    @Override
    public void onResume()
    {
        super.onResume();


        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mGalleryReceiver,
                new IntentFilter("ViewGallery"));
    }


/*

    public void pullCategories(){
        AppController.getInstance().cancelPendingRequests(this);
        AppController.getInstance().getRequestQueue().getCache().clear();
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(endpoint2);



        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");


                try {

                    parseJsonFeed(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            // making fresh volley request and getting json
            ///----------------------------------------------------??This is the modified section,Request.Method.GET
           JsonObjectRequest jsonReq = new JsonObjectRequest(
                    endpoint2,null, new Response.Listener<JSONObject>() {
            //CustomJsonRequest jsonReq = new CustomJsonRequest(Request.Method.POST,endpoint2,new Response.Listener<JSONObject>() {)
                @Override
                public void onResponse(JSONObject response) {
                       VolleyLog.d(TAG, "Response: " + response.toString());
                    if (response != null) {
                        parseJsonFeed(response);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });



            // Adding request to volley request queue
            AppController.getInstance().addToRequestQueue(jsonReq);
        }

    }

*/

    public void parseJsonFeed(JSONObject response)
    {
    images.clear();

        {
            try {
                JSONArray feedArray = response.getJSONArray("Categories");

                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject details = (JSONObject) feedArray.get(i);

                    Image image = new Image();
                    // image.setName(object.getString("name"));
                    image.setModelname(details.getString("ModelName"));

                    String s = details.getString("Image");
                    s = s.replace("\\", "");
                    image.setSmall(s.toString());
                    image.setMedium(s.toString());
                    image.setLarge(s.toString());
                    // image.setTimestamp(object.getString("timestamp"));
                    image.setTimestamp("");
                    image.setName(details.getString("Category"));

                    images.add(image);

                }
                mAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        }
    }


    private class DownloadCategories extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title

            // Set progressdialog message
            mProgressDialog.setMessage("Loading Categories...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();



        }
        @Override
        protected Void doInBackground(Void... args)
        {
            AppController.getInstance().cancelPendingRequests(this);
            AppController.getInstance().getRequestQueue().getCache().clear();
            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(endpoint2);

            if (entry != null) {
                // fetch the data from cache
                try {
                    String data = new String(entry.data, "UTF-8");


                    try {

                        parseJsonFeed(new JSONObject(data));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                // making fresh volley request and getting json
                ///----------------------------------------------------??This is the modified section,Request.Method.GET
                JsonObjectRequest jsonReq = new JsonObjectRequest(
                        endpoint2,null, new Response.Listener<JSONObject>() {
                    //CustomJsonRequest jsonReq = new CustomJsonRequest(Request.Method.POST,endpoint2,new Response.Listener<JSONObject>() {)
                    @Override
                    public void onResponse(JSONObject response) {
                        VolleyLog.d(TAG, "Response: " + response.toString());
                        if (response != null) {
                            parseJsonFeed(response);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                    }
                });



                // Adding request to volley request queue
                AppController.getInstance().addToRequestQueue(jsonReq);
            }


            return null;
        }
        @Override
        protected void onPostExecute(Void args)
        {
            mProgressDialog.dismiss();
        }
    }

}


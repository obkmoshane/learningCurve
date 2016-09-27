package com.hair.fleek.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hair.fleek.adapter.FeedListAdapter;
import com.hair.fleek.adapter.GalleryAdapter;
import com.hair.fleek.learningcurve.R;
import com.hair.fleek.model.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import app.AppController;
import data.FeedItem;

public class Main_Activity_Glide_Category extends AppCompatActivity {
    private String TAG = Main_Activity_Glide_Category.class.getSimpleName();
    private static final String endpoint = "http://api.androidhive.info/json/glide.json";
    private static final String endpoint2 = "http://thekumpany.co.za/phpmyadmin/androidscripts/StylR/LookBookGallery.php?Category=";
    ArrayList<Image> images;
    ProgressDialog pDialog;
    GalleryAdapter mAdapter;
    RecyclerView recyclerView;
    int retry =0;
    SharedPreferences prefs ;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__activity__glide);
        prefs =  PreferenceManager.getDefaultSharedPreferences(Main_Activity_Glide_Category.this);
        editor = prefs.edit();
        this.setTitle("Category: " + prefs.getString("Category",""));
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        pDialog = new ProgressDialog(this);
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getApplicationContext(), images);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        //fetchImages();
        pullGallery();
        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
                // fetchImages();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }


    private void fetchImages() {

        pDialog.setMessage("Downloading json...");
        pDialog.show();


        JsonArrayRequest req = new JsonArrayRequest(endpoint,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        pDialog.hide();

                        images.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                Image image = new Image();
                                image.setName(object.getString("name"));

                                JSONObject url = object.getJSONObject("url");
                                image.setSmall(url.getString("small"));
                                image.setMedium(url.getString("medium"));
                                image.setLarge(url.getString("large"));
                                image.setTimestamp(object.getString("timestamp"));

                                images.add(image);

                            } catch (JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                            }
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                pDialog.hide();
                /*if (retry<3)
                {
                    retry +=1; //increment to only try 3 times.
                    fetchImages();

                }*/


            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

    }


    public void pullGallery(){
        AppController.getInstance().cancelPendingRequests(this);
        AppController.getInstance().getRequestQueue().getCache().clear();
        Cache cache = AppController.getInstance().getRequestQueue().getCache();

       String endpoint3 = endpoint2 +  prefs.getString("Category","(1=1)");
        Cache.Entry entry = cache.get(endpoint3);

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
                    endpoint3,null, new Response.Listener<JSONObject>() {
                       @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "ResponseRD: " + response.toString());
                    if (response != null) {
                        parseJsonFeed(response);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "ErrorDA: " + error.getMessage());
                }
            });



            // Adding request to volley request queue
            AppController.getInstance().addToRequestQueue(jsonReq);
        }

    }


    public void parseJsonFeed(JSONObject response)
    {
        images.clear();

        {
            try {
                JSONArray feedArray = response.getJSONArray("Gallery");

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
}

package com.hair.fleek.learningcurve;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hair.fleek.adapter.FeedListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import app.AppController;
import data.FeedItem;

public class MainFeed extends AppCompatActivity {
    private static final String TAG = MainFeed.class.getSimpleName();
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private String URL_FEED ="http://thekumpany.co.za/phpmyadmin/androidscripts/StylR/feed.php";
    //"http://api.androidhive.info/feed/feed.json";
    private int startfrom,end,count,lastlimit;

    //hold


private FeedListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_feed);
        StrictMode.enableDefaults();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView) findViewById(R.id.list);

        feedItems = new ArrayList<FeedItem>();

        listAdapter = new FeedListAdapter(this, feedItems);
        listView.setAdapter(listAdapter);


     /*   getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5998")));
       getActionBar().setIcon(
                new ColorDrawable(getResources().getColor(android.R.color.transparent)));
*/        // We first check for cached request
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
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            // making fresh volley request and getting json
   ///----------------------------------------------------??This is the modified section,Request.Method.GET
            JsonObjectRequest jsonReq = new JsonObjectRequest(
                    URL_FEED,null, new Response.Listener<JSONObject>() {

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

    /**
     * Parsing json reponse and passing the data to feed view list com.hair.fleek.learningcurve.adapter
     * */
    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("feed");


            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                Log.i("ProPic",feedObj.getString("profilePic"));

                FeedItem item = new FeedItem();
                item.setId(feedObj.getInt("id"));
                item.setName(feedObj.getString("name"));

                // Image might be null sometimes
                String image = feedObj.isNull("image") ? null : feedObj
                        .getString("image");
                item.setImge(image);
                item.setStatus(feedObj.getString("status"));
                item.setHandle("@"+feedObj.getString("username"));

                item.setProfilePic("http://thekumpany.co.za/StylRHair/PotentialModels/fleek_logo.jpg");//feedObj.getString("profilePic"));
                item.setTimeStamp(feedObj.getString("timeStamp"));

                // url might be null sometimes
                String feedUrl = feedObj.isNull("url") ? null : feedObj
                        .getString("url");
                item.setUrl(feedUrl);// feedUrl);
                    Log.i("url",feedUrl);
                feedItems.add(item);
            }

            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public String PullByLimit(int getcount)
    {
        String fulllimit="";
        if (getcount<=1)
        {
            startfrom = 1;
            end = 5;
            count = count+1;
            lastlimit = end;
            fulllimit = startfrom + "," + lastlimit;
        }
        else
        {
            startfrom = lastlimit+1;
            end = lastlimit+6;
            lastlimit = end;
            count = count+1;
            fulllimit = startfrom + "," + lastlimit;
        }

        return fulllimit;
    }

}

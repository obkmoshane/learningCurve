package com.hair.fleek.learningcurve;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hair.fleek.adapter.FeedCommentAdapter;
import com.hair.fleek.adapter.FeedListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import app.AppController;
import data.CommentFeed;
import data.DownloadImageTask;
import data.FeedItem;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SocialDeck_feed.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SocialDeck_feed#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocialDeck_feed extends Fragment implements AbsListView.OnScrollListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private int preLast;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String TAG = SocialDeck_feed.class.getSimpleName();
    private ListView listView;
    private FeedListAdapter listAdapter;
  //  private FeedCommentAdapter commentlistadapter;
    private List<FeedItem> feedItems;
    private List<CommentFeed> CommentItems;
    private String URL_FEED ="http://thekumpany.co.za/phpmyadmin/androidscripts/StylR/feed.php";
    //"http://api.androidhive.info/feed/feed.json";
    private FeedListAdapter adapter;
    private OnFragmentInteractionListener mListener;
    private RelativeLayout btnhearts;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    Cache.Entry entry;
    ProgressDialog mProgressDialog;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SocialDeck_feed.
     */
    // TODO: Rename and change types and number of parameters
    public static SocialDeck_feed newInstance(String param1, String param2) {
        SocialDeck_feed fragment = new SocialDeck_feed();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SocialDeck_feed() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        StrictMode.enableDefaults();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = prefs.edit();

        if (prefs.getString("FeedCaller","Default").toString().equals("Default"))
        {
            //user clicked the main option, initiate normal load

          setRetainInstance(true);
            new DownloadAsync().execute();
           // pull();
        }
        else if (prefs.getString("FeedCaller","Default").equals("Clients"))
        {
            //method being initiated by user going through stylists profile. update URL_Feed.
                  URL_FEED="http://thekumpany.co.za/phpmyadmin/androidscripts/StylR/LoadCustomers.php?StylistID=KennyG";
           // pull();
            new DownloadAsync().execute();
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("SocialDeck");
       View v = inflater.inflate(R.layout.activity_main_feed,container,false);
        listView = (ListView)v.findViewById(R.id.list);
        feedItems = new ArrayList<FeedItem>();
        btnhearts = (RelativeLayout)v.findViewById(R.id.addheart);
        listAdapter = new FeedListAdapter(getActivity(), feedItems);
        listView.setAdapter(listAdapter);
        listView.setClickable(true);

        listView.setOnScrollListener(this);

        return v;
    }



    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("feed");
            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                Log.i("ProPic", feedObj.getString("profilePic"));

                FeedItem item = new FeedItem();

                item.setId(feedObj.getInt("id"));

                item.setName(feedObj.getString("name"));

                // Image might be null sometimes
                String image = feedObj.isNull("image") ? null : feedObj
                        .getString("image");
                item.setImge(image);
                item.setStatus(feedObj.getString("status"));

                item.setHandle("@" + feedObj.getString("username"));


                item.setTotalComments(feedObj.getString("TotalComments") + " Comments");
                String s = feedObj.getString("profilePic");
                s=s.replace("\\","");
                item.setProfilePic(s.toString());//"http://thekumpany.co.za/StylRHair/PotentialModels/fleek_logo.jpg");//s.toString());//"http://thekumpany.co.za/StylRHair/PotentialModels/fleek_logo.jpg");//feedObj.getString("profilePic"));

                item.setTimeStamp(feedObj.getString("timeStamp"));

                // url might be null sometimes
                String feedUrl = feedObj.isNull("url") ? null : feedObj
                        .getString("url");

                          item.setUrl(feedObj.getString("url").toString());// feedUrl);

                feedItems.add(item);
          //     CommentItems.add(comment);
            }
            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();
          //  commentlistadapter.notifyDataSetChanged();
        } catch (JSONException e) {

            e.printStackTrace();
        }
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

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
       // Toast.makeText(getActivity(), "Scrolling " + visibleItemCount, Toast.LENGTH_SHORT).show();
      //  switch(view.getId()) {
        //    case view.findViewById(R.id.list).getId():

                // Make your calculation stuff here. You have all your
                // needed info from the parameters of this function.

                // Sample calculation to determine if the last
                // item is fully visible.
                final int lastItem = firstVisibleItem + visibleItemCount;
                if(lastItem == totalItemCount) {
                    if(preLast!=lastItem){ //to avoid multiple calls for last item
                        Log.d("Last", "Last");
                        preLast = lastItem;
                    }
                }
      //  }
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
        void onFragmentInteraction(Uri uri);
    }
public void pull(){
    AppController.getInstance().cancelPendingRequests(this);
    AppController.getInstance().getRequestQueue().getCache().clear();
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
                Toast.makeText(getActivity().getApplicationContext(),"->"+ response.toString(),Toast.LENGTH_SHORT).show();
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


    private class DownloadAsync extends AsyncTask<Void,Void,Void>{


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            mProgressDialog  = new ProgressDialog(getActivity());
            // Set progressdialog title

            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();

            AppController.getInstance().cancelPendingRequests(this);
            AppController.getInstance().getRequestQueue().getCache().clear();
            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            entry = cache.get(URL_FEED);
        }
        @Override
        protected Void doInBackground(Void... params)
        {
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
                        URL_FEED, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getActivity().getApplicationContext(), "->" + response.toString(), Toast.LENGTH_SHORT).show();
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

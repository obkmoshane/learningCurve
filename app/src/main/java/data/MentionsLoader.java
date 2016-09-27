/*
* Copyright 2015 LinkedIn Corp. All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*/

package data;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.linkedin.android.spyglass.mentions.Mentionable;
import com.linkedin.android.spyglass.tokenization.QueryToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import app.AppController;
import data.models.GLOBAL;
import data.models.Person;

/**
 * Simple class to get suggestions from a JSONArray (represented as a file on disk), which can then
 * be mentioned by the user by tapping on the suggestion.
 */
public  abstract class MentionsLoader<T extends Mentionable> {

    protected T[] mData;
    private static final String TAG = MentionsLoader.class.getSimpleName();

    public MentionsLoader(final Resources res, final int resID) {
        new LoadJSONArray(res, resID).execute();
    }

    public abstract T[] loadData(JSONArray arr);

    // Returns a subset
    public List<T> getSuggestions(QueryToken queryToken) {
        String prefix = queryToken.getKeywords().toLowerCase();
        List<T> suggestions = new ArrayList<>();
        if (mData != null) {
            for (T suggestion : mData) {
                String name = suggestion.getSuggestiblePrimaryText().toLowerCase();
                if (name.startsWith(prefix)) {
                    suggestions.add(suggestion);
                }
            }
        }
        return suggestions;
    }

    // Loads data from JSONArray file, defined in the raw resources folder
    private class LoadJSONArray extends AsyncTask<Void, Void, JSONArray> {

        private final WeakReference<Resources> mRes;
        private final int mResId;

        public LoadJSONArray(Resources res, int resId) {
            mRes = new WeakReference<>(res);
            mResId = resId;
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            InputStream fileReader = mRes.get().openRawResource(mResId);
            Writer writer = new StringWriter();
            JSONArray arr = null;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileReader, "UTF-8"));
                String line = reader.readLine();
                while (line != null) {
                    writer.write(line);
                    line = reader.readLine();
                }
                String jsonString = writer.toString();
                arr = new JSONArray(jsonString);
            } catch (Exception e) {
                Log.e(TAG, "Unhandled exception while reading JSON", e);
            } finally {
                try {
                    fileReader.close();
                } catch (Exception e) {
                    Log.e(TAG, "Unhandled exception while closing JSON file", e);
                }
            }
            return arr;
        }

        @Override
        protected void onPostExecute(JSONArray arr) {
            super.onPostExecute(arr);
            pullStylists();

           // mData = loadData(arr);
        }


        public void pullStylists() {
                       Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get("http://thekumpany.co.za/phpmyadmin/androidscripts/StylR/Stylists.php");

            if (entry != null) {
                // fetch the data from cache
                Log.d("ENtry not empty", "--> Not Empty");
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
                        "http://thekumpany.co.za/phpmyadmin/androidscripts/StylR/Stylists.php", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        VolleyLog.d("Error", "ResponseStylists: " + response.toString());
                        if (response != null) {
                            parseJsonFeed(response);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Error", "ErrorStylists: " + error.getMessage());
                    }
                });

                // Adding request to volley request queue
                AppController.getInstance().addToRequestQueue(jsonReq);
            }

        }

        public void parseJsonFeed(JSONObject response) {
            JSONArray feedArray = null;
            try {
                feedArray = response.getJSONArray("Stylists");
                GLOBAL.GlobalfeedArray = response.getJSONArray("Stylists");
                //loadData(feedArray);
                Log.d("This is now the data", GLOBAL.GlobalfeedArray.toString());
                mData = loadData(GLOBAL.GlobalfeedArray);

            } catch (JSONException e) {

                e.printStackTrace();
            }


            //  Person[] data = new Person[data2.length()];
            //     Log.d("helping","Sweet " + data.length);
            try {
                Log.d("Ah", feedArray.toString());
                Log.d("Ah-Global", GLOBAL.GlobalfeedArray.toString());

                for (int i = 0; i < feedArray.length(); i++) {
                    /*JSONObject obj = feedArray.getJSONObject(i);
                    String first = obj.getString("firstname");
                    String last = obj.getString("lastname");
                    String url = "http://thekumpany.co.za/StylRHair/PotentialModels/fleek_logo.jpg";// obj.getString("picture");
                    String Handle = "@" + obj.getString("Handle");
                    //  data[i] = new Person(first, last, url,Handle);
                    GLOBAL.StylistData[i] = new Person(first, last, url, Handle);

                    Log.d("Loaded Name", i + obj.getString("Name").toString());
                */}
            } catch (Exception e) {
                Log.e("Error", "Unhandled exception while parsing person JSONArray", e);
            }


            //Log.d("Getting GLOBAL", GLOBAL.StylistData.toString());
            //  return GLOBAL.StylistData; //data
        }
    }
}

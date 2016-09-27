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

package data.models;

import android.content.res.Resources;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hair.fleek.adapter.FeedListAdapter;
import com.linkedin.android.spyglass.mentions.Mentionable;
import com.linkedin.android.spyglass.R;


import app.AppController;
import data.MentionsLoader;
import com.linkedin.android.spyglass.tokenization.QueryToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Model representing a person.
 */

public class Person implements Mentionable {

    private final String mFirstName;
    private final String mLastName;
    private final String mPictureURL;
    private final String mHandle;
    private final Person[] ab=null;

    public Person(String firstName, String lastName, String pictureURL,String Handle) {
        mFirstName = firstName;
        mLastName = lastName;
        mPictureURL = pictureURL;
        mHandle = Handle;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public String getPictureURL() {
        return mPictureURL;
    }

    public String getHandle(){return mHandle;}
    // --------------------------------------------------
    // Mentionable/Suggestible Implementation
    // --------------------------------------------------

    @NonNull
    @Override
    public String getTextForDisplayMode(MentionDisplayMode mode) {
        switch (mode) {
            case FULL:
                return getFullName();
            case PARTIAL:
                String[] words = getFullName().split(" ");
                return (words.length > 1) ? words[0] : "";
            case NONE:
            default:
                return "";
        }
    }

    @Override
    public MentionDeleteStyle getDeleteStyle() {
        // People support partial deletion
        // i.e. "John Doe" -> DEL -> "John" -> DEL -> ""
        return MentionDeleteStyle.PARTIAL_NAME_DELETE;
    }

    @Override
    public int getSuggestibleId() {
        return getFullName().hashCode();
    }

    @Override
    public String getSuggestiblePrimaryText() {
        return getFullName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFirstName);
        dest.writeString(mLastName);
        dest.writeString(mPictureURL);
        dest.writeString(mHandle);
    }

    public Person(Parcel in) {
        mFirstName = in.readString();
        mLastName = in.readString();
        mPictureURL = in.readString();
        mHandle = in.readString();
    }

    public static final Creator<Person> CREATOR
            = new Creator<Person>() {
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    // --------------------------------------------------
    // PersonLoader Class (loads people from JSON file)
    // --------------------------------------------------

    public static class PersonLoader extends MentionsLoader<Person> {
        private static final String TAG = PersonLoader.class.getSimpleName();

        public PersonLoader(Resources res) {
            super(res, com.hair.fleek.learningcurve.R.raw.people);
        }

        @Override
        public Person[] loadData(JSONArray arr) {


            Person[] data = new Person[arr.length()];



            // Person[] data = new Person[data2.length()];
            //pullStylists();

            try {
               // pullStylists();
                //Log.d("What theStatus",GLOBAL.GlobalfeedArray.toString());
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String first = obj.getString("firstname");
                    String last = obj.getString("lastname");
                    String url = "http://dummyimage.com/40x40/ec2f1a/ffffff&text=" +
                            obj.getString("firstname").toString().substring(0,1) +
                            "+" + obj.getString("lastname").toString().substring(0,1);// obj.getString("picture");
                    String Handle = "@" + obj.getString("Handle");
                    data[i] = new Person(first, last, url, Handle);

                }


             //   Log.d("What we have", GLOBAL.StylistData.toString());
            } catch (Exception e) {
                Log.e(TAG, "Unhandled exception while parsing person JSONArray", e);
            }

            return data;
        }

        // Modified to return suggestions based on both first and last name
        @Override
        public List<Person> getSuggestions(QueryToken queryToken) {
            String prefix = queryToken.getKeywords().toLowerCase();
            List<Person> suggestions = new ArrayList<>();
            if (mData != null) {
                for (Person suggestion : mData) {
                    String firstName = suggestion.getFirstName().toLowerCase();
                    String lastName = suggestion.getLastName().toLowerCase();
                    String handle = suggestion.getHandle().toLowerCase();

                    if (firstName.startsWith(prefix) || lastName.startsWith(prefix) || handle.startsWith(prefix, 1)) {
                        //  suggestions.add(suggestion.getHandle());
                        suggestions.add(suggestion);
                    }
                }
            }
            return suggestions;
        }
    }


}

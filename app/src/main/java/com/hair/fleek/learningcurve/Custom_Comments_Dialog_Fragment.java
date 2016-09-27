package com.hair.fleek.learningcurve;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Network;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.hair.fleek.adapter.FeedCommentAdapter;
import com.hair.fleek.adapter.FeedListAdapter;
import data.models.Person;
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
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.AppController;
import data.CommentFeed;
import data.FeedItem;
import data.models.Person;


/**
 * Created by Obakeng Moshane on 2016-03-28.
 */
public class Custom_Comments_Dialog_Fragment extends DialogFragment  implements QueryTokenReceiver,
        SuggestionsResultListener, SuggestionsVisibilityManager {
    Dialog dialog;
    private FeedCommentAdapter commentlistadapter;
    private List<CommentFeed> CommentItems;
    ImageButton btncomment;
    private static final String TAG = Custom_Comments_Dialog_Fragment.class.getSimpleName();
   String URL_FEED;// ="http://thekumpany.co.za/phpmyadmin/androidscripts/StylR/FeedComments.php?CommentID=18";
    private SharedPreferences pref;
    private SharedPreferences.Editor edt;
    String url = "http://www.thekumpany.co.za/phpmyadmin/androidscripts/StylR/InsertComment.php";
    private static final String BUCKET = "people-network";

    private RecyclerView recyclerView;
   // private MentionsEditText editor;
    private PersonMentionAdapter adapter;
    private Person.PersonLoader people;
    private MentionsEditText  actual_comment;
    ListView commentlistview;
    View r;
    @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            StrictMode.enableDefaults();
            dialog = new Dialog(getActivity());
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.setContentView(R.layout.comment_dialog);
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)    {
        r = inflater.inflate(R.layout.comment_dialog,container,false);
        commentlistview = (ListView) r.findViewById(R.id.list);
        btncomment = (ImageButton)r.findViewById(R.id.send);
        actual_comment = (MentionsEditText)r.findViewById(R.id.actual_comment);
        CommentItems = new ArrayList<CommentFeed>();
        commentlistadapter = new FeedCommentAdapter(getActivity(), CommentItems);
        commentlistview.setAdapter(commentlistadapter);
        commentlistview.setClickable(true);
        commentlistview.setAdapter(commentlistadapter);


        /**
         * For People mentions
         */
        View v = inflater.inflate(R.layout.grid_mentions,container,false);
        recyclerView = (RecyclerView)r.findViewById(R.id.mentions_grid);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); //specify total columns in Grid
        // recyclerView.setLayoutManager(new GridLayoutManager(this, 1))
        adapter = new PersonMentionAdapter(new ArrayList<Person>());
        recyclerView.setAdapter(adapter);

       // actual_comment = (MentionsEditText)r.findViewById(R.id.actual_comment);
        actual_comment.setTokenizer(new WordTokenizer());
        actual_comment.setQueryTokenReceiver(this);
        actual_comment.setSuggestionsVisibilityManager(this);
        actual_comment.setHint("Post Comment");
        people = new Person.PersonLoader(getResources());

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        edt = pref.edit();
        //URL_FEED = URL_FEED.concat("21");
        URL_FEED ="http://thekumpany.co.za/phpmyadmin/androidscripts/StylR/FeedComments.php?CommentID=" + pref.getInt("ClickedID",0);
        pull();

       btncomment.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (actual_comment.getText().toString().trim().length() == 0) {

               } else {
                   CommentFeed comment = new CommentFeed();
                   comment.setId(12);
                   comment.setName(pref.getString("firstname", "") + " " + pref.getString("lastname", ""));
                   comment.setComment(actual_comment.getText().toString().trim());
                   comment.setHandle("@" + pref.getString("username", "-"));
                   comment.setProfilePic(pref.getString("profilepic", "http://thekumpany.co.za/StylRHair/PotentialModels/fleek_logo.jpg"));
                   comment.setTimeStamp("2016");
                   CommentItems.add(comment);
                   commentlistadapter.notifyDataSetChanged();
                   Toast.makeText(getActivity().getApplicationContext(),
                           pref.getString("username", "0").toString(),Toast.LENGTH_SHORT).show();
                   AccountRegistration(pref.getString("username", "0").toString(),
                           pref.getInt("ClickedID", 0), actual_comment.getText().toString());
                   actual_comment.setText("");
               }
           }
       });

        return r;
    }

    public void pull()
    {
        String s;
       // s= URL_FEED.concat(""+ pref.getInt("ClickedID",0));
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
//Toast.makeText(getActivity().getApplicationContext(), URL_FEED ,Toast.LENGTH_LONG).show();

        Cache.Entry entry = cache.get(URL_FEED);//"http://thekumpany.co.za/phpmyadmin/androidscripts/StylR/FeedComments.php?CommentID=18");//+ pref.getInt("ClickedID",0));

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

    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("Comments");
            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                Log.i("ProPic", feedObj.getString("ProfilePic"));
                CommentFeed comment = new CommentFeed();
                comment.setId(feedObj.getInt("ID"));
                comment.setName(feedObj.getString("firstname") + " " + feedObj.getString("lastname"));
                comment.setComment(feedObj.getString("Comment"));
                comment.setHandle("@" + feedObj.getString("UserID"));
                String s = feedObj.getString("ProfilePic");
                s=s.replace("\\","");
                comment.setProfilePic(s.toString());//"http://thekumpany.co.za/StylRHair/PotentialModels/fleek_logo.jpg");//s.toString());//"http://thekumpany.co.za/StylRHair/PotentialModels/fleek_logo.jpg");//feedObj.getString("profilePic"));

                //        comment.setProfilePic(s.toString());
                comment.setTimeStamp("2016");//feedObj.getString("timeStamp"));
                //       comment.setTimeStamp(feedObj.getString("timeStamp"));
                // url might be null sometimes
                CommentItems.add(comment);
            }
            // notify data changes to list adapater
            commentlistadapter.notifyDataSetChanged();

            //  commentlistadapter.notifyDataSetChanged();
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }


    public void AccountRegistration(String UserID,Integer UploadID,String Comment) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        //  paramsemaail = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("UserID", UserID));//  firstname.getText().toString().replace("'", "''")));
        params.add(new BasicNameValuePair("UploadID", String.valueOf(UploadID)));//lastname.getText().toString().replace("'", "''")));
        params.add(new BasicNameValuePair("Comment", Comment));// password.getText().toString().replace("'", "''")));
        String resultServer = getHttpPost(url, params);

        String strStatusID = "0";
        String strError = "Unknown Status!";
        String strCommentsCount;
        String strMsg = "";
        JSONObject c;

        try {
            c = new JSONObject(resultServer);
            strStatusID = c.getString("StatusID");
            strError = c.getString("Error");
            strCommentsCount = c.getString("TotalComments");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (strStatusID.equals("0")) {
            Toast.makeText(getActivity().getApplicationContext(), "Something went wrong, please try again", Toast.LENGTH_LONG).show();
            Log.e("Not Saving", strError.toString());
        } else {
            if (strStatusID.equals("1")) {
               // Toast.makeText(getActivity().getApplicationContext(), "Comment Saved", Toast.LENGTH_LONG).show();

            } else {

            }

        }
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
            commentlistview.setVisibility(r.GONE);
        } else {
            recyclerView.setVisibility(RecyclerView.GONE);
            commentlistview.setVisibility(r.VISIBLE);
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
            handle = (TextView)itemView.findViewById(R.id.person_handle);
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
                    Toast.makeText(getActivity().getApplicationContext(),"Clicked " + person.getHandle().toString(),Toast.LENGTH_LONG).show();
                    actual_comment.insertMention(person);

                    recyclerView.swapAdapter(new PersonMentionAdapter(new ArrayList<Person>()), true);
                    displaySuggestions(false);
                    actual_comment.requestFocus();

                }
            });
        }

        @Override
        public int getItemCount() {
            return suggestions.size();
        }
    }

}

package com.hair.fleek.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.gesture.GestureOverlayView;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.hair.fleek.learningcurve.FeedImageView;
import com.hair.fleek.learningcurve.LipLockShow;
import com.hair.fleek.learningcurve.R;

import org.w3c.dom.Text;

import java.util.List;


import app.AppController;
import data.CommentFeed;
import data.DownloadImageTask;
import data.FeedItem;
import data.LinkEnabledTextView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Obakeng Moshane on 2016-02-04.
 */

public class FeedCommentAdapter     extends BaseAdapter  {
    private Activity activity;
    private LayoutInflater inflater;
    private List<CommentFeed> feedItems;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private LinkEnabledTextView check;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    Resources res ;
    Drawable drawnew,drawcurrent;
    private static final long DOUBLE_PRESS_INTERVAL = 1000L; // in millis
    private long thisTime = 0;
    private long prevTime = 0;
    private boolean firstTap = true;
    private long lastPressTime;
    boolean mHasDoubleClicked;
    Animation shake;
    private GestureDetector gestureScanner;
    Vibrator vb;
    interface onSubmitListener {
        void setOnSubmitListener(String arg);
    }
    public FeedCommentAdapter(Activity activity, List<CommentFeed> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        editor = prefs.edit();

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.feed_comment, parent, false);
        }
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();


        vb = (Vibrator)convertView.getContext().getSystemService(convertView.getContext().VIBRATOR_SERVICE);
        TextView name = (TextView) convertView.findViewById(R.id.comment_name);
        TextView timestamp = (TextView) convertView
                .findViewById(R.id.comment_timestamp);
        TextView statusMsg = (TextView) convertView
                .findViewById(R.id.comment_Msg);/*
        TextView url = (TextView) convertView.findViewById(R.id.txtUrl);*/
        //NetworkImageView
        NetworkImageView profilePic = (NetworkImageView) convertView
                .findViewById(R.id.comment_profilePic);
        TextView handle = (TextView)convertView.findViewById(R.id.comment_handle);






        final CommentFeed item = feedItems.get(position);


        name.setText(item.getName());
        handle.setText(item.getHandle());
        res= convertView.getResources();
        // feedImageView.setOnTouchListener();

       /* draw  = res.getDrawable(R.drawable.ic_favorite_border_black_18dp);
        hearttype.setImageDrawable(draw);*/
        // Converting timestamp into x ago format
//----------------------------------------------------------------------------------->
    /* CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getTimeStamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);*/
        timestamp.setText(item.getTimeStamp());//"21 May 2016");

        ///End Gestures

        // Chcek for empty status message
        if (!TextUtils.isEmpty(item.getComment())) {
            statusMsg.setText(item.getComment());
            statusMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            statusMsg.setVisibility(View.GONE);
        }

        // Checking for null feed url
       /* if (item.getUrl() != null) {
            url.setText(Html.fromHtml("<a href=\"" + item.getUrl() + "\">"
                    + "View Image in browser</a> "));

            // Making url clickable
            url.setMovementMethod(LinkMovementMethod.getInstance());
            url.setVisibility(View.VISIBLE);
        } else {
            // url is null, remove from the view
            url.setVisibility(View.GONE);
        }*/
        // user profile p
       profilePic.setImageUrl(item.getProfilePic(), imageLoader);
     //  new DownloadImageTask(profilePic)
      //          .execute(item.getProfilePic());//"http://thekumpany.co.za/StylRHair/PotentialModels/fleek_logo.jpg");//item.getProfilePic());

//http://dummyimage.com/300x300/ec2f1a/ffffff&text=A

        return convertView;
    }
    public void add(String message)
    {
        Toast.makeText(this.activity.getApplicationContext(),"Button Clicked :" + message.toString(),Toast.LENGTH_LONG).show();
    }


    public boolean onTouchEvent(MotionEvent me)
    {
        return gestureScanner.onTouchEvent(me);
    }



}
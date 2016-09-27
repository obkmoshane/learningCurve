package com.hair.fleek.adapter;

import android.app.Activity;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.hair.fleek.learningcurve.CapturPic;
import com.hair.fleek.learningcurve.Custom_Comments_Dialog_Fragment;
import com.hair.fleek.learningcurve.FeedImageView;
import com.hair.fleek.learningcurve.LipLockShow;
import com.hair.fleek.learningcurve.R;
import com.hair.fleek.learningcurve.ViewStylistProfile;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


import app.AppController;
import data.DownloadImageTask;
import data.FeedItem;
import data.models.GLOBAL;
import data.models.Person;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Obakeng Moshane on 2016-02-04.
 */

public class FeedListAdapter     extends BaseAdapter implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,AbsListView.OnScrollListener {
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    Resources res;
    Drawable drawnew, drawcurrent, drawlipcount;
    private static final long DOUBLE_PRESS_INTERVAL = 1000L; // in millis
    private long thisTime = 0;
    private long prevTime = 0;
    private boolean firstTap = true;
    private long lastPressTime;
    boolean mHasDoubleClicked;
    private int preLast;
    Animation shake;
    private GestureDetector gestureScanner;
    Vibrator vb;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        switch(view.getId()) {
            case android.R.id.list:

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
        }
    }

    interface onSubmitListener {
        void setOnSubmitListener(String arg);
    }

    public FeedListAdapter(Activity activity, List<FeedItem> feedItems) {
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
            convertView = inflater.inflate(R.layout.feed_item, parent, false);
        }
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();


        vb = (Vibrator) convertView.getContext().getSystemService(convertView.getContext().VIBRATOR_SERVICE);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView timestamp = (TextView) convertView
                .findViewById(R.id.timestamp);
        TextView statusMsg = (TextView) convertView
                .findViewById(R.id.txtStatusMsg);
        final TextView url = (TextView) convertView.findViewById(R.id.txtUrl);
        //NetworkImageView
        NetworkImageView profilePic = (NetworkImageView) convertView
                .findViewById(R.id.profilePic);
        TextView handle = (TextView) convertView.findViewById(R.id.handle);



        RelativeLayout btnHearts = (RelativeLayout) convertView.findViewById(R.id.addheart);

        final ImageView imglips = (ImageView) convertView.findViewById(R.id.imglips);

        final TextView heartcount = (TextView) convertView.findViewById(R.id.heartcount);
        final ImageView hearttype = (ImageView) convertView.findViewById(R.id.heart);
        final FeedImageView feedImageView = (FeedImageView) convertView
                .findViewById(R.id.feedImage1);
        final TextView plustext = (TextView) convertView.findViewById(R.id.heartext);

        final TextView commentsCount = (TextView) convertView.findViewById(R.id.commentscount);

        final ImageView comment = (ImageView) convertView.findViewById(R.id.comment);

        final FeedItem item = feedItems.get(position);

        shake = AnimationUtils.loadAnimation(convertView.getContext(), R.anim.shake);

        name.setText(item.getName());
        handle.setText(item.getHandle());
        res = convertView.getResources();
        // feedImageView.setOnTouchListener();

        timestamp.setText(item.getTimeStamp());//"21 May 2016");

        commentsCount.setText(item.getTotalComments());

        ///End Gestures

        // Chcek for empty status message
        if (!TextUtils.isEmpty(item.getStatus())) {
            statusMsg.setText(item.getStatus());
            statusMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            statusMsg.setVisibility(View.GONE);
        }
if (item.getUrl().toString()!="null") {
    url.setText(//Html.fromHtml("<a href=\"\">" +
             "@"+ item.getUrl() );//+ "</a> "));
    //url.setMovementMethod(LinkMovementMethod.getInstance());
    url.setVisibility(View.VISIBLE);
}
        else
{
    url.setVisibility(View.GONE);
}


        // Checking for null feed url
       /* if (item.getUrl() != "null") {
            url.setText(Html.fromHtml("<a href=\"\">"
                    + item.getUrl() +"</a> "));

            // Making url clickable
            url.setMovementMethod(LinkMovementMethod.getInstance());
            url.setVisibility(View.VISIBLE);
        } else {
            // url is null, remove from the view
            url.setVisibility(View.GONE);
        }*/


        // user profile pic

        profilePic.setImageUrl(item.getProfilePic(), imageLoader);


        // Feed image
        if (item.getImge() != null) {
            feedImageView.setImageUrl(item.getImge(), imageLoader);
            feedImageView.setVisibility(View.VISIBLE);
            feedImageView
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {

                            Log.i("Error Set:", item.getImge().toString());

                            feedImageView.setImageUrl("http://www.thekumpany.co.za/StylRHair/PotentialModels/beachAss.jpg", imageLoader);

                            // }
                        }

                        @Override
                        public void onSuccess() {
                            Log.i("Image Set", item.getImge().toString());
                        }
                    });
        } else {
            feedImageView.setVisibility(View.GONE);
        }

        if (prefs.getString(item.getName().toString() + "" + item.getStatus().toString() + "" + item.getItemId(), "Not").equalsIgnoreCase("Liked")) {
            drawnew = res.getDrawable(R.drawable.lip_full);
            hearttype.setImageDrawable(drawnew);
            imglips.setImageDrawable(drawnew);
            plustext.setText("-1");
        } else {
            drawnew = res.getDrawable(R.drawable.lip_empty);
            hearttype.setImageDrawable(drawnew);
            imglips.setImageDrawable(drawnew);
            plustext.setText("+1");
        }


        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vb.vibrate(45);
                Custom_Comments_Dialog_Fragment fragment = new Custom_Comments_Dialog_Fragment();
                add("ID" + item.getItemId());
                editor.putInt("ClickedID", item.getItemId());
                editor.commit();
                // pullStylists();
                fragment.show(activity.getFragmentManager(), "");


            }
        });

     url.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Toast.makeText(activity, "You clicked " + "@" + item.getUrl(), Toast.LENGTH_SHORT).show();
             //at this point you need to go to another activity passing the clicked handle.
             Intent intent = new Intent("link-stylist");
             // add data
             intent.putExtra("message", item.getUrl());
             editor.putString("CurrentStylist",item.getUrl()).commit();//store the StylistID
             LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);



         }
     });
        feedImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (firstTap) {
                    thisTime = SystemClock.uptimeMillis();
                    firstTap = false;
                } else {
                    prevTime = thisTime;
                    thisTime = SystemClock.uptimeMillis();

                    //Check that thisTime is greater than prevTime
                    //just incase system clock reset to zero
                    if (thisTime > prevTime) {
                        //Check if times are within our max delay
                        if ((thisTime - prevTime) <= DOUBLE_PRESS_INTERVAL) {
                            //We have detected a double tap!
                            vb.vibrate(45);
                            feedImageView.setAnimation(shake);
                            //  add("DOUBLE TAP DETECTED!!!" + "X-Axiz " + event.getX() + "Y-Axiz " + event.getY());;//, Toast.LENGTH_LONG).show();
                            //PUT YOUR LOGIC HERE!!!!
                            Integer a, b, c;
                            a = Integer.parseInt(heartcount.getText().toString());
                            b = a + 1;
                            c = -1;

                            res = v.getResources();

                            drawnew = res.getDrawable(R.drawable.lip_full);
                            drawcurrent = hearttype.getDrawable();
                            if (prefs.getString(item.getName().toString() + "" + item.getStatus().toString() + "" + item.getItemId(), "Not").equalsIgnoreCase("Liked")) {
                                //user already liked the image,  now unlike the image and reduce count
                                //its already liked, change to unlike
                                drawnew = res.getDrawable(R.drawable.lip_empty);


                                editor.putString(item.getName().toString() + "" + item.getStatus().toString() + "" + item.getItemId(), "Not Liked");
                                editor.commit();
                                b = a - 1;
                                c = 1;
                                plustext.setText("+1");

                            } else {
                                drawnew = res.getDrawable(R.drawable.lip_full);
                                editor.putString(item.getName().toString() + "" + item.getStatus().toString() + "" + item.getItemId(), "Liked");

                                editor.commit();
                                plustext.setText("-1");
                            }
                            hearttype.setImageDrawable(drawnew);
                            imglips.setImageDrawable(drawnew);

                            heartcount.setText(b.toString());
                        } else {
                            //Otherwise Reset firstTap
                            firstTap = true;
                        }
                    } else {
                        firstTap = true;
                    }
                }
                return false;
            }

        });


        imglips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer a, b, c;
                vb.vibrate(45);
                a = Integer.parseInt(heartcount.getText().toString());
                b = a + 1;
                c = -1;


                res = v.getResources();

                drawnew = res.getDrawable(R.drawable.lip_full);
                drawcurrent = imglips.getDrawable();


                if (prefs.getString(item.getName().toString() + "" + item.getStatus().toString() + "" + item.getItemId(), "Not").equalsIgnoreCase("Liked")) {
                    //user already liked the image,  now unlike the image and reduce count
                    //its alrGeady liked, change to unlike
                    drawnew = res.getDrawable(R.drawable.lip_empty);
                    editor.putString(item.getName().toString() + "" + item.getStatus().toString() + "" + item.getItemId(), "Not Liked");
                    editor.commit();
                    b = a - 1;
                    c = 1;
                    plustext.setText("+1");

                } else {
                    drawnew = res.getDrawable(R.drawable.lip_full);
                    editor.putString(item.getName().toString() + "" + item.getStatus().toString() + "" + item.getItemId(), "Liked");

                    editor.commit();
                    plustext.setText("-1");

                }


                imglips.setImageDrawable(drawnew);
                hearttype.setImageDrawable(drawnew);
                heartcount.setText(b.toString());

                // Log.i("Hello", "Yeah " + item.getStatus().toString());


                add("ID " + item.getItemId());
            }
        });

        hearttype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer a, b, c;
                vb.vibrate(45);
                a = Integer.parseInt(heartcount.getText().toString());
                b = a + 1;
                c = -1;

                //findDoubleClick();
                //if (mHasDoubleClicked) {

                //add("Yey mate, double click works:hover");

                //} else {

                res = v.getResources();

                drawnew = res.getDrawable(R.drawable.lip_full);
                drawcurrent = hearttype.getDrawable();


                if (prefs.getString(item.getName().toString() + "" + item.getStatus().toString() + "" + item.getItemId(), "Not").equalsIgnoreCase("Liked")) {
                    //user already liked the image,  now unlike the image and reduce count
                    //its alrGeady liked, change to unlike
                    drawnew = res.getDrawable(R.drawable.lip_empty);
                    editor.putString(item.getName().toString() + "" + item.getStatus().toString() + "" + item.getItemId(), "Not Liked");
                    editor.commit();
                    b = a - 1;
                    c = 1;
                    plustext.setText("+1");

                } else {
                    drawnew = res.getDrawable(R.drawable.lip_full);
                    editor.putString(item.getName().toString() + "" + item.getStatus().toString() + "" + item.getItemId(), "Liked");

                    editor.commit();
                    plustext.setText("-1");

                }
                hearttype.setImageDrawable(drawnew);
                imglips.setImageDrawable(drawnew);
                heartcount.setText(b.toString());


                // Log.i("Hello", "Yeah " + item.getStatus().toString());
                add("ID " + item.getItemId());
            }
            //}
        });

        return convertView;
    }

    public void add(String message) {
        Toast.makeText(this.activity.getApplicationContext(), "Button Clicked :" + message.toString(), Toast.LENGTH_LONG).show();
    }

    private boolean findDoubleClick() {
        // Get current time in nano seconds.
        long pressTime = System.currentTimeMillis();
        // If double click...
        if (pressTime - lastPressTime <= DOUBLE_PRESS_INTERVAL) {
            mHasDoubleClicked = true;

            // double click event....
        } else { // If not double click....
            mHasDoubleClicked = false;
            Handler myHandler = new Handler() {
                public void handleMessage(Message m) {

                    if (!mHasDoubleClicked) {
                        // single click event


                    }
                }
            };
            Message m = new Message();
            myHandler.sendMessageDelayed(m, DOUBLE_PRESS_INTERVAL);
        }
        lastPressTime = pressTime;
        return mHasDoubleClicked;
    }

    public boolean onTouchEvent(MotionEvent me) {
        return gestureScanner.onTouchEvent(me);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        add("-" + "DOWN" + "-");
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        add("-" + "FLING" + "-");
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        add("-" + "LONG PRESS" + "-");
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        add("-" + "SCROLL" + "-");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        add("-" + "SHOW PRESS" + "-");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        add("-" + "SINGLE TAP UP" + "-");
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        add("-" + "DOUBLE TAP" + "-");
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        add("-" + "DOUBLE TAP EVENT" + "-");
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        add("-" + "SINGLE TAP CONFORMED" + "-");
        return true;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }


}


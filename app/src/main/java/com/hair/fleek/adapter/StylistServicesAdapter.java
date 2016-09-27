package com.hair.fleek.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.hair.fleek.learningcurve.R;
import java.util.List;
import app.AppController;
import data.LinkEnabledTextView;
import data.StylistServices;

/**
 * Created by Obakeng Moshane on 2016-06-04.
 */

public class StylistServicesAdapter extends BaseAdapter  {
    private Activity activity;
    private LayoutInflater inflater;
    private List<StylistServices> serviceItems;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private LinkEnabledTextView check;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    Resources res ;

    Vibrator vb;
    interface onSubmitListener {
        void setOnSubmitListener(String arg);
    }
    public StylistServicesAdapter(Activity activity, List<StylistServices> serviceItems) {
        this.activity = activity;
        this.serviceItems = serviceItems;
    }

    @Override
    public int getCount() {
        return serviceItems.size();
    }

    @Override
    public Object getItem(int location) {
        return serviceItems.get(location);
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
            convertView = inflater.inflate(R.layout.stylist_services_view, parent, false);
        }
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();


        vb = (Vibrator)convertView.getContext().getSystemService(convertView.getContext().VIBRATOR_SERVICE);
        TextView servcicename = (TextView) convertView.findViewById(R.id.style_name);

        TextView servicedescription = (TextView) convertView
                .findViewById(R.id.style_description);

        NetworkImageView serviceimage = (NetworkImageView) convertView
                .findViewById(R.id.style_view_image);




        final StylistServices item = serviceItems.get(position);


        servcicename.setText(item.getservicename());
        servicedescription.setText(item.getServicedescription());
        res= convertView.getResources();
        // feedImageView.setOnTouchListener();

       /* draw  = res.getDrawable(R.drawable.ic_favorite_border_black_18dp);
        hearttype.setImageDrawable(draw);*/
        // Converting timestamp into x ago format
//----------------------------------------------------------------------------------->

        ///End Gestures




        // user profile p
       serviceimage.setImageUrl(item.getServiceimage(), imageLoader);
     //  new DownloadImageTask(profilePic)
      //          .execute(item.getProfilePic());//"http://thekumpany.co.za/StylRHair/PotentialModels/fleek_logo.jpg");//item.getProfilePic());

//http://dummyimage.com/300x300/ec2f1a/ffffff&text=A

        return convertView;
    }
    public void add(String message)
    {
        Toast.makeText(this.activity.getApplicationContext(),"Button Clicked :" + message.toString(),Toast.LENGTH_LONG).show();
    }





}
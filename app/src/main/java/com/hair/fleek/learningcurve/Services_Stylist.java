package com.hair.fleek.learningcurve;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.hair.fleek.adapter.FeedCommentAdapter;
import com.hair.fleek.adapter.StylistServicesAdapter;

import java.util.ArrayList;
import java.util.List;

import data.CommentFeed;
import data.StylistServices;

public class Services_Stylist extends AppCompatActivity {
ListView serviceslistview;
    private StylistServicesAdapter serviceslistAdapter;
    private List<StylistServices> servicesItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_services);
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); */
        serviceslistview = (ListView)findViewById(R.id.services_list);
        servicesItems = new ArrayList<StylistServices>();
        serviceslistAdapter = new StylistServicesAdapter(this, servicesItems);
        serviceslistview.setAdapter(serviceslistAdapter);
        serviceslistview.setClickable(true);
        serviceslistview.setAdapter(serviceslistAdapter);
        StylistServices services_det = new StylistServices();

        services_det.setServiceimage("http://thekumpany.co.za/pictures/Twist/Twist-Flat-Bump.jpg");
        services_det.setServicename("Nails to do");
        services_det.setServicedescription("Yes i can work my magic on your Nails");


        StylistServices services_det2 = new StylistServices();
        services_det2.setServiceimage("http://thekumpany.co.za/pictures/Twist/Twist-Flat-Bump.jpg");
        services_det2.setServicename("Virgin Weave");
        services_det2.setServicedescription("Yes i can work my magic on your hair");
        servicesItems.add(services_det2);
        servicesItems.add(services_det);


        serviceslistAdapter.notifyDataSetChanged();
    }

}

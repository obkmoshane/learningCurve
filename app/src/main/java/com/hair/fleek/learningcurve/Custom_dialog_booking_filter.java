package com.hair.fleek.learningcurve;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import app.NothingSelectedSpinnerAdapter;

/**
 * Created by Obakeng Moshane on 2016-03-03.
 */
public class Custom_dialog_booking_filter extends DialogFragment {
    onSubmitListener mListener;

 Spinner spinner,spinnerf;
    interface onSubmitListener {
        void setOnSubmitListener(String arg);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.custom_dialog_filter_booking);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
       ImageButton mCancel = (ImageButton)dialog.findViewById(R.id.booking_cancel);
       ImageButton mProceed = (ImageButton)dialog.findViewById(R.id.booking_submit);
       spinner = (Spinner)dialog.findViewById(R.id.hairornail);
        spinnerf = (Spinner)dialog.findViewById(R.id.styleslistview);
spinner.setPrompt("Select Category");
        spinnerf.setPrompt("Waiting for Category");


        // Spinner Drop down elements
        List<String> options = new ArrayList<String>();
        options.add("Hair");
        options.add("Nails");


        // Creating com.hair.fleek.learningcurve.adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, options);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data com.hair.fleek.learningcurve.adapter to spinner
     //   spinner.setAdapter(dataAdapter);

        spinner.setAdapter(new NothingSelectedSpinnerAdapter(dataAdapter,
                R.layout.contact_spinner_row_nothing_selected,this.getActivity()));

       spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
/*               if (spinner.getSelectedItem().toString().equals("Hair"))
               {
                   HairOptions();
               }
               else if(spinner.getSelectedItem().toString().equals("Nails"))
               {
                   NailOptions();
               }*/
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });
      /*  mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
    return dialog;

    }

    public void HairOptions()
    {
        List<String> options = new ArrayList<String>();
        options.add("HairStyle 1");
        options.add("HairStyle 2");


        // Creating com.hair.fleek.learningcurve.adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, options);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data com.hair.fleek.learningcurve.adapter to spinner
        spinnerf.setAdapter(dataAdapter);

    }
    public void NailOptions()
    {
        List<String> options = new ArrayList<String>();
        options.add("NailStyle 1");
        options.add("NailStyle 2");


        // Creating com.hair.fleek.learningcurve.adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, options);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data com.hair.fleek.learningcurve.adapter to spinner
        spinnerf.setAdapter(dataAdapter);
    }
}

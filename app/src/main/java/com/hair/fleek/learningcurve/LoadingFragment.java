package com.hair.fleek.learningcurve;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Obakeng Moshane on 2016-03-12.
 */

public class LoadingFragment extends DialogFragment {
String mtext = "";
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        StrictMode.enableDefaults();
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.custom_loading_dialog);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        TextView mtext = (TextView)dialog.findViewById(R.id.loadingtext);
        mtext.setText(mtext.toString());
        return dialog;
    }
}

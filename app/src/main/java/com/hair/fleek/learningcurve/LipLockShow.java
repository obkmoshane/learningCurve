package com.hair.fleek.learningcurve;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Obakeng Moshane on 2016-03-27.
 */
public class LipLockShow extends DialogFragment {

    interface onSubmitListener {
        void setOnSubmitListener(String arg);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        StrictMode.enableDefaults();
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.lips_layout_cliked);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        return  dialog;

    }
}

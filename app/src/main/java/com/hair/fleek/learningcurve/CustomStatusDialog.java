package com.hair.fleek.learningcurve;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Obakeng Moshane on 2016-02-14.
 */
public class CustomStatusDialog extends DialogFragment{



    Button mButton;
    EditText mEditText;
    onSubmitListener mListener;
    String text = "";
    String texttitle="";
    TextView mtextView;

    interface onSubmitListener {
        void setOnSubmitListener(String arg);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        mButton = (Button) dialog.findViewById(R.id.button1);
        mEditText = (EditText) dialog.findViewById(R.id.editText1);
        mtextView = (TextView)dialog.findViewById(R.id.dialogtitle);
        mEditText.setText(text);
        mtextView.setText(texttitle);
        mButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mListener.setOnSubmitListener(mEditText.getText().toString());
                   /* dismiss();*/
                dialog.dismiss();
            }
        });
        return dialog;
    }

}

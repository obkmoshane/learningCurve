package com.hair.fleek.learningcurve;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class Splash_Screen extends Activity {
    private Thread mSplashThread;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash__screen);
        Animation slides1 = AnimationUtils.loadAnimation(this,R.anim.slide_out_left);
        Animation slide2 = AnimationUtils.loadAnimation(this,R.anim.slide_in_right);
        prefs= PreferenceManager.getDefaultSharedPreferences(Splash_Screen.this);
        editor = prefs.edit();

        final Splash_Screen sPlashScreen = this;
        // The thread to wait for splash screen events

        mSplashThread =  new Thread(){
            @Override
            @Deprecated
            public void run(){
                try {
                    synchronized(this){
                        // Wait given period of time or exit on touch
                        wait(5000);
                    }
                }
                catch(InterruptedException ex){
                }

               finish();

                // Run next activity

                if (prefs.getString("StylRRegistered","False").equals("False"))
                {
                    Intent intent = new Intent();
                    intent.setClass(sPlashScreen, Registration.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setClass(sPlashScreen, Login.class);
                    startActivity(intent);
                }
               // LoadingFragment fragment1 = new LoadingFragment();
               // fragment1.mListener = Splash_Screen.this;
               // fragment1.text = "";// mTextView.getText().toString();
               // fragment1.texttitle = "Enter Code";
                //fragment1.show(getFragmentManager(), "");

                //stop();
            }
        };

        mSplashThread.start();
    }

    /**
     * Processes splash screen touch events
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent evt)
    {
        if(evt.getAction() == MotionEvent.ACTION_DOWN)
        {
            synchronized(mSplashThread){
                mSplashThread.notifyAll();
            }
        }
        return true;

}

}


package data;

/**
 * Created by Obakeng Moshane on 2016-03-09.
 */

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class InternetConnection {

    public boolean hasActiveInternetConnection(Context context)
    {
        if(isNetworkAvailable(context))
        {
          //  if(connectGoogle())
           // {
                return true;
           // }
          //  else
           // {
          //      return connectGoogle();
        //    }
        }
        else
        {
            Log.i("Internet Connection", "No Active Internet Connection");
            return false;

        }

    }

    public boolean isNetworkAvailable(Context ct)
    {
        ConnectivityManager connectivitymanager = (ConnectivityManager)ct.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivitymanager != null) {
            NetworkInfo netInfo = connectivitymanager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }

      //  NetworkInfo activeNetworkInfo = connectivitymanager.getActiveNetworkInfo();
        return false;//activeNetworkInfo!=null;
    }

    public static boolean connectGoogle()
    {
        try
        {
            HttpURLConnection urlc = (HttpURLConnection)(new URL("http://www.google.com").openConnection());
            urlc.setRequestProperty("User-Agent", "Test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(10000);
            urlc.connect();
            return (urlc.getResponseCode()==200);
        }
        catch(IOException e)
        {
            Log.i("IOException", "IOException Connecting to Google");
            return false;
        }
    }

}
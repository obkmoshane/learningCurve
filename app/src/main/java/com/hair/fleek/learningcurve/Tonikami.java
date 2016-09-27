package com.hair.fleek.learningcurve;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpConnection;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Tonikami extends AppCompatActivity implements View.OnClickListener {
    private static final int RESULT_LOAD_IMAGE=1;
    private static final String SERVER_ADDRESS = "http://tonikamitv.hostei.com/";
    ImageView imageToUpload,downloadedImage;
    Button uploadimage,downloadImage;
    EditText uploadimagename,downloadimagename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tonikami);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        imageToUpload = (ImageView)findViewById(R.id.imageToUpload);
        downloadedImage = (ImageView)findViewById(R.id.downloadimage);

        uploadimage = (Button)findViewById(R.id.buploadimage);
        downloadImage = (Button)findViewById(R.id.bdownloadimage);

        uploadimagename = (EditText)findViewById(R.id.etuploadname);
        downloadimagename = (EditText)findViewById(R.id.etdownloadname);

        imageToUpload.setOnClickListener(this);
        uploadimage.setOnClickListener(this);
        downloadImage.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode==RESULT_OK && data!=null)
        {
            Uri selectedImage = data.getData();
            imageToUpload.setImageURI(selectedImage);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.imageToUpload:
                Intent  galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);
                break;
            case R.id.buploadimage:
                Bitmap image  = ((BitmapDrawable)imageToUpload.getDrawable()).getBitmap();
                new UploadImage(image,uploadimagename.getText().toString());
                break;
            case R.id.bdownloadimage:
                break;
        }
    }
    private class UploadImage extends   AsyncTask<Void,Void,Void>
    {
        Bitmap image;
        String name;

        public UploadImage(Bitmap image,String name)
        {
            this.image = image;
            this.name = name;

        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(),"Image Uploaded",Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ByteArrayOutputStream  byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            String encodedimage  = Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT);

            ArrayList<NameValuePair> dataTosend = new ArrayList<>();
            dataTosend.add(new BasicNameValuePair("image",encodedimage));
            dataTosend.add(new BasicNameValuePair("name", name));

            HttpParams httpRequestParams  = getHttpRequestParams();

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "SavePicture.php");

            try
            {
                post.setEntity(new UrlEncodedFormEntity(dataTosend));
                client.execute(post);

            }catch(Exception e)
            {
                e.printStackTrace();
            }
            return null;


        }

    }
    private HttpParams getHttpRequestParams()
    {
        HttpParams httpRequestParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpRequestParams, 1000 * 30);
        HttpConnectionParams.setSoTimeout(httpRequestParams,1000*30);
        return httpRequestParams;
    }
    private class DownloadImage extends AsyncTask<Void,Void,Bitmap>
    {
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            return null;
        }
    }
}

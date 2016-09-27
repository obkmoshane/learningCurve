package com.hair.fleek.learningcurve;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Obakeng Moshane on 2016-03-04.
 */
public class custom_profile_crop  extends DialogFragment   {
    onSubmitListener mListener;
    final int CAMERA_CAPTURE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    String updatedImage;
    static final int PIC_CROP = 2;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static int RESULT_LOAD_IMAGE = 1;
    String CameraImageName;
    String username;
    boolean uploadstatus = false;
    private static final String SERVER_ADDRESS = "http://thekumpany.co.za/";
    ImageView img;
     Dialog dialog;
     Uri fileUri;
        private static final String IMAGE_DIRECTORY_NAME = "Fleek_profiles";
    //captured picture uri
    public Uri picUri;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    interface onSubmitListener {
        void setOnSubmitListener(String arg);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.custom_profile_pic_crop_upload);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        Button capture = (Button)dialog.findViewById(R.id.capture_btn);
        Button gallery = (Button)dialog.findViewById(R.id.gallery_btn);
        Button update = (Button)dialog.findViewById(R.id.upload_btn);
        img = (ImageView)dialog.findViewById(R.id.picture);
        username = "obiewan";
        prefs =  PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = prefs.edit();

        capture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    captureImage();
                } catch (ActivityNotFoundException ex) {
                    String errorMessage = "Whoops - your device doesn't support capturing images!";
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        gallery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery();
            }
        });

        update.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(getActivity().getApplicationContext(),"Dr here1",Toast.LENGTH_LONG).show();
                    Bitmap image = ((BitmapDrawable)img.getDrawable()).getBitmap(); //get the drawable bitmap of the image
                    new UploadImage(image, prefs.getString("username","").toString()).execute();
                }
                catch(Exception e)
                {
                    Log.i("ImageUpload","Exception on Image upload " + e.getMessage());
                    Toast.makeText(getActivity().getApplicationContext(), "Error on the Image Upload, Please try again", Toast.LENGTH_SHORT).show();

                }
            }
        });


        return dialog;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {

                performCrop();
            } else if (requestCode == 2) {
                //get the returned data
               // Toast.makeText(getActivity().getApplicationContext(), "We are here because you believe", Toast.LENGTH_LONG).show();
                Bundle extras = data.getExtras();
                //get the cropped bitmap
                Bitmap thePic = extras.getParcelable("data");
                //retrieve a reference to the ImageView
                ImageView picView = (ImageView) dialog.findViewById(R.id.picture);
                //display the returned cropped image
                picView.setImageBitmap(thePic);

                CameraImageName = fileUri.toString().substring(fileUri.toString().lastIndexOf("/") + 1);
               //Toast.makeText(getActivity().getApplicationContext(), "There :" + CameraImageName.toString(), Toast.LENGTH_LONG).show();
            } else if (requestCode == RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK)  //&& data !=null)
            {
                // Uri selectedImage = data.getData();
                fileUri = data.getData();

                performCrop();


            }
        }
        else if(resultCode==getActivity().RESULT_CANCELED)
        {

        }
        super.onActivityResult(requestCode, resultCode, data);
        // if the result is capturing Image


    }
    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, 100);
    }
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }


    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }


    public void performCrop()
    {
        try
        {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(fileUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
          //  Toast.makeText(getActivity().getApplicationContext(),"We are here now",Toast.LENGTH_LONG).show();
            startActivityForResult(cropIntent, PIC_CROP);


            //get the cropped bitmap
//            Intent data = cropIntent;
//            Bundle extras = data.getExtras();
//            //retrieve a reference to the ImageView
//            ImageView picView = (ImageView)dialog.findViewById(R.id.picture);
//            Bitmap thePic = extras.getParcelable("data");
//            picView.setImageBitmap(thePic);
//            //display the returned cropped image
////            picView.setImageDrawable(getActivity().getDrawable(R.drawable.ic_favorite_border_black_18dp));
        }



        catch(ActivityNotFoundException ex)
        {

        }
    }
public void gallery()
{
    Intent galleryIntent  = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
    galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
    startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);
}
    private class UploadImage extends AsyncTask<Void, Void,Void> {
        Bitmap image;
        String name;
        public UploadImage(Bitmap image,String name)
        {
            this.image = image;
            this.name = name;
        }
        @Override
        protected Void doInBackground(Void... params) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("image",encodedImage));
            dataToSend.add(new BasicNameValuePair("name",prefs.getString("username","").toString() + "_"+"dpic" ));
            dataToSend.add(new BasicNameValuePair("userID",prefs.getString("username","").toString()));

            HttpParams httpRequstParams = getHttpRequestParams();

            HttpClient client = new DefaultHttpClient(httpRequstParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "UpdateProfilePic.php");
            try
            {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);
                uploadstatus = true;
                /*
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                final String response = client.execute(post, responseHandler);

                System.out.println("Response : " + response.toString());
                Log.i("Response",response.toString());*/
            }catch(Exception e)
            {
                uploadstatus = false;
                Toast.makeText(getActivity().getApplicationContext(),"Error dude",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            if (uploadstatus) {
                Toast.makeText(getActivity().getApplicationContext(), "Image Uploaded " + name, Toast.LENGTH_LONG).show();
                mListener.setOnSubmitListener(prefs.getString("username","").toString()+"_dpic");
            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(),"Couldn't upload your profile pic",Toast.LENGTH_LONG).show();
            }
        }
    }
    private HttpParams getHttpRequestParams()
    {
        HttpParams httprequestParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httprequestParams, 1000 * 30);
        HttpConnectionParams.setSoTimeout(httprequestParams,1000*30);
        return httprequestParams;

    }



}

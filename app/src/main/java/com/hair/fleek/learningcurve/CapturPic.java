package com.hair.fleek.learningcurve;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.linkedin.android.spyglass.suggestions.SuggestionsResult;
import com.linkedin.android.spyglass.suggestions.interfaces.Suggestible;
import com.linkedin.android.spyglass.suggestions.interfaces.SuggestionsResultListener;
import com.linkedin.android.spyglass.suggestions.interfaces.SuggestionsVisibilityManager;
import com.linkedin.android.spyglass.tokenization.QueryToken;
import com.linkedin.android.spyglass.tokenization.impl.WordTokenizer;
import com.linkedin.android.spyglass.tokenization.interfaces.QueryTokenReceiver;
import com.linkedin.android.spyglass.ui.MentionsEditText;
import com.linkedin.android.spyglass.ui.RichEditorView;
import com.linkedin.android.spyglass.ui.wrappers.RichEditorFragment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.AndroidMultiPartEntity;
import data.DownloadImageTask;
import data.models.Person;

public class CapturPic extends AppCompatActivity implements CustomStatusDialog.onSubmitListener,
        QueryTokenReceiver,
        SuggestionsResultListener, SuggestionsVisibilityManager {
    public Intent done;
    public Vibrator vb;
    public String url, statusaccount;
    public String username, firstname, lastname, profilepic, confirmationcode, confirmed;
    final int CAMERA_CAPTURE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    String updatedImage;
    static final int PIC_CROP = 2;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static int RESULT_LOAD_IMAGE = 1;
    String CameraImageName;
    private static final String IMAGE_DIRECTORY_NAME = "Fleek";
    onSubmitListener mListener;
    boolean uploadstatus = false;
    String messageupload;
    private static final String SERVER_ADDRESS = "http://thekumpany.co.za/";


    private static final String BUCKET = "people-network";

    private RecyclerView recyclerView;
    // private MentionsEditText editor;
    private PersonMentionAdapter adapter;
    private Person.PersonLoader people;




    Dialog dialog;
    Uri fileUri;
    private HttpResponse response;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    RelativeLayout cameralayout,uploadlayout,gallerylayout,closelayout;
    ImageView img;
    FloatingActionButton options,close,gallery,camera,upload;
    MentionsEditText mentioneditor;
    RichEditorView uploadMessage;
    ImageButton sendToUpload;

    long totalSize = 0;
    String responseString;

    interface onSubmitListener {
        void setOnSubmitListener(String arg);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captur_pic);
        StrictMode.enableDefaults();

        prefs =  PreferenceManager.getDefaultSharedPreferences(CapturPic.this);
        editor = prefs.edit();

        options = (FloatingActionButton)findViewById(R.id.fab);
        cameralayout = (RelativeLayout)findViewById(R.id.cameralayout);
        uploadlayout = (RelativeLayout)findViewById(R.id.uploadlayout);
        gallerylayout = (RelativeLayout)findViewById(R.id.gallerylayout);
        closelayout = (RelativeLayout)findViewById(R.id.closelayout);
        img = (ImageView)findViewById(R.id.imageView);
        close = (FloatingActionButton)findViewById(R.id.close);
        gallery = (FloatingActionButton)findViewById(R.id.gallery);
        camera = (FloatingActionButton)findViewById(R.id.camera);
        upload = (FloatingActionButton)findViewById(R.id.upload);
        mentioneditor =(MentionsEditText)findViewById(R.id.uploadMention);
        sendToUpload = (ImageButton)findViewById(R.id.sendPicStatus);
        //progressB = (ProgressBar)findViewById(R.id.progressBar);
       // txtprogress = (TextView)findViewById(R.id.progressText)
       // ;
        recyclerView = (RecyclerView)findViewById(R.id.mentions_grid_pic);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //specify total columns in Grid
        //   recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new PersonMentionAdapter(new ArrayList<Person>());
        recyclerView.setAdapter(adapter);
        mentioneditor.setTokenizer(new WordTokenizer());
        mentioneditor.setQueryTokenReceiver(this);
        mentioneditor.setSuggestionsVisibilityManager(this);
        mentioneditor.setHint("Post Status [mention your Stylist @]");
        people = new Person.PersonLoader(getResources());

        Toast.makeText(getApplicationContext(),"Testing : "+ prefs.getString("username","").toString(),Toast.LENGTH_LONG).show();
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TurnUpTime("show");//chose to display all relavent action buttons
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TurnUpTime("hide"); //chose to hide all irrelavent action buttons
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery();

            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Upload Time",Toast.LENGTH_LONG).show();
                CustomStatusDialog fragment1 = new CustomStatusDialog();
                fragment1.mListener = CapturPic.this;
                fragment1.texttitle="Add Message?";
                fragment1.show(getFragmentManager(), "");
            }
        });


        sendToUpload.setOnClickListener(new View.OnClickListener()
        {
            @Override
        public void onClick(View v)
            {
                messageupload = mentioneditor.getText().toString();
                Bitmap image  = ((BitmapDrawable)img.getDrawable()).getBitmap();
                new UploadImage(image,prefs.getString("username","").toString() + "_" +CameraImageName.toString().substring(0,
                        CameraImageName.toString().lastIndexOf(".")).toString()).execute();
            }
        });

    }
    @Override
    public void setOnSubmitListener(String arg) {
        // Toast.makeText(getApplicationContext(),"We calling this at theh end " + arg.toString(),Toast.LENGTH_LONG).show();
        //new DownloadImageTask(user_picture).execute("http://thekumpany.co.za/StylRHair/ProfilePictures/"+ arg.toString() +".JPG");
        messageupload =  arg.toString();
        Bitmap image  = ((BitmapDrawable)img.getDrawable()).getBitmap();
        new UploadImage(image,prefs.getString("username","").toString() + "_" +CameraImageName.toString().substring(0,
                CameraImageName.toString().lastIndexOf(".")).toString()).execute();
       }
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view

                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data !=null)
        {
            Uri selectedImage = data.getData();

            performCrop();

            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            CameraImageName = picturePath.toString().substring(picturePath.toString().lastIndexOf("/") + 1);
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            Toast.makeText(getApplicationContext(),"->"+ CameraImageName.toString().substring(0,
                    CameraImageName.toString().lastIndexOf(".")).toString(),Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    private void previewCapturedImage() {
        try {
            // hide video preview
          //  videoPreview.setVisibility(View.GONE);

            img.setVisibility(View.VISIBLE);

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;
            performCrop();
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);

            img.setImageBitmap(bitmap);
            CameraImageName = fileUri.toString().substring(fileUri.toString().lastIndexOf("/") + 1);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void TurnUpTime(String option)
    {
        if (option=="show")
        {
            options.setVisibility(View.GONE);
            cameralayout.setVisibility(View.VISIBLE);
            uploadlayout.setVisibility(View.VISIBLE);
            gallerylayout.setVisibility(View.VISIBLE);
            closelayout.setVisibility(View.VISIBLE);
        }
        else  if (option=="hide")
        {
            options.setVisibility(View.VISIBLE);
            cameralayout.setVisibility(View.GONE);
            uploadlayout.setVisibility(View.GONE);
            gallerylayout.setVisibility(View.GONE);
            closelayout.setVisibility(View.GONE);
        }
    }
    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    public void gallery()
    {
        Intent galleryIntent  = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
     //   fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
      //  galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);
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

       }



        catch(ActivityNotFoundException ex)
        {

        }
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


    private class UploadImage extends AsyncTask<Void, Integer,Void> {
        Bitmap image;
        String name;
        public UploadImage(Bitmap image,String name)
        {
            this.image = image;
            this.name = name;
        }
        @Override
        protected  void onPreExecute()
        {
            //progressB.setProgress(0);
            super.onPreExecute();
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
         //   progressB.setVisibility(View.VISIBLE);

            // updating progress bar value
          //  progressB.setProgress(progress[0]);

            // updating percentage value
          //  txtprogress.setText(String.valueOf(progress[0]) + "%");
        }
        @Override
        protected Void doInBackground(Void... params) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("image",encodedImage));
            dataToSend.add(new BasicNameValuePair("name",name));
            dataToSend.add(new BasicNameValuePair("userID",prefs.getString("username","").toString()));
            dataToSend.add(new BasicNameValuePair("Status",messageupload.toString()));
            HttpParams httpRequstParams = getHttpRequestParams();

            HttpClient client = new DefaultHttpClient(httpRequstParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "SavePicture.php");
            try
            {

                   /* AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                            new AndroidMultiPartEntity.ProgressListener() {

                                @Override
                                public void transferred(long num) {
                                    publishProgress((int) ((num / (float) totalSize) * 100));
                                }
                            });
                    totalSize = entity.getContentLength();*/

                post.setEntity(new UrlEncodedFormEntity(dataToSend));
               client.execute(post);
                uploadstatus = true;
              /*  HttpEntity my_entity = uploadresponse.getEntity();
                int statuscode = uploadresponse.getStatusLine().getStatusCode();
                if (statuscode==200) {     //server response
                    responseString = uploadresponse.getEntity().toString();


                }
                else
                {
                    responseString = "Error Occured!Http Status Code: " + statuscode;
                    uploadstatus= false;
                }*/
               // Toast.makeText(getApplicationContext(),"Hey",Toast.LENGTH_SHORT).show();
                }catch(Exception e)
            {
                uploadstatus = false;
              //  Toast.makeText(getApplicationContext(),"Error dude",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            //Toast.makeText(getApplicationContext(),"Hey2",Toast.LENGTH_SHORT).show();
            if (uploadstatus) {
                Toast.makeText(getApplicationContext(), "Image Uploaded " + name, Toast.LENGTH_LONG).show();
             //   mListener.setOnSubmitListener(username);
            //    showAlert(responseString);
        }
            else
            {
                Toast.makeText(getApplicationContext(),"Couldn't upload your Image,please try again",Toast.LENGTH_LONG).show();
             //   showAlert(responseString);
            }
        }
    }
    private HttpParams getHttpRequestParams()
    {
        HttpParams httprequestParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httprequestParams, 1000 * 30);
        HttpConnectionParams.setSoTimeout(httprequestParams, 1000 * 30);
        return httprequestParams;

    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }




    @Override
    public List<String> onQueryReceived(final @NonNull QueryToken queryToken) {
        List<String> buckets = Arrays.asList(BUCKET);
        List<Person> suggestions = people.getSuggestions(queryToken);
        SuggestionsResult result = new SuggestionsResult(queryToken, suggestions);
        // Have suggestions, now call the listener (which is this activity)
        onReceiveSuggestionsResult(result, BUCKET);
        return buckets;
    }

    // --------------------------------------------------
    // SuggestionsResultListener Implementation
    // --------------------------------------------------

    @Override
    public void onReceiveSuggestionsResult(@NonNull SuggestionsResult result, @NonNull String bucket) {
        List<? extends Suggestible> suggestions = result.getSuggestions();
        adapter = new PersonMentionAdapter(result.getSuggestions());
        recyclerView.swapAdapter(adapter, true);
        boolean display = suggestions != null && suggestions.size() > 0;
        displaySuggestions(display);
    }

    // --------------------------------------------------
    // SuggestionsManager Implementation
    // --------------------------------------------------

    @Override
    public void displaySuggestions(boolean display) {
        if (display) {
            recyclerView.setVisibility(RecyclerView.VISIBLE);
            //  commentlistview.setVisibility(r.GONE);
        } else {
            recyclerView.setVisibility(RecyclerView.GONE);
            //  commentlistview.setVisibility(r.VISIBLE);
        }
    }

    @Override
    public boolean isDisplayingSuggestions() {
        return recyclerView.getVisibility() == RecyclerView.VISIBLE;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView picture;
        public TextView handle;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.person_name);
            picture = (ImageView) itemView.findViewById(R.id.person_image);
            handle = (TextView) itemView.findViewById(R.id.person_handle);
        }
    }

    private class PersonMentionAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<? extends Suggestible> suggestions = new ArrayList<>();

        public PersonMentionAdapter(List<? extends Suggestible> people) {
            suggestions = people;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_mention_item, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            Suggestible suggestion = suggestions.get(i);
            if (!(suggestion instanceof Person)) {
                return;
            }

            final Person person = (Person) suggestion;
            viewHolder.name.setText(person.getFullName());
            Glide.with(viewHolder.picture.getContext())
                    .load(person.getPictureURL())
                    .crossFade()
                    .into(viewHolder.picture);
            viewHolder.handle.setText(person.getHandle());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Clicked " + person.getHandle().toString(), Toast.LENGTH_LONG).show();
                    mentioneditor.insertMention(person);

                    recyclerView.swapAdapter(new PersonMentionAdapter(new ArrayList<Person>()), true);
                    displaySuggestions(false);
                    mentioneditor.requestFocus();
                }
            });
        }

        @Override
        public int getItemCount() {
            return suggestions.size();
        }

    }

}
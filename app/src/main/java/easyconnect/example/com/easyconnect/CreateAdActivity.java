package easyconnect.example.com.easyconnect;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;

import java.util.Random;

import android.net.NetworkInfo;
import android.net.ConnectivityManager;

import android.view.inputmethod.InputMethodManager;
import android.view.MotionEvent;

import android.widget.EditText;

public class CreateAdActivity extends AppCompatActivity implements View.OnClickListener{

    int isMyAd = 1;
    Button uploadImageButton;
    // this is the action code we use in our intent
    // this way we know we're looking at the response from our own action
    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    private String objectID;
    private ParseFile imageUploadFile;
    //This is the parseObject containing all relevant ad info stored in parse
    private ParseObject retrieveObject;
    //This is the image (bytes) contained in the above mentioned ParseObject
    private byte[] retrieveImage;
    DBHandler dbHandler;
    TextView fullName;
    TextView phoneNumber;
    TextView adTitle;
    TextView adDetails;
    TextView adImageUrl;
    ImageView adImage;
    TextView GameName;
    // the adImage ImageView in bytes
    byte[] image;
    public boolean done = false;
    char letter;
    SharedPreferences sharedPrefs;

    boolean redeemstat,tapstat,promotionExists=false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ad);



        dbHandler = new DBHandler(getBaseContext());

        FloatingActionButton createAdButton = (FloatingActionButton) findViewById(R.id.create_ad_button);
        createAdButton.setOnClickListener(this);
        fullName = (TextView) findViewById(R.id.fullName);
        //phoneNumber = (TextView) findViewById(R.id.phoneNumber);
        adTitle = (TextView) findViewById(R.id.adTitle);
        adDetails = (TextView) findViewById(R.id.adDetails);
        adImageUrl = (TextView) findViewById(R.id.adImageUrl);
        adImage = (ImageView)findViewById(R.id.adImage);
        // Locate the button in main.xml
        uploadImageButton = (Button) findViewById(R.id.uploadbtn);

        // Capture button clicks

        Intent intent = getIntent();
        ComponentName caller = getCallingActivity();

        //check the caller activity
      if(caller != null && caller.getClassName().compareTo("easyconnect.example.com.easyconnect.NfcTagReaderActivity") == 0)
    {

        long currenttime = System.currentTimeMillis();

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        long lasttaptime = sharedPrefs.getLong("lasttaptime", 0);

        if ((currenttime - lasttaptime) < 3600000) {
            new AlertDialog.Builder(this)
                    .setTitle("Tap Failed")
                    .setMessage("You need to wait an hour before you can try again!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(CreateAdActivity.this, ContactListActivity.class);

                            startActivity(intent);
                            finish();
                            return;
                        }
                    })

                    .setIcon(R.drawable.alert_icon)
                    .show();
       return; }
    else {
          SharedPreferences.Editor editor = sharedPrefs.edit();
          editor.putLong("lasttaptime", System.currentTimeMillis());


          editor.commit();
      }


            //Set global identifier to 0 i.e its not my ad
            isMyAd = 0;
            //[contact_name]|[phone_number]|[ad_title]|[ad_description]|[image_url]
            // No editing permission In the case of Extract from NFC
         letter = LetterGenerator(intent.getStringExtra("contact_name"));
        setTitle("You Got '" + Character.toString(letter) + "' !");


            fullName.setText(intent.getStringExtra("contact_name"));
     //   fullName.getBackground().clearColorFilter();
      //  fullName.setSelected(false);
        fullName.setEnabled(false);
            adTitle.setEnabled(false);
            adDetails.setEnabled(false);
            adImageUrl.setEnabled(false);


        createAdButton.setVisibility(View.GONE);


            //If we are using NFC the Image is getting loaded from parse. This function sets the retrieveParseObject and retrieveImage
            //In addition to initializing the view
         objectID = intent.getStringExtra("ad_objectID");

        RetrieveParseObjects(objectID, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {

                if (e == null) {
                    // object will be your imgupload ParseObject
                    retrieveObject = object;
                    //get records from parse
                    fullName.setText(retrieveObject.getString("Name"));




                    adDetails.setText(retrieveObject.getString("Details"));

                    // for now just hard code the default image url , otherwise picassa will crash
                    adImageUrl.setText(retrieveObject.getString("ImageUrl"));

                    // get the image bitmap from retrieveResult
                    ParseFile imageFile = (ParseFile) retrieveObject.get("ImageFile");





                    if(!promotionExists){
                        tapstat = true;
                        adTitle.setText(retrieveObject.getString("Title"));
                        final  String Name = fullName.getText().toString();
                        // String phone = phoneNumber.getText().toString();
                        final String Title = adTitle.getText().toString();
                        final   String Details = adDetails.getText().toString();
                        final String ImageUrl = adImageUrl.getText().toString();
                        final String GameName = "Lucky Letters!";


                        Cursor c;


                        dbHandler.open();


                        c = dbHandler.searchAdbyObj_ID(objectID);

                        c.moveToFirst();
                        Log.i("Loc", "collect ad: objectID=" + objectID);

                       final String firstLetter = Character.toString(letter);

                        imageFile.getDataInBackground(new GetDataCallback() {
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    // data has the bytes for the image
                                    retrieveImage = data;
                                    adImage.setImageBitmap(dbHandler.getImage(retrieveImage));
                                    final long rowID = dbHandler.insertAd(Title, Name, Details, ImageUrl, "N/A", 0, data, objectID, GameName, firstLetter);
                                    final  long adID = dbHandler.selectLastInsearted();
                                   dbHandler.close();
                                   // Toast.makeText(getApplicationContext(), "Inserted to AD_ID=" + adID, Toast.LENGTH_LONG).show();
                                    adTitle.setText(firstLetter);


                                } else {


                                }
                            }
                        });

                    }
                    else{  Cursor c;


                        dbHandler.open();


                        c = dbHandler.searchAdbyObj_ID(objectID);

                        c.moveToFirst();
                        byte[] image = c.getBlob(6);



                        if (image != null){
                            adImage.setImageBitmap(dbHandler.getImage(image));
                        }
                        dbHandler.close();


                    }




if (redeemstat == true){ retrieveObject.put("RedeemStat", retrieveObject.getInt("RedeemStat") + 1);
    retrieveObject.put("TapStat",retrieveObject.getInt("TapStat")+1);}
                    else
if(tapstat == true)
    retrieveObject.put("TapStat",retrieveObject.getInt("TapStat")+1);

                    retrieveObject.saveInBackground();
                    return;
                    // RetrieveParseObjects(objectID);

                } else {
                    return;

                    // something went wrong
                }
               }


        });

      promotionExists = updateMyWord();
        if(promotionExists == false){
           boolean isConnected = isNetworkAvailable();
        if(isConnected == false ){new AlertDialog.Builder(this)
                .setTitle("Tap Failed")
                .setMessage("You Need Internet Connection For This Game !")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(CreateAdActivity.this,ContactListActivity.class);

                        startActivity(intent);
                        finish();
                    }
                })

                .setIcon(R.drawable.alert_icon)
                .show();}
        }
        return;
    }




       else {
          uploadImageButton.setVisibility(View.VISIBLE);
            // User is creating this add.
            // Check ShredPreferenced and auto fill user information if available

            sharedPrefs = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            String firstName = sharedPrefs.getString("firstName", "");
            String lastName = sharedPrefs.getString("lastName", "");
            String UserPhoneNumber =  sharedPrefs.getString("phoneNumber", "");
            String UserFullName="";
            if (!firstName.isEmpty() || !lastName.isEmpty()){
                UserFullName = firstName + " " + lastName;
            }

            if(!UserFullName.isEmpty())
                fullName.setText(UserFullName);
            if(!UserPhoneNumber.isEmpty())
                phoneNumber.setText(UserPhoneNumber);
        }

        uploadImageButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                // in onCreate or any event where your want the user to
                // select a file
                //You can only Upload if you are creating not if you are extracting NFC info
                if (isMyAd == 1) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,
                            "Select Picture"), SELECT_PICTURE);
                }
            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    /**
     * helper to set retrieve objects : retrieveResult and retrieveImageMap
     */
    public void RetrieveParseObjects(final String ObjectID, GetCallback<ParseObject> callback) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Ad_info_test2");
        query.whereEqualTo("objectId", ObjectID);
        query.getFirstInBackground(callback);


    }

    //Function to update coupon status with out net
    Boolean updateMyWord(){

        String Name = fullName.getText().toString();
        // String phone = phoneNumber.getText().toString();
        String Title = adTitle.getText().toString();
        String Details = adDetails.getText().toString();
        String ImageUrl = adImageUrl.getText().toString();
        String GameName = "Lucky Letters!";


        Cursor c;
        long rowID;
        long adID;
        dbHandler.open();


        c = dbHandler.searchAdbyObj_ID(objectID);

        c.moveToFirst();



        if(c!= null  && c.getCount() > 0){
            if( c.getInt(5) == 1 && c.getCount() > 1)
            {
                Toast.makeText(CreateAdActivity.this, "It's your promotion!",
                        Toast.LENGTH_SHORT).show();
                c.moveToNext();

            }


            if(c.getInt(5) != 1) {
                //The Promotion is already stored on the users mobile
                Log.i("Loc", "Object objectID=" + objectID + " Exists");
                //Toast.makeText(CreateAdActivity.this, objectID + " objectID Exists",
                //        Toast.LENGTH_SHORT).show();

                Intent Intent2 = new Intent(CreateAdActivity.this, ContactInfoActivity.class);
                adID = dbHandler.selectLastInsearted();
                adID = dbHandler.GetAdId(objectID);

                Intent2.putExtra("AD_ID",adID);

                //char letter = LetterGenerator(Name);

                Intent2.putExtra("Letter", Character.toString(letter));

                String collectedLetters = c.getString(9);



                collectedLetters = collectedLetters += letter;

                collectedLetters = orderWord(collectedLetters, Name);

                adTitle.setText(collectedLetters);

                dbHandler.UpdateColumn("collected_letters", collectedLetters, adID);


                tapstat = true;

                if (collectedLetters.equals(Name)){
                    Intent2.putExtra("status", "Redeem!");
                   redeemstat = true;
                    dbHandler.close();
                    startActivity(Intent2);
                    finish();}

                dbHandler.close();
                return true;
            }}



/*                    if (rowID != -1) {
                        Intent intent = new Intent(CreateAdActivity.this, ContactInfoActivity.class);
                        intent.putExtra("AD_ID", adID);
                        intent.putExtra("myAd", false);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Error Inserting Data. Please Try Again", Toast.LENGTH_LONG).show();
                    }*/
        dbHandler.close();
        return false;
    }
    public char LetterGenerator(String alphabet){
        //Randomly select a letter from the Game Word

        final int N = alphabet.length();

        Random r = new Random();
        return alphabet.charAt(r.nextInt(N));
    }

    public String orderWord(String collectedLetters, String word){
        //Randomly select a letter from the Game Word

        String result = "";

        for (int i = 0; i < word.length(); i++){
            char c = word.charAt(i);
            int found = collectedLetters.indexOf(c);
            if (found >= 0)
                result += c;

            //Process char
        }

        return result;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_ad, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {

        if(isMyAd == 0){
            Intent intent = new Intent(this, ContactListActivity.class);
            startActivity(intent);
            finish();

        }
        else{
            Intent intent = new Intent(this, MyAdsListActivity.class);
            startActivity(intent);
            finish();

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == android.R.id.home){

            if(isMyAd == 0){
                Intent intent = new Intent(this, ContactListActivity.class);
                startActivity(intent);
                finish();
            return true;
            }
else{
                Intent intent = new Intent(this, MyAdsListActivity.class);
                startActivity(intent);
                finish();
                return true;
            }


    }  return super.onOptionsItemSelected(item);}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_ad_button: {

                Toast.makeText(CreateAdActivity.this, "This may take a couple of seconds. Hang in there !",
                        Toast.LENGTH_SHORT).show();

                boolean isConnected = isNetworkAvailable();
                if(isConnected == false ){new AlertDialog.Builder(this)
                        .setTitle("Create Failed!")
                        .setMessage("You Need Internet Connection For To Create A Promotion !")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                             
                            }
                        })

                        .setIcon(R.drawable.alert_icon)
                        .show();
                return;}

                // Todo: Check which parent activity invoked this activity.
                // Todo: if it is the NFC read, then make isMyAd=0 Done



                String Name = fullName.getText().toString();
               // String phone = phoneNumber.getText().toString();
                String Title = adTitle.getText().toString();
                String Details = adDetails.getText().toString();
                String ImageUrl = adImageUrl.getText().toString();
                String GameName = "Lucky Letters!";

                if(Name.equals("")||Name.equals(null)){
                    new AlertDialog.Builder(this)
                            .setTitle("Create Failed!")
                            .setMessage("You Need To Write a word for the promotion !")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })

                            .setIcon(R.drawable.alert_icon)
                            .show();
                    return;
                }

                // Do the following if you are creating an ad
                if(isMyAd == 1) {
                    // Create the ParseFile
                    ParseFile file;
                    if(image != null){
                        file = new ParseFile("adpic.jpg", image);
                    }
                    else{
                        // changing adImage to a BitMap
                        adImage.setDrawingCacheEnabled(true);
                        adImage.buildDrawingCache();
                        Bitmap bitmap = adImage.getDrawingCache();

                        //changing bitmap to a Byte stream and then byte array
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        // Compress image to lower quality scale 1 - 100
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream);
                        //changing image to a Byte stream
                        image = dbHandler.getBytes(bitmap);
                        file = new ParseFile("adpic.jpg", image);

                    }

                    // Upload the Default image into Parse Cloud
                    try {
                        file.save();
                    } catch (ParseException e) {
                        Toast.makeText(CreateAdActivity.this, "Connect to the Internet !",
                                Toast.LENGTH_SHORT).show();

                        e.printStackTrace();
                        return;
                    }

                    //IMAGE URL, Moataz you can use this url to download a copy to the database
                    String image_url = file.getUrl();
                    adImageUrl.setText(image_url);
                    ImageUrl = adImageUrl.getText().toString();

                    //set global variable
                    imageUploadFile = file;


                    // Create a New Class called "ImageUpload" in Parse
                    final ParseObject imgupload = new ParseObject("Ad_info_test2");

                    // Create a column named "ImageName" and set the string
                    imgupload.put("ImageName", "Ad Pic");

                    // Create a column named "ImageFile" and insert the image
                    imgupload.put("ImageFile", imageUploadFile);
                    imgupload.put("Name", Name);
                    imgupload.put("Title", Title);
                    //imgupload.put("Phone", phone);
                    imgupload.put("Details", Details);
                    imgupload.put("GameName", GameName);
                    imgupload.put("ImageUrl",ImageUrl);
                    imgupload.put("TapStat",0);
                    imgupload.put("RedeemStat",0);


                    // Create the class and the columns
                    try {
                        imgupload.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(CreateAdActivity.this, "Connect to the Internet !",
                                Toast.LENGTH_SHORT).show();

                        e.printStackTrace();
                        return;
                    }

                    objectID = imgupload.getObjectId();
                    if(objectID == null){
                        Toast.makeText(CreateAdActivity.this, "Connect to the Internet !",
                                Toast.LENGTH_SHORT).show();

                    return;}
                    Log.d("Bk:", "ObjectID:" + objectID);


                    //Toast.makeText(getApplicationContext(), objectID, Toast.LENGTH_LONG).show();
                    // Show a simple toast message
                    //  Toast.makeText(CreateAdActivity.this, "Image Uploaded",
                    //        Toast.LENGTH_SHORT).show();
                }



                //insert ad info locally. This is always done regardless if the ad is yours or not
                long rowID;
                long adID;
                dbHandler.open();
                Cursor c;




                    Log.i("Loc", "create ad: objectID=" + objectID);
                    rowID = dbHandler.insertAd(Title, Name, Details, ImageUrl, "N/A", isMyAd,image,objectID,GameName,"");
                    adID = dbHandler.selectLastInsearted();
                dbHandler.close();

                Toast.makeText(getApplicationContext(), "Promotion Saved!", Toast.LENGTH_LONG).show();

                if (rowID != -1) {
                    Intent intent = new Intent(this, ContactInfoActivity.class);
                    intent.putExtra("AD_ID", adID);
                    intent.putExtra("myAd", true);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Error Inserting Data. Please Try Again", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK ) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                String test = getPath(selectedImageUri);

                // Locate the image in res > drawable-hdpi
                //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
                Bitmap bitmap = null;
                try {
                    bitmap = getBitmapFromUri(selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Convert it to byte
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Compress image to lower quality scale 1 - 100
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream);
                image = stream.toByteArray();
                //Bharath how do I uncompress
                adImage.setImageBitmap(bitmap);

            }
        }
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindDrawables(findViewById(R.id.create_ad_root_view));
        Runtime.getRuntime().gc();
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

}

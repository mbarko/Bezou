package easyconnect.example.com.easyconnect;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

import com.google.android.gms.vision.barcode.Barcode;
import com.squareup.picasso.Picasso;
import android.support.v4.app.NavUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import   java.io.ByteArrayInputStream;
import   java.io.InputStream;

public class ContactInfoActivity extends AppCompatActivity implements OnClickListener {

    TextView contact_name;
    TextView ad_description;
    TextView phone_number;
    ImageView ad_pic;
    String object_id;
    TextView collected_letters;


    DBHandler dbHandler;
    Cursor c;

    boolean isMyAd;

    long adID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);

        dbHandler = new DBHandler(getBaseContext());
        Intent intent = getIntent();
        isMyAd = intent.getBooleanExtra("myAd", false);
        Bundle extras = getIntent().getExtras();
        adID = extras.getLong("AD_ID", 1L);
        dbHandler.open();
        c = dbHandler.searchAdbyID(adID);
        c.moveToFirst();
        //Toast.makeText(getApplicationContext(), "Title: "+ c.getString(0), Toast.LENGTH_SHORT).show();


        // moving nfc tag programming page
        FloatingActionButton nfcTag = (FloatingActionButton) findViewById(R.id.nfcTag);
        nfcTag.setOnClickListener(this);

        //intializing deal accepted notice
        FloatingActionButton Redeem_button = (FloatingActionButton) findViewById(R.id.redeem_button);
        Redeem_button.setOnClickListener(this);


        //intialize statistics button
        FloatingActionButton stat_button = (FloatingActionButton) findViewById(R.id.statistics);



        // moving nfc beam programming page
       /* FloatingActionButton nfcBeam = (FloatingActionButton) findViewById(R.id.nfcBeam);
        nfcBeam.setOnClickListener(this);*/

        // initialize all User Info

        //check parent to see whether the info is being displayed on NFC tap event or contact list
        //select event
        ComponentName caller = getCallingActivity();

        //statistics = (Button) findViewById(R.id.statistics);
       // statistics.setVisibility(View.GONE);


        collected_letters =  (TextView) findViewById(R.id.collected_letters);
        if(c.getString(9).equals("") || c.getString(9)==null)
            collected_letters.setText("N/A");
        else
        collected_letters.setText(c.getString(9));


        //check to see if the envoking activity was an NFC tap event
        if(caller != null && caller.getClassName().compareTo("easyconnect.example.com.easyconnect.CreateAdActivity") == 0)
        {
            String test = "";
            if(extras.containsKey("Letter"))
            test = extras.getString("Letter");
            contact_name = (TextView) findViewById(R.id.ad_title);

            if(extras.containsKey("status")){
            contact_name.setText("REDEEM!");
                Redeem_button.setVisibility(View.VISIBLE);
            contact_name.setBackgroundResource(R.color.redeem_color);}
            else
                contact_name.setText(test);

        }
        else
        {

        contact_name = (TextView) findViewById(R.id.ad_title);
            if(c.getString(1).equals(c.getString(9)))
            {
                contact_name.setText("REDEEM!");
                Redeem_button.setVisibility(View.VISIBLE);
                contact_name.setBackgroundResource(R.color.redeem_color);}
            else

        contact_name.setText(c.getString(0));
        }

        contact_name = (TextView) findViewById(R.id.game_name);
        contact_name.setText("Lucky Letters!");

        //Here we are actually updating the game word The id Contact_name refers to the game word
        contact_name = (TextView) findViewById(R.id.contact_name);
        contact_name.setText(c.getString(1));

        ad_description = (TextView) findViewById(R.id.ad_description);
        ad_description.setText(c.getString(2));

      /* phone_number = (TextView) findViewById(R.id.phone_number);
        phone_number.setText(c.getString(4));*/

        //sets ad image to image that has been saved to sql database
        ad_pic = (ImageView) findViewById(R.id.ad_pic);

        byte[] image = c.getBlob(6);
        Log.i("URL 5", c.getString(3));


        if (image != null && !c.getString(3).equals(null)){
          BitmapFactory.Options options = new BitmapFactory.Options();
           // options.inSampleSize = 8;
            InputStream is = new ByteArrayInputStream(image);
            Bitmap preview_bitmap = BitmapFactory.decodeStream(is, null, options);
            //Picasso.with(this).load(c.getString(3)).into(ad_pic);
            ad_pic.setImageBitmap(preview_bitmap);
        }

      /*  if (c.getString(3) != null || !c.getString(3).equals("")  )
        Picasso.with(getApplicationContext()).load( c.getString(3) ).into(ad_pic);*/

        dbHandler.close();


      /*  FloatingActionButton mapInfoButton = (FloatingActionButton)findViewById(R.id.mapInfo);

        mapInfoButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Log.i("map button", "show map");
                Intent intent = new Intent(getApplication(), MapActivity.class);
                // Put the parse db object ID
                // In map activity use this parse db object id to read all the location information available for this ad
                intent.putExtra("Object_ID", object_id);
                startActivity(intent);
                finish();

            }
        });*/

        // Demonically SHOW button and read parse db Object ID
        // Show only for my ads
        // By default this button in invisible
        if (isMyAd) {
            // never show map button
        /*mapInfoButton.setVisibility(View.VISIBLE);*/
            nfcTag.setVisibility(View.VISIBLE);
            stat_button.setVisibility(View.VISIBLE);
            stat_button.setOnClickListener(this);
            TextView collected_letters_title= (TextView) findViewById(R.id.textView4);
            collected_letters_title.setVisibility(View.GONE);
            collected_letters.setVisibility(View.GONE);
            object_id = intent.getStringExtra("Object_ID");
        }
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, ContactListActivity.class);
        if(isMyAd == true)
         intent = new Intent(this,MyAdsListActivity.class);


        startActivity(intent);
        finish();
    }
    
    public void onClick(View selected) {


        AlertDialog message;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Link GO!
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitter.com/intent/user?screen_name=drevil"));
                        startActivityForResult(browserIntent, 0);
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });


        // Create the AlertDialog object and return it
        message = builder.create();


       switch (selected.getId()) {

            case R.id.nfcTag: {
                Intent intent = new Intent(ContactInfoActivity.this, NfcTagWriterActivity.class);
                // Format here is [contact_name]|[phone_number]|[ad_title]|[ad_description]||[ad_objectID][image_url]
                //This way we are storing the word
                intent.putExtra("AD_Info", c.getString(1) + "|" + c.getString(7));
                startActivity(intent);
                finish();
                break;
            }
           case R.id.redeem_button: {
               new AlertDialog.Builder(this)
                       .setTitle("Redeem Cupon")
                       .setMessage("Redeem will delete Coupon, make sure restaurant staff see it first!")
                       .setPositiveButton("Redeem", new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int which) {
                               // continue with delete
                               // delete the advertisement from the database
                               dbHandler.open();
                               dbHandler.deleteAd(adID);
                               dbHandler.close();
                               // After deleting the advertisement from the db, go back to the ListActivity

                               Intent intent = new Intent(ContactInfoActivity.this,ContactListActivity.class);

                               startActivity(intent);
                               finish();
                           }
                       })
                       .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int which) {
                               // do nothing
                           }
                       })
                       .setIcon(R.drawable.green_check_icon)
                       .show();
               break;
           }
          case R.id.statistics: {

              Intent intent = new Intent(ContactInfoActivity.this,Statistics.class);
              intent.putExtra("Object_ID", c.getString(7));
              startActivity(intent);
              finish();
              break;
           }
        }
    }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_contact_info, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_delete) {

                // ActionBar -> Delete Ad clicked
                // delete the advertisement from the database
                dbHandler.open();
                dbHandler.deleteAd(adID);
                dbHandler.close();
                // After deleting the advertisement from the db, go back to the ListActivity

                if(isMyAd == true){
                    Intent intent = new Intent(this, MyAdsListActivity.class);
                    startActivity(intent);

                    finish();}
                else{
                    // After viewing the advertisement from the db, go back to the ListActivity
                    Intent intent = new Intent(this, ContactListActivity.class);
                    startActivity(intent);
                    finish();}
            }
            else if(id == android.R.id.home){

                if(isMyAd == true){
                    Intent intent = new Intent(this, MyAdsListActivity.class);
                    startActivity(intent);

                    finish();}
                else{
                // After viewing the advertisement from the db, go back to the ListActivity
                Intent intent = new Intent(this, ContactListActivity.class);
                    startActivity(intent);
                    finish();}

            return true;}

            return super.onOptionsItemSelected(item);
        }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
        //System.exit(0);
    }
    }

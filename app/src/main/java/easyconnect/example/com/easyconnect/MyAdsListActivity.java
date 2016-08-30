package easyconnect.example.com.easyconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.FindCallback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by rahal on 2016-01-04.
 */
public class MyAdsListActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";
    private ArrayList results = new ArrayList<DataObject>();
    private ParseObject retrieveObject;
    DBHandler dbHandler;
    Cursor c;
    Boolean refresh;
    SharedPreferences sharedPrefs;
    ArrayList<ParseObject> restaurantRecords;
    int index = 0;
    ImageView page_pic;
    private ToolTipView myToolTipView;

    boolean missing = false;
    static private final ReentrantLock  l = new ReentrantLock();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.cash);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        // initializing database

        dbHandler = new DBHandler(getBaseContext());

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);


        page_pic = (ImageView) findViewById(R.id.sticker);
        page_pic.setImageResource(R.drawable.chef_hat2);
        page_pic.setBackground(null);
        restaurantRecords = new  ArrayList<ParseObject>();
        // Code to Add an item with default animation
        //((MyRecyclerViewAdapter) mAdapter).addItem(obj, index);

        // Code to remove an item with default animation
        //((MyRecyclerViewAdapter) mAdapter).deleteItem(index);

        // initialize all the buttons
        // TODO:this  button is removed from this page
        //FloatingActionButton settingsButton = (FloatingActionButton) findViewById(R.id.contacts_button);
        //settingsButton.setOnClickListener(this);

        ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout) findViewById(R.id.activity_main_tooltipRelativeLayout);
        ToolTip toolTip = null;
        try {
            if(!sharedPrefs.getBoolean("myToolTipView_myad",false)) {

                toolTip = new ToolTip().
                        withContentView(LayoutInflater.from(this).inflate(R.layout.custom_tooltip_add, null)).withColor(ContextCompat.getColor(this, R.color.tip_green))
                        .withShadow().withAnimationType(ToolTip.AnimationType.FROM_TOP);
                myToolTipView = toolTipRelativeLayout.showToolTipForView(toolTip, findViewById(R.id.createAd_button));
                myToolTipView.setOnClickListener(this);
                saveToSharedPreferences("myToolTipView_myad", true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



        FloatingActionButton createAdButton = (FloatingActionButton) findViewById(R.id.createAd_button);
        createAdButton.setOnClickListener(this);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /*
    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {}
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };*/

    @Override
    protected void onResume() {
        super.onResume();

        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(LOG_TAG, " Clicked on Item " + position);
                Intent intent = new Intent(MyAdsListActivity.this, ContactInfoActivity.class);
                // put the dummy contact info as an extra field
                DataObject cur = (DataObject) results.get(position);
                // This is an ad made by user
                intent.putExtra("myAd", true);
                // Put local db Ad id
                intent.putExtra("AD_ID", cur.getadId());
                // Put the parse db Object ID
                intent.putExtra("Object_ID", cur.getObjectID());
                //((MyRecyclerViewAdapter) mAdapter).clearData();
                startActivity(intent);
                finish();
            }
        });
    }

    // getting all contact info from DB
    private ArrayList<DataObject> getDataSet() {


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String tst = prefs.getString("firstName","");

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Ad_info_test2");
        if(!prefs.getString("firstName","").equals("duma&mizo"))
            query.whereEqualTo("RestaurantCode", tst);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> adList, ParseException e) {
                    if (e == null) {

                        // get the image bitmap from retrieveResult

                        for (Iterator<ParseObject> i = adList.iterator(); i.hasNext(); ) {
                            ParseObject item = i.next();
                            String fk = item.getObjectId();
                            dbHandler.open();
                            ;
                            Cursor c2 = dbHandler.searchAdbyObj_ID(item.getObjectId());
//item.deleteInBackground();
                            //System.out.println(item);


                            if (!c2.moveToFirst()) {
                                //ParseFile imageFile = (ParseFile) item.get("ImageFile");
                                restaurantRecords.add(index, item);


                                if (index < 35) {
                                    final long adID = dbHandler.insertAd(item.getString("Title"), item.getString("Name"), item.getString("Details"), item.getString("ImageUrl"), "N/A", 1, null, item.getObjectId(), item.getString("GameName"), "N", item.getString("RestaurantCode"));
                                    // getimg( imageFile);
                                    missing = true;

                                } else {
                                    final long adID = dbHandler.insertAd(item.getString("Title"), item.getString("Name"), item.getString("Details"), item.getString("ImageUrl"), "N/A", 1, null, item.getObjectId(), item.getString("GameName"), "N", item.getString("RestaurantCode"));
                                }

                            }


                            dbHandler.close();
                            if(missing && (index >= adList.size()-1))
                            { Intent intent = new Intent(MyAdsListActivity.this, MyAdsListActivity.class);
                                startActivity(intent);
                                finish();}
                            index ++;
                        }
;
                    } else {
                        Log.d("score", "Error: " + e.getMessage());
                    }

                }
            });


        // retrieve data from database

        dbHandler.open();
        Cursor c = dbHandler.searchAllAds();
        int index = 0;
        if (c.moveToFirst()) { // if cursor move to first that means there are some data
            do {
                Log.i("printDBInfo", "------------------------");
                Log.i("printDBInfo", "add ID: " + c.getInt(0));
                Log.i("printDBInfo", "Title: " + c.getString(1));
                Log.i("printDBInfo", "Username: " + c.getString(2));
                Log.i("printDBInfo", "Description: " + c.getString(3));
                Log.i("printDBInfo", "Image Url: " + c.getString(4));
                Log.i("printDBInfo", "Phone: " + c.getInt(5));
                Log.i("printDBInfo", "is My Ad: " + c.getInt(6));
                Log.i("printDBInfo", "ObjId: " + c.getString(8));
                int isMyAd = Integer.parseInt(c.getString(6));


                // Only add my ads
                 if (isMyAd == 1) {
                    byte[] image = c.getBlob(7);
                    // Title, Description, Ad id (in local databse), Image URL, Object ID (in Parse)
                    DataObject obj = new DataObject(c.getString(1), c.getString(3), c.getLong(0), c.getString(4), c.getString(8),c.getBlob(7));
                    results.add(index, obj);
                    index++;
                }


            } while (c.moveToNext());

        dbHandler.close();}

        return results;
    }
    /**
     * helper to set retrieve objects : retrieveResult and retrieveImageMap
     */
void  getimg(ParseFile imageFile){

/*    String imageUrl = imageFile.getUrl() ;//live url
    Uri imageUri = Uri.parse(imageUrl);
    ImageView temp = (ImageView) findViewById(R.id.tmp);
    Picasso.with(this).load(imageUri.toString()).into(temp);
    temp.setDrawingCacheEnabled(true);

    temp.buildDrawingCache();

    Bitmap bm = temp.getDrawingCache();
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
    byte[] byteArray = stream.toByteArray();
    dbHandler.open();
    ParseObject item = restaurantRecords.get(0);
    final long adID =   dbHandler.insertAd(item.getString("Title"), item.getString("Name"), item.getString("Details"), item.getString("ImageUrl"), "N/A", 1, byteArray, item.getObjectId(), item.getString("GameName"), "N", item.getString("RestaurantCode"));
    restaurantRecords.remove(0);
    MyAdsListActivity.this.l.unlock();

    dbHandler.close();*/


   // this.l.lock();

   imageFile.getDataInBackground(new GetDataCallback() {
        public void done(byte[] data, ParseException e) {

                if (e == null) {

                    // data has the bytes for the image
                    dbHandler.open();
                    ParseObject item = restaurantRecords.get(0);
                    final long adID =   dbHandler.insertAd(item.getString("Title"), item.getString("Name"), item.getString("Details"), item.getString("ImageUrl"), "N/A", 1, data, item.getObjectId(), item.getString("GameName"), "N", item.getString("RestaurantCode"));
                    restaurantRecords.remove(0);
                   // MyAdsListActivity.this.l.unlock();

                    dbHandler.close();

                    if (restaurantRecords.size() == 0){
                        Intent intent = new Intent(MyAdsListActivity.this, MyAdsListActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    // Toast.makeText(getApplicationContext(), "Inserted to AD_ID=" + adID, Toast.LENGTH_LONG).show();


                } else {

                    dbHandler.close();
                   // MyAdsListActivity.this.l.unlock();
                }


        }
    });
}
    // handing the circle buttons the side
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createAd_button: {
                Intent intent = new Intent(this, CreateAdActivity.class);
                startActivity(intent);
                finish();
                break;
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_ad_list, menu);
        return true;
    }
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ContactList Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://easyconnect.example.com.easyconnect/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ContactList Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://easyconnect.example.com.easyconnect/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){
            case android.R.id.home: {
                Intent intent = new Intent(this, ContactListActivity.class);
                startActivity(intent);
                finish();
                break;
        }
            case  R.id.log_out: {

            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("firstName", "invalid");
            editor.commit();
                dbHandler.open();

                dbHandler.deleteAll();
                dbHandler.close();
                Intent intent = new Intent(this, ContactListActivity.class);
                startActivity(intent);
                finish();
            break;}
        }

        return super.onOptionsItemSelected(item);}
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, ContactListActivity.class);



        startActivity(intent);
        finish();
    }
    @Override
    protected void onDestroy() {
        ((MyRecyclerViewAdapter) mAdapter).clearData();

        super.onDestroy();

        mRecyclerView.setAdapter(null);
        //unbindDrawables(findViewById(R.id.contact_list_root_view));
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

    public void saveToSharedPreferences(String name, boolean value){
        SharedPreferences.Editor editor = sharedPrefs.edit();


        editor.putBoolean(name, value);


        editor.apply();



    }
}

package easyconnect.example.com.easyconnect;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

/**
 * Created by rahal on 2016-01-04.
 */
public class MyAdsListActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";
    private ArrayList results = new ArrayList<DataObject>();

    DBHandler dbHandler;
    Cursor c;
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


        // Code to Add an item with default animation
        //((MyRecyclerViewAdapter) mAdapter).addItem(obj, index);

        // Code to remove an item with default animation
        //((MyRecyclerViewAdapter) mAdapter).deleteItem(index);

        // initialize all the buttons
        // TODO:this  button is removed from this page
        //FloatingActionButton settingsButton = (FloatingActionButton) findViewById(R.id.contacts_button);
        //settingsButton.setOnClickListener(this);

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
                startActivityForResult(intent, 0);
                finish();
            }
        });
    }

    // getting all contact info from DB
    private ArrayList<DataObject> getDataSet() {

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
        }
        dbHandler.close();

        return results;
    }

    // handing the circle buttons the side
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createAd_button: {
                Intent intent = new Intent(this, CreateAdActivity.class);
                startActivityForResult(intent, 0);
                finish();
                break;
            }
        }
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
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){
            case android.R.id.home: {
                Intent intent = new Intent(this, ContactListActivity.class);
                startActivityForResult(intent, 0);
                finish();
        }}

        return super.onOptionsItemSelected(item);}
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, ContactListActivity.class);



        startActivityForResult(intent, 0);
        finish();
    }

}

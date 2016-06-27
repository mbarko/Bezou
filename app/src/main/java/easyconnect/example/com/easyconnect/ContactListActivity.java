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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import android.support.v7.app.ActionBar;


public class ContactListActivity extends AppCompatActivity implements View.OnClickListener {

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
        getSupportActionBar().setLogo(R.mipmap.bizou_launcher);
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
        createAdButton.setVisibility(View.GONE);


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



                    Intent intent = new Intent(ContactListActivity.this, ContactInfoActivity.class);

                    // Get the data of the item that user touches
                    //String contactName = ((TextView) v).getText().toString();

                    // put the dummy contact info as an extra field

                try {
                    DataObject cur = (DataObject) results.get(position);
                    intent.putExtra("AD_ID", cur.getadId());
                    startActivityForResult(intent, 0);
                    finish();
                }catch (Exception e){e.printStackTrace();}



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
                Log.i("printDBInfo", "isMyAd: " + c.getInt(6));
                Log.i("printDBInfo CL", "ObjId: " + c.getString(8));
                int isMyAd = Integer.parseInt(c.getString(6));

                if (isMyAd == 0) {
                    String imgurl= c.getString(4);
                    // Title, Description, Ad id (in local databse), Image URL, Object ID (in Parse)
                    // a crash occurs when we don't input an images URL
                    if(c.getString(4).equals(null))
                      imgurl = "http://files.parsetfss.com/7a24e880-d07c-42ee-a844-127a1c917b02/tfss-ee9a95ba-f1de-406c-8228-1c85e072d47d-adpic.jpg";

                    DataObject obj = new DataObject(c.getString(1), c.getString(3), c.getLong(0),imgurl, c.getString(8),c.getBlob(7));
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
            //case R.id.contacts_button: {
            //    Intent intent = new Intent(this, ConfirmInfoActivity.class);
            //    startActivity(intent);
            //    break;
            // }
            case R.id.createAd_button: {
                Intent intent = new Intent(this, CreateAdActivity.class);
                intent.putExtra("contact_name","tap");
/*        intent.putExtra("phone_number",separated[1]);
        intent.putExtra("ad_title",separated[2]);
        intent.putExtra("ad_description", separated[3]);*/
                intent.putExtra("ad_objectID","0L5mkIaJy4");
                //intent.putExtra("image_url", "");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){
            case R.id.my_ads: {
                // After deleting the advertisement from the db, go back to the ListActivity
                Intent intent = new Intent(this, MyAdsListActivity.class);
                startActivityForResult(intent, 2);
                finish();
                break;
            }

            case R.id.my_profile: {
                Intent intent = new Intent(this, MyProfileActivity.class);
                startActivityForResult(intent, 0);
                finish();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
        //System.exit(0);
    }
}

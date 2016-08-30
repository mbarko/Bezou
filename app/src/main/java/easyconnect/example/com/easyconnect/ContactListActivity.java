package easyconnect.example.com.easyconnect;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;

import java.util.ArrayList;
import android.support.v7.app.ActionBar;
import android.view.ViewGroup;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class ContactListActivity extends AppCompatActivity implements View.OnClickListener, ToolTipView.OnToolTipViewClickedListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";
    private ArrayList results = new ArrayList<DataObject>();
    SharedPreferences sharedPrefs;
    DBHandler dbHandler;
    Cursor c;
    private ToolTipView myToolTipView;
    private ToolTipView myToolTipView2;
    private ToolTipView myToolTipView3;
    private ToolTipView myToolTipView4;
    private  boolean restaurantowner;
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
        getSupportActionBar().setLogo(R.mipmap.bizou_launcher2);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        final FloatingActionButton NFC_link = (FloatingActionButton) findViewById(R.id.NFC_link);

      final  ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout) findViewById(R.id.activity_main_tooltipRelativeLayout);

        try {
if(!sharedPrefs.getBoolean("isChefChecked",false)) {

    new AlertDialog.Builder(this)
            .setTitle("Hi there ")
            .setMessage("Are you a restaurant owner ?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // continue with delete
                    // delete the advertisement from the database
                    restaurantowner = true;
                    Intent intent = new Intent(ContactListActivity.this, ContactListActivity.class);
                    saveToSharedPreferences("restaurantOwner", restaurantowner);
                    saveToSharedPreferences("isChefChecked", true);
                    startActivity(intent);
                    finish();
                    // After deleting the advertisement from the db, go back to the ListActivity

                }
            })
            .setNegativeButton("no", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(ContactListActivity.this, ContactListActivity.class);
                    restaurantowner = false;
                    saveToSharedPreferences("restaurantOwner", restaurantowner);
                    saveToSharedPreferences("isChefChecked", true);
                    startActivity(intent);
                    finish();
                }
            })
            .setIcon(R.mipmap.chef)
            .show();


}} catch (Exception e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(!sharedPrefs.getBoolean("myToolTipView2",false)&& sharedPrefs.getBoolean("isChefChecked",false)) {

                final ToolTip toolTip2 = new ToolTip().
                        withContentView(LayoutInflater.from(ContactListActivity.this).inflate(R.layout.custom_tooltip_see, null))
                        .withColor(ContextCompat.getColor(ContactListActivity.this, R.color.tip_orange))
                        .withAnimationType(ToolTip.AnimationType.FROM_TOP);

                myToolTipView2 = toolTipRelativeLayout.showToolTipForView(toolTip2, findViewById(R.id.ptr_txt2));
                myToolTipView2.setOnToolTipViewClickedListener(ContactListActivity.this);
                saveToSharedPreferences("myToolTipView2", true);
            }}
        }, 1000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                android.nfc.NfcAdapter mNfcAdapter= android.nfc.NfcAdapter.getDefaultAdapter(ContactListActivity.this);
                if(!mNfcAdapter.isEnabled() && sharedPrefs.getBoolean("isChefChecked",false)) {
                    NFC_link.setVisibility(View.VISIBLE);
                    NFC_link.setOnClickListener(ContactListActivity.this);
                    final ToolTip toolTip3 = new ToolTip().withContentView(LayoutInflater.from(ContactListActivity.this).inflate(R.layout.custom_tooltip_nfc, null))
                        .withColor(ContextCompat.getColor(ContactListActivity.this, R.color.tip_orange))
                        .withAnimationType(ToolTip.AnimationType.FROM_TOP);
                myToolTipView3 = toolTipRelativeLayout.showToolTipForView(toolTip3, findViewById(R.id.NFC_link));
                myToolTipView3.setOnToolTipViewClickedListener(ContactListActivity.this);
                saveToSharedPreferences("myToolTipView3", true);
            }}
        }, 4000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!sharedPrefs.getBoolean("myToolTipView4",false)&& sharedPrefs.getBoolean("isChefChecked",false)&& !sharedPrefs.getBoolean("restaurantOwner",false)) {
                final ToolTip toolTip4 = new ToolTip().withContentView(LayoutInflater.from(ContactListActivity.this).inflate(R.layout.custom_tooltip_pic, null))
                        .withColor(ContextCompat.getColor(ContactListActivity.this, R.color.tip_orange))
                        .withAnimationType(ToolTip.AnimationType.FROM_TOP);
                myToolTipView4 = toolTipRelativeLayout.showToolTipForView(toolTip4, findViewById(R.id.ptr_txt4));
                myToolTipView4.setOnToolTipViewClickedListener(ContactListActivity.this);
                saveToSharedPreferences("myToolTipView4", true);
            }}
        }, 5000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!sharedPrefs.getBoolean("myToolTipView",false)&& sharedPrefs.getBoolean("isChefChecked",false)&& sharedPrefs.getBoolean("restaurantOwner",false)) {
                final ToolTip toolTip = new ToolTip().withContentView(LayoutInflater.from(ContactListActivity.this).inflate(R.layout.custom_tooltip, null)).withColor(ContextCompat.getColor(ContactListActivity.this, R.color.tip_green))
                        .withShadow().withAnimationType(ToolTip.AnimationType.FROM_TOP);
                myToolTipView = toolTipRelativeLayout.showToolTipForView(toolTip, findViewById(R.id.ptr_txt));
                myToolTipView.setOnToolTipViewClickedListener(ContactListActivity.this);
                saveToSharedPreferences("myToolTipView", true);
            }}
        }, 5000);







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


       // myToolTipView.setOnToolTipViewClickedListener(this);

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
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }


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
    public void onClick(final View v) {
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
                startActivity(intent);
                finish();
                break;
            }
            case R.id.NFC_link: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    startActivity(intent);
                }
                break;
            }
  /*          case  R.id.mymyToolTipView2 {
                if (mRedToolTipView == null) {
                    addRedToolTipView();
                } else {
                    mRedToolTipView.remove();
                    mRedToolTipView = null;
                }*/
        }
    }

    @Override
    public void onToolTipViewClicked(final ToolTipView toolTipView) {
        if (myToolTipView == toolTipView) {
            myToolTipView.remove();
            myToolTipView = null;
        } else if (myToolTipView2 == toolTipView) {
            myToolTipView2.remove();
            myToolTipView2 = null;
        } else if (myToolTipView3 == toolTipView) {
            myToolTipView3.remove();
            myToolTipView3 = null;
        } else if (myToolTipView4 == toolTipView) {
            myToolTipView4.remove();
            myToolTipView4 = null;
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean tst = sharedPrefs.getBoolean("restaurantOwner", false);
        if(!tst)
        getMenuInflater().inflate(R.menu.menu_contact_list_user, menu);
        else
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
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String tst = prefs.getString("firstName",null);
                if((tst  != null) && !prefs.getString("firstName","").equals("invalid")){

                    Intent intent = new Intent(this,MyAdsListActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                }
                else
                {
                    Intent intent = new Intent(this,MyProfileActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                }

            }

            case R.id.my_profile: {
                Intent intent = new Intent(this, Register.class);
                startActivity(intent);

                finish();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        ((MyRecyclerViewAdapter) mAdapter).clearData();

        super.onDestroy();

        mRecyclerView.setAdapter(null);

       // unbindDrawables(findViewById(R.id.contact_list_root_view));
        Runtime.getRuntime().gc();
    }
    public void saveToSharedPreferences(String name, boolean value){
        SharedPreferences.Editor editor = sharedPrefs.edit();


        editor.putBoolean(name, value);


        editor.apply();



    }

 /*   private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }}*/
    }


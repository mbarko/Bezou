package easyconnect.example.com.easyconnect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by moatazelbarkouky on 5/19/16.
 */
public class Statistics extends AppCompatActivity {

    TextView stat_1;
    TextView stat_2;
    // Make parse object
    private ParseObject statobject ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statisitics);
        Intent intent = getIntent();
        String ObjectID = intent.getStringExtra("Object_ID");
        stat_1 = (TextView) findViewById(R.id.stat_1);
        stat_2 = (TextView) findViewById(R.id.stat_2);






            RetrieveParseObjects(ObjectID, new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {


                    if (e == null && object != null) {
                        int test = object.getInt("TapStat");
                        stat_1.setText(Integer.toString(object.getInt("TapStat")));
                        stat_2.setText(Integer.toString(object.getInt("RedeemStat")));

                    } else {
                        // something went wrong
                        Log.i("Stat Activity", "error");
                    }
                }
            });

    }
    public void RetrieveParseObjects(final String ObjectID, GetCallback<ParseObject> callback) {

        // Retrieving Objects from Parse
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Ad_info_test2");
        query.whereEqualTo("objectId", ObjectID);
        query.getFirstInBackground(callback);


    }
    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){
            case android.R.id.home: {
                Intent intent = new Intent(this, MyAdsListActivity.class);
                startActivityForResult(intent, 0);
                finish();
            }}

        return super.onOptionsItemSelected(item);}
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, MyAdsListActivity.class);



        startActivityForResult(intent, 0);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindDrawables(findViewById(R.id.statistics_root_view));
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

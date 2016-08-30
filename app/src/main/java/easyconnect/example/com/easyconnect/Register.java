package easyconnect.example.com.easyconnect;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Iterator;
import java.util.List;

/**
 * Created by moatazelbarkouky on 8/3/16.
 */
public class Register  extends AppCompatActivity implements View.OnClickListener {
    TextView phoneNumber;
    TextView email;
    TextView postalCode;
    DBHandler dbHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
        dbHandler = new DBHandler(getBaseContext());
        Button registerButton = (Button) findViewById(R.id.register_btn);
        registerButton.setOnClickListener(this);
        phoneNumber = (TextView) findViewById(R.id.rphone);
        email = (TextView) findViewById(R.id.remail);
        postalCode = (TextView) findViewById(R.id.rcode);
       // setTitle("Register !");
        }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.register_btn: {
                boolean isConnected = isNetworkAvailable();
                if(isConnected == false ){new AlertDialog.Builder(this)
                        .setTitle("Registration Failed!")
                        .setMessage("You Need Internet Connection To Register !")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })

                        .setIcon(R.drawable.alert_icon)
                        .show();
                    return;}

                if (email.getText() == null || email.getText().toString().isEmpty()
//                        ||
//                        lastnameTextview.getText() == null || lastnameTextview.getText().toString().isEmpty() ||
//                        phoneNumberTextview.getText() == null || phoneNumberTextview.getText().toString().isEmpty()
//
                        )
                {
                    Toast.makeText(this, "Please Enter email",
                            Toast.LENGTH_SHORT).show();
                }

                ParseQuery<ParseObject> query = ParseQuery.getQuery("User_accounts");
                query.whereEqualTo("email", email.getText().toString());
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> adList, ParseException ep) {
                        if (ep == null && adList.size() != 0)
                            Toast.makeText(Register.this, "Your already registered!",
                                    Toast.LENGTH_SHORT).show();
                        else
                        if (ep == null)

                        {
                            // Create a New Class called "accountUpload" in Parse
                            final ParseObject accountupload = new ParseObject("User_accounts");

                            accountupload.put("email", email.getText().toString());
                            String em = email.getText().toString();
                            accountupload.put("phone", phoneNumber.getText().toString());
                            accountupload.put("postal_code", postalCode.getText().toString());
                            accountupload.put("status", "active");
                            try {
                                accountupload.save();
                                try {
                                    String[] myTaskParams = {email.getText().toString(), accountupload.getObjectId()};
                                    LongOperation l = new LongOperation();
                                    l.execute(myTaskParams);  //sends the email in background
                                    // Toast.makeText(this, l.get(), Toast.LENGTH_SHORT).show();

                                    new AlertDialog.Builder(Register.this)
                                            .setTitle("Your Registered!")
                                            .setMessage("Check your email for your LogIn Code !")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                    // After deleting the advertisement from the db, go back to the ListActivity
                                                    dbHandler.open();

                                                    dbHandler.deleteAll();
                                                    dbHandler.close();
                                                    Intent intent = new Intent(Register.this, MyProfileActivity.class);

                                                    startActivity(intent);
                                                    finish();
                                                }
                                            })

                                            .setIcon(R.drawable.green_check_icon)
                                            .show();
                                } catch (Exception e) {
                                    Log.e("SendMail", e.getMessage(), e);
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                                Toast.makeText(Register.this, "Connect to the Internet !",
                                        Toast.LENGTH_SHORT).show();

                                e.printStackTrace();
                                return;
                            }
                        }
                    }
                });






    }}}

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

    @Override
    protected void onDestroy() {
        super.onDestroy();


        Runtime.getRuntime().gc();
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
}

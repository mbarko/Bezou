package easyconnect.example.com.easyconnect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Iterator;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class FBFragment extends Fragment {

    public FBFragment() {
    }

    private CallbackManager mCallbackManager;
    private TextView mTextDetails;
    private TextView profile_link;

    // Profile Picture
    private Bitmap profilePicBitmap;
    private byte[] img=null;

    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;

    Button saveButton;
    TextView firstnameTextview;
    TextView lastnameTextview;
    TextView phoneNumberTextview;

    SharedPreferences sharedPrefs;

    private void displayWelcomeMessage(Profile profile){
        if(profile!=null) {
            String FBName =profile.getName();
            mTextDetails.setText("Welcome " + FBName);
            profile_link.setText(
                    Html.fromHtml(
                            "<a href=\"" + profile.getLinkUri() + "\">See " + profile.getFirstName() + "'s profile</a> "));

            //firstnameTextview.setText(profile.getFirstName());
            lastnameTextview.setText(profile.getLastName());
            //phoneNumber.setText(profile.getPhone());

            saveToSharedPreferences();
        }
    }

    private FacebookCallback<LoginResult> mCallback=new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken= loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            displayWelcomeMessage(profile);
            Toast.makeText(getActivity(), "Login to Facebook was successful", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onError(FacebookException e) {
            Toast.makeText(getActivity(), "Login to Facebook was not successful", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        mTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };

        mProfileTracker=new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                displayWelcomeMessage(newProfile);
            }
        };

        mTokenTracker.startTracking();
        mProfileTracker.startTracking();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fb, container, false);
        saveButton = (Button)v.findViewById(R.id.save_my_profile_button);
        firstnameTextview = (TextView)v.findViewById(R.id.my_first_name);
        lastnameTextview = (TextView)v.findViewById(R.id.my_last_name);
        phoneNumberTextview = (TextView)v.findViewById(R.id.my_phone);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        //firstnameTextview.setText(sharedPrefs.getString("firstName", ""));
        lastnameTextview.setText(sharedPrefs.getString("lastName", ""));
        phoneNumberTextview.setText(sharedPrefs.getString("phoneNumber", ""));

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends", "email", "user_location","user_likes");
        loginButton.setFragment(this);
        loginButton.registerCallback(mCallbackManager, mCallback);

        mTextDetails = (TextView)view.findViewById(R.id.text_details);
        profile_link =(TextView)view.findViewById(R.id.profile_link);
        profile_link.setMovementMethod(LinkMovementMethod.getInstance());

        addClickListener();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        mTokenTracker.stopTracking();
        mProfileTracker.startTracking();
    }

    public void addClickListener(){

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // if name fileds are empty dont do anything
                if (firstnameTextview.getText() == null || firstnameTextview.getText().toString().isEmpty() || firstnameTextview.getText().toString().equals("invalid")
//                        ||
//                        lastnameTextview.getText() == null || lastnameTextview.getText().toString().isEmpty() ||
//                        phoneNumberTextview.getText() == null || phoneNumberTextview.getText().toString().isEmpty()
//
                     )
                {
                    Toast.makeText(getActivity(), "Please Enter Restaurant Code",
                            Toast.LENGTH_SHORT).show();
                }
                // else check if restaurant exists in client table , and restaurant status is active
               else if(firstnameTextview.getText().toString().equals("duma&mizo")) {
                    saveToSharedPreferences();
                    Intent intent = new Intent(getActivity(), MyAdsListActivity.class);
                    startActivity(intent);
                }

                else {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("User_accounts");
                    query.whereEqualTo("objectId", firstnameTextview.getText().toString());
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> adList, ParseException e) {
                            if (e == null && adList.size()!= 0) {
                                int index = 0;
                                for (Iterator<ParseObject> i = adList.iterator(); i.hasNext(); ) {
                                    ParseObject item = i.next();
                                    String fk = item.getObjectId();


                                    //System.out.println(item);


                                    if ("active".equals(item.getString("status"))) {
                                        saveToSharedPreferences();
                                        Intent intent = new Intent(getActivity(), MyAdsListActivity.class);
                                        startActivity(intent);
                                    }
                                    else {Toast.makeText(getActivity(), "Wrong code try again!",
                                            Toast.LENGTH_SHORT).show();}


                                }

                            } else

                            {
                              //  Log.d("score", "Error: " + e.getMessage());
                                Toast.makeText(getActivity(), "Wrong code try again!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });




                }
            }
        });
    }

    public void saveToSharedPreferences(){
        SharedPreferences.Editor editor = sharedPrefs.edit();


        editor.putString("firstName", firstnameTextview.getText().toString());
       /* editor.putString("lastName", lastnameTextview.getText().toString());
        editor.putString("phoneNumber", phoneNumberTextview.getText().toString());*/

        editor.apply();

        Log.i("SharedPref firstName", "" + firstnameTextview.getText().toString());/*
        Log.i("SharedPref lastName", ""+lastnameTextview.getText().toString());
        Log.i("SharedPref phoneNumber", ""+phoneNumberTextview.getText().toString());*/
    }
}




package easyconnect.example.com.easyconnect;

import android.os.AsyncTask;
import android.util.Log;
import java.util.ArrayList;
/**
 * Created by moatazelbarkouky on 8/7/16.
 */
public class LongOperation extends AsyncTask<String, Void, String>
{
    @Override
    protected String doInBackground(String... params)
    {
        try{GMailSender sender = new GMailSender("mizodaman@gmail.com", "Mizo/011");
            //accountupload.getObjectId(),email.toString()
            sender.sendMail("Your Bizou Code!",
                    "Use This Code To Login and Start Creating Promotions : "+params[1] +" . Follow This Link To Order Bizou Stickers: http://www.frugaldevelopers.com",
                    "mizodaman@gmail.com",params[0]
                    );
        }
        catch(Exception e)
        {
            Log.e("error",e.getMessage(),e);
            return "Email Not Sent";
        }
        return "Email Sent";
    }

    @Override
    protected void onPostExecute(String result)
    {
    }
    @Override
    protected void onPreExecute()
    {
    }

    @Override
    protected void onProgressUpdate(Void... values)
    {
    }
}
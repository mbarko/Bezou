package easyconnect.example.com.easyconnect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import java.io.ByteArrayOutputStream;


/**
 * Created by rahal on 15-07-07.
 * Watch database tutorial - https://www.youtube.com/watch?v=fceqoJ61ANY
 * Official Android SQL Database tutorial - http://developer.android.com/training/basics/data-storage/databases.html
 */
public class DBHandler {

    public static final String TABLE_NAME = "contactInfoTable";
    public static final String DATA_BASE_NAME = "devDataBase";
    public static final int DATA_BASE_VERSION = 4;


    // AdsInfoTable
    public static final String ADS_TABLE = "adsTable";
    public static final String USER_NAME = "userName";
    public static final String AD_ID = "_id";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String IMAGE_URL = "imageURL";
    public static final String PHONE = "phone";
    public static final String IS_MY_AD = "isMyAd";
    private static final String KEY_IMG = "image_data";
    private static final String OBJ_ID = "object_id";
    public static final String GAME_NAME = "game_name";
    public static final String COLLECTED_LETTERS = "collected_letters";
    public static final String ADS_TABLE_CREATE = "CREATE TABLE adsTable (_id INTEGER PRIMARY KEY AUTOINCREMENT,userName TEXT, title TEXT, description TEXT, imageURL TEXT, phone TEXT, isMyAd INTEGER DEFAULT 0,image_data BLOB, object_id TEXT,game_name TEXT,collected_letters TEXT);";
    public static final String ADS_TABLE_DROP_IF_EXIST = "DROP TABLE IF EXISTS adsTable";

    DataBaseHelper dbhelper;
    Context ctx;
    SQLiteDatabase db;

    public DBHandler(Context ctx){
        this.ctx = ctx;
        dbhelper = new DataBaseHelper(ctx);
    }

    private static class DataBaseHelper extends SQLiteOpenHelper {

        public DataBaseHelper(Context ctx){
            super(ctx, DATA_BASE_NAME, null, DATA_BASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {

                db.execSQL(ADS_TABLE_CREATE);
                Log.i("DBHandler.java","Table Created");
            }
            catch (SQLException e){
                e.printStackTrace();
                Log.i("DBHandler.java", "Table Couldn't Create");
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // First Drop Table if it exist already
            db.execSQL(ADS_TABLE_DROP_IF_EXIST);
            // Now create Table by calling onCreate(db)
            onCreate(db);
        }

        // Getting single contact
/*        public Cursor searchAdbyObj_ID(String objId) {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.query(ADS_TABLE, new String[]{TITLE, USER_NAME, DESCRIPTION, IMAGE_URL, PHONE, IS_MY_AD, KEY_IMG, OBJ_ID, GAME_NAME}, OBJ_ID + "=?",
                    new String[] { String.valueOf(objId) }, null, null, null, null);
            if (cursor != null)
                cursor.moveToFirst();


            // return cursor
            return cursor;
        }*/

    }

        //methods for handeling image data and converting back and forth between bitmap and bytes
        // convert from bitmap to byte array
        public static byte[] getBytes(Bitmap bitmap) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.PNG, 0, stream);
            return stream.toByteArray();
        }

        // convert from byte array to bitmap
        public static Bitmap getImage(byte[] image) {
            int len = image.length;
            return BitmapFactory.decodeByteArray(image, 0, len);
        }

    // Method for opening database
    public DBHandler open(){
        db = dbhelper.getWritableDatabase();
        return this;
    }

    // Method for closing database
    public void close(){
        dbhelper.close();
    }

    // Methods for adsTable
    public long insertAd (String adTitle, String userName, String adDescription, String imageURL, String phone, int isMyAd, byte[] image, String objectID, String gameName,String collectedLetters){
        ContentValues content = new ContentValues();
        content.put(TITLE, adTitle);
        content.put(USER_NAME, userName);
        content.put(DESCRIPTION, adDescription);
        content.put(IMAGE_URL, imageURL);
        content.put(PHONE, phone);
        content.put(IS_MY_AD, isMyAd);
        content.put(KEY_IMG,   image);
        content.put(OBJ_ID,   objectID);
        content.put(GAME_NAME, gameName);
        content.put(COLLECTED_LETTERS, collectedLetters);

        return db.insertOrThrow(ADS_TABLE, null, content);
    }

    public long selectLastInsearted(){
        String selectQuery = "SELECT _id FROM " + ADS_TABLE;
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToLast();
        return cursor.getLong(0);
    }

    public long GetAdId(String objectId){
        String selectQuery = "SELECT _id as _id FROM " + ADS_TABLE +" WHERE object_id = '"+objectId+"' AND isMyAd = 0" ;
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToNext();
        return cursor.getLong(0);
    }
    public Cursor searchAdbyID (long adId){
        return db.query(ADS_TABLE, new String[]{TITLE, USER_NAME, DESCRIPTION, IMAGE_URL, PHONE, IS_MY_AD, KEY_IMG, OBJ_ID, GAME_NAME, COLLECTED_LETTERS}, "_id=" + adId, null, null, null, null, null);
    }

   public Cursor searchAdbyObj_ID (String objId){
        return db.query(ADS_TABLE, new String[]{TITLE, USER_NAME, DESCRIPTION, IMAGE_URL, PHONE, IS_MY_AD, KEY_IMG, OBJ_ID, GAME_NAME,COLLECTED_LETTERS}, OBJ_ID + "=?",
                    new String[] { String.valueOf(objId) }, null, null, null, null);
    }



    public Cursor searchAllAds (){
        return db.query(ADS_TABLE, new String[]{AD_ID, TITLE, USER_NAME, DESCRIPTION, IMAGE_URL, PHONE, IS_MY_AD,KEY_IMG,OBJ_ID,GAME_NAME,COLLECTED_LETTERS}, null,null, null, null, null,null);
    }

    public int deleteAd(long adId)
    {
        return db.delete(ADS_TABLE, "_id=" + adId, null);
    }

    public void UpdateColumn(String ColumnName,String newValue,Long adID){
        String selectQuery = "UPDATE " + ADS_TABLE + " SET "+ColumnName+" = '"+newValue +"' WHERE _id = "+adID;
        db.execSQL(selectQuery);
    }
    
}

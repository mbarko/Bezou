package easyconnect.example.com.easyconnect;

/**
 * Created by nisalperera on 15-09-05.
 *
 * This object will encapsulate contact info of each person
 */
public class DataObject {

    private String mText1;
    private String mText2;
    //Game type
    private Long ad_id;
    private String imageURL;
    private String object_id;
    private byte[] image;

    DataObject(String text1, String text2 , Long id, String URL, String objid,byte[] img){
        mText1 = text1;
        mText2 = text2;
        ad_id = id;
        imageURL = URL;
        object_id = objid;
        image = img;
    }

    public String getmText1() {
        return mText1;
    }

    public void setmText1(String mText1) {
        this.mText1 = mText1;
    }

    public String getmText2() {
        return mText2;
    }

    public void setmText2(String mText2) { this.mText2 = mText2; }




    public Long getadId() {
        return ad_id;
    }

    public String getImageURL() { return imageURL;}

    public String getObjectID() { return object_id;}
    public byte[] getImage() { return image;}
}
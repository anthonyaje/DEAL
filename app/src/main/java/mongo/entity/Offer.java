package mongo.entity;

import mongo.entity.base.MongoObj;

import com.mongodb.DBObject;

/**
 * @author Lalala
 */
public class Offer extends MongoObj {

    public Offer() {
        collectionName = "Offer";
        columns = new String[]{"id", "user_id", "hashtag", "detail", "range", "gpsLat", "gpsLong", "request_time", "valid_time", "complete", "picture"};
    }

    public Offer(DBObject instance) {
        collectionName = "Offer";
        columns = new String[]{"id", "user_id", "hashtag", "detail", "range", "gpsLat", "gpsLong", "request_time", "valid_time", "complete", "picture"};
        for (int i = 0; i < columns.length; i++) {
            this.setValue(i, instance.get(columns[i]));
        }
    }

    private String user_id;
    private String hashtag;
    private String detail;
    private int range;
    private double gpsLat;
    private double gpsLong;
    private long request_time;
    private long valid_time;
    private int complete;
    private byte[] picture;

    @Override
    public Object getValue(int id) {
        switch (id) {
            case 0:
                return getId();
            case 1:
                return getUser_id();
            case 2:
                return getHashtag();
            case 3:
                return getDetail();
            case 4:
                return getRange();
            case 5:
                return getGpsLat();
            case 6:
                return getGpsLong();
            case 7:
                return getRequest_time();
            case 8:
                return getValid_time();
            case 9:
                return getComplete();
            case 10:
                return getPicture();
            default:
                return null;
        }
    }

    @Override
    public final void setValue(int column, Object value) {
        switch (column) {
            case 0:
                if (value != null)
                    setId(value.toString());
                break;
            case 1:
                if (value != null)
                    setUser_id(value.toString());
                break;
            case 2:
                if (value != null)
                    setHashtag(value.toString());
                break;
            case 3:
                if (value != null)
                    setDetail(value.toString());
                break;
            case 4:
                if (value != null)
                    setRange(Integer.parseInt(value.toString()));
                break;
            case 5:
                if (value != null)
                    setGpsLat(Double.parseDouble(value.toString()));
                break;
            case 6:
                if (value != null)
                    setGpsLong(Double.parseDouble(value.toString()));
                break;
            case 7:
                if (value != null)
                    setRequest_time(Long.parseLong(value.toString()));
                break;
            case 8:
                if (value != null)
                    setValid_time(Long.parseLong(value.toString()));
                break;
            case 9:
                if (value != null)
                    setComplete(Integer.parseInt(value.toString()));
                break;
            case 10:
                if (value != null)
                    setPicture((byte[]) value);
                break;
        }
    }

    /**
     * @return the user_id
     */
    public String getUser_id() {
        return user_id;
    }

    /**
     * @param user_id the user_id to set
     */
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    /**
     * @return the hashtag
     */
    public String getHashtag() {
        return hashtag;
    }

    /**
     * @param hashtag the hashtag to set
     */
    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    /**
     * @return the detail
     */
    public String getDetail() {
        return detail;
    }

    /**
     * @param detail the detail to set
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     * @return the range
     */
    public int getRange() {
        return range;
    }

    /**
     * @param range the range to set
     */
    public void setRange(int range) {
        this.range = range;
    }

    /**
     * @return the gpsLat
     */
    public double getGpsLat() {
        return gpsLat;
    }

    /**
     * @param gpsLat the gpsLat to set
     */
    public void setGpsLat(double gpsLat) {
        this.gpsLat = gpsLat;
    }

    /**
     * @return the gpsLong
     */
    public double getGpsLong() {
        return gpsLong;
    }

    /**
     * @param gpsLong the gpsLong to set
     */
    public void setGpsLong(double gpsLong) {
        this.gpsLong = gpsLong;
    }

    /**
     * @return the request_time
     */
    public long getRequest_time() {
        return request_time;
    }

    /**
     * @param request_time the request_time to set
     */
    public void setRequest_time(long request_time) {
        this.request_time = request_time;
    }

    /**
     * @return the valid_time
     */
    public long getValid_time() {
        return valid_time;
    }

    /**
     * @param valid_time the valid_time to set
     */
    public void setValid_time(long valid_time) {
        this.valid_time = valid_time;
    }

    /**
     * @return the complete
     */
    public int getComplete() {
        return complete;
    }

    /**
     * @param complete the complete to set
     */
    public void setComplete(int complete) {
        this.complete = complete;
    }

    /**
     * @return the picture
     */
    public byte[] getPicture() {
        return picture;
    }

    /**
     * @param picture the picture to set
     */
    public void setPicture(byte[] picture) {
        this.picture = picture;
    }
}

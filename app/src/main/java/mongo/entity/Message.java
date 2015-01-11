package mongo.entity;

import mongo.entity.base.MongoObj;

import com.mongodb.DBObject;

/**
 * @author Lalala
 */
public class Message extends MongoObj {
    public Message() {
        collectionName = "Message";
        columns = new String[]{"id", "user_1", "user_2", "timestamp", "message", "offer_id", "request_id", "read_timestamp"};
    }

    public Message(DBObject instance) {
        collectionName = "Message";
        columns = new String[]{"id", "user_1", "user_2", "timestamp", "message", "offer_id", "request_id", "read_timestamp"};
        for (int i = 0; i < columns.length; i++) {
            this.setValue(i, instance.get(columns[i]));
        }
    }

    private String user_1;
    private String user_2;
    private long timestamp;
    private String message;
    private String offer_id;
    private String request_id;
    private long read_timestamp;

    @Override
    public Object getValue(int id) {
        switch (id) {
            case 0:
                return getId();
            case 1:
                return getUser_1();
            case 2:
                return getUser_2();
            case 3:
                return getTimestamp();
            case 4:
                return getMessage();
            case 5:
                return getOffer_id();
            case 6:
                return getRequest_id();
            case 7:
                return getRead_timestamp();
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
                    setUser_1(value.toString());
                break;
            case 2:
                if (value != null)
                    setUser_2(value.toString());
                break;
            case 3:
                if (value != null)
                    setTimestamp(Long.parseLong(value.toString()));
                break;
            case 4:
                if (value != null)
                    setMessage(value.toString());
                break;
            case 5:
                if (value != null)
                    setOffer_id(value.toString());
                break;
            case 6:
                if (value != null)
                    setRequest_id(value.toString());
                break;
            case 7:
                if (value != null)
                    setRead_timestamp(Long.parseLong(value.toString()));
                break;
        }
    }

    /**
     * @return the user_1
     */
    public String getUser_1() {
        return user_1;
    }

    /**
     * @param user_1 the user_1 to set
     */
    public void setUser_1(String user_1) {
        this.user_1 = user_1;
    }

    /**
     * @return the user_2
     */
    public String getUser_2() {
        return user_2;
    }

    /**
     * @param user_2 the user_2 to set
     */
    public void setUser_2(String user_2) {
        this.user_2 = user_2;
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public String getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(String offer_id) {
        this.offer_id = offer_id;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public long getRead_timestamp() {
        return read_timestamp;
    }

    public void setRead_timestamp(long read_timestamp) {
        this.read_timestamp = read_timestamp;
    }
}

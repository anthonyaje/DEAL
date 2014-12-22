package cloud.entity;

import cloud.entity.base.MongoObj;
import com.mongodb.DBObject;

/**
 *
 * @author Lalala
 */
public class Message extends MongoObj {
    public Message() 
    {
        this.collectionName = "Message";
        columns = new String[] {"id", "user_1", "user_2", "timestamp", "message"};
    }
    
    public Message(DBObject instance)
    {
        this.collectionName = "Message";
        columns = new String[] {"id", "user_1", "user_2", "timestamp", "message"};
        for(int i=1; i<columns.length; i++)
        {
            this.setValue(i, instance.get(columns[i]));
        }
    }
    
    private String user_1;
    private String user_2;
    private long timestamp;
    private String message;

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
            default:
                return null;
        }
    }

    @Override
    public final void setValue(int column, Object value) {
        switch (column) {
            case 0:
                // Couldn't change ID
                break;
            case 1:
                setUser_1(value.toString());
                break;
            case 2:
                setUser_2(value.toString());
                break;
            case 3:
                setTimestamp(Long.parseLong(value.toString()));
                break;
            case 4:
                setMessage(value.toString());
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
}

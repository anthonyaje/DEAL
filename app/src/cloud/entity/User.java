package cloud.entity;

import cloud.entity.base.MongoObj;
import com.mongodb.DBObject;

/**
 *
 * @author Lalala
 */
public class User extends MongoObj {

    public User() {
        collectionName = "User";
        columns = new String[]{"id", "username", "password", "email", "picture"};
    }
    
    public User(DBObject instance)
    {
        collectionName = "User";
        columns = new String[]{"id", "username", "password", "email", "picture"};
        for(int i=1; i<columns.length; i++)
        {
            this.setValue(i, instance.get(columns[i]));
        }
    }

    private String _username;
    private String _password;
    private String _email;
    private byte[] _picture;

    @Override
    public Object getValue(int id) {
        switch (id) {
            case 0:
                return getId();
            case 1:
                return getUsername();
            case 2:
                return getPassword();
            case 3:
                return getEmail();
            case 4:
                return getPicture();
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
                setUsername(value.toString());
                break;
            case 2:
                setPassword(value.toString());
                break;
            case 3:
                setEmail(value.toString());
                break;
            case 4:
                setPicture((byte[])value);
                break;
        }
    }

    /**
     * @return the _username
     */
    public String getUsername() {
        return _username;
    }

    /**
     * @param _username the _username to set
     */
    public void setUsername(String _username) {
        this._username = _username;
    }

    /**
     * @return the _password
     */
    public String getPassword() {
        return _password;
    }

    /**
     * @param _password the _password to set
     */
    public void setPassword(String _password) {
        this._password = _password;
    }

    /**
     * @return the _email
     */
    public String getEmail() {
        return _email;
    }

    /**
     * @param _email the _email to set
     */
    public void setEmail(String _email) {
        this._email = _email;
    }

    /**
     * @return the _picture
     */
    public byte[] getPicture() {
        return _picture;
    }

    /**
     * @param _picture the _picture to set
     */
    public void setPicture(byte[] _picture) {
        this._picture = _picture;
    }
}

package mongo.entity;

import mongo.entity.base.MongoObj;

import com.mongodb.DBObject;

/**
 * @author Lalala
 */
public class Setting extends MongoObj {

    public Setting() {
        this.collectionName = "Setting";
        columns = new String[]{"id", "user_id", "default_range"};
        settings = new String[columns.length - 2];
    }

    public Setting(DBObject instance) {
        this.collectionName = "Setting";
        columns = new String[]{"id", "user_id", "default_range"};
        settings = new String[columns.length - 2];
        for (int i = 1; i < columns.length; i++) {
            this.setValue(i, instance.get(columns[i]));
        }
    }

    private String user_id;
    private String[] settings;

    @Override
    public Object getValue(int id) {
        if (id == 0) {
            return getId();
        } else if (id == 1) {
            return getUser_id();
        } else if (id - 2 < settings.length) {
            return getSettings()[id - 2];
        } else {
            return null;
        }
    }

    @Override
    public final void setValue(int column, Object value) {
        if (column == 0) {
            // Empty statement
        } else if (column == 1) {
            setUser_id(value.toString());
        } else if (column - 2 < settings.length) {
            settings[column - 2] = value.toString();
        }
    }

    /**
     * @return the user_id
     */
    public String getUser_id() {
        return user_id;
    }

    /**
     * @return the settings
     */
    public String[] getSettings() {
        return settings;
    }

    /**
     * @param user_id the user_id to set
     */
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}

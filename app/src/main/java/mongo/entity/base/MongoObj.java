package mongo.entity.base;

import mongo.controller.DbController;
import mongo.controller.ImageController;
import mongo.exception.MongoException;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.WriteResult;

/**
 *
 * @author Lalala
 */
public abstract class MongoObj {

    /**
     * Our program generated ID
     */
    protected String _id;
    /**
     * Name of this "Table" - MongoDB Collection
     */
    protected static String collectionName = "MongoObj";
    /**
     * Columns for MongoDB
     */
    protected static String columns[];

    /**
     * Initalize id
     */
    public MongoObj() {
        this._id = DbController.generateUID();
    }

    /**
     * Generate Basic DB Object to be saved into database
     * @return 
     */
    public BasicDBObject saveMongoDB() {
        BasicDBObject doc = new BasicDBObject();
        for (int i = 0; i < columns.length; i++) {
            if (getValue(i) != null) {
                doc.append(columns[i], getValue(i));
            }
        }
        return doc;
    }
    
    /**
     * Insert data into database
     * @param data Data to be put
     * @param name Collection name
     * @return null
     */
    public static String insertData(MongoObj data, String name)
    {
        return insertData(data, name, null);
    }
    
    /**
     * Insert data into database, with some picture attached
     * @param data Data to be put
     * @param name Collection name
     * @param image
     * @return ImageID
     */
    public static String insertData(MongoObj data, String name, byte[] image)
    {
        BasicDBObject saveMongoDB = data.saveMongoDB();
        String imgId = null;
        if(image!=null){
            System.out.println("Image is not null");
            imgId = insertImage(image, data.getId());
        }
        try {
            WriteResult insertData = DbController.getInstance().insertData(name, saveMongoDB);
            if(insertData!=null) {
                if (insertData.getError() == null)
                    System.out.println("Insert success");
            }
        } catch (MongoException ex) {
            System.err.println("Insert data error");
        }
        return imgId;
    }
    
    public static String insertImage(byte[] image, String instanceId)
    {
        DB db = DbController.getInstance().getDB();
        String fsId = ImageController.getInstance().StoreImageFile(instanceId, image, db);
        System.out.println("fsId: "+fsId);
        return fsId;
    }

    /**
     * Set the value of the object in specific column
     * @param column
     * @param value 
     */
    public abstract void setValue(int column, Object value);
    /**
     * Get value of the object in specific column
     * @param id column id
     * @return 
     */
    public abstract Object getValue(int id);

    /**
     * @return the collectionName
     */
    public static String getCollectionName() {
        return collectionName;
    }

    /**
     * @return the columns
     */
    public static String[] getColumns() {
        return columns;
    }

    /**
     * @return the _id
     */
    public String getId() {
        return _id;
    }

}

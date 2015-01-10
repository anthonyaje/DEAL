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
    protected String collectionName = "MongoObj";
    /**
     * Columns for MongoDB
     */
    protected String columns[];

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
     * @param data
     * @param collection 
     * @return null
     */
    public String insertData(MongoObj data, DBCollection collection)
    {
        return insertData(data, collection, null);
    }
    
    /**
     * Insert data into database, with some picture attached
     * @param data
     * @param collection
     * @param image
     * @return ImageID
     */
    public String insertData(MongoObj data, DBCollection collection, byte[] image)
    {
        BasicDBObject saveMongoDB = data.saveMongoDB();
        String imgId = null;
        if(image!=null){
            System.out.println("Image is not null");
            imgId = insertImage(image, data.getId());
        }
        try {
            WriteResult insertData = DbController.getInstance().insertData(collection, saveMongoDB);
            if(insertData!=null) {
                if (insertData.getError() == null)
                    System.out.println("Insert success");
            }
        } catch (MongoException ex) {
            System.err.println("Insert data error");
        }
        return imgId;
    }
    
    public String insertImage(byte[] image, String instanceId)
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
    public String getCollectionName() {
        return collectionName;
    }

    /**
     * @return the columns
     */
    public String[] getColumns() {
        return columns;
    }

    /**
     * @return the _id
     */
    public String getId() {
        return _id;
    }

}

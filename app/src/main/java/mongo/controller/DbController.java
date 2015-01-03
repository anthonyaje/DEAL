package mongo.controller;

import mongo.exception.MongoException;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Lalala
 */
public class DbController {

    public static String IP = "140.113.216.123";
    public static String database = "cloud";
    Mongo mongoClient;

    private DbController() {
    }

    public static DbController getInstance() {
        return DbControllerHolder.INSTANCE;
    }

    private static class DbControllerHolder {

        private static final DbController INSTANCE = new DbController();
    }
    
    public static String generateUID()
    {
        UUID id = UUID.randomUUID();
        return id.toString();
    }
    
    public DB getDB()
    {
        return mongoClient.getDB(database);
    }

    public void Open() {
        try {
            mongoClient = new Mongo(IP);
        } catch (UnknownHostException ex) {
            System.err.println("Unknown host exception : Open()\n" + ex.getMessage());
        }
    }

    public void Close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    /**
     * Get all data from table / collection
     * @param tablename Table name or Collection name
     * @return 
     */
    public DBCollection getCollection(String tablename) {
        DB db = mongoClient.getDB(database);
        DBCollection coll = db.getCollection(tablename);
        return coll;
    }
    
    /**
     * Return a query of data
     * @param coll Collection instance
     * @param column Column to be filtered
     * @param value Value to be filtered
     * @return 
     */
    public DBCursor filterCollection(DBCollection coll, String column, String value) {
        BasicDBObject query = new BasicDBObject(column, value);
        DBCursor find = coll.find(query);
        return find;
    }
    
    /**
     * Get all value, separated by delimiter
     * @param find Db cursor (A query)
     * @param columnNames Names of each column
     * @param delimiter delimiter to split each column's value
     * @return 
     */
    public List<String> showAllValue(DBCursor find, String[] columnNames, char delimiter) {
        List<String> result = new ArrayList();
        while (find.hasNext()) {
            DBObject next = find.next();
            StringBuilder sb = new StringBuilder();
            for(int i=0; i<columnNames.length; i++)
            {
                sb.append(next.get(columnNames[i]).toString());
                if(i<(columnNames.length-1)) sb.append(delimiter);
            }
        }
        return result;
    }
    
    /**
     * Delete a table / collection, please use it with care
     * @param tablename 
     */
    public void deleteCollection(String tablename)
    {
        getCollection(tablename).drop();
    }
    
    /**
     * Count the number of row inside one table / collection
     * @param tablename
     * @return 
     */
    public long countCollection(String tablename)
    {
        return getCollection(tablename).count();
    }
    
    /**
     * Insert a data to a table / collection
     * @param coll
     * @param doc
     * @return
     * @throws MongoException 
     */
    public WriteResult insertData(DBCollection coll, BasicDBObject doc) throws MongoException {
        if(mongoClient==null) throw new MongoException("Mongo Client is closed while inserting data");
        if(coll==null) throw new MongoException("Collection is null while inserting data");
        if(doc==null) throw new MongoException("Basic DB Object is null while inserting data");
        mongoClient.setWriteConcern(WriteConcern.JOURNAL_SAFE);
        return coll.insert(doc);
    }
}

package mongo.controller;

import mongo.controller.async.TaskCount;
import mongo.controller.async.TaskDelete;
import mongo.controller.async.TaskFilterCollection;
import mongo.controller.async.TaskFindOne;
import mongo.controller.async.TaskGetCollection;
import mongo.controller.async.TaskInsert;
import mongo.controller.async.TaskShowValue;
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
import java.util.concurrent.ExecutionException;

/**
 *
 * @author Lalala
 */
public class DbController {

    public static String IP = "140.113.216.123";
    public static String database = "cloud";
    private Mongo mongoClient;

    private DbController() {
    }

    public static DbController getInstance() {
        return DbControllerHolder.INSTANCE;
    }

    private static class DbControllerHolder {

        private static final DbController INSTANCE = new DbController();
    }

    public Mongo getMongoClient() {
        return mongoClient;
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

    public Mongo Open() {
        try {
            mongoClient = new Mongo(IP);
            return mongoClient;
        } catch (UnknownHostException ex) {
            System.err.println("Unknown host exception : Open()\n" + ex.getMessage());
            return null;
        }
    }

    public void Close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    /**
     * Get all data from table / collection (Async)
     * @param table Table name or Collection name
     * @return collection (table)
     * @return collection (table)
     */
    public DBCollection getCollection(String table) {
        TaskGetCollection task = new TaskGetCollection();
        task.execute(table);
        try {
            DBCollection collection = task.get();
            return collection;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Return a query of data
     * @param coll Collection instance
     * @param column Column to be filtered
     * @param value Value to be filtered
     * @return 
     */
    public DBCursor filterCollection(String coll, String column, String value) {
        TaskFilterCollection task = new TaskFilterCollection();
        task.execute(coll, column, value);
        try {
            DBCursor find = task.get();
            return find;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Return all data
     * @param coll Collection instance
     * @return
     */
    public DBCursor findAll(String coll) {
        TaskFilterCollection task = new TaskFilterCollection();
        task.execute(coll);
        try {
            DBCursor find = task.get();
            return find;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Return 1 data
     * @param table Table name
     * @return
     */
    public DBObject findOne(String table) {
        TaskFindOne task = new TaskFindOne();
        task.execute(table);
        try {
            DBObject find = task.get();
            return find;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get all value, separated by delimiter
     * @param find Db cursor (A query)
     * @param columnNames Names of each column
     * @param delimiter delimiter to split each column's value
     * @return 
     */
    public List<String> showAllValue(DBCursor find, String[] columnNames, char delimiter) {
        TaskShowValue task = new TaskShowValue();
        task.execute(find, columnNames, delimiter);
        try {
            List<String> result = task.get();
            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Delete a table / collection, please use it with care
     * @param tablename 
     */
    public void deleteCollection(String tablename)
    {
        TaskDelete task = new TaskDelete();
        task.execute(tablename);
    }
    
    /**
     * Count the number of row inside one table / collection
     * @param tablename
     * @return 
     */
    public long countCollection(String tablename)
    {
        TaskCount task = new TaskCount();
        task.execute(tablename);
        try {
            Long result = task.get();
            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -1;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Insert a data to a table / collection
     * @param coll
     * @param doc
     * @return
     * @throws MongoException 
     */
    public WriteResult insertData(String coll, BasicDBObject doc) throws MongoException {
        TaskInsert task = new TaskInsert();
        task.execute(coll, doc);
        try {
            WriteResult result = task.get();
            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}

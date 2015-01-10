package mongo.controller.async;

import android.os.AsyncTask;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;

import mongo.controller.DbController;

/**
 * Created by Lalala on 1/6/15.
 */
public class TaskFilterCollection extends AsyncTask<Object, Void, DBCursor> {
    @Override
    protected DBCursor doInBackground(Object... params) {
        if(params==null || params.length!=3) return null;
        Mongo mongoClient = DbController.getInstance().Open();
        if(mongoClient==null) return null;
        DB db = mongoClient.getDB(DbController.database);
        DBCollection coll = db.getCollection(params[0].toString());    // Table's name
        String column = (String) params[1];
        String value = (String) params[2];
        DBCursor find = null;
        if(column==null && value==null){
            find = coll.find();
        }
        else
        {
            BasicDBObject query = new BasicDBObject(column, value);
            find = coll.find(query);
        }
        DbController.getInstance().Close();
        return find;
    }
}

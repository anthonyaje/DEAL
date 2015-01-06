package mongo.controller.async;

import android.os.AsyncTask;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import mongo.controller.DbController;

/**
 * Created by Lalala on 1/6/15.
 */
public class TaskFindOne extends AsyncTask<String, Void, DBObject> {
    @Override
    protected DBObject doInBackground(String... params) {
        if(params==null || params.length!=1) return null;
        Mongo mongoClient = DbController.getInstance().Open();
        if(mongoClient==null) return null;
        DB db = mongoClient.getDB(DbController.database);
        DBCollection coll = db.getCollection(params[0]);    // Table's name
        DBObject obj = coll.findOne();
        DbController.getInstance().Close();
        return obj;
    }

}

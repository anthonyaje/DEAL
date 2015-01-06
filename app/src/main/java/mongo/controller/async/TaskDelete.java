package mongo.controller.async;

import android.os.AsyncTask;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

import mongo.controller.DbController;

/**
 * Created by Lalala on 1/6/15.
 */
public class TaskDelete extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... params) {
        if(params==null || params.length!=1) return null;
        Mongo mongoClient = DbController.getInstance().Open();
        if(mongoClient==null) return null;
        DB db = mongoClient.getDB(DbController.database);
        DBCollection coll = db.getCollection(params[0]);    // Table's name
        coll.drop();
        DbController.getInstance().Close();
        return null;
    }
}

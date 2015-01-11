package mongo.controller.async;

import android.os.AsyncTask;
import android.util.Log;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import java.util.ArrayList;
import java.util.List;

import mongo.controller.DbController;

/**
 * Created by Lalala on 1/6/15.
 */
public class TaskFilterCollection extends AsyncTask<Object, Void, List<DBObject>> {
    String TAG = "DEAL_" + TaskFilterCollection.class;

    @Override
    protected List<DBObject> doInBackground(Object... params) {
        if (params == null || params.length < 1) return null;
        Mongo mongoClient = DbController.getInstance().Open();
        if (mongoClient == null) return null;
        DB db = mongoClient.getDB(DbController.database);
        DBCollection coll = db.getCollection(params[0].toString());    // Table's name
        String column = null;
        String value = null;
        if (params.length == 3) {
            column = (String) params[1];
            value = (String) params[2];
        }
        DBCursor find = null;
        List<DBObject> result = new ArrayList();
        if (column == null && value == null) {
            find = coll.find();
        } else {
            BasicDBObject query = new BasicDBObject(column, value);
            find = coll.find(query);
        }
        if(find!=null)
        {
            while (find.hasNext()) {
                DBObject next = find.next();
                result.add(next);
                Log.d(TAG, next.toString());
            }
        }
        DbController.getInstance().Close();
        return result;
    }
}

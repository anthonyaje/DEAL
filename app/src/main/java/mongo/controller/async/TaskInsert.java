package mongo.controller.async;

import android.os.AsyncTask;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

import mongo.controller.DbController;
import mongo.exception.MongoException;

/**
 * Created by Lalala on 1/6/15.
 */
public class TaskInsert extends AsyncTask<Object, Void, WriteResult> {

    @Override
    protected WriteResult doInBackground(Object... params) {
        if(params==null || params.length!=2) return null;
        Mongo mongoClient = DbController.getInstance().Open();
        if(mongoClient==null) return null;
        DBCollection coll = (DBCollection) params[0];
        BasicDBObject doc = (BasicDBObject) params[1];
        if(coll==null) return null;
        if(doc==null) return null;
        mongoClient.setWriteConcern(WriteConcern.JOURNAL_SAFE);
        WriteResult writeResult = coll.insert(doc);
        DbController.getInstance().Close();
        return writeResult;
    }
}

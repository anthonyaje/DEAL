package mongo;

import android.os.AsyncTask;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import java.net.UnknownHostException;

import mongo.entity.User;

/**
 * Created by Lalala on 1/3/15.
 */
public class Connect extends AsyncTask<String, Void, String> {


    @Override
    protected String doInBackground(String... params) {
        try
        {
            Mongo mongoClient = null;
            try {
                mongoClient = new Mongo("140.113.216.123");
                DB db = mongoClient.getDB("cloud");
                DBCollection coll = db.getCollection("User");
                System.out.println("COUNT : "+coll.count());
                DBObject o = coll.findOne();
                System.out.println("USERNAME : "+o.get("username"));
                return o.get("username") + "";
            } catch (UnknownHostException ex) {
                System.err.println("Unknown host exception : Open()\n" + ex.getMessage());
            } finally {
                if(mongoClient!=null)
                    mongoClient.close();
            }
        }
        catch(Exception e)
        {
            System.out.println("ERRRORRRRR: "+e.getMessage());
        }
        return null;
    }
}

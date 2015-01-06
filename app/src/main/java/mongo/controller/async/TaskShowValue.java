package mongo.controller.async;

import android.os.AsyncTask;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import java.util.ArrayList;
import java.util.List;

import mongo.controller.DbController;

/**
 * Created by Lalala on 1/6/15.
 */
public class TaskShowValue extends AsyncTask<Object, Void, List<String>> {
    @Override
    protected List<String> doInBackground(Object... params) {
        if (params == null || params.length != 3) return null;
        Mongo mongoClient = DbController.getInstance().Open();
        if (mongoClient == null) return null;

        DBCursor find = (DBCursor) params[0];
        String[] columnNames = (String[]) params[1];
        Character delimiter = (Character) params[2];

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

        DbController.getInstance().Close();
        return result;
    }
}

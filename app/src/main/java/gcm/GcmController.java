package gcm;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * Created by Lalala on 1/11/15.
 */
public class GcmController {
    String TAG = "DEAL"+GcmController.class;
    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    public static final String SENDER_ID = "782343216258";
    /**
     * API KEY for GCM
     */
    public static final String API_KEY = "AIzaSyB0fIJkZ4j_N94utV5c-i90dgNE43-xCB4";
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int NUMBER_OF_TRIAL = 5;

    private GcmController() {
    }

    public static GcmController getInstance() {
        return WriterHolder.INSTANCE;
    }

    private static class WriterHolder {

        private static final GcmController INSTANCE = new GcmController();
    }

    /**
     * @param content   Message content
     * @param target_id Destination Id
     */
    public void sendMessage(final String content, final String target_id) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String target;
                target = target_id;
                Sender s = new Sender(GcmController.API_KEY);
                Message m = new Message.Builder()
                        .addData("message", content)
                        .addData("sender", "Server")
                        .build();
                try {
                    Result send = s.send(m, target, NUMBER_OF_TRIAL); // Try 5 times
                    Log.i(TAG, send.toString());
                    return send.toString();
                } catch (IOException ex) {
                    Log.e(TAG, ex.getMessage());
                    return ex.getMessage();
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                    return ex.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String msg) {
                // Show something ???
            }
        }.execute(null, null, null);
    }

    /**
     * @param content   Message content
     * @param target_list Destination Id
     */
    public void sendMessage(final String content, final List<String> target_list) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String target;
                Sender s = new Sender(GcmController.API_KEY);
                Message m = new Message.Builder()
                        .addData("message", content)
                        .addData("sender", "Server")
                        .build();
                try {
                    MulticastResult send = s.send(m, target_list, NUMBER_OF_TRIAL); // Try 5 times
                    Log.i(TAG, send.toString());
                    return send.toString();
                } catch (IOException ex) {
                    Log.e(TAG, ex.getMessage());
                    return ex.getMessage();
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                    return ex.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String msg) {
                // Show something ???
            }
        }.execute(null, null, null);
    }
}

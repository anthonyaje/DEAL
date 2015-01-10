package com.deal.aje.deal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mongo.controller.DbController;
import mongo.controller.ImageController;
import mongo.entity.Setting;
import mongo.entity.User;

public class LoginActivity extends FragmentActivity {

    private LoginButton loginBtn;
    private TextView userName;
    private Button nextBtn;
    final Context context = this;
    private UiLifecycleHelper uiHelper;
    SharedPreferences sp = null;
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private static final String GPS_RANGE = "100";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        if(sp.contains("UserName")){
            Log.d("DEAL:"," not first time login");
            Intent homeIntent = new Intent(this, home.class);
            // homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            finish();
        }

        userName = (TextView) findViewById(R.id.user_name);
        loginBtn = (LoginButton) findViewById(R.id.fb_login_button);
        nextBtn = (Button) findViewById(R.id.btn_next);
        loginBtn.setUserInfoChangedCallback(new UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                if (user != null) {
                    //Store the username to memory
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("UserName",user.getName());
                    editor.commit();

                    String uname = sp.getString("UserName","null");
                    Log.d("DEAL_LOG","name from sp: "+uname);

                    //Store the new user to database
                    User u = new User();
                    u.setUsername(user.getName());

                    Log.d("DEAL_LOG","Before insert Data");
                    u.insertData(u, u.getCollectionName());
                    Log.d("DEAL_LOG","After insert Data");
            /*
                    u = new User();
                    collection = DbController.getInstance().getCollection(u.getCollectionName());

                    DBCursor cursor = DbController.getInstance().filterCollection(collection, u.getColumns()[1], uname);
                    while(cursor.hasNext())
                    {
                        DBObject next = cursor.next();
                        if(next!=null){
                            u = new User(next);
                            Log.d("DEAL_LOG","after insertData name: "+u.getUsername());
                        }
                    }

            */
                    //Input new setting for the user
                    Setting s = new Setting();
                    s.setUser_id(u.getId());
                    s.getSettings()[0] = GPS_RANGE;

                    userName.setText("Hello, " + user.getName());
                } else {
                    userName.setText("");
                }
            }
        });

        nextBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(context, home.class));
            }
        });
    }

    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            if (state.isOpened()) {
                nextBtn.setEnabled(true);
                Log.d("FacebookSampleActivity", "Facebook session opened");
            } else if (state.isClosed()) {
                nextBtn.setEnabled(false);
                Log.d("FacebookSampleActivity", "Facebook session closed");
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        uiHelper.onSaveInstanceState(savedState);
    }

}

/*
public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
*/
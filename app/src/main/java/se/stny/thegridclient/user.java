package se.stny.thegridclient;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import se.stny.thegridclient.util.DownloadImageTask;
import se.stny.thegridclient.util.UserSettings;

public class user extends Activity {


    private UserSettings ses;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ses = new UserSettings(getApplicationContext());

        ((TextView) findViewById(R.id.agentName)).setText(ses.getUserDetails().get(UserSettings.AGENT_NAME));
        new DownloadImageTask((ImageView) findViewById(R.id.img_profile))
                .execute(ses.getUserDetails().get(UserSettings.IMAGE_URL).replace("sz=50", "sz=200"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.user_logout) {
            ses.logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

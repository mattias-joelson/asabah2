package se.stny.thegridclient;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import se.stny.thegridclient.util.DownloadImageTask;
import se.stny.thegridclient.util.userSettings;

public class user extends ActionBarActivity {


    private userSettings ses;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ses = new userSettings(getApplicationContext());

        debug(ses.getUserDetails().get(userSettings.IMAGE_URL));
        ((TextView) findViewById(R.id.agentName)).setText(ses.getUserDetails().get(userSettings.AGENT_NAME));
        new DownloadImageTask((ImageView) findViewById(R.id.img_profile))
                .execute(ses.getUserDetails().get(userSettings.IMAGE_URL).replace("sz=50", "sz=200"));

        Button button = (Button) findViewById(R.id.button);
        button.setText("logout");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ses.logoutUser();

            }


        });
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void debug(String string) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, string, duration);
        toast.show();
    }
}

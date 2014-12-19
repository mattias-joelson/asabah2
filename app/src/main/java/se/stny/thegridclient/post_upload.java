package se.stny.thegridclient;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import se.stny.thegridclient.gridCom.gridCom;
import se.stny.thegridclient.util.userSettings;

public class post_upload extends Activity {
    private final String TAG = "post_upload.java";
    gridCom postData = null;
    private JSONObject obj;
    private EditText txtbox;
    private userSettings ses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ses = new userSettings(getApplicationContext());
        setContentView(R.layout.activity_post_upload);
        try {
            obj = new JSONObject(getIntent().getStringExtra("json"));
        } catch (JSONException ej) {
            Log.e(TAG, "Called without an json object. closing");
            finish();
        }
        txtbox = (EditText) findViewById(R.id.statusUpdate);
        Button btnWithText = (Button) findViewById(R.id.UpdateWithText);
        Button btnWithoutText = (Button) findViewById(R.id.UpdateWithoutText);

        postData = new gridCom("updatescore", getString(R.string.API_KEY));
        btnWithText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                debug(txtbox.toString());
                try {
                    obj.put("status", URLEncoder.encode(txtbox.getText().toString(), "UTF-8"));
                    obj.put("no_mime", String.valueOf(1));

                } catch (JSONException ej) {
                    Log.e(TAG, "JSONEXCEPTION:" + ej.getMessage());
                    ej.printStackTrace();
                } catch (UnsupportedEncodingException eue) {
                    Log.e(TAG, "UnsupportedEncodingException:" + eue.getMessage());
                    eue.printStackTrace();
                }
                fini();

            }
        });
        btnWithoutText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                fini();
            }
        });
    }

    private void debug(String string) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, string, duration);
        toast.show();
    }

    private void fini() {

        try {
            postData.addHttpPostsFromJson(obj);
            JSONObject res = postData.getJSONFromUrl();
            if (res.getInt("status") != 200) {
                Log.e(TAG, "got returncode " + res.getString("status"));
                Log.e(TAG, res.toString());
                Log.e(TAG, obj.toString());

            }
        } catch (JSONException ee) {
            Log.e(TAG, ee.getMessage());
            ee.printStackTrace();
        }
        ses.checkLogin(false, true);
        finish();
    }
}




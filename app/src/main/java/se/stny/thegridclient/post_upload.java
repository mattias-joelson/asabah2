package se.stny.thegridclient;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import se.stny.thegridclient.gridCom.gridCom;
import se.stny.thegridclient.util.userSettings;

public class post_upload extends Activity {
    private final String TAG = "post_upload.java";
    private gridCom postData = null;
    private JSONObject obj;
    private EditText textBox;
    private userSettings ses;
    private boolean sendEmail = true;

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
        textBox = (EditText) findViewById(R.id.statusUpdate);
        Button btnWithText = (Button) findViewById(R.id.UpdateWithText);
        Button btnWithoutText = (Button) findViewById(R.id.UpdateWithoutText);
        Button btnUpdateSilent = (Button) findViewById(R.id.btnUpdateSilent);
        Switch sw = (Switch) findViewById(R.id.switch1);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                sendEmail = isChecked;

            }
        });

        postData = new gridCom("updatescore", getString(R.string.API_KEY));
        btnWithText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                debug(textBox.toString());
                try {
                    obj.put("status", URLEncoder.encode(textBox.getText().toString(), "UTF-8"));
                    obj.put("no_mime", String.valueOf(1));

                } catch (JSONException ej) {
                    Log.e(TAG, "JSONException:" + ej.getMessage());
                    ej.printStackTrace();
                } catch (UnsupportedEncodingException eue) {
                    Log.e(TAG, "UnsupportedEncodingException:" + eue.getMessage());
                    eue.printStackTrace();
                }
                FinishUpload();

            }
        });
        btnUpdateSilent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                debug(textBox.toString());
                try {
                    obj.put("status", URLEncoder.encode("s", "UTF-8"));
                    obj.put("no_mime", String.valueOf(1));

                } catch (JSONException ej) {
                    Log.e(TAG, "JSONException:" + ej.getMessage());
                    ej.printStackTrace();
                } catch (UnsupportedEncodingException eue) {
                    Log.e(TAG, "UnsupportedEncodingException:" + eue.getMessage());
                    eue.printStackTrace();
                }
                FinishUpload();

            }
        });
        btnWithoutText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                FinishUpload();
            }
        });
    }

    private void debug(String string) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, string, duration);
        toast.show();
    }

    private void FinishUpload() {

        try {
            if (!sendEmail) {
                obj.put("no_email", String.valueOf(1));
            }
            postData.addHttpPostsFromJson(obj);
            JSONObject res = postData.getJSONFromUrl();
            if (res.getInt("status") != 200) {
                Log.e(TAG, "Got return code " + res.getString("status"));
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




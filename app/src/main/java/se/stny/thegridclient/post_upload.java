package se.stny.thegridclient;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
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

import se.stny.thegridclient.gridCom.GridCom;
import se.stny.thegridclient.util.UserSettings;

public class post_upload extends Activity {
    private final String TAG = "post_upload.java";
    private GridCom postData = null;
    private GridCom postDebugData = null;
    private JSONObject obj;
    private JSONObject dbgdata;
    private EditText textBox;
    private UserSettings ses;
    private boolean sendEmail = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ses = new UserSettings(getApplicationContext());

        setContentView(R.layout.activity_post_upload);
        try {
            obj = new JSONObject(getIntent().getStringExtra("json"));
            dbgdata = new JSONObject(getIntent().getStringExtra("dbgdata"));
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

        postData = new GridCom("updatescore", getString(R.string.API_KEY));
        postDebugData = new GridCom("setdata", getString(R.string.API_KEY));
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

        Switch mySwitch = (Switch) findViewById(R.id.switch2);
        JSONObject res = null;
        try {
            if (!sendEmail) {
                obj.put("no_email", String.valueOf(1));
            }
            postData.addHttpPostsFromJson(obj);
            res = postData.getJSONFromUrl();
            if (res.getInt("status") != 200) {
                Log.e(TAG, "Got return code " + res.getString("status"));
                Log.e(TAG, res.toString());
                Log.e(TAG, obj.toString());
                Log.e(TAG, dbgdata.toString());
            }
        } catch (JSONException ee) {
            Log.e(TAG, ee.getMessage());
            ee.printStackTrace();
        }
        if (mySwitch.isChecked()) {
            try {
                JSONObject resDebug = new JSONObject();
                resDebug.put("userId", ses.getUserDetails().get(UserSettings.AGENT_NAME));
                resDebug.put("scan-result", "#SCANRES#");
                resDebug.put("SentData", "#SENTDATA#");
                resDebug.put("response", "#RESPONSE#");
                String dbgStr = resDebug.toString();
                dbgStr = dbgStr.replaceAll("\"#SCANRES#\"", "[" + dbgdata.toString() + "]");
                dbgStr = dbgStr.replaceAll("\"#SENTDATA#\"", "[" + obj.toString() + "]");
                if (res == null) {
                    res = new JSONObject();
                    res.put("response", "null");
                }
                dbgStr = dbgStr.replaceAll("\"#RESPONSE#\"", "[" + res.toString() + "]");
                postDebugData.addHttpPost("user", ses.getUserDetails().get(UserSettings.USER_ID));
                postDebugData.addHttpPost("device", getDevice());
                postDebugData.addHttpPost("data", dbgStr);
                debug(postDebugData.getJSONFromUrl().toString());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        ses.checkLogin(false, true);
        finish();
    }

    private String getDevice() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}




package se.stny.thegridclient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;

import se.stny.thegridclient.ocr.ocrCallback;
import se.stny.thegridclient.ocr.ocrScanner;
import se.stny.thegridclient.util.tgcDataClass;
import se.stny.thegridclient.util.userSettings;

public class upload extends Activity implements ocrCallback<Integer, JSONObject, String, String> {
    public static final String DATA_PATH = Environment.
            getExternalStorageDirectory().toString()
            + "/TheGrid/";
    public static final String lang = "eng";
    private static final String TAG = "Upload.java";
    userSettings prefs;
    private Bitmap IMG;
    private tgcDataClass statsData[];
    private ProgressDialog pDialog;
    private int totalProgressTime;
    private int currentProgressTime;

    private JSONObject dbgData;//TODO: REMOVE DEBUG
    private Uri imgUri;
    private userSettings ses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ses = new userSettings(getApplicationContext());
        if (!ses.isLoggedIn()) {
            ses.checkLogin(true, false);
            finish();
            debug("You are currently not logged in");
        }

        allocate_tgcStruct();

        dbgData = new JSONObject();  //TODO: REMOVE DEBUG
        this.currentProgressTime = 0;
        prefs = new userSettings(getApplicationContext());
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                this.imgUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                try {
                    this.IMG = BitmapFactory.decodeStream(getContentResolver()
                            .openInputStream(this.imgUri));
                } catch (FileNotFoundException e) {
                    Log.e(TAG, e.getMessage());
                    Log.e(TAG, e.getCause().toString());
                }
            }
        } else {
            Log.e(TAG, "Reached end of line");
        }
        new ocrScanner(DATA_PATH, this.IMG, this.getAssets(), lang, this.statsData, this).execute();


    }


    private void allocate_tgcStruct() {
        this.statsData = new tgcDataClass[26];
        Resources res = getResources();
        this.statsData[0] = new tgcDataClass(
                res.getString(R.string.i_unique_visits),
                res.getString(R.string.g_unique_visits),
                res.getInteger(R.integer.n_unique_visists));
        this.statsData[1] = new tgcDataClass(
                res.getString(R.string.i_xm_collected),
                res.getString(R.string.g_xm_collected),
                res.getInteger(R.integer.n_xm_collected));
        this.statsData[2] = new tgcDataClass(
                res.getString(R.string.i_distance_walked),
                res.getString(R.string.g_distance_walked),
                res.getInteger(R.integer.n_distance_walked));
        this.statsData[3] = new tgcDataClass(
                res.getString(R.string.i_resonators_deployed),
                res.getString(R.string.g_resonators_deployed),
                res.getInteger(R.integer.n_resonators_deployed));
        this.statsData[4] = new tgcDataClass(
                res.getString(R.string.i_links_created),
                res.getString(R.string.g_links_created),
                res.getInteger(R.integer.n_links_created));
        this.statsData[5] = new tgcDataClass(
                res.getString(R.string.i_control_fields_created),
                res.getString(R.string.g_control_fields_created),
                res.getInteger(R.integer.n_control_fields_created));
        this.statsData[6] = new tgcDataClass(
                res.getString(R.string.i_mu),
                res.getString(R.string.g_mu),
                res.getInteger(R.integer.n_mu));
        this.statsData[7] = new tgcDataClass(
                res.getString(R.string.i_longest_link),
                res.getString(R.string.g_longest_link),
                res.getInteger(R.integer.n_longest_link));
        this.statsData[8] = new tgcDataClass(
                res.getString(R.string.i_largest_control_field),
                res.getString(R.string.g_largest_control_field),
                res.getInteger(R.integer.n_largest_control_field));
        this.statsData[9] = new tgcDataClass(
                res.getString(R.string.i_xm_recharged),
                res.getString(R.string.g_xm_recharged),
                res.getInteger(R.integer.n_xm_recharged),
                10);
        this.statsData[10] = new tgcDataClass(
                res.getString(R.string.i_xm_recharged2),
                res.getString(R.string.g_xm_recharged),
                res.getInteger(R.integer.n_xm_recharged),
                9);
        this.statsData[11] = new tgcDataClass(
                res.getString(R.string.i_upc),
                res.getString(R.string.g_upc),
                res.getInteger(R.integer.n_upc));
        this.statsData[12] = new tgcDataClass(
                res.getString(R.string.i_mods_deployed),
                res.getString(R.string.g_mods_deployed),
                res.getInteger(R.integer.n_mods_deployed));
        this.statsData[13] = new tgcDataClass(
                res.getString(R.string.i_resonators_destroyed),
                res.getString(R.string.g_resonators_destroyed),
                res.getInteger(R.integer.n_resonators_destroyed));
        this.statsData[14] = new tgcDataClass(
                res.getString(R.string.i_portals_neutralized),
                res.getString(R.string.g_portals_neutralized),
                res.getInteger(R.integer.n_portals_neutralized));
        this.statsData[15] = new tgcDataClass(
                res.getString(R.string.i_links_destroyed),
                res.getString(R.string.g_links_destroyed),
                res.getInteger(R.integer.n_links_destroyed));
        this.statsData[16] = new tgcDataClass(
                res.getString(R.string.i_control_fields_destroyed),
                res.getString(R.string.g_control_fields_destroyed),
                res.getInteger(R.integer.n_control_fields_destroyed));
        this.statsData[17] = new tgcDataClass(
                res.getString(R.string.i_max_time_portal_held),
                res.getString(R.string.g_max_time_portal_held),
                res.getInteger(R.integer.n_max_time_portal_held));
        this.statsData[18] = new tgcDataClass(
                res.getString(R.string.i_max_time_link_maintained),
                res.getString(R.string.g_max_time_link_maintained),
                res.getInteger(R.integer.n_max_time_link_maintained));
        this.statsData[19] = new tgcDataClass(
                res.getString(R.string.i_max_link_lengthxdays),
                res.getString(R.string.g_max_link_lengthxdays),
                res.getInteger(R.integer.n_max_link_lengthxdays));
        this.statsData[20] = new tgcDataClass(
                res.getString(R.string.i_max_time_field_held),
                res.getString(R.string.g_max_time_field_held),
                res.getInteger(R.integer.n_max_time_field_held));
        this.statsData[21] = new tgcDataClass(
                res.getString(R.string.i_largest_field_muxdays),
                res.getString(R.string.g_largest_field_muxdays),
                res.getInteger(R.integer.n_largest_field_muxdays));
        this.statsData[22] = new tgcDataClass(
                res.getString(R.string.i_mission_completed),
                res.getString(R.string.g_mission_completed),
                res.getInteger(R.integer.n_mission_completed));
        this.statsData[23] = new tgcDataClass(
                res.getString(R.string.i_hacks),
                res.getString(R.string.g_hacks),
                res.getInteger(R.integer.n_hacks));
        this.statsData[24] = new tgcDataClass(
                res.getString(R.string.i_portals_captured),
                res.getString(R.string.g_portals_captured),
                res.getInteger(R.integer.n_portals_captured));
        this.statsData[25] = new tgcDataClass(
                res.getString(R.string.i_portals_discovered),
                res.getString(R.string.g_portals_discovered),
                res.getInteger(R.integer.n_portals_discovered));

        this.totalProgressTime = this.statsData.length;
    }


    @Override
    public void ocrStart() {
        this.pDialog = new ProgressDialog(this);
        this.pDialog.setMessage("Preparing to start ");
        this.pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.pDialog.setIndeterminate(true);
        this.pDialog.setMax(this.totalProgressTime);
        this.pDialog.setProgress(0);

        pDialog.show();
    }

    @Override
    public void ocrRunning() {
        pDialog.setMessage("Reading picture");
    }

    @Override
    public void ocrCompleted(JSONObject data) {
        if (pDialog.isShowing()) {
            try {
                pDialog.dismiss();
            } catch (Exception e) {// nothing }

            }
            try {
                data.put("user", prefs.getUserDetails().get(userSettings.USER_ID));
                data.put("statcat_innovator", String.valueOf(9));


                Intent i = new Intent(getApplicationContext(), post_upload.class);
                i.putExtra("json", data.toString());
                startActivity(i);
                finish();


            } catch (Exception e) {
                debug("WTF");
                debug(e.getMessage());
            }

        }
    }

    @Override
    public void ocrDebugData(String str, String status) {
        try {
            this.dbgData.put(str, status);
        } catch (JSONException ee) {
            Log.i(TAG, ee.getMessage());
        }
    }

    @Override
    public void ocrUpdate(Integer val) {
        this.currentProgressTime += val;

        this.pDialog.setProgress(this.currentProgressTime);
    }


    private void debug(String string) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, string, duration);
        toast.show();
    }
}

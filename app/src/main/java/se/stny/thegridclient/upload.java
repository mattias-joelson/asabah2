package se.stny.thegridclient;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.util.Pair;

import java.util.Set;

import se.stny.thegridclient.util.tgcStruct;
import se.stny.thegridclient.util.userSettings;

public class upload extends Activity {
    public static final String DATA_PATH = Environment
            .getExternalStorageDirectory().toString() + "/TheGrid/";
    public static final String lang = "eng";
    private static final String TAG = "Upload.java";
    userSettings prefs;
    private Set<Pair<String, Integer>> mStatNames;
    private tgcStruct statsData[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allocatetgcStruct();
    }

    private void allocatetgcStruct() {
        this.statsData = new tgcStruct[24];
        Resources res = getResources();
        this.statsData[0] = new tgcStruct(res.getString(R.string.i_unique_visits), res.getString(R.string.g_unique_visits), res.getInteger(R.integer.n_unique_visists));
        this.statsData[1] = new tgcStruct(res.getString(R.string.i_xm_collected), res.getString(R.string.g_xm_collected), res.getInteger(R.integer.n_xm_collected));
        this.statsData[2] = new tgcStruct(res.getString(R.string.i_distance_walked), res.getString(R.string.g_distance_walked), res.getInteger(R.integer.n_distance_walked));
        this.statsData[3] = new tgcStruct(res.getString(R.string.i_resonators_deployed), res.getString(R.string.g_resonators_deployed), res.getInteger(R.integer.n_resonators_deployed));
        this.statsData[4] = new tgcStruct(res.getString(R.string.i_links_created), res.getString(R.string.g_links_created), res.getInteger(R.integer.n_links_created));
        this.statsData[5] = new tgcStruct(res.getString(R.string.i_control_fields_created), res.getString(R.string.g_control_fields_created), res.getInteger(R.integer.n_control_fields_created));
        this.statsData[6] = new tgcStruct(res.getString(R.string.i_mu), res.getString(R.string.g_mu), res.getInteger(R.integer.n_mu));
        this.statsData[7] = new tgcStruct(res.getString(R.string.i_longest_link), res.getString(R.string.g_longest_link), res.getInteger(R.integer.n_longest_link));
        this.statsData[8] = new tgcStruct(res.getString(R.string.i_largest_control_field), res.getString(R.string.g_largest_control_field), res.getInteger(R.integer.n_largest_control_field));
        this.statsData[9] = new tgcStruct(res.getString(R.string.i_xm_recharged), res.getString(R.string.g_xm_recharged), res.getInteger(R.integer.n_xm_recharged), 10);
        this.statsData[10] = new tgcStruct(res.getString(R.string.i_xm_recharged2), res.getString(R.string.g_xm_recharged), res.getInteger(R.integer.n_xm_recharged), 9);
        this.statsData[11] = new tgcStruct(res.getString(R.string.i_upc), res.getString(R.string.g_upc), res.getInteger(R.integer.n_upc));
        this.statsData[12] = new tgcStruct(res.getString(R.string.i_mods_deployed), res.getString(R.string.g_mods_deployed), res.getInteger(R.integer.n_mods_deployed));
        this.statsData[13] = new tgcStruct(res.getString(R.string.i_resonators_destroyed), res.getString(R.string.g_resonators_destroyed), res.getInteger(R.integer.n_resonators_destroyed));
        this.statsData[14] = new tgcStruct(res.getString(R.string.i_portals_neutralized), res.getString(R.string.g_portals_neutralized), res.getInteger(R.integer.n_portals_neutralized));
        this.statsData[15] = new tgcStruct(res.getString(R.string.i_links_destroyed), res.getString(R.string.g_links_destroyed), res.getInteger(R.integer.n_links_destroyed));
        this.statsData[16] = new tgcStruct(res.getString(R.string.i_control_fields_destroyed), res.getString(R.string.g_control_fields_destroyed), res.getInteger(R.integer.n_control_fields_destroyed));
        this.statsData[17] = new tgcStruct(res.getString(R.string.i_max_time_portal_held), res.getString(R.string.g_max_time_portal_held), res.getInteger(R.integer.n_max_time_portal_held));
        this.statsData[18] = new tgcStruct(res.getString(R.string.i_max_time_link_maintained), res.getString(R.string.g_max_time_link_maintained), res.getInteger(R.integer.n_max_time_link_maintained));
        this.statsData[19] = new tgcStruct(res.getString(R.string.i_max_link_lengthxdays), res.getString(R.string.g_max_link_lengthxdays), res.getInteger(R.integer.n_max_link_lengthxdays));
        this.statsData[20] = new tgcStruct(res.getString(R.string.i_max_time_field_held), res.getString(R.string.g_max_time_field_held), res.getInteger(R.integer.n_max_time_field_held));
        this.statsData[21] = new tgcStruct(res.getString(R.string.i_largest_field_muxdays), res.getString(R.string.g_largest_field_muxdays), res.getInteger(R.integer.n_largest_field_muxdays));
        this.statsData[24] = new tgcStruct(res.getString(R.string.i_mission_completed), res.getString(R.string.g_mission_completed), res.getInteger(R.integer.n_mission_completed));
        this.statsData[23] = new tgcStruct(res.getString(R.string.i_hacks), res.getString(R.string.g_hacks), res.getInteger(R.integer.n_hacks));
    }


    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new userSettings(getApplicationContext());
        String[] paths = new String[]{DATA_PATH, DATA_PATH + "tessdata/"};
        debug("Status:" + prefs.isLoggedIn());
        prefs.checkLogin(true, false);
        if (!prefs.isLoggedIn())
            for (String path : paths) {
                File dir = new File(path);
                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                        return;
                    } else {
                        Log.v(TAG, "Created directory " + path + " on sdcard");
                    }
                }

            }

        // lang.traineddata file with the app (in assets folder)
        // You can get them at:
        // http://code.google.com/p/tesseract-ocr/downloads/list
        // This area needs work and optimization

        try {

            AssetManager assetManager = getAssets();
            InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
            //GZIPInputStream gin = new GZIPInputStream(in);
            OutputStream out = new FileOutputStream(DATA_PATH + "tessdata/" + lang + ".traineddata", false);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            //while ((lenf = gin.read(buff)) > 0) {
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            //gin.close();
            out.close();

            Log.v(TAG, "Copied " + lang + " traineddata");
        } catch (IOException e) {
            Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
        }


        setContentView(R.layout.activity_upload);


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                debug("sending to handler");
                decodeOCR(intent); // Handle single image being sent
                debug("returning from handler");
            }
        } else {
            debug("how the hell did i get here?");

// Handle other intents, such as being started from the home screen
        }
    }


    @SuppressWarnings("")
    private void decodeOCR(Intent intent) {
        Bitmap bm = null;
        String data = "";
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            try {
                bm = BitmapFactory.decodeStream(getContentResolver()
                        .openInputStream(imageUri));


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
        try {
            //noinspection
            assert bm != null;
            bm = bm.copy(Bitmap.Config.ARGB_8888, true);
        } catch (Exception en) {
            Log.i("info", Log.getStackTraceString(en));
        }
        try {

            data = extract(bm);

        } catch (Exception ee) {

            Log.i("info", Log.getStackTraceString(ee));
        }

        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("application/image");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"stefan.nygren@gmail.com"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "[TGS-DEBUG-DATA] " + getDeviceName());
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, data);
        emailIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(emailIntent, "Send debug data..."));
        //parseTextToVars(data);
    }


    //private void parseTextToVars(String data) {

    //}

    private void debug(String string) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, string, duration);
        toast.show();
    }

    private String getDeviceName() {
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
    }*/
}

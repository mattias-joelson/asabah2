package se.stny.thegridclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.googlecode.leptonica.android.Pixa;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.googlecode.tesseract.android.TessBaseAPI.PageSegMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

import se.stny.thegridclient.util.SystemUiHider;
import se.stny.thegridclient.util.userSettings;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class upload extends Activity {
    public static final String DATA_PATH = Environment
            .getExternalStorageDirectory().toString() + "/TheGrid/";
    public static final String lang = "eng";
    private static final String TAG = "Upload.java";
    userSettings prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new userSettings(getApplicationContext());
        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };
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
// Handle other intents, such as being started from the home screen
        }
    }

    private String extract(Bitmap bmp) throws ExecutionException, InterruptedException {
        String fin = "";
        Log.i("info", String.format("Image size: %d x %d", bmp.getWidth(), bmp.getHeight()));

        final TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.init(DATA_PATH, "eng");
        baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK);
        baseApi.setImage(bmp);
        Pixa allLinesPixa = baseApi.getTextlines();
        Log.i("info", String.format("allLinesPixa.size() == %d", allLinesPixa.size()));
        baseApi.setPageSegMode(PageSegMode.PSM_SINGLE_LINE);
        Rect boxRect = allLinesPixa.getBoxRect(1);
        baseApi.setRectangle(boxRect);

        baseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "0123456789,");

        baseApi.setRectangle(allLinesPixa.getBoxRect(3));
        baseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "");
        for (Rect curRect : allLinesPixa.getBoxRects()) {
            baseApi.setRectangle(curRect);
            String text = baseApi.getUTF8Text();

            fin = fin + "\n" + text;
        }
        baseApi.end();
        return fin;
    }


    private void decodeOCR(Intent intent)
    {
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
            bm = bm.copy(Bitmap.Config.ARGB_8888, true);
        } catch (NullPointerException en) {
            Log.i("info", Log.getStackTraceString(en));
        }
        try {

            data = extract(bm);

        } catch (ExecutionException ee) {
            String msg = "Unable to initialize Tesseract data files";
            if (ee.getCause() instanceof IOException)
                msg = msg + " - IOException";
            Log.i("info", Log.getStackTraceString(ee));

        } catch (InterruptedException ie) {
            Log.i("info", Log.getStackTraceString(ie));

        }
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("application/image");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"stefan.nygren@gmail.com"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "[TGS-DEBUG-DATA] " + getDeviceName());
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, data);
        emailIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(emailIntent, "Send debug data..."));
        parseTextToVars(data);
    }

    private void parseTextToVars(String data) {
    }

    private void debug(String string) {
        Context context = getApplicationContext();
        CharSequence text = string;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
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
    }
}

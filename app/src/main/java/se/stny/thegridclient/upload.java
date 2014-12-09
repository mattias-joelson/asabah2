package se.stny.thegridclient;

import se.stny.thegridclient.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.googlecode.tesseract.android.TessBaseAPI;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class upload extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;
    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;
    public static final String DATA_PATH = Environment
            .getExternalStorageDirectory().toString() + "/TheGrid/";
    public static final String lang = "eng";
    protected String _path;
    private static final String TAG = "upload.java";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

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
                OutputStream out = new FileOutputStream(DATA_PATH + "tessdata/" + lang + ".traineddata",false);

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


        _path = DATA_PATH + "/ocr.jpg";
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



    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */



    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */

    private void decodeOCR(Intent intent)
    {
        Bitmap bm;
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            try {
                bm = BitmapFactory.decodeStream(getContentResolver()
                        .openInputStream(imageUri));
                  bm = bm.copy(Bitmap.Config.ARGB_8888, true);
                TessBaseAPI baseApi = new TessBaseAPI();
                baseApi.setDebug(true);

                baseApi.init(DATA_PATH, lang);
                baseApi.setImage(bm);
                String recognizedText = baseApi.getUTF8Text();
                Log.v(TAG, "OCRED TEXT: " + recognizedText);
                baseApi.end();
                Pattern p = Pattern.compile("Max Time Portal Held (\\S+) days");
                Matcher m = p.matcher(recognizedText);
                while (m.find()) { // Find each match in turn; String can't do this.
                    int days = Integer.parseInt(m.group(1)); // Access a submatch group; String can't do this.

                    if(days < 3)
                    {
                        Log.v(TAG,"Not even Bronze guardian :(");
                    }
                    if (days >=3 && days < 10)
                    {
                        Log.v(TAG,"Bronze guardian :|");
                    }
                    if (days >=10 && days < 20)
                    {
                        Log.v(TAG,"Silver guardian :|");
                    }
                    if (days >=20 && days < 90)
                    {
                        Log.v(TAG,"Gold guardian :|");
                    }
                    if (days >=90 && days < 150)
                    {
                        Log.v(TAG,"Platinum guardian :)");
                    }
                    if (days >=150)
                    {
                        Log.v(TAG,"Onyx guardian :>");
                    }
                }

                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("application/image");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"stefan.nygren@gmail.com"});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"[TGS-DEBUG-DATA] " + getDeviceName());
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, recognizedText);
                emailIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
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

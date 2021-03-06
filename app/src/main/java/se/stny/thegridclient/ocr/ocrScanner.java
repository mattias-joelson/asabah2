
package se.stny.thegridclient.ocr;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;

import com.googlecode.leptonica.android.Pixa;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import se.stny.thegridclient.util.tgcDataClass;

public class ocrScanner extends AsyncTask<JSONObject, String, JSONObject> {
    private final String TAG = "ocrScanner.java";
    private volatile String lang;
    private volatile AssetManager Assets;
    private volatile Bitmap Image;
    private volatile String DATA_PATH;
    private volatile TessBaseAPI base;
    private ocrCallback<Integer, JSONObject, String, String> callback;
    private volatile tgcDataClass lines[];
    private JSONObject res;

    public ocrScanner(String DATA_PATH, Bitmap BMP, AssetManager assets, String lang, tgcDataClass lines[], ocrCallback<Integer, JSONObject, String, String> callback) {
        this.DATA_PATH = DATA_PATH;
        this.Image = BMP;
        this.Assets = assets;
        this.callback = callback;
        this.lang = lang;
        this.lines = lines;
    }

    protected void onPreExecute() {
        this.callback.ocrStart();
        File DATA_DIR = new File(DATA_PATH);
        File TESS_DATA_DIR = new File(DATA_DIR, "tessdata");
        try {
            if (!DATA_DIR.isDirectory()) {
                if (!DATA_DIR.mkdir()) {
                    throw new IOException();

                }
            }

            if (!(TESS_DATA_DIR.mkdir() || TESS_DATA_DIR.isDirectory()))
                throw new IOException();
        } catch (IOException e) {
            Log.e(TAG, "Uanble to make DATA DIR" + e.getMessage());
            e.printStackTrace();

        }

        try {
            InputStream in = Assets.open("tessdata/" + lang + ".traineddata");
            OutputStream out = new FileOutputStream(DATA_PATH + "tessdata/" + lang + ".traineddata", false);
// Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            Log.v(this.TAG, "Copied " + lang + " traineddata");
        } catch (IOException e) {
            Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
        }
        this.base = new TessBaseAPI();
        base.init(DATA_PATH, "eng");
        base.setImage(this.Image);
    }

    protected JSONObject doInBackground(JSONObject... params) {
        this.res = new JSONObject();
        callback.ocrRunning();
        base.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK);
        Pixa allLinesPixa = base.getTextlines();


        base.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);
        Rect boxRect = allLinesPixa.getBoxRect(1);
        base.setRectangle(boxRect);
        base.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "0123456789,");
        base.setRectangle(allLinesPixa.getBoxRect(3));
        try {
            this.callback.ocrDebugData(base.getUTF8Text(), "RAW-AP");
            this.res.put("statcat_ap", base.getUTF8Text().replaceAll("[^\\d]", ""));
            this.callback.ocrDebugData(String.valueOf(this.res.get("statcat_ap")), "PARSED-AP");
        } catch (JSONException e) {
            Log.e(TAG, "Unable to acquire ap");
        }
        base.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "");
        for (Rect curRect : allLinesPixa.getBoxRects()) {
            base.setRectangle(curRect);
            String tmpStr = base.getUTF8Text();
            translateToRightValue(tmpStr);
        }
        return this.res;
    }

    protected void onPostExecute(JSONObject finish) {
        try {
            for (tgcDataClass line : this.lines) {
                {
                    if (!line.checkUsed()) {
                        finish.put(line.getGridText(), "0");
                    }
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        callback.ocrCompleted(finish);
        base.end();
        Log.e(TAG, "TETRA");
    }

    private void translateToRightValue(String str) {
        try {

            for (tgcDataClass line : this.lines) {
                if (str.startsWith(line.getIngressText())) {
                    this.base.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "0123456789,M");
                    String[] stringArray = this.base.getUTF8Text().split("\\s");
                    int pos = line.getSplitPos() < 0 ? stringArray.length + line.getSplitPos() : line.getSplitPos();

                    this.res.put(line.getGridText(), stringArray[pos].replaceAll(",", "").replaceAll("day", "").replaceAll("s", "").replaceAll("[^0-9]", ""));
                    this.base.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "");
                    if (line.hasSibling()) {
                        this.lines[line.getSibling()].setAsUsed();
                        this.callback.ocrUpdate(1);
                    }
                    line.setAsUsed();
                    this.callback.ocrUpdate(1);
                    base.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "");
                    this.callback.ocrDebugData(str, "YES");
                    return;
                }
            }
            this.callback.ocrDebugData(str, "NO");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }
}
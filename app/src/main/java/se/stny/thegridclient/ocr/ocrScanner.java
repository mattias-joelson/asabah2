package se.stny.thegridclient.ocr;


import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;

import com.googlecode.leptonica.android.Pixa;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import se.stny.thegridclient.util.tgcDataClass;

public class ocrScanner extends AsyncTask<JSONObject, String, JSONObject> {
    private final String TAG = "ocrScanner.java";
    private volatile String lang;
    private volatile AssetManager Assets;
    private volatile Bitmap Image;
    private volatile String DATA_PATH;
    private volatile TessBaseAPI base;
    private ocrCallback<Integer, JSONObject> callback;

    private volatile tgcDataClass lines[];
    private List<NameValuePair> nameValuePairs = new ArrayList<>(2);

    public ocrScanner(String DATA_PATH, Bitmap BMP, AssetManager assets, String lang, tgcDataClass lines[], ocrCallback<Integer, JSONObject> callback) {
        this.DATA_PATH = DATA_PATH;
        this.Image = BMP;
        this.Assets = assets;
        this.callback = callback;
        this.lang = lang;
        this.lines = lines;

    }

    protected void onPreExecute() {
        this.callback.ocrStart();

        try {
            InputStream in = Assets.open("OcrData/" + lang + ".traineddata");
            OutputStream out = new FileOutputStream(DATA_PATH + "OcrData/" + lang + ".traineddata", false);

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
        JSONObject res = new JSONObject();
        callback.ocrRunning();
        base.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK);

        Pixa allLinesPixa = base.getTextlines();
        Log.i("info", String.format("allLinesPixa.size() == %d", allLinesPixa.size()));
        base.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);
        Rect boxRect = allLinesPixa.getBoxRect(1);
        base.setRectangle(boxRect);

        base.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "0123456789,");

        base.setRectangle(allLinesPixa.getBoxRect(3));
        base.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "");
        for (Rect curRect : allLinesPixa.getBoxRects()) {
            base.setRectangle(curRect);
            translateToRightValue(base.getUTF8Text().replaceAll(",", ""));

        }

        return res;
    }

    protected void onPostExecute(ArrayList finish) {
        for (tgcDataClass line : this.lines) {
            {
                if (!line.checkUsed()) {
                    try {
                        nameValuePairs.add(new BasicNameValuePair(line.getGridText(), "0"));
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Log.e(TAG, e.getCause().toString());
                    }


                }
            }
        }
        callback.ocrCompleted(finish);
        base.end();
        Log.e(TAG, "TETRA");
    }


    private void translateToRightValue(String str) {
        try {
            this.base.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "0123456789,");

            for (int i = 0; i < this.lines.length; i++) {
                if (str.startsWith(lines[i].getIngressText())) {
                    this.base.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "0123456789,M");
                    String[] stringArray = this.base.getUTF8Text().split("\\s");
                    int pos = this.lines[i].getSplitPos() < 0 ? stringArray.length + this.lines[i].getSplitPos() : this.lines[i].getSplitPos();

                    this.nameValuePairs.add(new BasicNameValuePair(this.lines[i].getGridText(), stringArray[pos]));
                    this.base.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "");
                    if (this.lines[i].hasSibling()) {
                        this.lines[this.lines[i].getSibling()].setAsUsed();
                        this.callback.ocrUpdate(1);
                    }
                    this.lines[i].setAsUsed();
                    this.callback.ocrUpdate(1);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        base.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "");

    }


}

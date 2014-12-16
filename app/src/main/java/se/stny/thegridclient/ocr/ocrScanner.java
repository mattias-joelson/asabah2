package se.stny.thegridclient.ocr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;

import com.googlecode.leptonica.android.Pixa;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import se.stny.thegridclient.R;

public class ocrScanner extends AsyncTask<JSONObject, String, JSONObject> {
    private final String TAG = "ocrScanner.java";
    private volatile String lang;
    private volatile AssetManager Assets;
    private volatile Bitmap Image;
    private volatile String DATA_PATH;
    private volatile TessBaseAPI base;
    private Context context;
    private ocrCallback callback;
    private ProgressDialog progress;


    public ocrScanner(String DATA_PATH, Bitmap BMP, AssetManager assets, String lang, Context context, ocrCallback callback) {
        this.DATA_PATH = DATA_PATH;
        this.Image = BMP;
        this.Assets = assets;
        this.context = context;
        this.callback = callback;
        this.lang = lang;

    }

    protected void onPreExecute() {
        this.progress = new ProgressDialog(this.context);
        this.progress.setIndeterminate(true);
        this.progress.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.spinner_loading));
        this.progress.setMessage("Converting stats.");
        this.progress.setProgress(0);
        this.progress.show();
        try {
            InputStream in = Assets.open("OcrData/" + lang + ".traineddata");
            //GZIPInputStream gin = new GZIPInputStream(in);
            OutputStream out = new FileOutputStream(DATA_PATH + "OcrData/" + lang + ".traineddata", false);

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
            res = translateToRightValue(res, base.getUTF8Text());

        }
        base.end();
        return res;
    }


    private JSONObject translateToRightValue(JSONObject res, String str) {
        try {
            base.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "0123456789,");


            if (str.startsWith(context.getString(R.string.upv))) {
                res.put(context.getString(R.string.grid_unique_visits), base.getUTF8Text().split("\\s"));
            } else if (str.startsWith(context.getString(R.string.portals_discovered))) {
                res.put(context.getString(R.string.grid_portals_discovered), base.getUTF8Text().split("\\s"));
            } else if (str.startsWith(context.getString(R.string.portals_neutralized))) {
                res.put(context.getString(R.string.grid_portalsneut), base.getUTF8Text().split("\\s"));
            }
            /*
            "statcat_innovator":"Select the highest level you had on November 15, 2014 11:59pm PST",
                    "statcat_dis_xm":"XM Collected",
                    "statcat_hac_hacks":"Hacks",
                    "statcat_bui_resdeploy":"Resonators Deployed",
                    "statcat_con_linkscreated":"Links Created",
                    "statcat_min_fieldscreated":"Fields Created",
                    "statcat_bui_mucap":"MU Captured",
                    "statcat_bui_longlink":"Longest Link",
                    "statcat_bui_lafield":"Largest Control Field",
                    "statcat_bui_xmrecharged":"XM Recharged",
                    "statcat_bui_portalcap":"Portals Captured",
                    "statcat_bui_unportalcap":"Unique Portals Captured",
                    "statcat_pur_resdestroyed":"Resonators Destroyed",
                    "statcat_pur_linksdestroyed":"Links Destroyed",
                    "statcat_pur_fieldsdestroyed":"Fields Destroyed",
                    "statcat_hea_distance":"Distance Walked",
                    "statcat_gua_maxtime":"Time Portal Held",
                    "statcat_def_linkmain":"Time Link Maintained",
                    "statcat_def_linkxdays":"Link Length*Days",
                    "statcat_def_fieldheld":"Time Field Held",
                    "statcat_def_muxdays":"MUs*Days"
*/

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        base.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "");
        return res;
    }


}

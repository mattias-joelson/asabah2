package se.stny.thegridclient.util;


import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class theGridClient extends AsyncTask<Void, Void, JSONObject> {
    protected final String TAG = "HttpClient";
    private String API_KEY = "";
    private String URL;


    volatile private JSONObject result = null;
    private volatile runState myState;
    private List<NameValuePair> nameValuePairs = new ArrayList<>(2);

    public theGridClient(String URL, String API_KEY) {
        this.URL = "http://the-grid.org/api/?" + URL;
        this.API_KEY = API_KEY;
        myState = runState.NOT_STARTED;

    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public runState getState() {
        return this.myState;
    }

    public void addHttpPost(String id, String Val) {
        nameValuePairs.add(new BasicNameValuePair(id, Val));
    }

    public JSONObject getJSONFromUrl() {
        this.execute();
        try {
            while (true) {
                if (!(this.myState == runState.NOT_STARTED)) break;

            }
            while (true) {
                if (!(this.myState == runState.RUNNING)) break;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return result;
    }

    @Override

    protected JSONObject doInBackground(Void... params) {
        try {
            myState = runState.RUNNING;
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPostRequest = new HttpPost(URL);

            // Set HTTP parameters
            httpPostRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpPostRequest.setHeader("X-THEGRID_API_KEY", this.API_KEY);


            long t = System.currentTimeMillis();
            HttpResponse response = httpclient.execute(httpPostRequest);
            Log.i(TAG, "HTTPResponse received in [" + (System.currentTimeMillis() - t) + "ms]");

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Read the content stream
                InputStream instream = entity.getContent();

                // convert content stream to a String
                String resultString = convertStreamToString(instream);
                instream.close();
                resultString = "[" + resultString.replace("\n", "") + "]";
                resultString = resultString.substring(1, resultString.length() - 1); // remove wrapping "[" and "]"
                result = new JSONObject(resultString);

                // Raw DEBUG output of our received JSON object:
                Log.i(TAG, "<JSONObject>\n" + result.toString() + "\n</JSONObject>");


            }

        } catch (Exception e) {
            myState = runState.ERROR;
            e.printStackTrace();
        }
        if (result != null) {
            myState = runState.FINISHED;
            return result;
        } else {
            return null;
        }
    }

    protected void onPostExecute(JSONObject jObject) {
        result = jObject;
    }

    public enum runState {
        NOT_STARTED, RUNNING, FINISHED, ERROR



    }
}
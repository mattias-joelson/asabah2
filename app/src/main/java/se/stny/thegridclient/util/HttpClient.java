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

public class HttpClient extends AsyncTask<Void, Void, JSONObject> {
    private final String TAG = "HttpClient";
    private String URL;

    private JSONObject result = null;
    private List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

    public HttpClient(String URL) {
        this.URL = "http://the-grid.org/api/?" + URL;

    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
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

    public void addHttpPost(String id, String Val) {
        nameValuePairs.add(new BasicNameValuePair(id, Val));
    }

    public JSONObject getJSONFromUrl() {
        this.execute();
        return result;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {

        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPostRequest = new HttpPost(URL);

            // Set HTTP parameters
            httpPostRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpPostRequest.setHeader("X-THEGRID_API_KEY", "");


            long t = System.currentTimeMillis();
            HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);
            Log.i(TAG, "HTTPResponse received in [" + (System.currentTimeMillis() - t) + "ms]");

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Read the content stream
                InputStream instream = entity.getContent();

                // convert content stream to a String
                String resultString = convertStreamToString(instream);
                instream.close();
                resultString = resultString.substring(1, resultString.length() - 1); // remove wrapping "[" and "]"
                resultString = resultString.replace("\n", "");
                JSONObject jsonObjRecv = new JSONObject(resultString);

                // Raw DEBUG output of our received JSON object:
                Log.i(TAG, "<JSONObject>\n" + jsonObjRecv.toString() + "\n</JSONObject>");

                return jsonObjRecv;
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(JSONObject jObject) {
        result = jObject;
    }
}
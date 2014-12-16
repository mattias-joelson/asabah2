package se.stny.thegridclient.ocr;

import org.json.JSONObject;


public interface ocrCallback {
    public abstract void onFinishRecognition(JSONObject recognizedText);
}
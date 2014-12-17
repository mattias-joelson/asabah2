package se.stny.thegridclient.ocr;

public interface ocrCallback<Found, Result, DebugString, DebugFound> {
    void ocrStart();

    void ocrRunning();

    void ocrCompleted(Result result);

    void ocrDebugData(DebugString str, DebugFound status);  //TODO: REMOVE DEBUG

    void ocrUpdate(Found val);
}
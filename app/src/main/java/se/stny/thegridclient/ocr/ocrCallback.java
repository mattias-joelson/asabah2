package se.stny.thegridclient.ocr;

public interface ocrCallback<Found, Result> {
    void ocrStart();

    void ocrRunning();

    void ocrCompleted(Result result);

    void ocrUpdate(Found val);
}
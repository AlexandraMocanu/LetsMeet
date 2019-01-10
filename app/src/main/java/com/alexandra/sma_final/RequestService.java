package com.alexandra.sma_final;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import realm.Topic;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestService extends Service {

    private String jwtToken = null;

    private static final String BASE_API = "http://192.168.1.114/api";
    private static final String AUTH_API = BASE_API + "/authenticate";

    private static final String TOPICS_API = BASE_API + "/topics";
    private static final String TOPICS_NEARBY_API = TOPICS_API + "/nearby"; // in km
    private static final String RATINGS_API = BASE_API + "/ratings";
    private static final String MESSAGES_API = BASE_API + "/messages";
    private static final String CONVERSATIONS_API = BASE_API + "/conversations";

    @Override
    public IBinder onBind(Intent intent) {
        IBinder iBinder = super.onBind(intent);

    }

    public void authenticate(Topic topic) throws IOException {
        URL url = new URL(AUTH_API);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            urlConnection.setRequestMethod("POST");
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setDoOutput(true);
            //add body

            OutputStream request = new BufferedOutputStream(urlConnection.getOutputStream());
            writeStream(request);

            InputStream response = new BufferedInputStream(urlConnection.getInputStream());
            readStream(response);
        } catch (IOException e){
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }

    }

    public Topic fetchNearbyTopics(Topic topic) throws IOException {
        Topic ret = null;
        URL url = new URL(TOPICS_NEARBY_API + "?distance=20");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            urlConnection.setRequestMethod("POST");
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setDoOutput(true);

            OutputStream request = new BufferedOutputStream(urlConnection.getOutputStream());
            writeStream(request);

            InputStream response = new BufferedInputStream(urlConnection.getInputStream());
            readStream(response);
        } catch (IOException e){
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }
    }
}
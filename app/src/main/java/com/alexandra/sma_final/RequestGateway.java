package com.alexandra.sma_final;

import io.realm.Realm;
import io.realm.RealmModel;
import org.json.JSONObject;
import realm.Topic;
import realm.User;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestGateway {

    private String jwtToken = null;

    private Realm realm;

    private static final String BASE_API = "http://192.168.1.114/api";
    private static final String AUTH_API = BASE_API + "/authenticate";

    private static final String TOPICS_API = BASE_API + "/topics";
    private static final String TOPICS_NEARBY_API = TOPICS_API + "/nearby"; // in km
    private static final String RATINGS_API = BASE_API + "/ratings";
    private static final String MESSAGES_API = BASE_API + "/messages";
    private static final String CONVERSATIONS_API = BASE_API + "/conversations";

    public RequestGateway() {
        realm = Realm.getDefaultInstance();
    }


    public void authenticate(User user) {
        User login = new User() {{
            setUsername("admin");
            setPassword("admin");
        }};
        RealmModel ret = withBodyRequest("POST", AUTH_API, User.class, login);
        realm.insertOrUpdate(ret);
    }

    public void getNearbyTopics(double coordX, double coordY, Integer dist) {
        Topic location = new Topic() {{
            setCoordX(coordX);
            setCoordY(coordY);
        }};
        String urlStr = TOPICS_NEARBY_API;
        if(dist != null) {
            urlStr += "?distance=" + dist.toString();
        }
        RealmModel ret = withBodyRequest("POST", urlStr , Topic.class, location);
        realm.insertOrUpdate(ret);
    }

    public void writeStream(Object obj, OutputStream request) {
        try {
            Object wrap = JSONObject.wrap(obj);
            request.write(wrap.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public RealmModel readStream(InputStream response, Class clazz) {
        RealmModel ret = null;
        try {
            ret = realm.createObjectFromJson(clazz, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }


    public RealmModel noBodyRequest(String reqMethod, String urlStr, Class clazz) {
        RealmModel ret = null;
        URL url = null;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(urlStr);
            urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setRequestMethod(reqMethod);
                urlConnection.setDoOutput(true);

                InputStream response = new BufferedInputStream(urlConnection.getInputStream());
                ret = readStream(response, clazz);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public RealmModel withBodyRequest(String reqMethod, String urlStr, Class clazz, Object obj) {
        RealmModel ret = null;
        URL url = null;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(urlStr);
            urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setRequestMethod(reqMethod);
                urlConnection.setDoOutput(true);

                OutputStream request = new BufferedOutputStream(urlConnection.getOutputStream());
                writeStream(obj, request);


                InputStream response = new BufferedInputStream(urlConnection.getInputStream());
                ret = readStream(response, clazz);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
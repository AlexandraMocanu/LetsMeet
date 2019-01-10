package com.alexandra.sma_final;

import android.app.Application;
import android.os.AsyncTask;
import io.realm.Realm;
import io.realm.RealmModel;
import org.json.JSONObject;

import realm.TokenHolder;
import realm.Topic;
import realm.User;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class RequestGateway {

    private String jwtToken = null;

    private Realm realm;

    private static final String BASE_API = "http://localhost/api";
    private static final String AUTH_API = BASE_API + "/authenticate";
    private static final String WHO_AM_I_API = BASE_API + "/account";

    private static final String TOPICS_API = BASE_API + "/topics";
    private static final String TOPICS_NEARBY_API = TOPICS_API + "/nearby"; // in km
    private static final String RATINGS_API = BASE_API + "/ratings";
    private static final String MESSAGES_API = BASE_API + "/messages";
    private static final String CONVERSATIONS_API = BASE_API + "/conversations";

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";

    public RequestGateway() {
        realm = Realm.getDefaultInstance();
    }

    public void authenticate() {
        User login = new User() {{
            setUsername(USERNAME);
            setPassword(PASSWORD);
        }};

        try {
            URL url = new URL(AUTH_API);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                OutputStream request = new BufferedOutputStream(urlConnection.getOutputStream());
                writeStream(login, request);


                InputStream response = new BufferedInputStream(urlConnection.getInputStream());
                TokenHolder ret = (TokenHolder) readStream(response, TokenHolder.class);
                jwtToken = ret.getIdToken();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public User whoAmI(){
        return getCurrentUser();
    }

    public User getCurrentUser() {
        return (User) noBodyRequest("GET", WHO_AM_I_API, User.class);
    }

    public void getNearbyTopics(double coordX, double coordY, Integer dist) {
        Topic location = new Topic() {{
            setCoordX(coordX);
            setCoordY(coordY);
        }};
        String urlStr = TOPICS_NEARBY_API;
        if (dist != null) {
            urlStr += "?distance=" + dist.toString();
        }
        RealmModel ret = withBodyRequest("POST", urlStr, Topic.class, location);
        realm.insertOrUpdate(ret);
    }



    public void getConversations() {

    }

    public String objToStr(Object obj){
        Object wrap = JSONObject.wrap(obj);
        return wrap.toString();
    }

    private String convertInputStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
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
        //urlStr, reqMethod, obj
        AsyncTask<String, Void, String> execute = new RequestTask().execute(urlStr, reqMethod);
        try {
            return realm.createOrUpdateObjectFromJson(clazz, execute.get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private final class RequestTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            //urlStr, reqMethod, obj
            String ret = null;
            URL url = null;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod(strings[1]);

                    setupRequest(urlConnection);

                    if(strings[2] != null && !strings[2].equals("")) {
                        OutputStream request = new BufferedOutputStream(urlConnection.getOutputStream());
                        writeStream(strings[2], request);
                    }
                    authAwareConnect(urlConnection);

                    InputStream response = new BufferedInputStream(urlConnection.getInputStream());
                    ret = convertInputStreamToString(response);
//                    ret = readStream(response, clazz);
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

    public void setupRequest(HttpURLConnection urlConnection) {


        urlConnection.setRequestProperty("Authorization", "Bearer " + jwtToken);
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setConnectTimeout(1000 * 5);

    }

    public void authAwareConnect(HttpURLConnection urlConnection) {
        try {
            urlConnection.connect();

            while(jwtToken == null
                    || urlConnection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED){
                authenticate();
                urlConnection.connect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public RealmModel withBodyRequest(String reqMethod, String urlStr, Class clazz, Object obj) {
        //urlStr, reqMethod, obj

        AsyncTask<String, Void, String> execute = new RequestTask().execute(urlStr, reqMethod, objToStr(obj));
        try {
            return realm.createOrUpdateObjectFromJson(clazz, execute.get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
package com.alexandra.sma_final;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

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

    private static final String BASE_API = "http://192.168.1.114:8080/api";
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

        AsyncTask<Object, Void, String> post = new RequestTask1().execute(AUTH_API, "POST", "{\"username\":\"admin\",\"password\":\"admin\"}");
//        try {
//            jwtToken = post.get();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }

    public void getCurrentUser() {
        noBodyRequest("GET", WHO_AM_I_API, User.class);
//        realm.executeTransaction();
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
        withBodyRequest("POST", urlStr, Topic.class, location);
//        realm.insertOrUpdate(ret);
    }



    public void getConversations() {

    }

    public String objToStr(Object obj){
        JSONObject wrap = (JSONObject) JSONObject.wrap(obj);
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


    public void noBodyRequest(String reqMethod, String urlStr, Class clazz) {
        //urlStr, reqMethod, obj
        AsyncTask<Object, Void, String> execute = new RequestTask().execute(urlStr, reqMethod, clazz);
//        try {
//            return realm.createOrUpdateObjectFromJson(clazz, execute.get());
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    private final class RequestTask1 extends AsyncTask<Object,Void,String> {

        @Override
        protected String doInBackground(Object... strings) {
            //urlStr, reqMethod, obj
            String ret = null;
            URL url = null;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL((String)strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod((String)strings[1]);

                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.setConnectTimeout(1000 * 5);

                    if(strings.length >= 3) {
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

        @Override
        protected void onPostExecute(String result) {
            Log.d("TAG","jwt " + result);
            jwtToken = result;
        }
    }

    private final class RequestTask extends AsyncTask<Object,Void,String> {

        private Class clazz;

        @Override
        protected String doInBackground(Object... strings) {
            //urlStr, reqMethod, obj
            String ret = null;
            URL url = null;
            clazz = (Class) strings[2];
            HttpURLConnection urlConnection = null;
            try {
                url = new URL((String) strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod((String) strings[1]);

                    setupRequest(urlConnection);

                    if(strings.length >= 4) {
                        OutputStream request = new BufferedOutputStream(urlConnection.getOutputStream());
                        writeStream(strings[3], request);
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


        @Override
        protected void onPostExecute(String result) {
            Log.d("TAG","smth " + result);
            realm.createOrUpdateObjectFromJson(clazz, result);
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


    public void withBodyRequest(String reqMethod, String urlStr, Class clazz, Object obj) {
        //urlStr, reqMethod, obj

        AsyncTask<Object, Void, String> execute = new RequestTask().execute(urlStr, reqMethod, clazz, objToStr(obj));
//        try {
//            return realm.createOrUpdateObjectFromJson(clazz, execute.get());
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return null;
    }
}
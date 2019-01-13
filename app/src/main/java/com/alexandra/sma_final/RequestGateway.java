package com.alexandra.sma_final;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.alexandra.sma_final.rest.TokenHolderDTO;
import com.alexandra.sma_final.rest.UserDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import androidx.annotation.Nullable;
import io.realm.Realm;
import io.realm.RealmModel;

import org.json.JSONObject;

import realm.Topic;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class RequestGateway {

    private static final String TAG = "RequestGateway";

    private String jwtToken = null;

    private Realm realm;
    private Gson gson;
    private MyApplication app;
    private UserDTO currentUser = null;

    private static final String MUST_AUTHENTICATE = "}}UNAUTHORIZED{{";
    private static final String EMU_LOCALHOST = "10.0.2.2";
    private static final String BASE_API = "http://" + EMU_LOCALHOST + ":8080/api";
    //    private static final String BASE_EMU_API = "http://:8080/api";
    private static final String AUTH_API = BASE_API + "/authenticate";
    private static final String WHO_AM_I_API = BASE_API + "/account";

    private static final String TOPICS_API = BASE_API + "/topics";
    private static final String TOPICS_NEARBY_API = TOPICS_API + "/nearby"; // in km
    private static final String RATINGS_API = BASE_API + "/ratings";
    private static final String MESSAGES_API = BASE_API + "/messages";
    // TODO: 2019-01-13 Add my conversations REST endpoint
    private static final String CONVERSATIONS_API = BASE_API + "/conversations";

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";

    public RequestGateway(MyApplication app) {
        this.app = app;
        realm = Realm.getDefaultInstance();
        gson = new GsonBuilder().create();
    }

    public void authenticate(@Nullable Callback cb) {
//        "{\"username\":\"admin\",\"password\":\"admin\"}"

        HashMap<String, String> loginVM = new HashMap<>();
        loginVM.put("username", USERNAME);
        loginVM.put("password", PASSWORD);
        byte[] bytes = gson.toJson(loginVM).getBytes();

        new LoginTask()
                .execute(AUTH_API, "POST", bytes, cb);
    }

    public void getCurrentUser(AsyncResponse<UserDTO> responseReceiver) {
        CurrentUserTask task = new CurrentUserTask();
        task.delegate = responseReceiver;
        task.execute(WHO_AM_I_API, "GET");
    }

    public void getNearbyTopics(double coordX, double coordY, @Nullable Integer dist) {
//        HashMap<String, Double> location = new HashMap<>();
//        location.put("coordX", coordX);
//        location.put("coordY", coordY);
//
//        String json = gson.toJson(location);

        Topic location = new Topic();
        location.setCoordX(coordX);
        location.setCoordY(coordY);
        String urlStr = TOPICS_NEARBY_API;
        if (dist != null) {
            urlStr += "?distance=" + dist.toString();
        }
        new RequestPersistTask().execute(urlStr, "POST", Topic.class, false, location);
//        realm.insertOrUpdate(ret);
    }

    public void getNearbyTopics(String city) {
//        HashMap<String, Double> location = new HashMap<>();
//        location.put("coordX", coordX);
//        location.put("coordY", coordY);
//
//        String json = gson.toJson(location);

        Topic location = new Topic();
        location.setCity(city);
        new RequestPersistTask().execute(TOPICS_NEARBY_API, "POST", true, Topic.class, location);
    }


//    public void getConversations() {
//        if(currentUser == null){
//            Log.e(TAG, "Need to get user info first!");
//        }
//        new RequestPersistTask().execute(CONVERSATIONS_API, "GET", false, Conversation.class);
//    }


    public String objToStr(Object obj) {
        JSONObject wrap = (JSONObject) JSONObject.wrap(obj);
        return wrap.toString();
    }

    private static String inputStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

//    public void writeStream(Object obj, OutputStream request, Class clazz) {
//        try {
//            byte[] bytes = gson.toJson(obj, clazz).getBytes();
//            request.write(bytes);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

    public static void addHeaders(HttpURLConnection urlConnection) {
        urlConnection.setDoInput(true);
        urlConnection.setConnectTimeout(1000 * 5);
        urlConnection.setRequestProperty("Content-Type", "application/json");
    }

//    public RealmModel readStream(InputStream response, Class<RealmModel> clazz) {
//        RealmModel ret = null;
//        try {
//            ret = realm.createObjectFromJson(clazz, response);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return ret;
//    }


    //TODO: socket timeout exception
    //TODO: connection refused

    public void noBodyRequest(String reqMethod, String urlStr, Class clazz) {
        AsyncTask<Object, Void, String> execute = new RequestPersistTask().execute(urlStr, reqMethod, clazz);
    }

    public void setupRequest(HttpURLConnection urlConnection) {
        addHeaders(urlConnection);
        if (jwtToken != null)
            urlConnection.setRequestProperty("Authorization", "Bearer " + jwtToken);

    }

//    public void authAwareConnect(HttpURLConnection urlConnection) throws IOException {
//        urlConnection.connect();
//
//        if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED){
//            authenticate();
//            urlConnection.connect();
//        }
//    }


    //urlStr, reqMethod, bytes

    /**
     * Does request and returns result Body as String
     *
     * @param params - urlStr, reqMethod, [bytes]
     * @return response String
     */
    private String doRequest(Object... params) {
        //urlStr, reqMethod, [bytes]
        if (params.length < 2) {
            Log.e(TAG, "doRequest Method requires at least 2 parameters");
            return null;
        }
        String urlStr = (String) params[0];
        String requestMethod = (String) params[1];
        String ret = null;
        URL url = null;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(urlStr);
            urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setRequestMethod(requestMethod);
                Log.d(TAG, requestMethod + " ---> " + urlStr);

                setupRequest(urlConnection);

                if (params.length == 3 && params[2] != null) {
                    byte[] body = (byte[]) params[2];
                    urlConnection.setDoOutput(true);
                    OutputStream request = new BufferedOutputStream(urlConnection.getOutputStream());

                    request.write(body);

                    request.flush();
                    Log.d(TAG, requestMethod + " ---> " + urlStr);
                    Log.d(TAG, "Body: " + new String(body, "UTF-8"));
                } else {
                    Log.d(TAG, requestMethod + " ---> " + urlStr);
                }
                urlConnection.connect();


                InputStream response = new BufferedInputStream(urlConnection.getInputStream());
                ret = inputStreamToString(response);

                Log.d(TAG, urlConnection.getResponseCode() + "<---" + urlStr + ": " + ret);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "HTTP error!");
                Log.e(TAG, urlConnection.getResponseCode() + ": " + urlConnection.getResponseMessage());
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Log.d(TAG, "Request was unauthorized! Trying to authenticate.");
                    authenticate(new RequestCallback(params));
                }
                e.printStackTrace();
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

    //urlStr, reqMethod, bytes, [callback]
    @SuppressLint("StaticFieldLeak")
    private final class LoginTask extends AsyncTask<Object, Void, String> {

        private Callback cb = null;

        @Override
        protected String doInBackground(Object... params) {
            if (params.length == 4 && params[3] != null) {
                cb = (Callback) params[3];
            }
            return doRequest(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(String result) {
            String idToken = gson.fromJson(result, TokenHolderDTO.class).getIdToken();
            Log.d(TAG, "Got JWT: " + idToken);
            jwtToken = idToken;
            if (cb != null) {
                cb.execute();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private final class CurrentUserTask extends AsyncTask<Object, Void, String> {

        private AsyncResponse<UserDTO> delegate = null;

        @Override
        protected String doInBackground(Object... params) {
            return doRequest(params);
        }

        @Override
        protected void onPostExecute(String result) {
            if (delegate != null) {
                Log.d(TAG, "Current user is: " + result);
                currentUser = gson.fromJson(result, UserDTO.class);
                delegate.processFinish(currentUser);
            }
        }
    }

    //urlStr, reqMethod, class, [obj]
    @SuppressLint("StaticFieldLeak")
    private final class RequestPersistTask extends AsyncTask<Object, Void, String> {

        private Class<RealmModel> clazz;
        private boolean shouldClear;

        @Override
        protected String doInBackground(Object... params) {
            //urlStr, reqMethod, class, clear, [obj]
            try {
                while (jwtToken == null) {
                    Log.d(TAG,"Waiting for JWT!");
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (params.length < 4) {
                Log.e(TAG, "RequestPersist requires at least 4 parameters");
            }
            clazz = (Class<RealmModel>) params[2];
            byte[] bytes = null;
            shouldClear = (boolean) params[3];

            if (params.length == 5 && params[4] != null) {
                RealmModel obj = (RealmModel) params[4];
                bytes = gson.toJson(obj, clazz).getBytes();
            }

            return doRequest(params[0], params[1], bytes);
        }


        @Override
        protected void onPostExecute(String result) {
            Log.d("TAG", "Persisting " + clazz.getSimpleName() + ": " + result);
            realm.beginTransaction();
            realm.createOrUpdateAllFromJson(clazz, result);
            realm.cancelTransaction();
        }
    }

    private final class RequestCallback implements Callback {

        private Object[] params;

        private RequestCallback(Object[] params) {
            this.params = params;
        }

        @Override
        public void execute() {
            doRequest(params);
        }
    }
}
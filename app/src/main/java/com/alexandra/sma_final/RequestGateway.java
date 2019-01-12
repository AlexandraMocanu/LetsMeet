package com.alexandra.sma_final;

import android.os.AsyncTask;
import android.util.Log;

import com.alexandra.sma_final.rest.TokenHolderDTO;
import com.alexandra.sma_final.rest.UserDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.realm.Realm;
import io.realm.RealmModel;

import org.json.JSONObject;

import realm.Topic;
import realm.User;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

public class RequestGateway {

    private static final String TAG = "RequestGateway";

    private String jwtToken = null;

    private Realm realm;
    private Gson gson;
    private MyApplication app;

    private static final String EMU_LOCALHOST = "10.0.2.2";
    private static final String BASE_API = "http://" + EMU_LOCALHOST + ":8080/api";
    //    private static final String BASE_EMU_API = "http://:8080/api";
    private static final String AUTH_API = BASE_API + "/authenticate";
    private static final String WHO_AM_I_API = BASE_API + "/account";

    private static final String TOPICS_API = BASE_API + "/topics";
    private static final String TOPICS_NEARBY_API = TOPICS_API + "/nearby"; // in km
    private static final String RATINGS_API = BASE_API + "/ratings";
    private static final String MESSAGES_API = BASE_API + "/messages";
    private static final String CONVERSATIONS_API = BASE_API + "/conversations";

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";

    public RequestGateway(MyApplication app) {
        this.app = app;
        realm = Realm.getDefaultInstance();
        gson = new GsonBuilder().create();
    }

    public void authenticate() {
//        "{\"username\":\"admin\",\"password\":\"admin\"}"

        HashMap<String, String> loginVM = new HashMap<>();
        loginVM.put("username", USERNAME);
        loginVM.put("password", PASSWORD);
        byte[] bytes = gson.toJson(loginVM).getBytes();

        new LoginTask()
                .execute(AUTH_API, "POST", bytes);
    }

    public void getCurrentUser(AsyncResponse<UserDTO> responseReceiver) {
        CurrentUserTask task = new CurrentUserTask();
        task.delegate = responseReceiver;
        task.execute(WHO_AM_I_API, "GET");
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

    public void writeStream(Object obj, OutputStream request, Class clazz) {
        try {
            byte[] bytes = gson.toJson(obj, clazz).getBytes();
            request.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void addHeaders(HttpURLConnection urlConnection) {
        urlConnection.setDoInput(true);
        urlConnection.setConnectTimeout(1000 * 5);
        urlConnection.setRequestProperty("Content-Type", "application/json");
    }

    public RealmModel readStream(InputStream response, Class<RealmModel> clazz) {
        RealmModel ret = null;
        try {
            ret = realm.createObjectFromJson(clazz, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }


    /**
     * Does request and returns result Body as String
     *
     * @param params - urlStr, reqMethod, [bytes]
     * @return response String
     */
    private String doRequest(Object... params) {
        String urlStr = (String) params[0];
        String requestMethod = (String) params[1];
        //urlStr, reqMethod, [bytes]
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

                Log.d(TAG, urlConnection.getResponseCode() + ": " + ret + "<---" + urlStr);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "HTTP error!");
                Log.e(TAG, urlConnection.getResponseCode() + ": " + urlConnection.getResponseMessage());
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


    private final class LoginTask extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object... params) {
            return doRequest(params);
        }

        @Override
        protected void onPostExecute(String result) {
            String idToken = gson.fromJson(result, TokenHolderDTO.class).getIdToken();
            Log.d(TAG, "Got JWT: " + idToken);
            jwtToken = idToken;
            getCurrentUser(app);
        }
    }
    //TODO: socket timeout exception
    //TODO: connection refused

    public void noBodyRequest(String reqMethod, String urlStr, Class clazz) {
        AsyncTask<Object, Void, String> execute = new RequestTask().execute(urlStr, reqMethod, clazz);
    }

    private final class CurrentUserTask extends AsyncTask<Object, Void, String> {

        public AsyncResponse<UserDTO> delegate = null;

        @Override
        protected String doInBackground(Object... params) {
            return doRequest(params);
        }

        @Override
        protected void onPostExecute(String result) {
            if (delegate != null) {
                Log.d(TAG, "Current user is: " + result);
                delegate.processFinish(gson.fromJson(result, UserDTO.class));
            }
        }
    }

    private final class RequestTask extends AsyncTask<Object, Void, String> {

        private Class clazz;

        @Override
        protected String doInBackground(Object... strings) {
            //urlStr, reqMethod, obj, class
            String ret = null;
            URL url = null;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL((String) strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod((String) strings[1]);

                    setupRequest(urlConnection);

                    if (strings.length == 4) {
                        clazz = (Class) strings[2];
                        OutputStream request = new BufferedOutputStream(urlConnection.getOutputStream());
                        writeStream(strings[2], request, (Class) strings[3]);
                    }
                    authAwareConnect(urlConnection);

                    InputStream response = new BufferedInputStream(urlConnection.getInputStream());
                    ret = inputStreamToString(response);
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
            Log.d("TAG", "smth " + result);
            realm.createOrUpdateObjectFromJson(clazz, result);
        }
    }

    public void setupRequest(HttpURLConnection urlConnection) {
        addHeaders(urlConnection);
        if (jwtToken != null)
            urlConnection.setRequestProperty("Authorization", "Bearer " + jwtToken);

    }

    public void authAwareConnect(HttpURLConnection urlConnection) throws IOException {
        urlConnection.connect();

        while (jwtToken == null
                || urlConnection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            authenticate();
            urlConnection.connect();
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
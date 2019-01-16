package com.alexandra.sma_final;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.alexandra.sma_final.rest.NullX509TrustManager;
import com.alexandra.sma_final.rest.TokenHolderDTO;
import com.alexandra.sma_final.rest.UserDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import androidx.annotation.Nullable;
import io.realm.Realm;
import io.realm.RealmModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import realm.Conversation;
import realm.Topic;
import realm.User;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class RequestGateway {

    private static final String TAG = "RequestGateway";

    private String jwtToken = null;

    private Realm realm;
    private Gson gson;
    private MyApplication app;
    private UserDTO currentUser = null;

    private static final boolean USES_SSL = true;
    private static final String EMU_LOCALHOST = "10.0.2.2";
    private static final String BASE_API = "https://" + EMU_LOCALHOST + ":8080/api";
    //    private static final String BASE_EMU_API = "http://:8080/api";
    private static final String AUTH_API = BASE_API + "/authenticate";
    private static final String WHO_AM_I_API = BASE_API + "/account";
    private static final String USERS_API = BASE_API + "/users";

    private static final String TOPICS_API = BASE_API + "/topics";
    private static final String TOPICS_NEARBY_API = TOPICS_API + "/nearby"; // in km
    private static final String RATINGS_API = BASE_API + "/ratings";
    private static final String MESSAGES_API = BASE_API + "/messages";
    // TODO: 2019-01-13 Add my conversations REST endpoint
    private static final String MY_CONVERSATIONS_API = BASE_API + "/conversations/me";

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";

    public RequestGateway(MyApplication app) {
        this.app = app;
        realm = Realm.getDefaultInstance();
        gson = new GsonBuilder().create();
    }

    public void authenticate(@Nullable Callback cb) {
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

    public void getAllUsers() {
        new RequestPersistTask().execute(USERS_API, "GET", User.class, true);
    }

    public void getUserByUsername(String username) {
        new RequestPersistTask().execute(USERS_API + "/" + username, "GET", User.class, false);
    }

    public void getNearbyTopics(double coordX, double coordY, @Nullable Integer dist) {
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
        try {
            String cityUrl = URLEncoder.encode(city, "UTF-8");
            new RequestPersistTask().execute(TOPICS_NEARBY_API + "?city=" + cityUrl, "GET", Topic.class, false);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "City name could not be encoded!");
        }
    }


    public void getUserConversations() {
        new RequestPersistTask().execute(MY_CONVERSATIONS_API, "GET", Conversation.class, false);
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

//    public void writeStream(Object obj, OutputStream request, Class clazz) {
//        try {
//            byte[] bytes = gson.toJson(obj, clazz).getBytes();
//            request.write(bytes);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

    public static void addHeaders(HttpsURLConnection urlConnection) {

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

    public void setupRequest(HttpsURLConnection urlConnection) {
        addHeaders(urlConnection);
        if (jwtToken != null)
            urlConnection.setRequestProperty("Authorization", "Bearer " + jwtToken);

    }

//    public void authAwareConnect(HttpsURLConnection urlConnection) throws IOException {
//        urlConnection.connect();
//
//        if(urlConnection.getResponseCode() == HttpsURLConnection.HTTP_UNAUTHORIZED){
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
        HttpsURLConnection urlConnection = null;
        try {
            url = new URL(urlStr);
            if (USES_SSL) {
                urlConnection = setUpHttpsConnection(urlStr);
            } else {
                urlConnection = (HttpsURLConnection) url.openConnection();
            }
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
                if (urlConnection.getResponseCode() == HttpsURLConnection.HTTP_UNAUTHORIZED) {
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
                    Log.d(TAG, "Waiting for JWT!");
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
            Object json = null;
            try {
                json = new JSONTokener(result).nextValue();
                realm.beginTransaction();
                if (json instanceof JSONObject){
                    Log.d(TAG, "Persisting " + clazz.getSimpleName() + ": " + result);
                    realm.createOrUpdateObjectFromJson(clazz, result);
                }
                else if (json instanceof JSONArray){
                    Log.d(TAG, "Persisting " + clazz.getSimpleName() + "s: " + result);
                    realm.createOrUpdateAllFromJson(clazz, result);
                }
                realm.cancelTransaction();
            } catch (JSONException e) {
                Log.e(TAG, "This is not a valid JSON: " + result);
            }
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

    /**
     * Set up a connection to littlesvr.ca using HTTPS. An entire function
     * is needed to do this because littlesvr.ca has a self-signed certificate.
     * <p>
     * The caller of the function would do something like:
     * HttpsURLConnection urlConnection = setUpHttpsConnection("https://littlesvr.ca");
     * InputStream in = urlConnection.getInputStream();
     * And read from that "in" as usual in Java
     * <p>
     * Based on code from:
     * https://developer.android.com/training/articles/security-ssl.html#SelfSigned
     */
    public HttpsURLConnection setUpHttpsConnection(String urlString) {
        try {
            // Load CAs from an InputStream
            // (could be from a resource or ByteArrayInputStream or ...)
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            // My CRT file that I put in the assets folder
            // I got this file by following these steps:
            // * Go to https://littlesvr.ca using Firefox
            // * Click the padlock/More/Security/View Certificate/Details/Export
            // * Saved the file as littlesvr.crt (type X.509 Certificate (PEM))
            // The MainActivity.context is declared as:
            // public static Context context;
            // And initialized in MainActivity.onCreate() as:
            // MainActivity.context = getApplicationContext();
            InputStream caInput = new BufferedInputStream(app.getBaseContext().getAssets().open("tls/ca.cer"));
            Certificate ca = cf.generateCertificate(caInput);
//            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
//                    Log.i("NullHostnameVerifier", "Approving certificate for " + hostname);
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new NullX509TrustManager()}, new SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());


            // Create a TrustManager that trusts the CAs in our KeyStore
//            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
//            SSLContext context = SSLContext.getInstance("TLS");
//            context.init(null, tmf.getTrustManagers(), null);

            // Tell the URLConnection to use a SocketFactory from our SSLContext
            URL url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());

            return urlConnection;
        } catch (Exception ex) {
            Log.e(TAG, "Failed to establish SSL connection to server: " + ex.toString());
            return null;
        }
    }
}
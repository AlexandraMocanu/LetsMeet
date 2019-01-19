package com.alexandra.sma_final;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

import io.realm.exceptions.RealmException;
import realm.Conversation;
import realm.GetIdCompliant;
import realm.Message;
import realm.Rating;
import realm.Topic;
import realm.User;

import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public class RequestGateway {

    private static final String TAG = "RequestGateway";

    private String jwtToken = null;

    private MyApplication mApp;
    private Context mContext;

    private Realm realm;
    private Gson gson;
    private UserDTO currentUser = null;

    //Connection parameters
    private static final boolean USES_SSL = true;
    private static final boolean USES_EMULATOR = false;
    private static boolean isConnected = false;

    //API link constants
    private static final String EMU_LOCALHOST = "10.0.2.2";
    private static final String DOMAIN_BASE_API = USES_EMULATOR ?
            EMU_LOCALHOST : "192.168.1.110";
    private static final String BASE_API = "https://" + DOMAIN_BASE_API + ":8080/api";
    private static final String AUTH_API = BASE_API + "/authenticate";
    private static final String WHO_AM_I_API = BASE_API + "/account";
    private static final String USERS_API = BASE_API + "/users";

    private static final String TOPICS_API = BASE_API + "/topics";
    private static final String TOPICS_NEARBY_API = TOPICS_API + "/nearby"; // in km
    private static final String RATINGS_API = BASE_API + "/ratings";
    private static final String MESSAGES_API = BASE_API + "/messages";
    private static final String CONVERSATIONS_API = BASE_API + "/conversations";
    private static final String MY_CONVERSATIONS_API = BASE_API + "/conversations/me";

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";

    public RequestGateway(MyApplication mApp) {
        this.mApp = mApp;
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

    public String postOrPut(GetIdCompliant obj) {
        return obj.getId() == null ? "POST" : "PUT";
    }

    public void putConversation(Conversation conversation) {
        new RequestPersistTask().execute(CONVERSATIONS_API, postOrPut(conversation), Conversation.class, false, conversation);
    }

    public void putMessage(Message message) {
        new RequestPersistTask().execute(MESSAGES_API, postOrPut(message), Message.class, false, message);
    }

    public void putRating(Rating rating) {
        new RequestPersistTask().execute(RATINGS_API, postOrPut(rating), Rating.class, false, rating);
    }

    public void putTopic(Topic topic) {
        new RequestPersistTask().execute(TOPICS_API, postOrPut(topic), Topic.class, false, topic);
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

    public static void addHeaders(HttpsURLConnection urlConnection) {

        urlConnection.setDoInput(true);
        urlConnection.setConnectTimeout(1000 * 5);
        urlConnection.setRequestProperty("Content-Type", "application/json");
    }


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
            }catch(SSLHandshakeException e) {
                Log.e(TAG, "SSl Handshake Exception! Server is not running in tls mode!");
                Log.getStackTraceString(e);
            } catch (SocketTimeoutException e) {
                Log.w(TAG, "Timeout exception!");
                if (USES_EMULATOR) {
//                    Toast.makeText(mApp.getApplicationContext(), "EMULATOR MODE: Timeout exception. Server should be running on the same device", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "USES_EMULATOR is true! Make sure that the server is running on the same machine as the emulator!");
                }
                Log.getStackTraceString(e);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "HTTP error!");
                Log.e(TAG, urlConnection.getResponseCode() + ": " + urlConnection.getResponseMessage());
                if (urlConnection.getResponseCode() == HttpsURLConnection.HTTP_UNAUTHORIZED) {
                    Log.d(TAG, "Request was unauthorized! Trying to authenticate.");
                    authenticate(new RequestCallback(params));
                }
                e.printStackTrace();
            } catch (ConnectException e) {
                Log.w(TAG, e.getMessage() + " Try checking your connection to the server!");
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

        private static final String TAG = "RequestGateway - LoginTask";


        private Callback cb = null;

        @Override
        protected String doInBackground(Object... params) {
            if (params.length == 4 && params[3] != null) {
                cb = (Callback) params[3];
            }
            String jwtJson = doRequest(params[0], params[1], params[2]);
            try {
                while (jwtJson == null){
                    if (isConnected) {
                        Log.w(TAG, "Retrying to fetch JWT!");
                        Thread.sleep(50);
                    } else {
                        Log.d(TAG, "Could not fetch JWT... Will retry later");
                        Thread.sleep(3000);
                    }
                    jwtJson = doRequest(params[0], params[1], params[2]);
                    isConnected = isConnected && checkInternetConnection();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return jwtJson;
        }

        @Override
        protected void onPostExecute(String result) {
            isConnected = true;
            TokenHolderDTO tokenJson = gson.fromJson(result, TokenHolderDTO.class);
            if (tokenJson == null) {
                Log.e(TAG, "Server responded to Login Request with null!");
                return;
            }
            String idToken = tokenJson.getIdToken();
            Log.d(TAG, "Got JWT: " + idToken);
            jwtToken = idToken;
            if (cb != null) {
                cb.execute();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private final class CurrentUserTask extends AsyncTask<Object, Void, String> {
        private static final String TAG = "RequestGateway - CurrentUserTask";

        private AsyncResponse<UserDTO> delegate = null;

        @Override
        protected String doInBackground(Object... params) {
            try {
                while (jwtToken == null) {
                    if (isConnected) {
                        Log.d(TAG, "Waiting for JWT!");
                        Thread.sleep(50);
                    } else {
                        Log.v(TAG, "Waiting for connection to server!");
                        Thread.sleep(5000);
                    }
                    isConnected = isConnected && checkInternetConnection();
                }
            } catch (InterruptedException e) {
                Log.getStackTraceString(e);
            }
            return doRequest(params);
        }

        @Override
        protected void onPostExecute(String result) {
            isConnected = true;
            if (delegate != null) {
                Log.d(TAG, "Current user is: " + result);
                currentUser = gson.fromJson(result, UserDTO.class);
                delegate.processFinish(currentUser);
            }
        }
    }

    //urlStr, reqMethod, class, clear, [obj]
    @SuppressLint("StaticFieldLeak")
    private final class RequestPersistTask extends AsyncTask<Object, Void, String> {
        private static final String TAG = "RequestGateway - RequestPersistTask";

        private Class<RealmModel> clazz;
        private boolean shouldClear;
        private String urlStr = null;

        //urlStr, reqMethod, class, clear, [obj]
        @Override
        protected String doInBackground(Object... params) {
            try {
                while (jwtToken == null) {
                    if (isConnected) {
                        Log.d(TAG, "Waiting for JWT!");
                        Thread.sleep(50);
                    } else {
                        Log.v(TAG, "Waiting for connection to server!");
                        Thread.sleep(10000);
                    }
                    isConnected = isConnected && checkInternetConnection();
                }
            } catch (InterruptedException e) {
                Log.getStackTraceString(e);
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
            isConnected = true;
            try {
                JSONTokener json = new JSONTokener(result);
                if (result == null || json == null) {
                    Log.w(TAG, "Request to " + urlStr + "returned null!");
                    return;
                }
                Object firstField = json.nextValue();
//                realm.beginTransaction();
                if (firstField instanceof JSONObject) {
                    Log.v(TAG, "Persisting " + clazz.getSimpleName() + ": " + result);
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            if (shouldClear)
                                realm.delete(clazz);
                            realm.createOrUpdateObjectFromJson(clazz, result);
                        }
                    });
//                    realm.createOrUpdateObjectFromJson(clazz, result);
                } else if (firstField instanceof JSONArray) {
                    Log.v(TAG, "Persisting " + clazz.getSimpleName() + "s: " + result);
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            if (shouldClear)
                                realm.delete(clazz);
                            realm.createOrUpdateAllFromJson(clazz, result);
                        }
                    });
//                    realm.createOrUpdateAllFromJson(clazz, result);
                }
//                realm.commitTransaction();
            } catch (JSONException | IllegalArgumentException e) {
                Log.e(TAG, "This is not a valid JSON: " + result);
                Log.getStackTraceString(e);
            } catch (RealmException e) {
                Log.e(TAG, "Realm exception for JSON: " + result);
                Log.getStackTraceString(e);
            }
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
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
            InputStream caInput = new BufferedInputStream(mApp.getBaseContext().getAssets().open("tls/ca.cer"));
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

    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (null == ni) {
            Log.d(TAG, "no internet connection!");
//            Toast.makeText(mApp.getApplicationContext(), "no internet connection", Toast.LENGTH_LONG).show();
            return false;
        } else {
//            Toast.makeText(mApp.getApplicationContext(), "Internet Connect is detected .. check access to sire", Toast.LENGTH_LONG).show();
            return isOnline();
        }
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
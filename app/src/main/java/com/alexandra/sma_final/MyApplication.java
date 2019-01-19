package com.alexandra.sma_final;

import android.app.Application;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;

import com.alexandra.sma_final.font.FontsOverride;
import com.alexandra.sma_final.font.TypefaceUtil;
import com.alexandra.sma_final.rest.UserDTO;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import realm.City;
import realm.Conversation;
import realm.Message;
import realm.Rating;
import realm.Topic;
import realm.User;

public class MyApplication extends Application implements AsyncResponse<UserDTO> {


    private Location currentLocation;
    public static String city;
    public RequestGateway requestGateway;

    private UserDTO currentUser;

    @Override
    public void onCreate() {
        super.onCreate();

//        TypefaceUtil.overrideFont(this, "SERIF", "font/Montserrat-Regular.ttf");

        FontsOverride.setDefaultFont(this, "DEFAULT", "font/Montserrat-Regular.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "font/Montserrat-Regular.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "font/Montserrat-Regular.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "font/Montserrat-Regular.ttf");

        Realm.init(getApplicationContext());

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        Realm.getInstance(realmConfiguration);

        requestGateway = new RequestGateway(this);
        requestGateway.authenticate(new CurrentUserCallback(this));

        doGetRequests();
//        createMockObjects();
//        doPutRequests();

        GPSTracker gps = new GPSTracker(this);
        currentLocation = gps.location;

        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        List<Address> address = null;
        try {
            address = geoCoder.getFromLocation(gps.latitude, gps.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (address != null && address.size() > 0) {
            city = address.get(0).getLocality();
        } else {
            System.out.println("asdasasdasdasdasdasd city not found !");
            Toast.makeText(this, "Could not get current location! Is GeoLocation active?", Toast.LENGTH_LONG).show();
            city = "Timisoara";
        }
    }

    public void doGetRequests() {
        requestGateway.getNearbyTopics(45.731527D, 21.240686D, null);
        requestGateway.getNearbyTopics("Timisoara");
        requestGateway.getAllUsers();
        requestGateway.getUserByUsername("system");
        requestGateway.getUserConversations();
    }

    public void doPutRequests() {
        //USER IS ALWAYS REQUIRED
        requestGateway.putTopic(new Topic() {{
            setScore(10);
            setTitle("My New Topic");
        }});
        requestGateway.putConversation(new Conversation());
        requestGateway.putMessage(new Message() {{
            setText("My new Message text");
            setUserId(5L);
            setTimestampMillis(System.currentTimeMillis());
        }});
        requestGateway.putRating(new Rating() {{
            setScore(1);
        }});
    }

    public void createMockObjects() {
        User user1 = new User();
        user1.setId(Long.valueOf(1));
        user1.setUsername("user" + 1);
        user1.setKarma(123);
        User user2 = new User();
        user2.setId(Long.valueOf(2));
        user2.setUsername("user" + 2);
        user2.setKarma(345);

        Topic topic1 = new Topic();
        topic1.setId(Long.valueOf(1));
        topic1.setTitle("Padurice");
        topic1.setCoordX(45.731527);
        topic1.setCoordY(21.240686);
        Message topicM1 = new Message();
        topicM1.setId(Long.valueOf(11));
        topicM1.setUserId(user1.getId());
        topicM1.setText("Padurice - Request 1");
        topicM1.setTimestampMillis(Long.valueOf(12344));
        topic1.setMessage(topicM1);
        topic1.setPostedBy(user1);
        topic1.setArchived(false);
        topic1.setScore(1);
        topic1.setCity("Timisoara");

        Topic topic2 = new Topic();
        topic2.setId(Long.valueOf(2));
        topic2.setTitle("McDonalds");
        topic2.setCoordX(45.738999);
        topic2.setCoordY(21.240389);
        Message topicM2 = new Message();
        topicM2.setId(Long.valueOf(12));
        topicM2.setUserId(user1.getId());
        topicM2.setText("McDonalds - request 2");
        topicM2.setTimestampMillis(Long.valueOf(12344));
        topic2.setMessage(topicM2);
        topic2.setPostedBy(user1);
        topic2.setArchived(false);
        topic2.setScore(1);
        topic2.setCity("Timisoara");

        Topic topic3 = new Topic();
        topic3.setId(Long.valueOf(3));
        topic3.setTitle("UPT Electro");
        topic3.setCoordX(45.747757);
        topic3.setCoordY(21.225703);
        Message topicM3 = new Message();
        topicM3.setId(Long.valueOf(13));
        topicM3.setUserId(user1.getId());
        topicM3.setText("UPT Electro - request 3");
        topicM3.setTimestampMillis(Long.valueOf(12344));
        topic3.setMessage(topicM3);
        topic3.setPostedBy(user2);
        topic3.setArchived(false);
        topic3.setScore(1);
        topic3.setCity("Timisoara");

        Topic topic4 = new Topic();
        topic4.setId(Long.valueOf(4));
        topic4.setTitle("Fantana cu pesti");
        topic4.setCoordX(45.752780);
        topic4.setCoordY(21.225349);
        Message topicM4 = new Message();
        topicM4.setId(Long.valueOf(14));
        topicM4.setUserId(user2.getId());
        topicM4.setText("Fantana cu pesti - request 4");
        topicM4.setTimestampMillis(Long.valueOf(12344));
        topic4.setMessage(topicM4);
        topic4.setPostedBy(user2);
        topic4.setArchived(false);
        topic4.setScore(1);
        topic4.setCity("Timisoara");

        Topic topic5 = new Topic();
        topic5.setId(Long.valueOf(5));
        topic5.setTitle("Piata Unirii");
        topic5.setCoordX(45.757929);
        topic5.setCoordY(21.229029);
        Message topicM5 = new Message();
        topicM5.setId(Long.valueOf(15));
        topicM5.setUserId(user1.getId());
        topicM5.setText("Piata Unirii - request 5");
        topicM5.setTimestampMillis(Long.valueOf(12344));
        topic5.setMessage(topicM5);
        topic5.setPostedBy(user1);
        topic5.setArchived(false);
        topic5.setScore(1);
        topic5.setCity("Timisoara");

        Topic topic6 = new Topic();
        topic6.setId(Long.valueOf(6));
        topic6.setTitle("Catedrala");
        topic6.setCoordX(45.751305);
        topic6.setCoordY(21.224457);
        Message topicM6 = new Message();
        topicM6.setId(Long.valueOf(16));
        topicM6.setUserId(user2.getId());
        topicM6.setText("Catedrala - request 6");
        topicM6.setTimestampMillis(Long.valueOf(12344));
        topic6.setMessage(topicM6);
        topic6.setPostedBy(user2);
        topic6.setArchived(false);
        topic6.setScore(1);
        topic6.setCity("Timisoara");

        Topic topic7 = new Topic();
        topic7.setId(Long.valueOf(7));
        topic7.setTitle("Casino");
        topic7.setCoordX(46.768994);
        topic7.setCoordY(23.577911);
        Message topicM7 = new Message();
        topicM7.setId(Long.valueOf(17));
        topicM7.setUserId(user1.getId());
        topicM7.setText("Casino - request 7");
        topicM7.setTimestampMillis(Long.valueOf(12344));
        topic7.setMessage(topicM7);
        topic7.setPostedBy(user1);
        topic7.setArchived(false);
        topic7.setScore(1);
        topic7.setCity("Cluj - Napoca");

        Topic topic8 = new Topic();
        topic8.setId(Long.valueOf(8));
        topic8.setTitle("Observator");
        topic8.setCoordX(46.755706);
        topic8.setCoordY(23.593950);
        Message topicM8 = new Message();
        topicM8.setId(Long.valueOf(18));
        topicM8.setUserId(user2.getId());
        topicM8.setText("Observator - request 8");
        topicM8.setTimestampMillis(Long.valueOf(12344));
        topic8.setMessage(topicM8);
        topic8.setPostedBy(user2);
        topic8.setArchived(false);
        topic8.setScore(1);
        topic8.setCity("Cluj - Napoca");

        City city = new City();
        city.setName("Timisoara");

        Message m1 = new Message();
        m1.setId(Long.valueOf(1));
        m1.setText("message 1");
        m1.setTimestampMillis(Long.valueOf(123004));
        m1.setUserId(user1.getId());
        Message m2 = new Message();
        m2.setId(Long.valueOf(2));
        m2.setText("message 2");
        m2.setTimestampMillis(Long.valueOf(123004));
        m2.setUserId(user2.getId());

        Realm realm = null;
        try { // I could use try-with-resources here
            realm = Realm.getDefaultInstance();
            realm.executeTransactionAsync(realm1 -> {
                realm1.insertOrUpdate(user1);
                realm1.insertOrUpdate(user2);
                realm1.insertOrUpdate(user2);
                realm1.insertOrUpdate(topicM1);
                realm1.insertOrUpdate(topicM2);
                realm1.insertOrUpdate(topicM3);
                realm1.insertOrUpdate(topicM4);
                realm1.insertOrUpdate(topicM5);
                realm1.insertOrUpdate(topicM6);
                realm1.insertOrUpdate(topicM7);
                realm1.insertOrUpdate(topicM8);
                realm1.insertOrUpdate(topic1);
                realm1.insertOrUpdate(topic2);
                realm1.insertOrUpdate(topic3);
                realm1.insertOrUpdate(topic4);
                realm1.insertOrUpdate(topic5);
                realm1.insertOrUpdate(topic6);
                realm1.insertOrUpdate(topic7);
                realm1.insertOrUpdate(topic8);
                realm1.insertOrUpdate(city);
                realm1.insertOrUpdate(m1);
                realm1.insertOrUpdate(m2);
            });
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    @NonNull
    public UserDTO getCurrentUser() {
        if(currentUser == null){
            UserDTO userDTO = new UserDTO();
            userDTO.setId(-1L);
            userDTO.setUsername("fallback-user-1");
            return userDTO;
        }
        return currentUser;
    }

    public void setCurrentUser(UserDTO currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public void processFinish(UserDTO output) {
        setCurrentUser(output);
    }

    private class CurrentUserCallback implements Callback {
        private AsyncResponse<UserDTO> delegate;

        public CurrentUserCallback(AsyncResponse<UserDTO> delegate) {
            this.delegate = delegate;
        }

        @Override
        public void execute() {
            requestGateway.getCurrentUser(delegate);
        }
    }
}

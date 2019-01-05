package com.alexandra.sma_final;

import android.app.Application;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.alexandra.sma_final.font.TypefaceUtil;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import realm.City;
import realm.Topic;
import realm.User;

public class MyApplication extends Application {

    private Location currentLocation;
    public static String city;

    @Override
    public void onCreate() {
        super.onCreate();

//        TypefaceUtil.overrideFont(this, "SERIF", "font/Montserrat-Regular.ttf");

        Realm.init(getApplicationContext());

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        Realm.getInstance(realmConfiguration);

        createMockObjects();

        GPSTracker gps = new GPSTracker(this);
        currentLocation = gps.location;

        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        List<Address> address = null;
        try {
            address = geoCoder.getFromLocation(gps.latitude, gps.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (address.size() > 0) {
            city = address.get(0).getLocality();
        }
    }

    public void createMockObjects(){

        User user1 = new User();
        user1.setID(Long.valueOf(1)); user1.setUsername("anon"+1);
        User user2 = new User();
        user2.setID(Long.valueOf(2)); user2.setUsername("anon"+2);

        Topic topic1 = new Topic();
        topic1.setID(Long.valueOf(1));
        topic1.setTitle("Padurice");
        topic1.setCoordX(45.731527);
        topic1.setCoordY(21.240686);
        topic1.setRequest("Padurice - request 1");
        topic1.setPostedBy(user1);
        topic1.setArchived(false);
        topic1.setScore(1);
        topic1.setCity("Timisoara");

        Topic topic2 = new Topic();
        topic2.setID(Long.valueOf(2));
        topic2.setTitle("McDonalds");
        topic2.setCoordX(45.738999);
        topic2.setCoordY(21.240389);
        topic2.setRequest("McDonalds - request 2");
        topic2.setPostedBy(user1);
        topic2.setArchived(false);
        topic2.setScore(1);
        topic2.setCity("Timisoara");

        Topic topic3 = new Topic();
        topic3.setID(Long.valueOf(3));
        topic3.setTitle("UPT Electro");
        topic3.setCoordX(45.747757);
        topic3.setCoordY(21.225703);
        topic3.setRequest("UPT Electro - request 3");
        topic3.setPostedBy(user2);
        topic3.setArchived(false);
        topic3.setScore(1);
        topic3.setCity("Timisoara");

        Topic topic4 = new Topic();
        topic4.setID(Long.valueOf(4));
        topic4.setTitle("Fantana cu pesti");
        topic4.setCoordX(45.752780);
        topic4.setCoordY(21.225349);
        topic4.setRequest("Fantana cu pesti - request 4");
        topic4.setPostedBy(user2);
        topic4.setArchived(false);
        topic4.setScore(1);
        topic4.setCity("Timisoara");

        Topic topic5 = new Topic();
        topic5.setID(Long.valueOf(5));
        topic5.setTitle("Piata Unirii");
        topic5.setCoordX(45.757929);
        topic5.setCoordY(21.229029);
        topic5.setRequest("Piata Unirii - request 5");
        topic5.setPostedBy(user1);
        topic5.setArchived(false);
        topic5.setScore(1);
        topic5.setCity("Timisoara");

        Topic topic6 = new Topic();
        topic6.setID(Long.valueOf(6));
        topic6.setTitle("Catedrala");
        topic6.setCoordX(45.751305);
        topic6.setCoordY(21.224457);
        topic6.setRequest("Catedrala - request 6");
        topic6.setPostedBy(user2);
        topic6.setArchived(false);
        topic6.setScore(1);
        topic6.setCity("Timisoara");

        Topic topic7 = new Topic();
        topic7.setID(Long.valueOf(7));
        topic7.setTitle("Casino");
        topic7.setCoordX(46.768994);
        topic7.setCoordY(23.577911);
        topic7.setRequest("Casino - request 7");
        topic7.setPostedBy(user1);
        topic7.setArchived(false);
        topic7.setScore(1);
        topic7.setCity("Cluj - Napoca");

        Topic topic8 = new Topic();
        topic8.setID(Long.valueOf(8));
        topic8.setTitle("Observator");
        topic8.setCoordX(46.755706);
        topic8.setCoordY(23.593950);
        topic8.setRequest("Observator - request 8");
        topic8.setPostedBy(user2);
        topic8.setArchived(false);
        topic8.setScore(1);
        topic8.setCity("Cluj - Napoca");

        City city = new City();
        city.setName("Timisoara");

        Realm realm = null;
        try { // I could use try-with-resources here
            realm = Realm.getDefaultInstance();
            realm.executeTransactionAsync(realm1 -> {
                realm1.insertOrUpdate(user1);
                realm1.insertOrUpdate(user2);
                realm1.insertOrUpdate(topic1);
                realm1.insertOrUpdate(topic2);
                realm1.insertOrUpdate(topic3);
                realm1.insertOrUpdate(topic4);
                realm1.insertOrUpdate(topic5);
                realm1.insertOrUpdate(topic6);
                realm1.insertOrUpdate(topic7);
                realm1.insertOrUpdate(topic8);
                realm1.insertOrUpdate(city);
            });
        } finally {
            if(realm != null) {
                realm.close();
            }
        }
    }
}

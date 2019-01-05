package com.alexandra.sma_final;

import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.alexandra.sma_final.view.CityC;
import com.alexandra.sma_final.view.CityCAdapter;
import com.alexandra.sma_final.view.ExpandableRecyclerAdapter;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import realm.Pin;
import realm.Topic;

public class MyPinsActivity extends BaseActivity {

    protected String activityName = "My Pins";

    private static CityCAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<Topic> data;

    private List<CityC> cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        setListeners();
        mActivity.setText(getActivityName());
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_SELECTED);
        bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
        bottomNavigationView.getMenu().findItem(R.id.pinned_action).setChecked(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_dashboard);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //my pins
        data = new ArrayList<Topic>();
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                final RealmResults<Pin> pins  = realm.where(Pin.class).findAll();
                if (pins.size() != 0){
                    for (Pin p: pins){
                        final RealmResults<Topic> topic  = realm.where(Topic.class)
                                .equalTo("ID", p.getTopicID()).findAll();

                        if(topic.size() > 0){
                            for(Topic t : topic){
                                data.add(t);
                            }
                        }
                    }
                }
            });
        }

        data.sort(new Comparator<Topic>() {
            @Override
            public int compare(Topic o1, Topic o2) {
                return o1.getCity().compareTo(o2.getCity());
            }
        });

        cities = new ArrayList<>();
        ArrayList<String> citiesname = new ArrayList<>();
        HashMap<String, ArrayList<Topic>> cityTopics = new HashMap<>();
        for(int i = 0; i < data.size(); i++){
            ArrayList<Topic> topics = cityTopics.get(data.get(i).getCity());
            if(topics == null) {
                topics = new ArrayList<>();
            }
            topics.add(data.get(i));
            cityTopics.put(data.get(i).getCity(), topics);
            if(!citiesname.contains(data.get(i).getCity())) {
                citiesname.add(data.get(i).getCity());
            }
        }
        for(int j = 0; j < cityTopics.size(); j++) {
            cities.add(new CityC(citiesname.get(j), cityTopics.get(citiesname.get(j))));
        }

        adapter = new CityCAdapter(this, cities, "MYPINS");
        adapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
            @Override
            public void onListItemExpanded(int position) {
                CityC expandedMovieCategory = cities.get(position);
            }

            @Override
            public void onListItemCollapsed(int position) {
                CityC collapsedMovieCategory = cities.get(position);
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        adapter.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public String getActivityName() {
        return activityName;
    }
}

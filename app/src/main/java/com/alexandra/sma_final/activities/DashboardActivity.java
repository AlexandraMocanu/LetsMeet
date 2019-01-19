package com.alexandra.sma_final.activities;

import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.alexandra.sma_final.R;
import com.alexandra.sma_final.customviews.CityC;
import com.alexandra.sma_final.customviews.CityCAdapter;
import com.alexandra.sma_final.customviews.ExpandableRecyclerAdapter;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import realm.Topic;

public class DashboardActivity extends BaseActivity {

    protected String activityName = "Dashboard";

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
        bottomNavigationView.getMenu().findItem(R.id.dashboard_action).setChecked(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_dashboard);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<Topic>();
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                final RealmResults<Topic> topics  = realm.where(Topic.class).findAll();
                if (topics.size() != 0){
                    for(Topic t : topics){
                        data.add(t);
                    }
                }
            });
        }

        data.sort(new Comparator<Topic>() {
            @Override
            public int compare(Topic o1, Topic o2) {
                if(o1 != null && o2 != null){
                    String city1 = o1.getCity();
                    String city2 = o2.getCity();
                    if(city1 == null || city2 == null){
                        return 0;
                    }
                    return city1.compareTo(city2);
                }
                return 0;
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

        adapter = new CityCAdapter(this, cities, "DASHBOARD");
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

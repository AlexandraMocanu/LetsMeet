package com.alexandra.sma_final.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexandra.sma_final.R;
import com.alexandra.sma_final.customviews.MontserratTextView;

import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import realm.City;

public class ModifyCitiesFragment extends Fragment{

    private static final int REQUEST_SELECT_PLACE = 1000;
    private RecyclerView mRecyclerView;
    private ListFavoriteCitiesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private PlaceAutocompleteFragment mSearchCity;
//    private PlaceAutocompleteFragment mSearcyCity;

    private static View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.places_autocomplete, container, false);

            mSearchCity = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
            mSearchCity.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    ModifyCitiesFragment.this.onPlaceSelected(place);
                }

                @Override
                public void onError(Status status) {
                    ModifyCitiesFragment.this.onError(status);
                }
            });
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                    .build();
            mSearchCity.setFilter(typeFilter);

            mRecyclerView = (RecyclerView) view.findViewById(R.id.favorite_cities_list);
            mLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            ArrayList<City> cities = getCities();
            mAdapter = new ListFavoriteCitiesAdapter(cities);
            mRecyclerView.setAdapter(mAdapter);

        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }
        return view;

    }

    public void onPlaceSelected(Place place) {
        City c = new City();
        c.setName(new String(place.getName().toString()));
        Realm realm = null;
        try { // I could use try-with-resources here
            realm = Realm.getDefaultInstance();
            realm.executeTransactionAsync(realm1 -> {
                realm1.insertOrUpdate(c);
            });
        } finally {
            if(realm != null) {
                realm.close();
            }
        }

        if(!mAdapter.contains(c)){
            mAdapter.addCity(c);
            mAdapter.notifyItemInserted(mAdapter.getItemCount());
            Toast.makeText(
                    getActivity(),
                    "Succesfully added city",
                    Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(
                     getActivity(),
                    "The city already exists in your favorites",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void onError(Status status){
        Toast.makeText(
            getActivity(),
            "The city couldn't be added to the list of favorites. Might be because you already added it sometime before.",
            Toast.LENGTH_LONG).show();
    }

    private ArrayList<City> getCities(){
        ArrayList<City> favorites = new ArrayList<>();
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                final RealmResults<City> cities  = realm.where(City.class).findAll();
                for(City c : cities){
                    favorites.add(c);
                }
            });
        }

        return favorites;
    }

    private class ListFavoriteCitiesAdapter extends RecyclerView.Adapter<ListFavoriteCitiesAdapter.MyViewHolder> {

        private ArrayList<City> favoriteCities;

        public class MyViewHolder extends RecyclerView.ViewHolder{
            public MontserratTextView cityName;
            public Button mRemoveButton;

            public MyViewHolder(View itemView){
                super(itemView);

                this.cityName = (MontserratTextView) itemView.findViewById(R.id.city);
                this.mRemoveButton = (Button) itemView.findViewById(R.id.remove_city);
            }
        }

        public ListFavoriteCitiesAdapter(ArrayList<City> results){
            favoriteCities = results;
        }

        @Override
        public ListFavoriteCitiesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View cityView = inflater.inflate(R.layout.city_item, parent, false);

            MyViewHolder vh = new MyViewHolder(cityView);
            return vh;
        }

        @Override
        public void onBindViewHolder(ListFavoriteCitiesAdapter.MyViewHolder viewHolder, int position) {
            City city = favoriteCities.get(position);

            MontserratTextView MontserratTextView = viewHolder.cityName;
            MontserratTextView.setText(city.getName());
            Button removeButton = viewHolder.mRemoveButton;
            removeButton.setText("Remove city");
            removeButton.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "font/Montserrat-Regular.ttf"));
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String cityName = city.getName();

                    try (Realm realm = Realm.getDefaultInstance()) {
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                RealmResults<City> rows = bgRealm.where(City.class).equalTo("name", cityName).findAll();
                                rows.deleteAllFromRealm();
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(
                                        v.getContext(),
                                        "Succesfully deleted city",
                                        Toast.LENGTH_LONG).show();
                                if(position > favoriteCities.size()){
                                    favoriteCities.remove(favoriteCities.size() - 1);
                                    notifyItemRemoved(favoriteCities.size() - 1);
                                }else{
                                    favoriteCities.remove(position);
                                    notifyItemRemoved(position);
                                }

                            }
                        });
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return favoriteCities.size();
        }

        public void addCity(City c){
            favoriteCities.add(c);
        }

        public boolean contains(City c){
            for(int i = 0; i < favoriteCities.size(); i++){
                if(favoriteCities.get(i).getName().equals(c.getName())){
                    return true;
                }
            }
            return false;
        }
    }
}

package com.alexandra.sma_final.customviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexandra.sma_final.R;

import java.util.List;

import realm.Topic;

public class CityCAdapter extends ExpandableRecyclerAdapter<CityCViewHolder, TopicsViewHolder> {

    private LayoutInflater mInflator;
    private String caller;

    public CityCAdapter(Context context, List<? extends ParentListItem> parentItemList, String caller) {
        super(parentItemList);
        mInflator = LayoutInflater.from(context);
        this.caller = caller;
    }

    @Override
    public CityCViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View cityNameView = mInflator.inflate(R.layout.cityc_view, parentViewGroup, false);
        return new CityCViewHolder(cityNameView);
    }

    @Override
    public TopicsViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View topicsView = mInflator.inflate(R.layout.cardview_dashboard, childViewGroup, false);
        return new TopicsViewHolder(topicsView, caller, this);
    }

    @Override
    public void onBindParentViewHolder(CityCViewHolder cityCViewHolder, int position, ParentListItem parentListItem) {
        CityC city = (CityC) parentListItem;
        cityCViewHolder.bind(city);
    }

    @Override
    public void onBindChildViewHolder(TopicsViewHolder topicsViewHolder, int position, Object childListItem) {
        Topic topic = (Topic) childListItem;
        topicsViewHolder.bind(topic);
    }
}

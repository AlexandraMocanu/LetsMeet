package com.alexandra.sma_final.view;

import java.util.List;

import realm.Topic;

public class CityC implements ParentListItem {
    private String mName;
    private List<Topic> mTopics;

    public CityC(String name, List<Topic> topics) {
        mName = name;
        mTopics = topics;
    }

    public String getName() {
        return mName;
    }

    @Override
    public List<?> getChildItemList() {
        return mTopics;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}

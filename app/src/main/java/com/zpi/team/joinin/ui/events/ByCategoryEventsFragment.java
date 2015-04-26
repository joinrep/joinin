package com.zpi.team.joinin.ui.events;

import com.zpi.team.joinin.entities.Category;

/**
 * Created by Arkadiusz on 2015-04-25.
 */
public class ByCategoryEventsFragment extends EventsRecyclerFragment {

    private Category mCategory;

    public ByCategoryEventsFragment setCategory(Category c){
        mCategory = c;
        return this;
    }

    @Override
    public int getType() {
        return BY_CATEGORY;
    }

    @Override
    public Category getCategory() {
        return mCategory;
    }
}

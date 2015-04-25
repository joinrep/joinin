package com.zpi.team.joinin.ui.common;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.entities.Category;

/**
 * Created by Arkadiusz on 2015-04-25.
 */
public class CategoryEventsFragment extends EventsRecyclerFragment {

    private Category mCategory;

    public CategoryEventsFragment setCategory(Category c){
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

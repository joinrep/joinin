package com.zpi.team.joinin.ui.newevent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.entities.Category;

import java.util.List;

/**
 * Created by Arkadiusz on 2015-04-03.
 */
public class CategoryAdapter extends ArrayAdapter {
    private Context mContext;
    private List<Category> mCategories;
    private boolean usePlaceholder = true;
    private String placeholder;

    public CategoryAdapter(Context context, int layout, List<Category> categories) {
        super(context,layout);
        mContext = context;
        mCategories = categories;
        setPromptText();

    }

    private void setPromptText(){
        this.placeholder = mContext.getResources().getString(R.string.choose_category);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // TODO AK wycentrowac nad zaznaczonym
        usePlaceholder = false;
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        notifyDataSetChanged();
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.spinner, null);

        TextView category = (TextView) v.findViewById(R.id.categoryField);
        category.setText(mCategories.get(position).getName());

        if(position == 0 && usePlaceholder) {
            category.setText(placeholder);
            category.setTextColor(mContext.getResources().getColor(R.color.black_54));
        }

        return category;
    }

    public void usePlaceholder(boolean usePlaceholder) {
        this.usePlaceholder = usePlaceholder;
    }

    @Override
    public int getCount() {
        return mCategories.size();
    }

    @Override
    public Object getItem(int position) {
        return mCategories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

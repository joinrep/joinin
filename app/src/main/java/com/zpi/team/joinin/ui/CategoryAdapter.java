package com.zpi.team.joinin.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.entities.Category;

import java.util.List;

/**
 * Created by Arkadiusz on 2015-04-03.
 */
public class CategoryAdapter extends ArrayAdapter {
    Context mContext;
    List<Category> mCategories;
    boolean isFirst = true;
    String firstElement;

    public CategoryAdapter(Context context, int layout, List<Category> categories) {
        super(context,layout);
        mContext = context;
        mCategories = categories;
        setPromptText();

    }

    private void setPromptText(){
        String prompt = mContext.getResources().getString(R.string.choose_category);
        this.firstElement = mCategories.get(0).getName();
        mCategories.get(0).setName(prompt);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // TODO AK wycentrowac nad zaznaczonym
        if(isFirst) {
            mCategories.get(0).setName(firstElement);
            isFirst = false;
        }
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

        if(position == 0 && isFirst)
            category.setTextColor(mContext.getResources().getColor(R.color.black_54));

        return category;
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

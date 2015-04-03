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
    boolean mChoosen = false;

    public CategoryAdapter(Context context, int layout, List<Category> categories) {
        super(context,layout);
        mContext = context;
        mCategories = categories;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.spinner, null);

        TextView category = (TextView) v.findViewById(R.id.categoryField);
        if(position == 0 && !mChoosen) {
            category.setText(mContext.getResources().getString(R.string.choose_category));
            category.setTextColor(mContext.getResources().getColor(R.color.black_54));
        }
        else{
            category.setText(mCategories.get(position).getName());
            mChoosen = true;
        }
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

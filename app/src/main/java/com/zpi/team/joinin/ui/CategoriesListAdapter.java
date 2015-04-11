package com.zpi.team.joinin.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.entities.Category;

import java.util.List;

/**
 * Created by MK on 2015-04-03.
 */
public class CategoriesListAdapter extends BaseAdapter {
    private static List<Category> categories;
    private Context mContext;

    public CategoriesListAdapter(Context categoriesFragment, List<Category> categories) {
        this.categories = categories;
        mContext = categoriesFragment;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return categories.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.categories_list_item, null);
            holder = new ViewHolder();
            holder.categoryName = (TextView) convertView.findViewById(R.id.categoriesText);
            holder.categoryIcon = (ImageView) convertView.findViewById(R.id.categoriesImage);
            holder.categoryStar = (ImageView) convertView.findViewById(R.id.categoriesStar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //TODO MK set category icon
        final Category category = (Category) getItem(position);
        Context context = holder.categoryIcon.getContext();
        int iconId = context.getResources().getIdentifier(category.getIconPath(), "drawable", context.getPackageName());

        holder.categoryName.setText(categories.get(position).getName());
        holder.categoryIcon.setImageResource(iconId);

        final ImageView categoryStar = holder.categoryStar;
        if (category.isUserFavorite()) {
            categoryStar.setImageResource(R.drawable.ic_star);
        } else {
            categoryStar.setImageResource(R.drawable.ic_star_outline);
        }

        holder.categoryStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO MK add to user favorite

                if (category.isUserFavorite()) {
                    category.setUserFavorite(false);
                    categoryStar.setImageResource(R.drawable.ic_star_outline);
                } else {
                    category.setUserFavorite(true);
                    categoryStar.setImageResource(R.drawable.ic_star);
                }
                ((MainActivity) mContext).updateNavDrawerItems();

            }
        });




        return convertView;
    }

    static class ViewHolder{
        ImageView categoryIcon, categoryStar;
        TextView categoryName;
    }
}



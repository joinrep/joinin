package com.zpi.team.joinin.ui.categories;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Category;
import com.zpi.team.joinin.entities.User;
import com.zpi.team.joinin.repository.CategoryRepository;
import com.zpi.team.joinin.ui.main.MainActivity;

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
        final Category category = (Category) getItem(position);
        holder.categoryName.setText(category.getName());
        holder.categoryIcon.setImageResource(category.getIconId());

        final ImageView categoryStar = holder.categoryStar;
        toggleCategoryStar(category, categoryStar);

        holder.categoryStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new ToggleFavorite(category, categoryStar).execute();
                toggleFavCategory(category, categoryStar);

            }
        });
        return convertView;
    }

    static class ViewHolder{
        ImageView categoryIcon, categoryStar;
        TextView categoryName;
    }

    private void toggleCategoryStar(Category category, ImageView categoryStar) {
        if (category.isUserFavorite()) {
            categoryStar.setImageResource(R.drawable.ic_category_star);
        } else {
            categoryStar.setImageResource(R.drawable.ic_category_star_outline);
        }
    }

    private void toggleFavCategory(Category category, ImageView categoryStar) {
        category.setUserFavorite(!category.isUserFavorite());
        toggleCategoryStar(category, categoryStar);
        ((MainActivity) mContext).updateNavDrawerItems();
    }

    /*
    private class ToggleFavorite extends AsyncTask<String, String, String> {
        SessionStorage storage = SessionStorage.getInstance();
        private Category category;
        private ImageView categoryStar;
        ProgressDialog progressDialog;

        ToggleFavorite(Category category, ImageView categoryStar) {
            super();
            this.category = category;
            this.categoryStar = categoryStar;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(mContext, null, mContext.getResources().getString(R.string.loading_favorite_category), true);
        }

        protected String doInBackground(String... args) {
            new CategoryRepository().setFavorite(storage.getUser(), category, !category.isUserFavorite());
            return "dumb";
        }

        protected void onPostExecute(String s) {
            category.setUserFavorite(!category.isUserFavorite());
            if (category.isUserFavorite()) {
                categoryStar.setImageResource(R.drawable.ic_category_star);
            } else {
                categoryStar.setImageResource(R.drawable.ic_category_star_outline);
            }
            ((MainActivity) mContext).updateNavDrawerItems();
            progressDialog.dismiss();
        }
    }
    */
}



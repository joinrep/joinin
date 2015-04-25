package com.zpi.team.joinin.ui.categories;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Category;
import com.zpi.team.joinin.ui.common.CategoryEventsFragment;
import com.zpi.team.joinin.ui.main.MainActivity;

import java.util.List;

/**
 * Created by MK on 2015-04-03.
 */

public class CategoriesFragment extends Fragment {

    private ListView list ;
    private ArrayAdapter<String> adapter ;
    private List<Category> categories = SessionStorage.getInstance().getCategories();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        ListView lv = (ListView)view.findViewById(R.id.categoriesList);
        lv.setAdapter(new CategoriesListAdapter(getActivity(), categories));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> av, View view, int position, long arg3)
            {
                Log.d("ListView", categories.get(position).getName());
                Category category = categories.get(position);
                Fragment fragment = new CategoryEventsFragment().setCategory(category);

                int menuPosition = 1;
                if (category.isUserFavorite()) {

                    menuPosition = MainActivity.ADD_CATEGORY_POSITION;
                    for (Category cat : categories) {
                        if (cat.isUserFavorite()) {
                            menuPosition++;
                        }
                        if (cat == category) {
                            break;
                        }
                    }
                }
                ((MainActivity)getActivity()).switchFragment(fragment, menuPosition);
            }
        });

        return view;
    }

}
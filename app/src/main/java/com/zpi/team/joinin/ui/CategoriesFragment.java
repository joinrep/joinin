package com.zpi.team.joinin.ui;

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

import java.util.List;

/**
 * Created by MK on 2015-04-03.
 */

public class CategoriesFragment extends Fragment {

    private ListView list ;
    private ArrayAdapter<String> adapter ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        List<Category> categories = getCategories();
        ListView lv = (ListView)view.findViewById(R.id.categoriesList);
        lv.setAdapter(new CategoriesListAdapter(getActivity(), categories));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> av, View view, int position, long arg3)
            {
                Log.d("ListView", ""+position);
            }
        });

        return view;
    }

    private List<Category> getCategories() {
        /*List<Category> result = new ArrayList<Category>();
        result.add(new Category(1,"Piłka nożna", "ic_bike"));
        result.add(new Category(2,"Koszykówka", "ic_place"));
        return result;*/
        return SessionStorage.getInstance().getCategories();
    }
}
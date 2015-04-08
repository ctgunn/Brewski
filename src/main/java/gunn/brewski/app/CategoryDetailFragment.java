package gunn.brewski.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by SESA300553 on 4/7/2015.
 */
public class CategoryDetailFragment extends Fragment {

    public CategoryDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category_detail, container, false);
        return rootView;
    }
}

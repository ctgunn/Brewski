package gunn.brewski.app;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class CategoryListActivity extends ActionBarActivity implements CategoryListFragment.Callback {
    private final String LOG_TAG = BreweryListActivity.class.getSimpleName();

    private static final String CATEGORY_DETAIL_FRAGMENT_TAG = "CATDFTAG";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_category_list);

        if (findViewById(R.id.category_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.category_detail_container, new CategoryDetailFragment(), CATEGORY_DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        CategoryListFragment categoryListFragment =  ((CategoryListFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_category_list));
        categoryListFragment.setUseTodayLayout(!mTwoPane);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category_list, menu);
        return true;
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(CategoryDetailFragment.CATEGORY_DETAIL_URI, contentUri);

            CategoryDetailFragment categoryDetailFragment = new CategoryDetailFragment();
            categoryDetailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.category_detail_container, categoryDetailFragment, CATEGORY_DETAIL_FRAGMENT_TAG)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, CategoryDetailActivity.class).setData(contentUri);
            startActivity(intent);
        }
    }
}
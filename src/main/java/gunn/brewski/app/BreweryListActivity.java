package gunn.brewski.app;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class BreweryListActivity extends ActionBarActivity implements BreweryListFragment.Callback {
    private final String LOG_TAG = BreweryListActivity.class.getSimpleName();

    private static final String BREWERY_DETAIL_FRAGMENT_TAG = "BREWDFTAG";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_brewery_list);

        if (findViewById(R.id.brewery_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.brewery_detail_container, new BreweryDetailFragment(), BREWERY_DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        BreweryListFragment breweryListFragment =  ((BreweryListFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_brewery_list));
        breweryListFragment.setUseTodayLayout(!mTwoPane);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_brewery_list_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(BreweryDetailFragment.BREWERY_DETAIL_URI, contentUri);

            BreweryDetailFragment breweryDetailFragment = new BreweryDetailFragment();
            breweryDetailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.brewery_detail_container, breweryDetailFragment, BREWERY_DETAIL_FRAGMENT_TAG)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, BreweryDetailActivity.class).setData(contentUri);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent dashboardIntent = new Intent(this, DashboardActivity.class);
        startActivity(dashboardIntent);
    }
}

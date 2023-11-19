package gunn.brewski.app;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class BeerListActivity extends ActionBarActivity implements BeerListFragment.Callback {
    private final String LOG_TAG = BeerListActivity.class.getSimpleName();

    private static final String BEER_DETAIL_FRAGMENT_TAG = "BEERDFTAG";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beer_list);

        if (findViewById(R.id.beer_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.beer_detail_container, new BeerDetailFragment(), BEER_DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        BeerListFragment beerListFragment =  ((BeerListFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_beer_list));
        beerListFragment.setUseTodayLayout(!mTwoPane);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_beer_list_activity, menu);
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
            args.putParcelable(BeerDetailFragment.BEER_DETAIL_URI, contentUri);

            BeerDetailFragment beerDetailFragment = new BeerDetailFragment();
            beerDetailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.beer_detail_container, beerDetailFragment, BEER_DETAIL_FRAGMENT_TAG)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, BeerDetailActivity.class).setData(contentUri);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent dashboardIntent = new Intent(this, DashboardActivity.class);
        startActivity(dashboardIntent);
    }
}

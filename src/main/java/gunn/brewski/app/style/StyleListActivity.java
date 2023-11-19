package gunn.brewski.app;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class StyleListActivity extends ActionBarActivity implements StyleListFragment.Callback {
    private final String LOG_TAG = StyleListActivity.class.getSimpleName();

    private static final String STYLE_DETAIL_FRAGMENT_TAG = "STYLEDFTAG";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_style_list);

        if (findViewById(R.id.style_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.style_detail_container, new StyleDetailFragment(), STYLE_DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        StyleListFragment styleListFragment =  ((StyleListFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_style_list));
        styleListFragment.setUseTodayLayout(!mTwoPane);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_style_list_activity, menu);
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
            args.putParcelable(StyleDetailFragment.STYLE_DETAIL_URI, contentUri);

            StyleDetailFragment styleDetailFragment = new StyleDetailFragment();
            styleDetailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.style_detail_container, styleDetailFragment, STYLE_DETAIL_FRAGMENT_TAG)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, StyleDetailActivity.class).setData(contentUri);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent dashboardIntent = new Intent(this, DashboardActivity.class);
        startActivity(dashboardIntent);
    }
}

package gunn.brewski.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import gunn.brewski.app.sync.BrewskiSyncAdapter;


public class MainActivity extends ActionBarActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    public static Context applicationContext;
    public static Application application;

    private static final String CATEGORY_DETAILFRAGMENT_TAG = "CATDFTAG";
    private static final String BREWERY_DETAILFRAGMENT_TAG = "BREWDFTAG";

    private boolean mTwoPane;
    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applicationContext = getApplicationContext();
        application = getApplication();
        mLocation = Utility.getPreferredLocation(this);

        Intent loadingScreenIntent = new Intent(this, LoadingScreenActivity.class);
        loadingScreenIntent.putExtra("screenLoading", "dashboard");
        startActivity(loadingScreenIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

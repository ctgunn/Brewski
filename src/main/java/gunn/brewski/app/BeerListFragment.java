package gunn.brewski.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import gunn.brewski.app.data.BrewskiContract;
import gunn.brewski.app.sync.BrewskiSyncAdapter;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {link OnFragmentInteractionListener}
 * interface.
 */
public class BeerListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LOG_TAG = BeerListFragment.class.getSimpleName();
    private BeerListAdapter mBeerListAdapter;

    private static final String BEERS_SHARE_HASHTAG = " #BrewskiBeers";

    private ShareActionProvider mBeersShareActionProvider;
    private String mBeers;

    private ListView mBeerListView;
    private int mPosition = ListView.INVALID_POSITION;
    private boolean mUseTodayLayout;

    private static final String SELECTED_KEY = "selected_position";

    private static final int BEER_LIST_LOADER = 0;
    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] BEER_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            BrewskiContract.BeerEntry.TABLE_NAME + "." + BrewskiContract.BeerEntry._ID,
            BrewskiContract.BeerEntry.COLUMN_BEER_ID,
            BrewskiContract.BeerEntry.COLUMN_BEER_NAME,
            BrewskiContract.BeerEntry.COLUMN_BEER_DESCRIPTION,
            BrewskiContract.BeerEntry.COLUMN_BREWERY_ID,
            BrewskiContract.BeerEntry.COLUMN_CATEGORY_ID,
            BrewskiContract.BeerEntry.COLUMN_STYLE_ID,
            BrewskiContract.BeerEntry.COLUMN_LABEL_LARGE,
            BrewskiContract.BeerEntry.COLUMN_LABEL_MEDIUM,
            BrewskiContract.BeerEntry.COLUMN_LABEL_ICON
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_BEER_ID = 1;
    static final int COL_BEER_NAME = 2;
    static final int COL_BEER_DESCRIPTION = 3;
    static final int COL_BREWERY_ID = 4;
    static final int COL_CATEGORY_ID = 5;
    static final int COL_STYLE_ID = 6;
    static final int COL_LABEL_LARGE = 7;
    static final int COL_LABEL_MEDIUM = 8;
    static final int COL_LABEL_ICON = 9;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    public BeerListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
//        updateBrewski();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_beer_list_fragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_beer_list_share);

        // Get the provider and hold onto it to set/change the share intent.
        mBeersShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mBeers != null) {
            mBeersShareActionProvider.setShareIntent(createShareBeersIntent());
        }
    }

    private Intent createShareBeersIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mBeers + BEERS_SHARE_HASHTAG);

        return shareIntent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The ForecastAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mBeerListAdapter = new BeerListAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_beer_list, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        mBeerListView = (ListView) rootView.findViewById(R.id.listview_beer);
        mBeerListView.setAdapter(mBeerListAdapter);
        // We'll call our MainActivity
        mBeerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                BrewskiApplication.setCurrentBeerId(cursor.getString(COL_BEER_ID));
                if (cursor != null) {
                    ((Callback) getActivity()).onItemSelected(
                        BrewskiContract.BeerEntry.buildBeerUriWithBeerId(cursor.getString(COL_BEER_ID))
                    );
                }

                mPosition = position;
            }
        });

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        mBeerListAdapter.setUseTodayLayout(mUseTodayLayout);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(BEER_LIST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    // since we read the location when we create the loader, all we need to do is restart things
    void onLocationChanged( ) {
        updateBrewski();
        getLoaderManager().restartLoader(BEER_LIST_LOADER, null, this);
    }

    private void updateBrewski() {
        BrewskiSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, filter the query to return weather only for
        // dates after or including today.

        // Sort order:  Ascending, by date.
        String sortOrder = BrewskiContract.BeerEntry.COLUMN_BEER_NAME + " ASC";

        Uri beerUri = BrewskiContract.BeerEntry.BEER_CONTENT_URI;

        return new CursorLoader(
            getActivity(),
            beerUri,
            BEER_COLUMNS,
            null,
            null,
            sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mBeerListAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mBeerListView.smoothScrollToPosition(mPosition);
        }

        mBeers = "Check out all these awesome beers that I found on this cool new app, BREWSKI.";

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mBeersShareActionProvider != null) {
            mBeersShareActionProvider.setShareIntent(createShareBeersIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBeerListAdapter.swapCursor(null);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
        if (mBeerListAdapter != null) {
            mBeerListAdapter.setUseTodayLayout(mUseTodayLayout);
        }
    }
}

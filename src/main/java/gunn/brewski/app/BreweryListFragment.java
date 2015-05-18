package gunn.brewski.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
public class BreweryListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LOG_TAG = BreweryListFragment.class.getSimpleName();
    private BreweryListAdapter mBreweryListAdapter;

    private ListView mBreweryListView;
    private int mPosition = ListView.INVALID_POSITION;
    private boolean mUseTodayLayout;

    private static final String SELECTED_KEY = "selected_position";

    private static final int BREWERY_LIST_LOADER = 1;
    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] BREWERY_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            BrewskiContract.BreweryEntry.TABLE_NAME + "." + BrewskiContract.BreweryEntry._ID,
            BrewskiContract.BreweryEntry.COLUMN_BREWERY_ID,
            BrewskiContract.BreweryEntry.COLUMN_BREWERY_NAME,
            BrewskiContract.BreweryEntry.COLUMN_BREWERY_DESCRIPTION,
            BrewskiContract.BreweryEntry.COLUMN_BREWERY_WEBSITE,
            BrewskiContract.BreweryEntry.COLUMN_ESTABLISHED,
            BrewskiContract.BreweryEntry.COLUMN_IMAGE_LARGE,
            BrewskiContract.BreweryEntry.COLUMN_IMAGE_MEDIUM,
            BrewskiContract.BreweryEntry.COLUMN_IMAGE_ICON
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_BREWERY_ID = 1;
    static final int COL_BREWERY_NAME = 2;
    static final int COL_BREWERY_DESCRIPTION = 3;
    static final int COL_IMAGE_MEDIUM = 7;

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

    public BreweryListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_brewery_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_brewery_list) {
//            openPreferredLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The ForecastAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mBreweryListAdapter = new BreweryListAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_brewery_list, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        mBreweryListView = (ListView) rootView.findViewById(R.id.listview_brewery);
        mBreweryListView.setAdapter(mBreweryListAdapter);
        // We'll call our MainActivity
        mBreweryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                BrewskiApplication.setCurrentBreweryId(cursor.getString(COL_BREWERY_ID));
                if (cursor != null) {
                    ((Callback) getActivity()).onItemSelected(
                            BrewskiContract.BreweryEntry.buildBreweryUriWithBreweryId(cursor.getString(COL_BREWERY_ID))
                    );
                }

                mPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        mBreweryListAdapter.setUseTodayLayout(mUseTodayLayout);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(BREWERY_LIST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    // since we read the location when we create the loader, all we need to do is restart things
    void onLocationChanged( ) {
        updateBrewery();
        getLoaderManager().restartLoader(BREWERY_LIST_LOADER, null, this);
    }

    private void updateBrewery() {
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

        // Sort order:  Ascending, by brewery name.
        String sortOrder = BrewskiContract.BreweryEntry.COLUMN_BREWERY_NAME + " ASC";

        Uri breweryUri = BrewskiContract.BreweryEntry.BREWERY_CONTENT_URI;

        return new CursorLoader(getActivity(),
                breweryUri,
                BREWERY_COLUMNS,
                BrewskiContract.BreweryEntry.TABLE_NAME + "." +
                BrewskiContract.BreweryEntry.COLUMN_BREWERY_NAME + " IS NOT NULL",
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mBreweryListAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mBreweryListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBreweryListAdapter.swapCursor(null);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
        if (mBreweryListAdapter != null) {
            mBreweryListAdapter.setUseTodayLayout(mUseTodayLayout);
        }
    }
}

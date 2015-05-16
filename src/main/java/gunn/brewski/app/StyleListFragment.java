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
public class StyleListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LOG_TAG = StyleListFragment.class.getSimpleName();
    private StyleListAdapter mStyleListAdapter;

    private ListView mStyleListView;
    private int mPosition = ListView.INVALID_POSITION;
    private boolean mUseTodayLayout;

    private static final String SELECTED_KEY = "selected_position";

    private static final int STYLE_LIST_LOADER = 1;
    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] STYLE_COLUMNS = {
        // In this case the id needs to be fully qualified with a table name, since
        // the content provider joins the location & weather tables in the background
        // (both have an _id column)
        // On the one hand, that's annoying.  On the other, you can search the weather table
        // using the location set by the user, which is only in the Location table.
        // So the convenience is worth it.
        BrewskiContract.StyleEntry.TABLE_NAME + "." + BrewskiContract.StyleEntry._ID,
        BrewskiContract.StyleEntry.COLUMN_STYLE_ID,
        BrewskiContract.StyleEntry.COLUMN_STYLE_NAME,
        BrewskiContract.StyleEntry.COLUMN_STYLE_SHORT_NAME,
        BrewskiContract.StyleEntry.COLUMN_STYLE_DESCRIPTION,
        BrewskiContract.StyleEntry.COLUMN_CATEGORY_ID
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_STYLE_ID = 1;
    static final int COL_STYLE_NAME = 2;
    static final int COL_STYLE_SHORT_NAME = 3;
    static final int COL_STYLE_DESCRIPTION = 4;

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

    public StyleListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_style_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_style_list) {
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
        mStyleListAdapter = new StyleListAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_brewery_list, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        mStyleListView = (ListView) rootView.findViewById(R.id.listview_brewery);
        mStyleListView.setAdapter(mStyleListAdapter);
        // We'll call our MainActivity
        mStyleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((Callback) getActivity()).onItemSelected(
                            BrewskiContract.StyleEntry.STYLE_CONTENT_URI
                    );
                }

                mPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        mStyleListAdapter.setUseTodayLayout(mUseTodayLayout);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(STYLE_LIST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    // since we read the location when we create the loader, all we need to do is restart things
    void onLocationChanged( ) {
        updateStyle();
        getLoaderManager().restartLoader(STYLE_LIST_LOADER, null, this);
    }

    private void updateStyle() {
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
        // Sort order:  Ascending, by date.
        String sortOrder = BrewskiContract.StyleEntry.COLUMN_STYLE_NAME + " ASC";

        Uri styleListUri = BrewskiContract.StyleEntry.STYLE_CONTENT_URI;

        return new CursorLoader(getActivity(),
                styleListUri,
                STYLE_COLUMNS,
                BrewskiContract.StyleEntry.TABLE_NAME + "." +
                    BrewskiContract.StyleEntry.COLUMN_STYLE_NAME + " IS NOT NULL AND " +
                    BrewskiContract.StyleEntry.TABLE_NAME + "." +
                    BrewskiContract.StyleEntry.COLUMN_STYLE_DESCRIPTION + " IS NOT NULL",
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mStyleListAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mStyleListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mStyleListAdapter.swapCursor(null);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
        if (mStyleListAdapter != null) {
            mStyleListAdapter.setUseTodayLayout(mUseTodayLayout);
        }
    }
}

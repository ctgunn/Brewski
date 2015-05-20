package gunn.brewski.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;


import gunn.brewski.app.data.BrewskiContract;
import gunn.brewski.app.sync.BrewskiSyncAdapter;

public class StyleListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LOG_TAG = StyleListFragment.class.getSimpleName();
    private StyleListAdapter mStyleListAdapter;

    private static final String STYLES_SHARE_HASHTAG = " #BrewskiStyles";

    private ShareActionProvider mStylesShareActionProvider;
    private String mStyles;

    private ListView mStyleListView;
    private int mPosition = ListView.INVALID_POSITION;
    private boolean mUseTodayLayout;
    private boolean loadingMore;
    private IntentFilter styleFilter;

    private static final String SELECTED_KEY = "selected_position";

    private BroadcastReceiver styleReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            loadingMore = false;
            getLoaderManager().restartLoader(STYLE_LIST_LOADER, null, StyleListFragment.this);
        }
    };

    private static final int STYLE_LIST_LOADER = 1;

    private static final String[] STYLE_COLUMNS = {
        BrewskiContract.StyleEntry.TABLE_NAME + "." + BrewskiContract.StyleEntry._ID,
        BrewskiContract.StyleEntry.COLUMN_STYLE_ID,
        BrewskiContract.StyleEntry.COLUMN_STYLE_NAME,
        BrewskiContract.StyleEntry.COLUMN_STYLE_SHORT_NAME,
        BrewskiContract.StyleEntry.COLUMN_STYLE_DESCRIPTION
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

        styleFilter = new IntentFilter("moreStylesLoaded");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_style_list_fragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_style_list_share);

        // Get the provider and hold onto it to set/change the share intent.
        mStylesShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mStyles != null) {
            mStylesShareActionProvider.setShareIntent(createShareStylesIntent());
        }
    }

    private Intent createShareStylesIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mStyles + STYLES_SHARE_HASHTAG);

        return shareIntent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                BrewskiApplication.setCurrentStyleId(cursor.getString(COL_STYLE_ID));
                if (cursor != null) {
                    ((Callback) getActivity()).onItemSelected(
                            BrewskiContract.StyleEntry.buildStyleUriWithStyleId(cursor.getString(COL_STYLE_ID))
                    );
                }

                mPosition = position;
            }
        });

//        mStyleListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem,
//                                 int visibleItemCount, int totalItemCount) {
//
//                int lastInScreen = firstVisibleItem + visibleItemCount;
//
//                if ((lastInScreen >= (totalItemCount - 25)) && !(loadingMore)) {
//                    loadingMore = true;
//                    syncStyle();
//                }
//
//                mPosition = lastInScreen + 5;
//            }
//        });

        loadingMore = true;
        syncStyle();

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

    private void syncStyle() {
        BrewskiSyncAdapter.syncImmediately(getActivity(), "style");
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

        loadingMore = false;

        mStyles = "Check out all these awesome styles of beer that I found on this cool new app, BREWSKI.";

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mStylesShareActionProvider != null) {
            mStylesShareActionProvider.setShareIntent(createShareStylesIntent());
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

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(styleReceiver, styleFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(styleReceiver);
    }
}

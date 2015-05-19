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
import android.widget.TextView;

import gunn.brewski.app.data.BrewskiContract.CategoryEntry;

/**
 * Created by SESA300553 on 4/7/2015.
 */
public class CategoryDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = CategoryDetailFragment.class.getSimpleName();
    static final String CATEGORY_DETAIL_URI = "CATEGORY_URI";

    private static final String CATEGORY_SHARE_HASHTAG = " #BrewskiCategory";

    private ShareActionProvider mCategoryShareActionProvider;
    private String mCategory;
    private Uri mCategoryUri;

    private static final int CATEGORY_DETAIL_LOADER = 0;

    private static final String[] CATEGORY_DETAIL_COLUMNS = {
        CategoryEntry.TABLE_NAME + "." + CategoryEntry._ID,
        CategoryEntry.COLUMN_CATEGORY_ID,
        CategoryEntry.COLUMN_CATEGORY_NAME,
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_CATEGORY_ID = 0;
    public static final int COL_CATEGORY_NAME = 1;

    private TextView mCategoryNameView;

    public CategoryDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mCategoryUri = arguments.getParcelable(CategoryDetailFragment.CATEGORY_DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_category_detail, container, false);
        mCategoryNameView = (TextView) rootView.findViewById(R.id.detail_category_name_textview);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_category_detail_fragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_category_share);

        // Get the provider and hold onto it to set/change the share intent.
        mCategoryShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mCategory != null) {
            mCategoryShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mCategory + CATEGORY_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CATEGORY_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onLocationChanged( String newLocation ) {
        // replace the uri, since the location has changed
        Uri uri = mCategoryUri;
        if (null != uri) {
//            long date = BrewskiContract.CategoryEntry.getDateFromUri(uri);
//            Uri updatedUri = BrewskiContract.CategoryEntry.buildWeatherLocationWithDate(newLocation, date);
//            mCategoryUri = updatedUri;
            getLoaderManager().restartLoader(CATEGORY_DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mCategoryUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mCategoryUri,
                    CATEGORY_DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            // Read description from cursor and update view
            String categoryName = data.getString(COL_CATEGORY_NAME);
            mCategoryNameView.setText(categoryName);

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mCategoryShareActionProvider != null) {
                mCategoryShareActionProvider.setShareIntent(createShareForecastIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
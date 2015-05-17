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
import android.widget.ImageView;
import android.widget.TextView;

import gunn.brewski.app.data.BrewskiContract;
import gunn.brewski.app.data.BrewskiContract.StyleEntry;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {link StyleDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {link StyleDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StyleDetailFragment extends android.support.v4.app.Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = StyleDetailFragment.class.getSimpleName();
    static final String STYLE_DETAIL_URI = "STYLE_URI";

    private static final String STYLE_SHARE_HASHTAG = " #BrewskiStyle";

    private ShareActionProvider mStyleShareActionProvider;
    private String mStyle;
    private Uri mStyleUri;

    private static final int STYLE_DETAIL_LOADER = 0;

    private static final String[] STYLE_DETAIL_COLUMNS = {
        StyleEntry.TABLE_NAME + "." + StyleEntry._ID,
        StyleEntry.COLUMN_STYLE_ID,
        StyleEntry.COLUMN_STYLE_NAME,
        StyleEntry.COLUMN_STYLE_SHORT_NAME,
        StyleEntry.COLUMN_STYLE_DESCRIPTION,
        StyleEntry.COLUMN_CATEGORY_ID
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_STYLE_ID = 1;
    public static final int COL_STYLE_NAME = 2;
    public static final int COL_STYLE_SHORT_NAME = 3;
    public static final int COL_STYLE_DESCRIPTION = 4;
    public static final int COL_CATEGORY_ID = 5;

    private TextView mStyleNameView;
//    private TextView mStyleShortNameView;
    private TextView mStyleDescriptionView;
//    private TextView mCategoryNameView;

    public StyleDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mStyleUri = arguments.getParcelable(StyleDetailFragment.STYLE_DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_style_detail, container, false);
        mStyleNameView = (TextView) rootView.findViewById(R.id.detail_style_name_textview);
//        mStyleShortNameView = (TextView) rootView.findViewById(R.id.detail_style_short_name_textview);
        mStyleDescriptionView = (TextView) rootView.findViewById(R.id.detail_style_description_textview);
//        mCategoryNameView = (TextView) rootView.findViewById(R.id.detail_style_category_name_textview);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_style_detail, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_style_share);

        // Get the provider and hold onto it to set/change the share intent.
        mStyleShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mStyle != null) {
            mStyleShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mStyle + STYLE_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(STYLE_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onLocationChanged( String newLocation ) {
        // replace the uri, since the location has changed
        Uri uri = mStyleUri;
        if (null != uri) {
//            long date = BrewskiContract.StyleEntry.getDateFromUri(uri);
//            Uri updatedUri = BrewskiContract.StyleEntry.buildWeatherLocationWithDate(newLocation, date);
//            mStyleUri = updatedUri;
            getLoaderManager().restartLoader(STYLE_DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mStyleUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mStyleUri,
                    STYLE_DETAIL_COLUMNS,
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
            String styleName = data.getString(COL_STYLE_NAME);
            mStyleNameView.setText(styleName);

//            // Read description from cursor and update view
//            String styleShortName = data.getString(COL_STYLE_SHORT_NAME);
//            mStyleShortNameView.setText(styleShortName);

            // Read description from cursor and update view
            String styleDescription = data.getString(COL_STYLE_DESCRIPTION);
            mStyleDescriptionView.setText(styleDescription);

//            // Read description from cursor and update view
//            String styleCategoryName = data.getString(COL_CATEGORY_ID);
//            mCategoryNameView.setText(styleCategoryName);

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mStyleShareActionProvider != null) {
                mStyleShareActionProvider.setShareIntent(createShareForecastIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}

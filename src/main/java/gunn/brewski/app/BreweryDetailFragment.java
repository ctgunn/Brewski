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

import com.nostra13.universalimageloader.core.ImageLoader;

import gunn.brewski.app.data.BrewskiContract.BreweryEntry;

/**
 * Created by SESA300553 on 4/7/2015.
 */
public class BreweryDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = BreweryDetailFragment.class.getSimpleName();
    static final String BREWERY_DETAIL_URI = "BREWERY_URI";

    private static final String BREWERY_SHARE_HASHTAG = " #BrewskiBrewery";

    private ShareActionProvider mBreweryShareActionProvider;
    private String mBrewery;
    private Uri mBreweryUri;

    private static final int BREWERY_DETAIL_LOADER = 0;

    private static final String[] BREWERY_DETAIL_COLUMNS = {
            BreweryEntry.TABLE_NAME + "." + BreweryEntry._ID,
            BreweryEntry.COLUMN_BREWERY_ID,
            BreweryEntry.COLUMN_BREWERY_NAME,
            BreweryEntry.COLUMN_BREWERY_DESCRIPTION,
            BreweryEntry.COLUMN_BREWERY_WEBSITE,
            BreweryEntry.COLUMN_ESTABLISHED,
            BreweryEntry.COLUMN_IMAGE_LARGE,
            BreweryEntry.COLUMN_IMAGE_MEDIUM,
            BreweryEntry.COLUMN_IMAGE_ICON
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_BREWERY_ID = 1;
    public static final int COL_BREWERY_NAME = 2;
    public static final int COL_BREWERY_DESCRIPTION = 3;
    public static final int COL_BREWERY_WEBSITE = 4;
    public static final int COL_ESTABLISHED = 5;
    public static final int COL_IMAGE_MEDIUM = 7;

    private ImageView mBreweryIconView;
    private TextView mBreweryNameView;
    private TextView mBreweryDescriptionView;
    private TextView mBreweryWebsiteView;
    private TextView mEstablishedView;

    public BreweryDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mBreweryUri = arguments.getParcelable(BreweryDetailFragment.BREWERY_DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_brewery_detail, container, false);
        mBreweryIconView = (ImageView) rootView.findViewById(R.id.detail_brewery_icon);
        mBreweryNameView = (TextView) rootView.findViewById(R.id.detail_brewery_name_textview);
        mBreweryDescriptionView = (TextView) rootView.findViewById(R.id.detail_brewery_description_textview);
        mBreweryWebsiteView = (TextView) rootView.findViewById(R.id.detail_brewery_website_textview);
        mEstablishedView = (TextView) rootView.findViewById(R.id.detail_established_textview);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_brewery_detail, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_brewery_share);

        // Get the provider and hold onto it to set/change the share intent.
        mBreweryShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mBrewery != null) {
            mBreweryShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mBrewery + BREWERY_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(BREWERY_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onBreweryChanged( String newLocation ) {
        // replace the uri, since the location has changed
        Uri uri = mBreweryUri;
        if (null != uri) {
//            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
//            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
//            mUri = updatedUri;
            getLoaderManager().restartLoader(BREWERY_DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mBreweryUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mBreweryUri,
                    BREWERY_DETAIL_COLUMNS,
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
            // Read weather condition ID from cursor
            int breweryId = data.getInt(COL_BREWERY_ID);

            // Use weather art image
            if (null != data.getString(COL_IMAGE_MEDIUM)) {
                ImageLoader.getInstance().displayImage(data.getString(COL_IMAGE_MEDIUM), mBreweryIconView);
            }
            else {
                mBreweryIconView.setImageResource(R.drawable.brewski_default);
            }

            // Read date from cursor and update views for day of week and date
            String breweryName = data.getString(COL_BREWERY_NAME);
            mBreweryNameView.setText(breweryName);

            // Read date from cursor and update views for day of week and date
            String breweryDescription = data.getString(COL_BREWERY_DESCRIPTION);
            mBreweryDescriptionView.setText(breweryDescription);

            // For accessibility, add a content description to the icon field
            mBreweryIconView.setContentDescription(breweryDescription);

            // Read date from cursor and update views for day of week and date
            String breweryWebsite = data.getString(COL_BREWERY_WEBSITE);
            mBreweryWebsiteView.setText(breweryWebsite);

            // Read date from cursor and update views for day of week and date
            String established = data.getString(COL_ESTABLISHED);
            mEstablishedView.setText(established);

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mBreweryShareActionProvider != null) {
                mBreweryShareActionProvider.setShareIntent(createShareForecastIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}

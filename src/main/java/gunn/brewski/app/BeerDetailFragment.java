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
import gunn.brewski.app.data.BrewskiContract.BeerEntry;

/**
 * Created by SESA300553 on 4/7/2015.
 */
public class BeerDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = BeerDetailFragment.class.getSimpleName();
    static final String BEER_DETAIL_URI = "BEER_URI";

    private static final String BEER_SHARE_HASHTAG = " #BrewskiBeer";

    private ShareActionProvider mBeerShareActionProvider;
    private String mBeer;
    private Uri mBeerUri;

    private static final int BEER_DETAIL_LOADER = 0;

    private static final String[] BEER_DETAIL_COLUMNS = {
        BeerEntry.TABLE_NAME + "." + BeerEntry._ID,
        BeerEntry.COLUMN_BEER_ID,
        BeerEntry.COLUMN_BEER_NAME,
        BeerEntry.COLUMN_BEER_DESCRIPTION,
        BeerEntry.COLUMN_BREWERY_ID,
        BeerEntry.COLUMN_CATEGORY_ID,
        BeerEntry.COLUMN_STYLE_ID,
        BeerEntry.COLUMN_LABEL_LARGE,
        BeerEntry.COLUMN_LABEL_MEDIUM,
        BeerEntry.COLUMN_LABEL_ICON,
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_BEER_ID = 1;
    public static final int COL_BEER_NAME = 2;
    public static final int COL_BEER_DESCRIPTION = 3;
    public static final int COL_BREWERY_ID = 4;
    public static final int COL_CATEGORY_ID = 5;
    public static final int COL_STYLE_ID = 6;
    public static final int COL_LABEL_LARGE = 7;
    public static final int COL_LABEL_MEDIUM = 8;
    public static final int COL_LABEL_ICON = 9;

    private ImageView mBeerLabelIconView;
    private TextView mBeerNameView;
    private TextView mBeerDescriptionView;
    private TextView mBreweryNameView;
    private TextView mCategoryNameView;
    private TextView mStyleNameView;

    public BeerDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mBeerUri = arguments.getParcelable(BeerDetailFragment.BEER_DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_beer_detail, container, false);
        mBeerLabelIconView = (ImageView) rootView.findViewById(R.id.detail_beer_icon);
        mBeerNameView = (TextView) rootView.findViewById(R.id.detail_beer_name_textview);
        mBeerDescriptionView = (TextView) rootView.findViewById(R.id.detail_beer_description_textview);
        mBreweryNameView = (TextView) rootView.findViewById(R.id.detail_brew_name_textview);
        mCategoryNameView = (TextView) rootView.findViewById(R.id.detail_cat_name_textview);
        mStyleNameView = (TextView) rootView.findViewById(R.id.detail_sty_name_textview);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_beer_detail, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_beer_share);

        // Get the provider and hold onto it to set/change the share intent.
        mBeerShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mBeer != null) {
            mBeerShareActionProvider.setShareIntent(createShareBeerIntent());
        }
    }

    private Intent createShareBeerIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mBeer + BEER_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(BEER_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onBeerChanged( String newLocation ) {
        // replace the uri, since the location has changed
        Uri uri = mBeerUri;
        if (null != uri) {
            // TODO: THIS IS WHERE I NEED TO PULL IN THE BREWERY, CATEGORY, AND STYLE NAMES.

//            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
//            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
//            mBeerUri = updatedUri;
            getLoaderManager().restartLoader(BEER_DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mBeerUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mBeerUri,
                    BEER_DETAIL_COLUMNS,
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
            int beerId = data.getInt(COL_BEER_ID);

            // Use weather art image
            //mBeerLabelIconView.setImageResource(Utility.getArtResourceForWeatherCondition(beerId));

            // Read description from cursor and update view
            String beerName = data.getString(COL_BEER_NAME);
            mBeerNameView.setText(beerName);

            // Read description from cursor and update view
            String beerDescription = data.getString(COL_BEER_DESCRIPTION);
            mBeerDescriptionView.setText(beerDescription);

            // For accessibility, add a content description to the icon field
            mBeerLabelIconView.setContentDescription(beerDescription);

            // Read description from cursor and update view
            String breweryName = data.getString(COL_BREWERY_ID);
            mBreweryNameView.setText(breweryName);

            // Read description from cursor and update view
            if(null != data.getString(COL_CATEGORY_ID)) {
                String categoryName = data.getString(COL_CATEGORY_ID);
                mCategoryNameView.setText(categoryName);
            }
            else {
                mCategoryNameView.setText("N/A");
            }

            // Read description from cursor and update view
            if(null != data.getString(COL_STYLE_ID)) {
                String styleName = data.getString(COL_STYLE_ID);
                mStyleNameView.setText(styleName);
            }
            else {
                mStyleNameView.setText("N/A");
            }

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mBeerShareActionProvider != null) {
                mBeerShareActionProvider.setShareIntent(createShareBeerIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}

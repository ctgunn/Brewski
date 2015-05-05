package gunn.brewski.app.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import gunn.brewski.app.BrewskiApplication;
import gunn.brewski.app.MainActivity;

public class BrewskiContentProvider extends ContentProvider {
    public final String LOG_TAG = BrewskiContentProvider.class.getSimpleName();

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private BrewskiDbHelper mOpenHelper;

//    static final int WEATHER = 100;
//    static final int WEATHER_WITH_LOCATION = 101;
//    static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
//    static final int LOCATION = 300;

    static final int BEER = 100;
    static final int INDIVIDUAL_BEER = 101;
    static final int BREWERY_OF_BEER = 102;
    static final int CATEGORY_OF_BEER = 103;
    static final int STYLE_OF_BEER = 104;
//    static final int INGREDIENTS_OF_BEER = 105;
    static final int BREWERY = 200;
    static final int INDIVIDUAL_BREWERY = 201;
    static final int BEERS_OF_BREWERY = 202;
    static final int LOCATIONS_OF_BREWERY = 203;
    static final int CATEGORY = 300;
    static final int INDIVIDUAL_CATEGORY = 301;
    static final int STYLES_OF_CATEGORY = 302;
    static final int BEERS_OF_CATEGORY = 303;
    static final int STYLE = 400;
    static final int INDIVIDUAL_STYLE = 401;
    static final int CATEGORY_OF_STYLE = 402;
    static final int BEERS_OF_STYLE = 403;

    private static final SQLiteQueryBuilder sBrewskiBeerQueryBuilder;
    private static final SQLiteQueryBuilder sBrewskiBreweryQueryBuilder;
    private static final SQLiteQueryBuilder sBrewskiCategoryQueryBuilder;
    private static final SQLiteQueryBuilder sBrewskiStyleQueryBuilder;

    static {
        sBrewskiBeerQueryBuilder = new SQLiteQueryBuilder();
        sBrewskiBreweryQueryBuilder = new SQLiteQueryBuilder();
        sBrewskiCategoryQueryBuilder = new SQLiteQueryBuilder();
        sBrewskiStyleQueryBuilder = new SQLiteQueryBuilder();

        sBrewskiBeerQueryBuilder.setTables(BrewskiContract.BeerEntry.TABLE_NAME);
        sBrewskiBreweryQueryBuilder.setTables(BrewskiContract.BreweryEntry.TABLE_NAME);
        sBrewskiCategoryQueryBuilder.setTables(BrewskiContract.CategoryEntry.TABLE_NAME);
        sBrewskiStyleQueryBuilder.setTables(BrewskiContract.StyleEntry.TABLE_NAME);
    }

    private static final String sIndividualBeer =
            BrewskiContract.BeerEntry.TABLE_NAME +
                " WHERE " + BrewskiContract.BeerEntry.TABLE_NAME + "." +
                BrewskiContract.BeerEntry.COLUMN_BEER_ID + "= ? ";

    private static final String sBreweryOfBeer =
            BrewskiContract.BreweryEntry.TABLE_NAME + " INNER JOIN " +
                BrewskiContract.BeerEntry.TABLE_NAME +
                " ON " + BrewskiContract.BreweryEntry.TABLE_NAME + "." +
                BrewskiContract.BreweryEntry.COLUMN_BREWERY_ID + " = " +
                BrewskiContract.BeerEntry.TABLE_NAME + "." +
                BrewskiContract.BeerEntry.COLUMN_BREWERY_ID +
                " WHERE " + BrewskiContract.BeerEntry.TABLE_NAME + "." +
                BrewskiContract.BeerEntry.COLUMN_BEER_ID + " = ?";

    private static final String sCategoryOfBeer =
            BrewskiContract.CategoryEntry.TABLE_NAME + " INNER JOIN " +
                BrewskiContract.StyleEntry.TABLE_NAME +
                " ON " + BrewskiContract.CategoryEntry.TABLE_NAME + "." +
                BrewskiContract.CategoryEntry.COLUMN_CATEGORY_ID + " = " +
                BrewskiContract.StyleEntry.TABLE_NAME + "." +
                BrewskiContract.StyleEntry.COLUMN_CATEGORY_ID + " INNER JOIN " +
                BrewskiContract.BeerEntry.TABLE_NAME +
                " ON " + BrewskiContract.StyleEntry.TABLE_NAME + "." +
                BrewskiContract.StyleEntry.COLUMN_STYLE_ID + " = " +
                BrewskiContract.BeerEntry.TABLE_NAME + "." +
                BrewskiContract.BeerEntry.COLUMN_STYLE_ID +
                " WHERE " + BrewskiContract.BeerEntry.TABLE_NAME + "." +
                BrewskiContract.BeerEntry.COLUMN_BEER_ID + " = ?";

    private static final String sStyleOfBeer =
            BrewskiContract.StyleEntry.TABLE_NAME + " INNER JOIN " +
                BrewskiContract.BeerEntry.TABLE_NAME +
                " ON " + BrewskiContract.StyleEntry.TABLE_NAME + "." +
                BrewskiContract.StyleEntry.COLUMN_STYLE_ID + " = " +
                BrewskiContract.BeerEntry.TABLE_NAME + "." +
                BrewskiContract.BeerEntry.COLUMN_STYLE_ID +
                " WHERE " + BrewskiContract.BeerEntry.TABLE_NAME + "." +
                BrewskiContract.BeerEntry.COLUMN_BEER_ID + " = ?";

    //TODO: CREATE BEER INGREDIENTS QUERY.

    private static final String sIndividualBrewery =
            BrewskiContract.BreweryEntry.TABLE_NAME +
                " WHERE " + BrewskiContract.BreweryEntry.TABLE_NAME + "." +
                BrewskiContract.BreweryEntry.COLUMN_BREWERY_ID + " + ?";

    private static final String sBeersOfBrewery =
            BrewskiContract.BeerEntry.TABLE_NAME + " INNER JOIN " +
                BrewskiContract.BreweryEntry.TABLE_NAME +
                " ON " + BrewskiContract.BeerEntry.TABLE_NAME + "." +
                BrewskiContract.BeerEntry.COLUMN_BREWERY_ID + " = " +
                BrewskiContract.BreweryEntry.TABLE_NAME + "." +
                BrewskiContract.BreweryEntry.COLUMN_BREWERY_ID +
                " WHERE " + BrewskiContract.BreweryEntry.TABLE_NAME + "." +
                BrewskiContract.BreweryEntry.COLUMN_BREWERY_ID + " = ?";

    // TODO: CREATE BREWERY LOCATIONS QUERY.

    private static final String sIndividualCategory =
            BrewskiContract.CategoryEntry.TABLE_NAME +
                " WHERE " + BrewskiContract.CategoryEntry.TABLE_NAME + "." +
                BrewskiContract.CategoryEntry.COLUMN_CATEGORY_ID + " = ?";

    private static final String sStylesOfCategory =
            BrewskiContract.StyleEntry.TABLE_NAME + " INNER JOIN " +
                BrewskiContract.CategoryEntry.TABLE_NAME +
                " ON " + BrewskiContract.StyleEntry.TABLE_NAME + "." +
                BrewskiContract.StyleEntry.COLUMN_CATEGORY_ID + " = " +
                BrewskiContract.CategoryEntry.TABLE_NAME + "." +
                BrewskiContract.CategoryEntry.COLUMN_CATEGORY_ID +
                " WHERE " + BrewskiContract.CategoryEntry.TABLE_NAME + "." +
                BrewskiContract.CategoryEntry.COLUMN_CATEGORY_ID + " = ?";

    private static final String sBeersOfCategory =
            BrewskiContract.BeerEntry.TABLE_NAME + " INNER JOIN " +
                BrewskiContract.StyleEntry.TABLE_NAME +
                " ON " + BrewskiContract.BeerEntry.TABLE_NAME + "." +
                BrewskiContract.BeerEntry.COLUMN_STYLE_ID + " = " +
                BrewskiContract.StyleEntry.TABLE_NAME + "." +
                BrewskiContract.StyleEntry.COLUMN_STYLE_ID + " INNER JOIN " +
                BrewskiContract.CategoryEntry.TABLE_NAME +
                " ON " + BrewskiContract.StyleEntry.TABLE_NAME + "." +
                BrewskiContract.StyleEntry.COLUMN_CATEGORY_ID + " = " +
                BrewskiContract.CategoryEntry.TABLE_NAME + "." +
                BrewskiContract.CategoryEntry.COLUMN_CATEGORY_ID +
                " WHERE " + BrewskiContract.CategoryEntry.TABLE_NAME + "." +
                BrewskiContract.CategoryEntry.COLUMN_CATEGORY_ID + " = ?";

    private static final String sIndividualStyle =
            BrewskiContract.StyleEntry.TABLE_NAME +
                " WHERE " + BrewskiContract.StyleEntry.TABLE_NAME + "." +
                BrewskiContract.StyleEntry.COLUMN_STYLE_ID + " = ?";

    private static final String sCategoryOfStyle =
            BrewskiContract.CategoryEntry.TABLE_NAME + " INNER JOIN " +
                BrewskiContract.StyleEntry.TABLE_NAME +
                " ON " + BrewskiContract.CategoryEntry.TABLE_NAME + "." +
                BrewskiContract.CategoryEntry.COLUMN_CATEGORY_ID + " = " +
                BrewskiContract.StyleEntry.TABLE_NAME + "." +
                BrewskiContract.StyleEntry.COLUMN_CATEGORY_ID +
                " WHERE " + BrewskiContract.StyleEntry.TABLE_NAME + "." +
                BrewskiContract.StyleEntry.COLUMN_STYLE_ID;

    private static final String sBeersOfStyle =
            BrewskiContract.BeerEntry.TABLE_NAME + " INNER JOIN " +
                BrewskiContract.StyleEntry.TABLE_NAME +
                " ON " + BrewskiContract.BeerEntry.TABLE_NAME + "." +
                BrewskiContract.BeerEntry.COLUMN_STYLE_ID + " = " +
                BrewskiContract.StyleEntry.TABLE_NAME + " = " +
                BrewskiContract.StyleEntry.COLUMN_STYLE_ID +
                " WHERE " + BrewskiContract.StyleEntry.TABLE_NAME + "." +
                BrewskiContract.StyleEntry.COLUMN_STYLE_ID + " = ?";

    private Cursor getIndividualBeer(Uri uri, String[] projection, String sortOrder) {
        String beerId = BrewskiApplication.getCurrentBeerId();

        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{beerId};
        selection = sIndividualBeer;

        return sBrewskiBeerQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getBreweryByBeer(Uri uri, String[] projection, String sortOrder) {
        String beerId = BrewskiApplication.getCurrentBeerId();

        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{beerId};
        selection = sBreweryOfBeer;

        return sBrewskiBreweryQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getStyleByBeer(Uri uri, String[] projection, String sortOrder) {
        String beerId = BrewskiApplication.getCurrentBeerId();

        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{beerId};
        selection = sStyleOfBeer;

        return sBrewskiStyleQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getCategoryByBeer(Uri uri, String[] projection, String sortOrder) {
        String beerId = BrewskiApplication.getCurrentBeerId();

        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{beerId};
        selection = sCategoryOfBeer;

        return sBrewskiCategoryQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getIndividualBrewery(Uri uri, String[] projection, String sortOrder) {
        String breweryId = BrewskiApplication.getCurrentBreweryId();

        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{breweryId};
        selection = sIndividualBrewery;

        return sBrewskiBreweryQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getBeersByBrewery(Uri uri, String[] projection, String sortOrder) {
        String breweryId = BrewskiApplication.getCurrentBreweryId();

        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{breweryId};
        selection = sBeersOfBrewery;

        return sBrewskiBeerQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getIndividualCategory(Uri uri, String[] projection, String sortOrder) {
        String categoryId = BrewskiApplication.getCurrentCategoryId();

        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{categoryId};
        selection = sIndividualCategory;

        return sBrewskiCategoryQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getStylesByCategory(Uri uri, String[] projection, String sortOrder) {
        String categoryId = BrewskiApplication.getCurrentCategoryId();

        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{categoryId};
        selection = sStylesOfCategory;

        return sBrewskiStyleQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getBeersByCategory(Uri uri, String[] projection, String sortOrder) {
        String categoryId = BrewskiApplication.getCurrentCategoryId();

        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{categoryId};
        selection = sBeersOfCategory;

        return sBrewskiBeerQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getIndividualStyle(Uri uri, String[] projection, String sortOrder) {
        String styleId = BrewskiApplication.getCurrentStyleId();

        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{styleId};
        selection = sIndividualStyle;

        return sBrewskiStyleQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getCategoryByStyle(Uri uri, String[] projection, String sortOrder) {
        String styleId = BrewskiApplication.getCurrentStyleId();

        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{styleId};
        selection = sCategoryOfStyle;

        return sBrewskiCategoryQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getBeersByStyle(Uri uri, String[] projection, String sortOrder) {
        String styleId = BrewskiApplication.getCurrentStyleId();

        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{styleId};
        selection = sBeersOfStyle;

        return sBrewskiBeerQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the WEATHER, WEATHER_WITH_LOCATION, WEATHER_WITH_LOCATION_AND_DATE,
        and LOCATION integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = BrewskiContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, BrewskiContract.PATH_BEER, BEER);
        matcher.addURI(authority, BrewskiContract.PATH_BEER + "/*", INDIVIDUAL_BEER);
        matcher.addURI(authority, BrewskiContract.PATH_BEER + "/*", BREWERY_OF_BEER);
        matcher.addURI(authority, BrewskiContract.PATH_BEER + "/*", CATEGORY_OF_BEER);
        matcher.addURI(authority, BrewskiContract.PATH_BEER + "/*", STYLE_OF_BEER);
//        matcher.addURI(authority, BrewskiContract.PATH_BEER + "/*", INGREDIENTS_OF_BEER);

        matcher.addURI(authority, BrewskiContract.PATH_BREWERY, BREWERY);
        matcher.addURI(authority, BrewskiContract.PATH_BREWERY + "/*", INDIVIDUAL_BREWERY);
        matcher.addURI(authority, BrewskiContract.PATH_BREWERY + "/*", BEERS_OF_BREWERY);
        matcher.addURI(authority, BrewskiContract.PATH_BREWERY + "/*", LOCATIONS_OF_BREWERY);

        matcher.addURI(authority, BrewskiContract.PATH_CATEGORY, CATEGORY);
        matcher.addURI(authority, BrewskiContract.PATH_CATEGORY + "/*", INDIVIDUAL_CATEGORY);
        matcher.addURI(authority, BrewskiContract.PATH_CATEGORY + "/*", STYLES_OF_CATEGORY);
        matcher.addURI(authority, BrewskiContract.PATH_CATEGORY + "/*", BEERS_OF_CATEGORY);

        matcher.addURI(authority, BrewskiContract.PATH_STYLE, STYLE);
        matcher.addURI(authority, BrewskiContract.PATH_STYLE + "/*", INDIVIDUAL_STYLE);
        matcher.addURI(authority, BrewskiContract.PATH_STYLE + "/*", CATEGORY_OF_STYLE);
        matcher.addURI(authority, BrewskiContract.PATH_STYLE + "/*", BEERS_OF_STYLE);

        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new WeatherDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new BrewskiDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.

     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case BEER:
                return BrewskiContract.BeerEntry.BEER_CONTENT_TYPE;
            case INDIVIDUAL_BEER:
                return BrewskiContract.BeerEntry.BEER_CONTENT_ITEM_TYPE;
            case BREWERY_OF_BEER:
                return BrewskiContract.BreweryEntry.BREWERY_CONTENT_ITEM_TYPE;
            case CATEGORY_OF_BEER:
                return BrewskiContract.CategoryEntry.CATEGORY_CONTENT_ITEM_TYPE;
            case STYLE_OF_BEER:
                return BrewskiContract.StyleEntry.STYLE_CONTENT_ITEM_TYPE;
//            case INGREDIENTS_OF_BEER:
//                return BrewskiContract.BeerEntry.BEER_CONTENT_TYPE;
            case BREWERY:
                return BrewskiContract.BreweryEntry.BREWERY_CONTENT_TYPE;
            case INDIVIDUAL_BREWERY:
                return BrewskiContract.BreweryEntry.BREWERY_CONTENT_ITEM_TYPE;
            case BEERS_OF_BREWERY:
                return BrewskiContract.BeerEntry.BEER_CONTENT_TYPE;
//            case LOCATIONS_OF_BREWERY:
//                return BrewskiContract.BreweryEntry.BREWERY_CONTENT_TYPE;
            case CATEGORY:
                return BrewskiContract.CategoryEntry.CATEGORY_CONTENT_TYPE;
            case INDIVIDUAL_CATEGORY:
                return BrewskiContract.CategoryEntry.CATEGORY_CONTENT_ITEM_TYPE;
            case STYLES_OF_CATEGORY:
                return BrewskiContract.StyleEntry.STYLE_CONTENT_TYPE;
            case BEERS_OF_CATEGORY:
                return BrewskiContract.BeerEntry.BEER_CONTENT_TYPE;
            case STYLE:
                return BrewskiContract.StyleEntry.STYLE_CONTENT_TYPE;
            case INDIVIDUAL_STYLE:
                return BrewskiContract.StyleEntry.STYLE_CONTENT_ITEM_TYPE;
            case CATEGORY_OF_STYLE:
                return BrewskiContract.CategoryEntry.CATEGORY_CONTENT_ITEM_TYPE;
            case BEERS_OF_STYLE:
                return BrewskiContract.BeerEntry.BEER_CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "beer"
            case BEER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        BrewskiContract.BeerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "weather/*/*"
            case INDIVIDUAL_BEER: {
                retCursor = getIndividualBeer(uri, projection, sortOrder);
                break;
            }
            // "weather/*"
            case BREWERY_OF_BEER: {
                retCursor = getCategoryByBeer(uri, projection, sortOrder);
                break;
            }
            // "weather/*"
            case CATEGORY_OF_BEER: {
                retCursor = getCategoryByBeer(uri, projection, sortOrder);
                break;
            }
            // "weather/*"
            case STYLE_OF_BEER: {
                retCursor = getStyleByBeer(uri, projection, sortOrder);
                break;
            }
            // "brewery"
            case BREWERY: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        BrewskiContract.BreweryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "weather/*"
            case INDIVIDUAL_BREWERY: {
                retCursor = getIndividualBrewery(uri, projection, sortOrder);
                break;
            }
            // "weather/*"
            case BEERS_OF_BREWERY: {
                retCursor = getBeersByBrewery(uri, projection, sortOrder);
                break;
            }
            // "category"
            case CATEGORY: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        BrewskiContract.CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "weather/*"
            case INDIVIDUAL_CATEGORY: {
                retCursor = getIndividualCategory(uri, projection, sortOrder);
                break;
            }
            // "weather/*"
            case STYLES_OF_CATEGORY: {
                retCursor = getStylesByCategory(uri, projection, sortOrder);
                break;
            }
            // "weather/*"
            case BEERS_OF_CATEGORY: {
                retCursor = getBeersByCategory(uri, projection, sortOrder);
                break;
            }
            // "style"
            case STYLE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        BrewskiContract.StyleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "weather/*"
            case INDIVIDUAL_STYLE: {
                retCursor = getIndividualStyle(uri, projection, sortOrder);
                break;
            }
            // "weather/*"
            case CATEGORY_OF_STYLE: {
                retCursor = getCategoryByStyle(uri, projection, sortOrder);
                break;
            }
            // "weather/*"
            case BEERS_OF_STYLE: {
                retCursor = getBeersByStyle(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case BEER: {
                long _id = db.insert(BrewskiContract.BeerEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = BrewskiContract.BeerEntry.buildBeerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case BREWERY: {
                long _id = db.insert(BrewskiContract.BreweryEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = BrewskiContract.BreweryEntry.buildBreweryUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CATEGORY: {
                long _id = db.insert(BrewskiContract.CategoryEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = BrewskiContract.CategoryEntry.buildCategoryUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case STYLE: {
                long _id = db.insert(BrewskiContract.StyleEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = BrewskiContract.StyleEntry.buildStyleUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case BEER:
                rowsDeleted = db.delete(
                        BrewskiContract.BeerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BREWERY:
                rowsDeleted = db.delete(
                        BrewskiContract.BreweryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CATEGORY:
                rowsDeleted = db.delete(
                        BrewskiContract.CategoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STYLE:
                rowsDeleted = db.delete(
                        BrewskiContract.StyleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case BEER:
                rowsUpdated = db.update(BrewskiContract.BeerEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case BREWERY:
                rowsUpdated = db.update(BrewskiContract.BreweryEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case CATEGORY:
                rowsUpdated = db.update(BrewskiContract.CategoryEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case STYLE:
                rowsUpdated = db.update(BrewskiContract.StyleEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;

        switch (match) {
            case BEER:
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(BrewskiContract.BeerEntry.TABLE_NAME, null, value);

                        if (_id != -1) {
                            returnCount++;
                        }
                    }

                    db.setTransactionSuccessful();
                }
                catch(SQLiteConstraintException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                finally {
                    db.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri, null);

                return returnCount;
            case BREWERY:
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(BrewskiContract.BreweryEntry.TABLE_NAME, null, value);

                        if (_id != -1) {
                            returnCount++;
                        }
                    }

                    db.setTransactionSuccessful();
                }
                catch(SQLiteConstraintException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                finally {
                    db.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri, null);

                return returnCount;
            case CATEGORY:
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(BrewskiContract.CategoryEntry.TABLE_NAME, null, value);

                        if (_id != -1) {
                            returnCount++;
                        }
                    }

                    db.setTransactionSuccessful();
                }
                catch(SQLiteConstraintException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                finally {
                    db.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri, null);

                return returnCount;
            case STYLE:
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(BrewskiContract.StyleEntry.TABLE_NAME, null, value);

                        if (_id != -1) {
                            returnCount++;
                        }
                    }

                    db.setTransactionSuccessful();
                }
                catch(SQLiteConstraintException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                finally {
                    db.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri, null);

                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(16)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
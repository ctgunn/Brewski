package gunn.brewski.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import gunn.brewski.app.data.BrewskiContract.ProfileEntry;
import gunn.brewski.app.data.BrewskiContract.CategoryEntry;
import gunn.brewski.app.data.BrewskiContract.BeerEntry;
import gunn.brewski.app.data.BrewskiContract.BreweryEntry;
import gunn.brewski.app.data.BrewskiContract.StyleEntry;
import gunn.brewski.app.data.BrewskiContract.XAnalysisEntry;

/**
 * Created by SESA300553 on 4/2/2015.
 */
public class BrewskiDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "weather.db";

    public BrewskiDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_PROFILE_TABLE = "CREATE TABLE " + ProfileEntry.TABLE_NAME + " (" +
                ProfileEntry._ID + " INTEGER PRIMARY KEY," +
                ProfileEntry.COLUMN_FIRST_NAME + " TEXT NOT NULL, " +
                ProfileEntry.COLUMN_LAST_NAME + " TEXT NOT NULL, " +
                ProfileEntry.COLUMN_ADDRESS_LINE_1 + " TEXT NOT NULL, " +
                ProfileEntry.COLUMN_ADDRESS_LINE_2 + " TEXT NOT NULL, " +
                ProfileEntry.COLUMN_ADDRESS_CITY + " TEXT NOT NULL, " +
                ProfileEntry.COLUMN_ADDRESS_STATE + " TEXT NOT NULL, " +
                ProfileEntry.COLUMN_ADDRESS_POSTAL_CODE + " TEXT NOT NULL, " +
                " );";

        final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" +
                CategoryEntry._ID + " INTEGER PRIMARY KEY," +
                CategoryEntry.COLUMN_CATEGORY_ID + " TEXT NOT NULL, " +
                CategoryEntry.COLUMN_CATEGORY_NAME + " TEXT NOT NULL, " +
                " );";

        final String SQL_CREATE_BEER_TABLE = "CREATE TABLE " + BeerEntry.TABLE_NAME + " (" +
                BeerEntry._ID + " INTEGER PRIMARY KEY," +
                BeerEntry.COLUMN_BEER_ID + " TEXT NOT NULL, " +
                BeerEntry.COLUMN_BEER_NAME + " TEXT NOT NULL, " +
                BeerEntry.COLUMN_BEER_DESCRIPTION + " TEXT NOT NULL, " +
                BeerEntry.COLUMN_BREWERY_ID + " TEXT NOT NULL, " +
                BeerEntry.COLUMN_CATEGORY_ID + " TEXT NOT NULL, " +
                BeerEntry.COLUMN_STYLE_ID + " TEXT NOT NULL, " +
                BeerEntry.COLUMN_LABEL_LARGE + " TEXT NOT NULL, " +
                BeerEntry.COLUMN_LABEL_MEDIUM + " TEXT NOT NULL, " +
                BeerEntry.COLUMN_LABEL_ICON + " TEXT NOT NULL, " +
                " );";

        final String SQL_CREATE_BREWERY_TABLE = "CREATE TABLE " + BreweryEntry.TABLE_NAME + " (" +
                BreweryEntry._ID + " INTEGER PRIMARY KEY," +
                BreweryEntry.COLUMN_BREWERY_ID + " TEXT NOT NULL, " +
                BreweryEntry.COLUMN_BREWERY_NAME + " TEXT NOT NULL, " +
                BreweryEntry.COLUMN_BREWERY_DESCRIPTION + " TEXT NOT NULL, " +
                BreweryEntry.COLUMN_BREWERY_WEBSITE + " TEXT NOT NULL, " +
                BreweryEntry.COLUMN_ESTABLISHED + " TEXT NOT NULL, " +
                BreweryEntry.COLUMN_IMAGE_LARGE + " TEXT NOT NULL, " +
                BreweryEntry.COLUMN_IMAGE_MEDIUM + " TEXT NOT NULL, " +
                BreweryEntry.COLUMN_IMAGE_ICON + " TEXT NOT NULL, " +
                " );";

        final String SQL_CREATE_STYLE_TABLE = "CREATE TABLE " + StyleEntry.TABLE_NAME + " (" +
                StyleEntry._ID + " INTEGER PRIMARY KEY," +
                StyleEntry.COLUMN_STYLE_ID + " TEXT NOT NULL, " +
                StyleEntry.COLUMN_STYLE_NAME + " TEXT NOT NULL, " +
                StyleEntry.COLUMN_STYLE_SHORT_NAME + " TEXT NOT NULL, " +
                StyleEntry.COLUMN_STYLE_DESCRIPTION + " TEXT NOT NULL, " +
                StyleEntry.COLUMN_CATEGORY_ID + " TEXT NOT NULL, " +
                " );";
        // TODO: CREATE INGREDIENTS TABLE

        // TODO: CREATE LOCATIONS TABLE

        final String SQL_CREATE_X_ANALYSIS_TABLE = "CREATE TABLE " + XAnalysisEntry.TABLE_NAME + " (" +
                XAnalysisEntry._ID + " INTEGER PRIMARY KEY," +
                XAnalysisEntry.COLUMN_USER_ID + " TEXT NOT NULL, " +
                XAnalysisEntry.COLUMN_CATEGORY_ID + " TEXT NOT NULL, " +
                XAnalysisEntry.COLUMN_BEER_ID + " TEXT NOT NULL, " +
                XAnalysisEntry.COLUMN_BREWERY_ID + " TEXT NOT NULL, " +
                XAnalysisEntry.COLUMN_STYLE_ID + " TEXT NOT NULL, " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_PROFILE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CATEGORY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BEER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BREWERY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_STYLE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_X_ANALYSIS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProfileEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BeerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BreweryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + StyleEntry.TABLE_NAME);
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + XAnalysisEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

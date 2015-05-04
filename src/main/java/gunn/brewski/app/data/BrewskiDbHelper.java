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

    static final String DATABASE_NAME = "brewski.db";

    public BrewskiDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_PROFILE_TABLE = "CREATE TABLE " + ProfileEntry.TABLE_NAME + " (" +
                ProfileEntry._ID + " INTEGER PRIMARY KEY," +
                ProfileEntry.COLUMN_FIRST_NAME + " TEXT, " +
                ProfileEntry.COLUMN_LAST_NAME + " TEXT, " +
                ProfileEntry.COLUMN_ADDRESS_LINE_1 + " TEXT, " +
                ProfileEntry.COLUMN_ADDRESS_LINE_2 + " TEXT, " +
                ProfileEntry.COLUMN_ADDRESS_CITY + " TEXT, " +
                ProfileEntry.COLUMN_ADDRESS_STATE + " TEXT, " +
                ProfileEntry.COLUMN_ADDRESS_POSTAL_CODE + " TEXT " +
                " );";

        final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" +
                CategoryEntry._ID + " INTEGER PRIMARY KEY," +
                CategoryEntry.COLUMN_CATEGORY_ID + " TEXT UNIQUE, " +
                CategoryEntry.COLUMN_CATEGORY_NAME + " TEXT " +
                " );";

        final String SQL_CREATE_BEER_TABLE = "CREATE TABLE " + BeerEntry.TABLE_NAME + " (" +
                BeerEntry._ID + " INTEGER PRIMARY KEY," +
                BeerEntry.COLUMN_BEER_ID + " TEXT UNIQUE, " +
                BeerEntry.COLUMN_BEER_NAME + " TEXT, " +
                BeerEntry.COLUMN_BEER_DESCRIPTION + " TEXT, " +
                BeerEntry.COLUMN_BREWERY_ID + " TEXT, " +
                BeerEntry.COLUMN_CATEGORY_ID + " TEXT, " +
                BeerEntry.COLUMN_STYLE_ID + " TEXT, " +
                BeerEntry.COLUMN_LABEL_LARGE + " TEXT, " +
                BeerEntry.COLUMN_LABEL_MEDIUM + " TEXT, " +
                BeerEntry.COLUMN_LABEL_ICON + " TEXT " +
                " );";

        final String SQL_CREATE_BREWERY_TABLE = "CREATE TABLE " + BreweryEntry.TABLE_NAME + " (" +
                BreweryEntry._ID + " INTEGER PRIMARY KEY," +
                BreweryEntry.COLUMN_BREWERY_ID + " TEXT UNIQUE, " +
                BreweryEntry.COLUMN_BREWERY_NAME + " TEXT, " +
                BreweryEntry.COLUMN_BREWERY_DESCRIPTION + " TEXT, " +
                BreweryEntry.COLUMN_BREWERY_WEBSITE + " TEXT, " +
                BreweryEntry.COLUMN_ESTABLISHED + " TEXT, " +
                BreweryEntry.COLUMN_IMAGE_LARGE + " TEXT, " +
                BreweryEntry.COLUMN_IMAGE_MEDIUM + " TEXT, " +
                BreweryEntry.COLUMN_IMAGE_ICON + " TEXT " +
                " );";

        final String SQL_CREATE_STYLE_TABLE = "CREATE TABLE " + StyleEntry.TABLE_NAME + " (" +
                StyleEntry._ID + " INTEGER PRIMARY KEY," +
                StyleEntry.COLUMN_STYLE_ID + " TEXT UNIQUE, " +
                StyleEntry.COLUMN_STYLE_NAME + " TEXT, " +
                StyleEntry.COLUMN_STYLE_SHORT_NAME + " TEXT, " +
                StyleEntry.COLUMN_STYLE_DESCRIPTION + " TEXT, " +
                StyleEntry.COLUMN_CATEGORY_ID + " TEXT " +
                " );";
        // TODO: CREATE INGREDIENTS TABLE

        // TODO: CREATE LOCATIONS TABLE

        final String SQL_CREATE_X_ANALYSIS_TABLE = "CREATE TABLE " + XAnalysisEntry.TABLE_NAME + " (" +
                XAnalysisEntry._ID + " INTEGER PRIMARY KEY," +
                XAnalysisEntry.COLUMN_USER_ID + " TEXT NOT NULL, " +
                XAnalysisEntry.COLUMN_CATEGORY_ID + " TEXT, " +
                XAnalysisEntry.COLUMN_BEER_ID + " TEXT, " +
                XAnalysisEntry.COLUMN_BREWERY_ID + " TEXT, " +
                XAnalysisEntry.COLUMN_STYLE_ID + " TEXT " +
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

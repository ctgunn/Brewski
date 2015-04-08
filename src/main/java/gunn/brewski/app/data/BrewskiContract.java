package gunn.brewski.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by SESA300553 on 4/2/2015.
 */
public class BrewskiContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.android.sunshine.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.

//    ================================================================================
//    ================================================================================

//    public static final String PATH_WEATHER = "weather";
//    public static final String PATH_LOCATION = "location";

//    ================================================================================
//    ================================================================================

    public static final String PATH_PROFILE = "profile";
    public static final String PATH_CATEGORY = "category";
    public static final String PATH_BEER = "beer";
    public static final String PATH_BREWERY = "brewery";
    public static final String PATH_STYLE = "style";
    public static final String PATH_X_ANALYSIS = "x_analysis";

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static final class ProfileEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PROFILE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PROFILE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PROFILE;

        // Table name
        public static final String TABLE_NAME = "profile";

        // Columns for the user's first and last name.
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "last_name";

        // This will hold the user's address which will be used by default for finding breweries near by.
        public static final String COLUMN_ADDRESS_LINE_1 = "address_line_1";
        public static final String COLUMN_ADDRESS_LINE_2 = "address_line_2";
        public static final String COLUMN_ADDRESS_CITY = "address_city";
        public static final String COLUMN_ADDRESS_STATE = "address_state";
        public static final String COLUMN_ADDRESS_POSTAL_CODE = "address_postal_code";

        public static Uri buildProfileUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class CategoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;

        // Table name
        public static final String TABLE_NAME = "category";

        // Foreign Category ID that is received from BreweryDB.
        public static final String COLUMN_CATEGORY_ID = "category_id";
        // Category name that is received from BreweryDB.
        public static final String COLUMN_CATEGORY_NAME = "category_name";

        public static Uri buildCategoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class BeerEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BEER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BEER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BEER;

        // Table name
        public static final String TABLE_NAME = "beer";

        // Foreign Beer ID that is received from BreweryDB.
        public static final String COLUMN_BEER_ID = "beer_id";
        // Beer name that is received from BreweryDB.
        public static final String COLUMN_BEER_NAME = "beer_name";
        // Beer description that is received from BreweryDB.
        public static final String COLUMN_BEER_DESCRIPTION = "beer_description";
        // Style ID that is received from BreweryDB.
        public static final String COLUMN_STYLE_ID = "style_id";
        // URL that links to the large label image for the corresponding beer,
        // which is received from BreweryDB.
        public static final String COLUMN_LABEL_LARGE = "label_large";
        // URL that links to the medium label image for the corresponding beer,
        // which is received from BreweryDB.
        public static final String COLUMN_LABEL_MEDIUM = "label_medium";
        // URL that links to the icon label image for the corresponding beer,
        // which is received from BreweryDB.
        public static final String COLUMN_LABEL_ICON = "label_icon";



        public static Uri buildBeerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class BreweryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BREWERY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BREWERY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BREWERY;

        // Table name
        public static final String TABLE_NAME = "brewery";

        // Foreign Brewery ID that is received from BreweryDB.
        public static final String COLUMN_BREWERY_ID = "brewery_id";
        // Brewery name that is received from BreweryDB.
        public static final String COLUMN_BREWERY_NAME = "brewery_name";
        // Brewery description that is received from BreweryDB.
        public static final String COLUMN_BREWERY_DESCRIPTION = "brewery_description";
        // The year that the brewery was established according to BreweryDB.
        public static final String COLUMN_ESTABLISHED = "established";
        // URL that links to the large image for the corresponding brewery,
        // which is received from BreweryDB.
        public static final String COLUMN_IMAGE_LARGE = "image_large";
        // URL that links to the medium image for the corresponding brewery,
        // which is received from BreweryDB.
        public static final String COLUMN_IMAGE_MEDIUM = "image_medium";
        // URL that links to the icon image for the corresponding brewery,
        // which is received from BreweryDB.
        public static final String COLUMN_IMAGE_ICON = "image_icon";

        public static Uri buildBreweryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class StyleEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STYLE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STYLE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STYLE;

        // Table name
        public static final String TABLE_NAME = "style";

        // Foreign Style ID that is received from BreweryDB.
        public static final String COLUMN_STYLE_ID = "style_id";
        // Style name that is received from BreweryDB.
        public static final String COLUMN_STYLE_NAME = "style_name";
        // Style description that is received from BreweryDB.
        public static final String COLUMN_STYLE_DESCRIPTION = "style_description";
        // Foreign Category ID that is received from BreweryDB, and that they
        // style is associated with.
        public static final String COLUMN_CATEGORY_ID = "category_id";

        public static Uri buildStyleUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class XAnalysisEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_X_ANALYSIS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_X_ANALYSIS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_X_ANALYSIS;

        // Table name
        public static final String TABLE_NAME = "x_analysis";

        // User ID that is created as the primary key id in the profile table.
        public static final String COLUMN_USER_ID = "user_id";
        // Foreign Category ID that is received from BreweryDB.
        public static final String COLUMN_CATEGORY_ID = "category_id";
        // Foreign Beer ID that is received from BreweryDB.
        public static final String COLUMN_BEER_ID = "beer_id";
        // Foreign Brewery ID that is received from BreweryDB.
        public static final String COLUMN_BREWERY_ID = "brewery_id";
        // Foreign Style ID that is received from BreweryDB.
        public static final String COLUMN_STYLE_ID = "style_id";


        public static Uri buildXAnalysisUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

//    ================================================================================
//    ================================================================================

//    /* Inner class that defines the table contents of the location table */
//    public static final class LocationEntry implements BaseColumns {
//
//        public static final Uri CONTENT_URI =
//                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
//
//        public static final String CONTENT_TYPE =
//                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
//        public static final String CONTENT_ITEM_TYPE =
//                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
//
//        // Table name
//        public static final String TABLE_NAME = "location";
//
//        // The location setting string is what will be sent to openweathermap
//        // as the location query.
//        public static final String COLUMN_LOCATION_SETTING = "location_setting";
//
//        // Human readable location string, provided by the API.  Because for styling,
//        // "Mountain View" is more recognizable than 94043.
//        public static final String COLUMN_CITY_NAME = "city_name";
//
//        // In order to uniquely pinpoint the location on the map when we launch the
//        // map intent, we store the latitude and longitude as returned by openweathermap.
//        public static final String COLUMN_COORD_LAT = "coord_lat";
//        public static final String COLUMN_COORD_LONG = "coord_long";
//
//        public static Uri buildLocationUri(long id) {
//            return ContentUris.withAppendedId(CONTENT_URI, id);
//        }
//    }
//
//    /* Inner class that defines the table contents of the weather table */
//    public static final class WeatherEntry implements BaseColumns {
//
//        public static final Uri CONTENT_URI =
//                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();
//
//        public static final String CONTENT_TYPE =
//                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
//        public static final String CONTENT_ITEM_TYPE =
//                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
//
//        public static final String TABLE_NAME = "weather";
//
//        // Column with the foreign key into the location table.
//        public static final String COLUMN_LOC_KEY = "location_id";
//        // Date, stored as long in milliseconds since the epoch
//        public static final String COLUMN_DATE = "date";
//        // Weather id as returned by API, to identify the icon to be used
//        public static final String COLUMN_WEATHER_ID = "weather_id";
//
//        // Short description and long description of the weather, as provided by API.
//        // e.g "clear" vs "sky is clear".
//        public static final String COLUMN_SHORT_DESC = "short_desc";
//
//        // Min and max temperatures for the day (stored as floats)
//        public static final String COLUMN_MIN_TEMP = "min";
//        public static final String COLUMN_MAX_TEMP = "max";
//
//        // Humidity is stored as a float representing percentage
//        public static final String COLUMN_HUMIDITY = "humidity";
//
//        // Humidity is stored as a float representing percentage
//        public static final String COLUMN_PRESSURE = "pressure";
//
//        // Windspeed is stored as a float representing windspeed  mph
//        public static final String COLUMN_WIND_SPEED = "wind";
//
//        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south).  Stored as floats.
//        public static final String COLUMN_DEGREES = "degrees";
//
//        public static Uri buildWeatherUri(long id) {
//            return ContentUris.withAppendedId(CONTENT_URI, id);
//        }
//
//        /*
//            Student: This is the buildWeatherLocation function you filled in.
//         */
//        public static Uri buildWeatherLocation(String locationSetting) {
//            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
//        }
//
//        public static Uri buildWeatherLocationWithStartDate(
//                String locationSetting, long startDate) {
//            long normalizedDate = normalizeDate(startDate);
//            return CONTENT_URI.buildUpon().appendPath(locationSetting)
//                    .appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
//        }
//
//        public static Uri buildWeatherLocationWithDate(String locationSetting, long date) {
//            return CONTENT_URI.buildUpon().appendPath(locationSetting)
//                    .appendPath(Long.toString(normalizeDate(date))).build();
//        }
//
//        public static String getLocationSettingFromUri(Uri uri) {
//            return uri.getPathSegments().get(1);
//        }
//
//        public static long getDateFromUri(Uri uri) {
//            return Long.parseLong(uri.getPathSegments().get(2));
//        }
//
//        public static long getStartDateFromUri(Uri uri) {
//            String dateString = uri.getQueryParameter(COLUMN_DATE);
//            if (null != dateString && dateString.length() > 0)
//                return Long.parseLong(dateString);
//            else
//                return 0;
//        }
//    }
}

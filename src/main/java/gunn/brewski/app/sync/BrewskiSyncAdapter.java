package gunn.brewski.app.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.Time;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import gunn.brewski.app.BrewskiApplication;
import gunn.brewski.app.MainActivity;
import gunn.brewski.app.R;
import gunn.brewski.app.Utility;
import gunn.brewski.app.data.BrewskiContract;

/**
 * Created by SESA300553 on 4/2/2015.
 */
public class BrewskiSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = BrewskiSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 1000 * 60 * 60 * 24;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = SYNC_INTERVAL;
    private static final int BEER_NOTIFICATION_ID = 3004;
    private static final int BREWERY_NOTIFICATION_ID = 4004;


    private static final String[] NOTIFY_BEER_OF_THE_DAY = new String[] {
            BrewskiContract.BeerEntry.COLUMN_BEER_ID,
            BrewskiContract.BeerEntry.COLUMN_BEER_NAME,
            BrewskiContract.BeerEntry.COLUMN_BEER_DESCRIPTION,
            BrewskiContract.BeerEntry.COLUMN_LABEL_ICON
    };

    // these indices must match the projection
    private static final int COLUMN_BEER_ID = 0;
    private static final int COLUMN_BEER_NAME = 1;
    private static final int COLUMN_BEER_DESCRIPTION = 2;
    private static final int COLUMN_LABEL_ICON = 3;

    public BrewskiSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");

        performBeerSync();
        performBrewerySync();
        performCategorySync();
        performStyleSync();

        return;
    }

    private void performBeerSync() {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection beerUrlConnection = null;
        BufferedReader beerReader = null;

        // Will contain the raw JSON response as a string.
        String beerJsonStr = null;

        String format = "json";
        String api_key = "1be59a6cc44af64d5c7d6aafad061f23";
        String endpoint = "beers";

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String BREWERY_DB_BASE_URL =
                    "http://api.brewerydb.com/v2/" + endpoint + "?";
            final String FORMAT_PARAM = "format";
            final String KEY_PARAM = "key";

            Uri builtBeerUri = Uri.parse(BREWERY_DB_BASE_URL).buildUpon()
                    .appendQueryParameter(KEY_PARAM, api_key)
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .build();

            URL beerUrl = new URL(builtBeerUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            beerUrlConnection = (HttpURLConnection) beerUrl.openConnection();
            beerUrlConnection.setRequestMethod("GET");
            beerUrlConnection.connect();

            // Read the input stream into a String
            InputStream beerInputStream = beerUrlConnection.getInputStream();
            StringBuffer beerBuffer = new StringBuffer();
            if (beerInputStream == null) {
                // Nothing to do.
                return;
            }
            beerReader = new BufferedReader(new InputStreamReader(beerInputStream));

            String line;
            while ((line = beerReader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                beerBuffer.append(line + "\n");
            }

            if (beerBuffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }

            beerJsonStr = beerBuffer.toString();

            getBeerDataFromJson(beerJsonStr);
        }
        catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        }
        catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        finally {
            if (beerUrlConnection != null) {
                beerUrlConnection.disconnect();
            }
            if (beerReader != null) {
                try {
                    beerReader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    private void performBrewerySync() {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection beerUrlConnection = null;
        BufferedReader beerReader = null;

        // Will contain the raw JSON response as a string.
        String beerJsonStr = null;

        String format = "json";
        String api_key = "1be59a6cc44af64d5c7d6aafad061f23";
        String endpoint = "beers";
        String page = ((BrewskiApplication) MainActivity.application).getCurrentBeerPage().toString();

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String BREWERY_DB_BASE_URL =
                    "http://api.brewerydb.com/v2/" + endpoint + "?";
            final String FORMAT_PARAM = "format";
            final String KEY_PARAM = "key";
            final String PAGE_PARAM = "p";

            Uri builtBeerUri = Uri.parse(BREWERY_DB_BASE_URL).buildUpon()
                    .appendQueryParameter(PAGE_PARAM, page)
                    .appendQueryParameter(KEY_PARAM, api_key)
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .build();

            URL beerUrl = new URL(builtBeerUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            beerUrlConnection = (HttpURLConnection) beerUrl.openConnection();
            beerUrlConnection.setRequestMethod("GET");
            beerUrlConnection.connect();

            // Read the input stream into a String
            InputStream beerInputStream = beerUrlConnection.getInputStream();
            StringBuffer beerBuffer = new StringBuffer();
            if (beerInputStream == null) {
                // Nothing to do.
                return;
            }
            beerReader = new BufferedReader(new InputStreamReader(beerInputStream));

            String line;
            while ((line = beerReader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                beerBuffer.append(line + "\n");
            }

            if (beerBuffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }

            beerJsonStr = beerBuffer.toString();

            getBeerDataFromJson(beerJsonStr);
        }
        catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        }
        catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        finally {
            if (beerUrlConnection != null) {
                beerUrlConnection.disconnect();
            }
            if (beerReader != null) {
                try {
                    beerReader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    private void performCategorySync() {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection beerUrlConnection = null;
        BufferedReader beerReader = null;

        // Will contain the raw JSON response as a string.
        String beerJsonStr = null;

        String format = "json";
        String api_key = "1be59a6cc44af64d5c7d6aafad061f23";
        String endpoint = "beers";

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String BREWERY_DB_BASE_URL =
                    "http://api.brewerydb.com/v2/" + endpoint + "?";
            final String FORMAT_PARAM = "format";
            final String KEY_PARAM = "key";

            Uri builtBeerUri = Uri.parse(BREWERY_DB_BASE_URL).buildUpon()
                    .appendQueryParameter(KEY_PARAM, api_key)
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .build();

            URL beerUrl = new URL(builtBeerUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            beerUrlConnection = (HttpURLConnection) beerUrl.openConnection();
            beerUrlConnection.setRequestMethod("GET");
            beerUrlConnection.connect();

            // Read the input stream into a String
            InputStream beerInputStream = beerUrlConnection.getInputStream();
            StringBuffer beerBuffer = new StringBuffer();
            if (beerInputStream == null) {
                // Nothing to do.
                return;
            }
            beerReader = new BufferedReader(new InputStreamReader(beerInputStream));

            String line;
            while ((line = beerReader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                beerBuffer.append(line + "\n");
            }

            if (beerBuffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }

            beerJsonStr = beerBuffer.toString();

            getBeerDataFromJson(beerJsonStr);
        }
        catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        }
        catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        finally {
            if (beerUrlConnection != null) {
                beerUrlConnection.disconnect();
            }
            if (beerReader != null) {
                try {
                    beerReader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    private void performStyleSync() {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection beerUrlConnection = null;
        BufferedReader beerReader = null;

        // Will contain the raw JSON response as a string.
        String beerJsonStr = null;

        String format = "json";
        String api_key = "1be59a6cc44af64d5c7d6aafad061f23";
        String endpoint = "beers";

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String BREWERY_DB_BASE_URL =
                    "http://api.brewerydb.com/v2/" + endpoint + "?";
            final String FORMAT_PARAM = "format";
            final String KEY_PARAM = "key";

            Uri builtBeerUri = Uri.parse(BREWERY_DB_BASE_URL).buildUpon()
                    .appendQueryParameter(KEY_PARAM, api_key)
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .build();

            URL beerUrl = new URL(builtBeerUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            beerUrlConnection = (HttpURLConnection) beerUrl.openConnection();
            beerUrlConnection.setRequestMethod("GET");
            beerUrlConnection.connect();

            // Read the input stream into a String
            InputStream beerInputStream = beerUrlConnection.getInputStream();
            StringBuffer beerBuffer = new StringBuffer();
            if (beerInputStream == null) {
                // Nothing to do.
                return;
            }
            beerReader = new BufferedReader(new InputStreamReader(beerInputStream));

            String line;
            while ((line = beerReader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                beerBuffer.append(line + "\n");
            }

            if (beerBuffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }

            beerJsonStr = beerBuffer.toString();

            getBeerDataFromJson(beerJsonStr);
        }
        catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        }
        catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        finally {
            if (beerUrlConnection != null) {
                beerUrlConnection.disconnect();
            }
            if (beerReader != null) {
                try {
                    beerReader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    /**
     * Take the String representing the complete beer results in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getBeerDataFromJson(String beerJsonStr) throws JSONException {

        // Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // These are the names of the JSON objects that need to be extracted.
        // Beer Information
        final String BDB_BEER_ID = "id";
        final String BDB_BEER_NAME = "name";
        final String BDB_BEER_DESCRIPTION = "description";
        final String BDB_BEER_BREWERIES = "breweries";
        final String BDB_BREWERY_ID = "id";
        final String BDB_BREWERY_NAME = "name";
        final String BDB_BREWERY_DESCRIPTION = "description";
        final String BDB_BREWERY_ESTABLISHED = "established";
        final String BDB_BREWERY_IMAGES = "images";
        final String BDB_BREWERY_IMAGE_LARGE = "large";
        final String BDB_BREWERY_IMAGE_MEDIUM = "medium";
        final String BDB_BREWERY_IMAGE_ICON = "icon";
        final String BDB_BEER_STYLE = "style";
        final String BDB_STYLE_ID = "id";
        final String BDB_STYLE_NAME = "name";
        final String BDB_STYLE_SHORT_NAME = "shortName";
        final String BDB_STYLE_DESCRIPTION = "description";
        final String BDB_STYLE_CATEGORY = "category";
        final String BDB_CATEGORY_ID = "id";
        final String BDB_CATEGORY_NAME = "name";
        final String BDB_BEER_LABELS = "labels";
        final String BDB_BEER_LABEL_ICON = "icon";
        final String BDB_BEER_LABEL_MEDIUM = "medium";
        final String BDB_BEER_LABEL_LARGE = "large";

        // Beer information.  Each beer's info is an element of the "list" array.
        final String BDB_DATA = "data";

        try {
            JSONObject beerJson = new JSONObject(beerJsonStr);
            JSONArray beerArray = beerJson.getJSONArray(BDB_DATA);

            // Insert the new beer information into the database
            Vector<ContentValues> beerContentValuesVector = new Vector<ContentValues>(beerArray.length());

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            for(int i = 0; i < beerArray.length(); i++) {
                // These are the values that will be collected.
                String beerId;
                String beerName;
                String beerDescription;
                String beerStyle;
                String beerLabelIcon;
                String beerLabelMedium;
                String beerLabelLarge;

                // Get the JSON object representing the day
                JSONObject beerInfo = beerArray.getJSONObject(i);

                // Cheating to convert this to UTC time, which is what we want anyhow

                beerId = beerInfo.getString(BDB_BEER_ID);
                beerName = beerInfo.getString(BDB_BEER_NAME);
                beerDescription = beerInfo.getString(BDB_BEER_DESCRIPTION);
                beerStyle = beerInfo.getString(BDB_BEER_STYLE);

                JSONObject beerLabels = beerInfo.getJSONObject(BDB_BEER_LABELS);
                beerLabelIcon = beerLabels.getString(BDB_BEER_LABEL_ICON);
                beerLabelMedium = beerLabels.getString(BDB_BEER_LABEL_MEDIUM);
                beerLabelLarge = beerLabels.getString(BDB_BEER_LABEL_LARGE);

                ContentValues beerValues = new ContentValues();

                beerValues.put(BrewskiContract.BeerEntry.COLUMN_BEER_ID, beerId);
                beerValues.put(BrewskiContract.BeerEntry.COLUMN_BEER_NAME, beerName);
                beerValues.put(BrewskiContract.BeerEntry.COLUMN_BEER_DESCRIPTION, beerDescription);
                beerValues.put(BrewskiContract.BeerEntry.COLUMN_STYLE_ID, beerStyle);
                beerValues.put(BrewskiContract.BeerEntry.COLUMN_LABEL_ICON, beerLabelIcon);
                beerValues.put(BrewskiContract.BeerEntry.COLUMN_LABEL_MEDIUM, beerLabelMedium);
                beerValues.put(BrewskiContract.BeerEntry.COLUMN_LABEL_LARGE, beerLabelLarge);

                beerContentValuesVector.add(beerValues);
            }

            int inserted = 0;
            // add to database
            if ( beerContentValuesVector.size() > 0 ) {
                ContentValues[] beerContentValuesArray = new ContentValues[beerContentValuesVector.size()];
                beerContentValuesVector.toArray(beerContentValuesArray);
                getContext().getContentResolver().bulkInsert(BrewskiContract.BeerEntry.BEER_CONTENT_URI, beerContentValuesArray);

//                // delete old data so we don't build up an endless history
//                getContext().getContentResolver().delete(BrewskiContract.BeerEntry.BEER_CONTENT_URI,
//                        BrewskiContract.BeerEntry.COLUMN_DATE + " <= ?",
//                        new String[] {Long.toString(dayTime.setJulianDay(julianStartDay-1))});

                notifyBeerOfTheDay();
            }

            Log.d(LOG_TAG, "Sync Complete. " + beerContentValuesVector.size() + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void getBreweryDataFromJson(String breweryJsonStr) throws JSONException {
        // Brewery Information
        final String BDB_BREWERY_ID = "id";
        final String BDB_BREWERY_NAME = "name";
        final String BDB_BREWERY_DESCRIPTION = "description";
        final String BDB_BREWERY_WEBSITE = "website";
        final String BDB_BREWERY_ESTABLISHED = "established";
        final String BDB_BREWERY_IMAGES = "images";
        final String BDB_BREWERY_IMAGES_ICON = "icon";
        final String BDB_BREWERY_IMAGES_MEDIUM = "medium";
        final String BDB_BREWERY_IMAGES_LARGE = "large";
    }

    private void getCategoryDataFromJson(String categoryJsonStr) throws JSONException {
        // Category Information
        final String BDB_CATEGORY_ID = "id";
        final String BDB_CATEGORY_NAME = "name";
    }

    private void getStyleDataFromJson(String styleJsonStr) throws JSONException {
        // Style Information
        final String BDB_STYLE_ID = "id";
        final String BDB_STYLE_NAME = "name";
    }

    private void notifyBeerOfTheDay() {
        Context context = getContext();
        //checking the last update and notify if it' the first of the day
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        boolean displayNotifications = prefs.getBoolean(displayNotificationsKey,
                Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));

        if ( displayNotifications ) {

            String lastNotificationKey = context.getString(R.string.pref_last_notification);
            long lastSync = prefs.getLong(lastNotificationKey, 0);

            if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS) {
                // Last sync was more than 1 day ago, let's send a notification with the weather.
//                String locationQuery = Utility.getPreferredLocation(context);

                Uri beerUri = BrewskiContract.BeerEntry.buildBeerUri(System.currentTimeMillis());

                // we'll query our contentProvider, as always
                Cursor cursor = context.getContentResolver().query(beerUri, NOTIFY_BEER_OF_THE_DAY, null, null, null);

                if (cursor.moveToFirst()) {
                    int weatherId = cursor.getInt(COLUMN_BEER_ID);
                    double high = cursor.getDouble(COLUMN_BEER_NAME);
                    double low = cursor.getDouble(COLUMN_BEER_DESCRIPTION);
                    String desc = cursor.getString(COLUMN_LABEL_ICON);

                    int iconId = Utility.getIconResourceForWeatherCondition(weatherId);
                    Resources resources = context.getResources();
                    Bitmap largeIcon = BitmapFactory.decodeResource(resources,
                            Utility.getArtResourceForWeatherCondition(weatherId));
                    String title = context.getString(R.string.app_name);

                    // Define the text of the forecast.
                    String contentText = String.format(context.getString(R.string.format_notification),
                            desc,
                            Utility.formatTemperature(context, high),
                            Utility.formatTemperature(context, low));

                    // NotificationCompatBuilder is a very convenient way to build backward-compatible
                    // notifications.  Just throw in some data.
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getContext())
                                    .setColor(resources.getColor(R.color.sunshine_light_blue))
                                    .setSmallIcon(iconId)
                                    .setLargeIcon(largeIcon)
                                    .setContentTitle(title)
                                    .setContentText(contentText);

                    // Make something interesting happen when the user clicks on the notification.
                    // In this case, opening the app is sufficient.
                    Intent resultIntent = new Intent(context, MainActivity.class);

                    // The stack builder object will contain an artificial back stack for the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager =
                            (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    // WEATHER_NOTIFICATION_ID allows you to update the notification later on.
                    mNotificationManager.notify(BEER_NOTIFICATION_ID, mBuilder.build());

                    //refreshing last sync
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong(lastNotificationKey, System.currentTimeMillis());
                    editor.commit();
                }
                cursor.close();
            }
        }
    }

//    /**
//     * Helper method to handle insertion of a new location in the weather database.
//     *
//     * @param locationSetting The location string used to request updates from the server.
//     * @param cityName A human-readable city name, e.g "Mountain View"
//     * @param lat the latitude of the city
//     * @param lon the longitude of the city
//     * @return the row ID of the added location.
//     */
//    long addLocation(String locationSetting, String cityName, double lat, double lon) {
//        long locationId;
//
//        // First, check if the location with this city name exists in the db
//        Cursor locationCursor = getContext().getContentResolver().query(
//                BrewskiContract.LocationEntry.CONTENT_URI,
//                new String[]{BrewskiContract.LocationEntry._ID},
//                BrewskiContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
//                new String[]{locationSetting},
//                null);
//
//        if (locationCursor.moveToFirst()) {
//            int locationIdIndex = locationCursor.getColumnIndex(BrewskiContract.LocationEntry._ID);
//            locationId = locationCursor.getLong(locationIdIndex);
//        } else {
//            // Now that the content provider is set up, inserting rows of data is pretty simple.
//            // First create a ContentValues object to hold the data you want to insert.
//            ContentValues locationValues = new ContentValues();
//
//            // Then add the data, along with the corresponding name of the data type,
//            // so the content provider knows what kind of value is being inserted.
//            locationValues.put(BrewskiContract.LocationEntry.COLUMN_CITY_NAME, cityName);
//            locationValues.put(BrewskiContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
//            locationValues.put(BrewskiContract.LocationEntry.COLUMN_COORD_LAT, lat);
//            locationValues.put(BrewskiContract.LocationEntry.COLUMN_COORD_LONG, lon);
//
//            // Finally, insert location data into the database.
//            Uri insertedUri = getContext().getContentResolver().insert(
//                    BrewskiContract.LocationEntry.CONTENT_URI,
//                    locationValues
//            );
//
//            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
//            locationId = ContentUris.parseId(insertedUri);
//        }
//
//        locationCursor.close();
//        // Wait, that worked?  Yes!
//        return locationId;
//    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
        }

        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        BrewskiSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
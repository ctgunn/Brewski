package gunn.brewski.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.text.format.Time;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SESA300553 on 4/2/2015.
 */
public class Utility {
    public static Bitmap drawableFromUrl(String url) throws IOException {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection)new URL(url) .openConnection();
        connection.setRequestProperty("User-agent","Mozilla/4.0");

        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        
        return x;

//        URL newurl = new URL(url);
//        Bitmap mIcon_val = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
//        return mIcon_val;
    }


    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
    }
}
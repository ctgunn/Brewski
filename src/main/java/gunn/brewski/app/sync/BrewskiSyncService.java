package gunn.brewski.app.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by SESA300553 on 4/2/2015.
 */
public class BrewskiSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static BrewskiSyncAdapter sBrewskiSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("SunshineSyncService", "onCreate - SunshineSyncService");
        synchronized (sSyncAdapterLock) {
            if (sBrewskiSyncAdapter == null) {
                sBrewskiSyncAdapter = new BrewskiSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sBrewskiSyncAdapter.getSyncAdapterBinder();
    }
}

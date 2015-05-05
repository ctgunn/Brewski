package gunn.brewski.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import gunn.brewski.app.sync.BrewskiSyncAdapter;

public class LoadingScreenActivity extends FragmentActivity {
    private final String LOG_TAG = LoadingScreenActivity.class.getSimpleName();

    public static final String DASHBOARD = "dashboard";
    public static final String PROFILE = "profile";
    public static final String CATEGORIES = "categories";
    public static final String BEERS = "beers";
    public static final String BREWERIES = "breweries";
    public static final String SCREEN_LOADING = "screenLoading";
    public static final String NO_SCREEN = "noScreen";

    private String screenLoading;

    //A ProgressDialog object
    private ProgressDialog progressDialog;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        screenLoading = getIntent().getStringExtra(SCREEN_LOADING);

        //Initialize a LoadViewTask object and call the execute() method
        new LoadViewTask().execute();

    }

    //To use the AsyncTask, it must be subclassed
    private class LoadViewTask extends AsyncTask<Void, Integer, Void> {
        //Before running code in separate thread
        @Override
        protected void onPreExecute() {
            //Create a new progress dialog
            progressDialog = ProgressDialog.show(LoadingScreenActivity.this,"Loading...",
                    "Loading app, please wait...", false, false);
        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params) {
            /* This is just a code that delays the thread execution 4 times,
             * during 850 milliseconds and updates the current progress. This
             * is where the code that is going to be executed on a background
             * thread must be placed.
             */
            try {
                if(DASHBOARD.equals(screenLoading)) {
//                    BrewskiSyncAdapter.syncImmediately(LoadingScreenActivity.this);
                }
                else if(PROFILE.equals(screenLoading)) {
                    //TODO: CALL QUERIES THAT WILL POPULATE THE LIST VIEWS ON THE PROFILE SCREEN.
                }
                else if(CATEGORIES.equals(screenLoading)) {
                    //TODO: CALL QUERY THAT WILL POPULATE THE LIST VIEW ON THE CATEGORIES SCREEN.
                }
                else if(BEERS.equals(screenLoading)) {
                    //TODO: CALL QUERY THAT WILL POPULATE THE LIST VIEW ON THE BEERS SCREEN.
                }
                else if(BREWERIES.equals(screenLoading)) {
                    //TODO: CALL QUERY THAT WILL POPULATE THE LIST VIEW ON THE BREWERIES SCREEN.
                }
                else {
                    Log.d(LOG_TAG, "User is trying to access a screen that doesn't exist.");
                }

                //Get the current thread's token
                synchronized (this) {
                    //Initialize an integer (that will act as a counter) to zero
                    int counter = 0;

                    //While the counter is smaller than four
                    while(counter <= 4) {
                        //Wait 850 milliseconds
                        this.wait(850);

                        //Increment the counter
                        counter++;

                        //Set the current progress.
                        //This value is going to be passed to the onProgressUpdate() method.
                        publishProgress(counter * 15);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        //Update the progress
        @Override
        protected void onProgressUpdate(Integer... values) {
            //set the current progress of the progress dialog
            progressDialog.setProgress(values[0]);
        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result) {
            //close the progress dialog
            progressDialog.dismiss();

            Context applicationContext = BrewskiApplication.getContext();

            if(DASHBOARD.equals(screenLoading)) {
                Intent dashboardIntent = new Intent(LoadingScreenActivity.this, DashboardActivity.class);
                startActivity(dashboardIntent);
            }
            else if(PROFILE.equals(screenLoading)) {
                Intent profileIntent = new Intent(LoadingScreenActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
            }
            else if(CATEGORIES.equals(screenLoading)) {
                Intent categoryIntent = new Intent(LoadingScreenActivity.this, CategoryListActivity.class);
                startActivity(categoryIntent);
            }
            else if(BEERS.equals(screenLoading)) {
                Intent beerIntent = new Intent(LoadingScreenActivity.this, BeerListActivity.class);
                startActivity(beerIntent);
            }
            else if(BREWERIES.equals(screenLoading)) {
                Intent breweryIntent = new Intent(LoadingScreenActivity.this, BreweryListActivity.class);
                startActivity(breweryIntent);
            }
            else {
                DialogFragment noScreenDialog = new ScreenDoesNotExistDialogFragment();
                noScreenDialog.show(getSupportFragmentManager(), NO_SCREEN);
            }
        }
    }

    public static class ScreenDoesNotExistDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("The screen you are attempting to access does not exist.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}

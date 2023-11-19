package gunn.brewski.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DashboardActivity extends ActionBarActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        /**
         * Creating all buttons instances
         * */
        // Dashboard News feed button
//        final Button btn_profile = (Button) findViewById(R.id.btn_profile);

        // Dashboard Friends button
        final Button btn_categories = (Button) findViewById(R.id.btn_categories);

        // Dashboard Messages button
        final Button btn_beers = (Button) findViewById(R.id.btn_beers);

        // Dashboard Places button
        final Button btn_breweries = (Button) findViewById(R.id.btn_breweries);

        /**
         * Handling all button click events
         * */

//            @Override
//            public void onClick(View view) {
//                // Launching News Feed Screen
//                showLoadingScreen(btn_profile.getText().toString().toLowerCase());
//            }
//        });

        // Listening Friends button click
        btn_categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launching News Feed Screen
                showLoadingScreen(btn_categories.getText().toString().toLowerCase());
            }
        });

        // Listening Messages button click
        btn_beers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launching News Feed Screen
                showLoadingScreen(btn_beers.getText().toString().toLowerCase());
            }
        });

        // Listening Messages button click
        btn_breweries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launching News Feed Screen
                showLoadingScreen(btn_breweries.getText().toString().toLowerCase());
            }
        });
    }

    public void showLoadingScreen(String buttonText) {
        Intent loadingScreenIntent = new Intent(getApplicationContext(), LoadingScreenActivity.class);
        loadingScreenIntent.putExtra("screenLoading", buttonText);
        startActivity(loadingScreenIntent);
    }

    @Override
    public void onBackPressed() {
        return;
    }

}
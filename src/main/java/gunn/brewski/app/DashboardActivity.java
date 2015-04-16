package gunn.brewski.app;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class DashboardActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);

        /**
         * Creating all buttons instances
         * */
        // Dashboard News feed button
        Button btn_profile = (Button) findViewById(R.id.btn_profile);

        // Dashboard Friends button
        Button btn_categoriess = (Button) findViewById(R.id.btn_categories);

        // Dashboard Messages button
        Button btn_beers = (Button) findViewById(R.id.btn_beers);

        // Dashboard Places button
        Button btn_breweries = (Button) findViewById(R.id.btn_breweries);

        /**
         * Handling all button click events
         * */

        // Listening to News Feed button click
        btn_profile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching News Feed Screen
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(i);
            }
        });

        // Listening Friends button click
        btn_categoriess.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching News Feed Screen
                Intent i = new Intent(getApplicationContext(), CategoryListActivity.class);
                startActivity(i);
            }
        });

        // Listening Messages button click
        btn_beers.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching News Feed Screen
                Intent i = new Intent(getApplicationContext(), BeerListActivity.class);
                startActivity(i);
            }
        });

        // Listening Messages button click
        btn_breweries.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching News Feed Screen
                Intent i = new Intent(getApplicationContext(), BreweryListActivity.class);
                startActivity(i);
            }
        });

        // Listening to Places button click
//        btn_breweries.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                // Launching News Feed Screen
//                Intent i = new Intent(getApplicationContext(), BreweryListActivity.class);
//                startActivity(i);
//            }
//        });
    }
}
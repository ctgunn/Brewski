package gunn.brewski.app;

import android.app.Application;

/**
 * Created by SESA300553 on 4/19/2015.
 */
public class BrewskiApplication extends Application {
    private String currentBeerId;
    private String currentBreweryId;
    private String currentCategoryId;
    private String currentStyleId;
    private String currentIngredientId;
    private String currentLocationId;

    public String getCurrentBeerId() {
        return currentBeerId;
    }

    public String getCurrentBreweryId() {
        return currentBreweryId;
    }

    public String getCurrentStyleId() {
        return currentStyleId;
    }

    public String getCurrentIngredientId() {
        return currentIngredientId;
    }

    public String getCurrentLocationId() {
        return currentLocationId;
    }

    public String getCurrentCategoryId() {
        return currentCategoryId;
    }

    public void setCurrentBeerId(String currentBeerId) {
        this.currentBeerId = currentBeerId;
    }

    public void setCurrentBreweryId(String currentBreweryId) {
        this.currentBreweryId = currentBreweryId;
    }

    public void setCurrentStyleId(String currentStyleId) {
        this.currentStyleId = currentStyleId;
    }

    public void setCurrentIngredientId(String currentIngredientId) {
        this.currentIngredientId = currentIngredientId;
    }

    public void setCurrentLocationId(String currentLocationId) {
        this.currentLocationId = currentLocationId;
    }

    public void setCurrentCategoryId(String currentCategoryId) {
        this.currentCategoryId = currentCategoryId;
    }
}

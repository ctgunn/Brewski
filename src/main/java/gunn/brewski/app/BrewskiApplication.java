package gunn.brewski.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by SESA300553 on 4/19/2015.
 */
public class BrewskiApplication extends Application {
    private static String currentBeerId;
    private static Integer currentBeerPage;
    private static Integer numberOfBeerPages;
    private static String currentBreweryId;
    private static Integer currentBreweryPage;
    private static Integer numberOfBreweryPages;
    private static String currentCategoryId;
    private static Integer currentCategoryPage;
    private static Integer numberOfCategoryPages;
    private static String currentStyleId;
    private static Integer currentStylePage;
    private static Integer numberOfStylePages;
    private static String currentIngredientId;
    private static String currentLocationId;
    private static Context context;
    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        BrewskiApplication.context = getApplicationContext();
        BrewskiApplication.application = this;
        BrewskiApplication.currentBeerPage = 1;
        BrewskiApplication.numberOfBeerPages = 1;
        BrewskiApplication.currentBreweryPage = 1;
        BrewskiApplication.numberOfBreweryPages = 1;
        BrewskiApplication.currentCategoryPage = 1;
        BrewskiApplication.numberOfCategoryPages = 1;
        BrewskiApplication.currentStylePage = 1;
        BrewskiApplication.numberOfStylePages = 1;
    }

    public static Application getApplication() {
        return BrewskiApplication.application;
    }

    public static Context getContext() {
        return BrewskiApplication.context;
    }

    public static String getCurrentBeerId() {
        return BrewskiApplication.currentBeerId;
    }

    public static Integer getCurrentBeerPage() {
        return BrewskiApplication.currentBeerPage;
    }

    public static Integer getNumberOfBeerPages() {
        return BrewskiApplication.numberOfBeerPages;
    }

    public static String getCurrentBreweryId() {
        return BrewskiApplication.currentBreweryId;
    }

    public static Integer getCurrentBreweryPage() {
        return BrewskiApplication.currentBreweryPage;
    }

    public static Integer getNumberOfBreweryPages() {
        return BrewskiApplication.numberOfBreweryPages;
    }

    public static String getCurrentStyleId() {
        return BrewskiApplication.currentStyleId;
    }

    public static Integer getCurrentStylePage() {
        return BrewskiApplication.currentStylePage;
    }

    public static Integer getNumberOfStylePages() {
        return BrewskiApplication.numberOfStylePages;
    }

    public static String getCurrentIngredientId() {
        return BrewskiApplication.currentIngredientId;
    }

    public static String getCurrentLocationId() {
        return BrewskiApplication.currentLocationId;
    }

    public static String getCurrentCategoryId() {
        return BrewskiApplication.currentCategoryId;
    }

    public static Integer getCurrentCategoryPage() {
        return BrewskiApplication.currentCategoryPage;
    }

    public static Integer getNumberOfCategoryPages() {
        return BrewskiApplication.numberOfCategoryPages;
    }

    public static void setCurrentBeerId(String currentBeerId) {
        BrewskiApplication.currentBeerId = currentBeerId;
    }

    public static void setApplication(Application application) {
        BrewskiApplication.application = application;
    }

    public static void setContext(Context context) {
        BrewskiApplication.context = context;
    }

    public static void setCurrentBeerPage(Integer currentBeerPage) {
        BrewskiApplication.currentBeerPage = currentBeerPage;
    }

    public static void setNumberOfBeerPages(Integer numberOfBeerPages) {
        BrewskiApplication.numberOfBeerPages = numberOfBeerPages;
    }

    public static void setCurrentBreweryId(String currentBreweryId) {
        BrewskiApplication.currentBreweryId = currentBreweryId;
    }

    public static void setCurrentBreweryPage(Integer currentBreweryPage) {
        BrewskiApplication.currentBreweryPage = currentBreweryPage;
    }

    public static void setNumberOfBreweryPages(Integer numberOfBreweryPages) {
        BrewskiApplication.numberOfBreweryPages = numberOfBreweryPages;
    }

    public static void setCurrentStyleId(String currentStyleId) {
        BrewskiApplication.currentStyleId = currentStyleId;
    }

    public static void setCurrentStylePage(Integer currentStylePage) {
        BrewskiApplication.currentStylePage = currentStylePage;
    }

    public static void setNumberOfStylePages(Integer numberOfStylePages) {
        BrewskiApplication.numberOfStylePages = numberOfStylePages;
    }

    public static void setCurrentIngredientId(String currentIngredientId) {
        BrewskiApplication.currentIngredientId = currentIngredientId;
    }

    public static void setCurrentLocationId(String currentLocationId) {
        BrewskiApplication.currentLocationId = currentLocationId;
    }

    public static void setCurrentCategoryId(String currentCategoryId) {
        BrewskiApplication.currentCategoryId = currentCategoryId;
    }

    public static void setCurrentCategoryPage(Integer currentCategoryPage) {
        BrewskiApplication.currentCategoryPage = currentCategoryPage;
    }

    public static void setNumberOfCategoryPages(Integer numberOfCategoryPages) {
        BrewskiApplication.numberOfCategoryPages = numberOfCategoryPages;
    }
}

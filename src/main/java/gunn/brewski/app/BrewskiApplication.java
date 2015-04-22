package gunn.brewski.app;

import android.app.Application;

/**
 * Created by SESA300553 on 4/19/2015.
 */
public class BrewskiApplication extends Application {
    private String currentBeerId;
    private Integer currentBeerPage;
    private Integer numberOfBeerPages;
    private String currentBreweryId;
    private Integer currentBreweryPage;
    private Integer numberOfBreweryPages;
    private String currentCategoryId;
    private Integer currentCategoryPage;
    private Integer numberOfCategoryPages;
    private String currentStyleId;
    private Integer currentStylePage;
    private Integer numberOfStylePages;
    private String currentIngredientId;
    private String currentLocationId;

    public String getCurrentBeerId() {
        return currentBeerId;
    }

    public Integer getCurrentBeerPage() {
        return currentBeerPage;
    }

    public Integer getNumberOfBeerPages() {
        return numberOfBeerPages;
    }

    public String getCurrentBreweryId() {
        return currentBreweryId;
    }

    public Integer getCurrentBreweryPage() {
        return currentBreweryPage;
    }

    public Integer getNumberOfBreweryPages() {
        return numberOfBreweryPages;
    }

    public String getCurrentStyleId() {
        return currentStyleId;
    }

    public Integer getCurrentStylePage() {
        return currentStylePage;
    }

    public Integer getNumberOfStylePages() {
        return numberOfStylePages;
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

    public Integer getCurrentCategoryPage() {
        return currentCategoryPage;
    }

    public Integer getNumberOfCategoryPages() {
        return numberOfCategoryPages;
    }

    public void setCurrentBeerId(String currentBeerId) {
        this.currentBeerId = currentBeerId;
    }

    public void setCurrentBeerPage(Integer currentBeerPage) {
        this.currentBeerPage = currentBeerPage;
    }

    public void setNumberOfBeerPages(Integer numberOfBeerPages) {
        this.numberOfBeerPages = numberOfBeerPages;
    }

    public void setCurrentBreweryId(String currentBreweryId) {
        this.currentBreweryId = currentBreweryId;
    }

    public void setCurrentBreweryPage(Integer currentBreweryPage) {
        this.currentBreweryPage = currentBreweryPage;
    }

    public void setNumberOfBreweryPages(Integer numberOfBreweryPages) {
        this.numberOfBreweryPages = numberOfBreweryPages;
    }

    public void setCurrentStyleId(String currentStyleId) {
        this.currentStyleId = currentStyleId;
    }

    public void setCurrentStylePage(Integer currentStylePage) {
        this.currentStylePage = currentStylePage;
    }

    public void setNumberOfStylePages(Integer numberOfStylePages) {
        this.numberOfStylePages = numberOfStylePages;
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

    public void setCurrentCategoryPage(Integer currentCategoryPage) {
        this.currentCategoryPage = currentCategoryPage;
    }

    public void setNumberOfCategoryPages(Integer numberOfCategoryPages) {
        this.numberOfCategoryPages = numberOfCategoryPages;
    }
}

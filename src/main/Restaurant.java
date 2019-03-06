package ca.ece.ubc.cpen221.mp5;

import java.util.ArrayList;
import java.util.List;

public class Restaurant extends Business
{
    private final String url, state, city, fullAddress, photoUrl;
    private double longitude, latitude;
    private List<String> neighborhood, schools;

    public Restaurant(String url, String businessId, String name, String state, String type, String city, String fullAddress, String photoUrl,
                      double longitude, double stars, double latitude, int reviewCount, int price, List<String> neighborhood, List<String> categories,
                      List<String> schools, boolean open)
    {
        this.url = url;
        this.businessId = businessId;
        this.name = name;
        this.state = state;
        this.type = type;
        this.city = city;
        this.fullAddress = fullAddress;
        this.photoUrl = photoUrl;
        this.longitude = longitude;
        this.latitude = latitude;
        this.stars = stars;
        this.reviewCount = reviewCount;
        this.price = price;
        this.neighborhood = neighborhood;
        this.schools = schools;
        this.categories = categories;
        this.open = open;
    }

    public Restaurant(Restaurant restaurant) {
        this.url = restaurant.getUrl();
        this.businessId = restaurant.getId();
        this.name = restaurant.getName();
        this.state = restaurant.getState();
        this.type = restaurant.getType();
        this.city = restaurant.getCity();
        this.fullAddress = restaurant.getFullAddress();
        this.photoUrl = restaurant.getPhotoUrl();
        this.longitude = restaurant.getLongitude();
        this.latitude = restaurant.getLatitude();
        this.stars = restaurant.getRating();
        this.reviewCount = restaurant.getReviewCount();
        this.price = restaurant.getPrice();
        this.neighborhood = restaurant.getNeighborhood();
        this.schools = restaurant.getSchools();
        this.categories = restaurant.getCategories();
        this.open = restaurant.getOpen();
    }

    /**
     * Return the restaurant's url
     *
     * @return a String representing the restaurants url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Return the restaurant's state
     *
     * @return a String representing the restaurant's state
     */
    public String getState()
    {
        return state;
    }

    /**
     * Return the restaurant's city
     *
     * @return a String representing the restaurant's city
     */
    public String getCity()
    {
        return city;
    }

    /**
     * Return the restaurant's full address
     *
     * @return a String representing the restaurant's full address
     */
    public String getFullAddress()
    {
        return fullAddress;
    }

    /**
     * Return the restaurant's photo url
     *
     * @return a String representing the restaurant's photo url
     */
    public String getPhotoUrl()
    {
        return photoUrl;
    }

    /**
     * Return the restaurant's longitude
     *
     * @return the double longitude of the restaurant
     */
    public double getLongitude()
    {
        return longitude;
    }

    /**
     * Return the restaurant's latitude
     *
     * @return the double latitude of the restaurant
     */
    public double getLatitude()
    {
        return latitude;
    }

    /**
     * Return the neighborhoods that the restaurant is located in
     *
     * @return a List of Strings, with each string representing a neighborhood
     */
    public List<String> getNeighborhood()
    {
        List<String> neighborhood = new ArrayList<>();
        neighborhood.addAll(this.neighborhood);
        return neighborhood;
    }

    /**
     * Return the schools the restaurant is located near
     *
     * @return a List of Strings, with each string representing a school
     */
    public List<String> getSchools()
    {
        List<String> schools = new ArrayList<>();
        schools.addAll(this.schools);
        return schools;
    }
}

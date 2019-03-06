package ca.ece.ubc.cpen221.mp5;

import java.util.ArrayList;
import java.util.List;

public class Business implements Reviewable {
    protected String businessId, name;
    protected String type;
    protected double stars;
    protected int reviewCount;
    protected int price;
    protected List<String> categories;
    protected boolean open;

    /**
     * Return the identifier of the business
     *
     * @return the identifier of the business
     */
    public String getId() {
        return businessId;
    }

    /**
     * Return the name of the business
     *
     * @return the name of the business
     */
    public String getName() {
        return name;
    }

    /**
     * Return the type
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Return the average rating of this business
     *
     * @return the average rating of this business
     */
    public double getRating() {
        return stars;
    }

    /**
     * Updates the star rating count of the business
     *
     * @param stars is the new double star rating. The star rating is on a 0.5 scale
     */
    public void updateRating(double stars)
    {
        this.stars = stars;
    }

    /**
     * Return the review count of the business
     *
     * @return the review count of the business
     */
    public int getReviewCount() {
        return reviewCount;
    }

    /**
     * Updates the review count of the business
     *
     * @param change is the integer number of reviews being added to the count
     */
    public void updateReviewCount(int change)
    {
        reviewCount = reviewCount + change;
    }
    /**
     * Return the categories associated with the business
     *
     * @return the categories associated with the business
     */
    public List<String> getCategories() {
        return new ArrayList<>(categories);
    }

    /**
     * Return an integer representing how price-y the business is
     *
     * @return a rating representing how price-y the business is
     */
    public int getPrice() {
        return price;
    }

    /**
     * Return true if the business is currently operating, otherwise return false
     *
     * @return true if the business is currently operating, otherwise return false
     */
    public boolean getOpen () {
        return open;
    }

    /**
     * Compare two Business objects for equality
     *
     * @param that is not null
     * @return true if this Business and the other Business represent the same
     *         Business, and false otherwise
     */
    @Override
    public boolean equals(Object that)
    {
        if (that instanceof Business)
        {
            return this.businessId.equals(((Business) that).businessId); // returns true if the businesses have the same businessId
        }
        else
        {
            return false;
        }
    }

    /**
     * Compute the hashCode for a Restaurant object
     *
     * @return the hashCode for a Restaurant object
     */
    @Override
    public int hashCode()
    {
        return businessId.hashCode();
    }
}

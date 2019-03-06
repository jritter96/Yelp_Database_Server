package ca.ece.ubc.cpen221.mp5;

import java.util.*;

public class User
{
    private final String url, type, userId, name;
    private int reviewCount;
    private double averageStars;
    private Map<String, Integer> votes;

    public User(String url, String type, String userId, String name, int reviewCount, double averageStars, Map<String, Integer> votes)
    {
        this.url = url;
        this.type = type;
        this.userId = userId;
        this.name = name;
        this.reviewCount = reviewCount;
        this.averageStars = averageStars;
        this.votes = votes;
    }

    public User(User user) {
        this.url = user.getUrl();
        this.type = user.getType();
        this.userId = user.getUserId();
        this.name = user.getName();
        this.reviewCount = user.getReviewCount();
        this.averageStars = user.getAverageStars();
        this.votes = user.getVotes();
    }

    /**
     * Compare two User objects for equality.
     * 
     * @param that
     *            is not null
     * @return true if this User and that User represent
     *         the same two User objects and false otherwise.
     */
    @Override
    public boolean equals(Object that)
    {
        if (that instanceof User)
        {
            return this.userId.equals(((User) that).userId); // returns true if the users have the same userId
        }
        else
        {
            return false;
        }
    }

    /**
     * Compute the hashCode for a user
     * 
     * @return the hashCode for this user
     */
    @Override
    public int hashCode()
    {
        return userId.hashCode();
    }

    /**
     * Return the url of the user
     *
     * @return the url of the user
     */
    public String getUrl() {
        return url;
    }

    /**
     * Return the type of this entry (always user)
     *
     * @return the type (user)
     */
    public String getType() {
        return type;
    }

    /**
     * Return the identifier of the user
     *
     * @return the identifier of the user
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Return the name of the user
     *
     * @return the name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Return the number of reviews created by the user
     *
     * @return the number of reviews created by the user
     */
    public int getReviewCount () {
        return reviewCount;
    }

    /**
     * Return the average rating of all of the user's reviews
     *
     * @return the average rating of all of the user's reviews
     */
    public double getAverageStars() {
        return averageStars;
    }

    /**
     * Return the number of votes from other users toward this user for each category in {cool, funny, useful}
     *
     * @return the number of votes from other users toward this user for each category in {cool, funny, useful}
     */
    public Map<String, Integer> getVotes () {
        return new HashMap<>(votes);
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
     * Updates the review count of the business
     *
     * @param averageStars is the new double value for average reviews
     */
    public void updateAverageStars(double averageStars)
    {
        this.averageStars = averageStars;
    }
}

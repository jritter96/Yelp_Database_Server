package ca.ece.ubc.cpen221.mp5;

import java.util.*;

public class Review
{
    private final String type, businessId, reviewId, text, userId, date;
    private int stars;
    private Map<String, Integer> votes;

    public Review(String type, String businessId, String reviewId, String text, String userId, String date, int stars, Map<String, Integer> votes)
    {
        this.type = type;
        this.businessId = businessId;
        this.reviewId = reviewId;
        this.text = text;
        this.userId = userId;
        this.date = date;
        this.stars = stars;
        this.votes = votes;
    }

    public Review(Review review) {
        this.type = review.getType();
        this.businessId = review.getBusinessId();
        this.reviewId = review.getReviewId();
        this.text = review.getText();
        this.userId = review.getUserId();
        this.date = review.getDate();
        this.stars = review.getStars();
        this.votes = review.getVotes();
    }

    /**
     * Return the review's type
     *
     * @return a String representing the review's type
     */
    public String getType()
    {
        return type;
    }

    /**
     * Return the review's stars
     *
     * @return the int amount of stars given in the review
     */
    public int getStars()
    {
        return stars;
    }

    /**
     * Return the review's votes
     *
     * @return a Map of Strings to Integers representing the
     *      amount of votes given in each of the mapped categories in a review
     */
    public Map<String, Integer> getVotes()
    {
        Map<String, Integer> votes = new HashMap<>(this.votes);
        return votes;
    }

    /**
     * Return the review's businessId
     *
     * @return a String representing the review's type
     */
    public String getBusinessId()
    {
        return businessId;
    }

    /**
     * Return the review's date
     *
     * @return a String representing the review's date
     */
    public String getDate()
    {
        return date;
    }

    /**
     * Return the review's reviewId
     *
     * @return a String representing the review's reviewId
     */
    public String getReviewId()
    {
        return reviewId;
    }

    /**
     * Return the review's text
     *
     * @return a String of the review's text
     */
    public String getText()
    {
        return text;
    }

    /**
     * Return the review's userId
     *
     * @return a String representing the review's userId
     */
    public String getUserId()
    {
        return userId;
    }

    /**
     * Compare two Review objects for equality
     *
     * @param that is not null
     * @return true if this Review and the other Review represent the same
     *         Review and false otherwise
     */
    @Override
    public boolean equals(Object that)
    {
        if (that instanceof Review)
        {
            if ((userId.equals(((Review) that).userId)) && (businessId.equals(((Review) that).businessId)) && (reviewId.equals(((Review) that).reviewId))) // returns true if the reviews have the same reviewId or were written by the same user
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Compute the hashCode for a Review object
     *
     * @return the hashCode for a Review object
     */
    @Override
    public int hashCode()
    {
        return reviewId.hashCode();
    }
}

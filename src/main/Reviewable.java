package ca.ece.ubc.cpen221.mp5;

import java.util.List;

public interface Reviewable {

    /**
     * Return the average rating of this entry
     *
     * @return the average rating of this entry
     */
    double getRating();

    /**
     * Return the name of the object being reviewed
     *
     * @return the name of the object being reviewed
     */
    String getName();

    /**
     * Return the identifier of the object being reviewed
     *
     * @return the identifier of the object being reviewed
     */
    String getId();

    /**
     * Return the review count of the object
     *
     * @return the review count of the object
     */
    int getReviewCount();

    /**
     * Return the type of the object
     *
     * @return the type of the object
     */
    String getType();

    /**
     * Return the categories associated with the object
     *
     * @return the categories associated with the object
     */
    List<String> getCategories();
}

package ca.ece.ubc.cpen221.mp5;

public class Coordinate
{
    private double x, y;

    public Coordinate(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Compare two Coordinate objects for equality.
     *
     * @param other
     *            is not null
     * @return true if this Coordinate and the other Coordinate represent
     *         the same two Coordinate objects and false otherwise.
     */
    @Override
    public boolean equals(Object other) {

        if (other instanceof Coordinate) {
            return ((this.x == ((Coordinate) other).getX()) && (this.y == ((Coordinate) other).getY()));
        } else
            return false;

    }

    /**
     * Get the distance between two coordinate points.
     *
     * @param point1 the location of the first coordinate
     *            is not null
     * @param point2 the location of the second coordinate
     *            is not null
     *
     * @return the distance between point1 and point2
     */
    public static double getDistanceBetweenPoints(Coordinate point1, Coordinate point2) {
        double xDistance = point2.getX() - point1.getX();
        double yDistance = point2.getY() - point1.getY();
        return Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
    }

    /**
     * Return the X position of this coordinate (longitude)
     *
     * @return the X position of this coordinate
     */
    public double getX()
    {
        return x;
    }

    /**
     * Return the Y position of this coordinate (latitude)
     *
     * @return the Y position of this coordinate
     */
    public double getY()
    {
        return y;
    }

    /**
     * Set the X position of this coordinate (longitude)
     *
     * Mutates this coordinate's X position
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Set the Y position of this coordinate (longitude)
     *
     * Mutates this coordinate's Y position
     */
    public void setY(double y) {
        this.y = y;
    }
}

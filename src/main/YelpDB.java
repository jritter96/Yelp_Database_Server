package ca.ece.ubc.cpen221.mp5;

import javax.json.*;
import javax.json.stream.JsonParser;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.ToDoubleBiFunction;

public class YelpDB implements MP5Db
{
    private final String restaurantFile, userFile, reviewFile;
    private Set<Restaurant> restaurants = new HashSet<>();
    private Set<User> users = new HashSet<>();
    private Set<Review> reviews = new HashSet<>();
    private int newUserId = 0;
    private int newReviewId = 0;
    private int newRestaurantId = 0;

    public YelpDB(String restaurantFile, String reviewFile, String userFile)
    {
        this.restaurantFile = restaurantFile;
        this.userFile = userFile;
        this.reviewFile = reviewFile;
    }

    /**
     * generates a list of reviews that share the inputted businessId
     *
     * @param businessId is a non null String
     * @return A list of Reviews such that each review's businessId field
     *            is equals to the inputted businessId
     */
    public List<Review> getReviewsByBusinessId(String businessId) {
        List<Review> businessReviews = new ArrayList<>();
        for(Review review: reviews) {
            if(review.getBusinessId().equals(businessId));
                businessReviews.add(review);
        }
        return businessReviews;
    }

    /**
     * generates a list of reviews that share the inputted userId
     *
     * @param userId is a non-null String
     *
     * @return A list of Reviews such that each review's userId field
     *            is equals to the inputted userId
     */
    public List<Review> getReviewsByUserId(String userId)
    {
        List<Review> userReviews = new ArrayList<>();
        for (Review r : reviews) // iterates over all of the reviews
        {
            if (r.getUserId().equals(userId)) // checks if the current review was written by the specified user
            {
                userReviews.add(r); // adds the review to the list of user reviews
            }
        }
        return userReviews;
    }

    /**
     * generates a list of reviews that share the inputted restaurantId
     *
     * @param restaurantId is a non-null String
     *
     * @return A list of Reviews such that each review's restaurantId field
     *            is equals to the inputted restaurantId
     */
    private Restaurant getRestaurantById(String restaurantId)
    {
        Restaurant restaurant = null;
        for (Restaurant r : restaurants) // iterates over all of the restaurants
        {
            if (r.getId().equals(restaurantId)) // checks if the current restaurant has the specified restaurantId
            {
                restaurant = r;
            }
        }
        return restaurant;
    }

    /**
     * Fetches a reference to a copy of the existing restaurant with the matching restaurantId
     *
     * @param restaurantId a String representing the restaurant's ID
     *
     * @return A list of Reviews such that each review's restaurantId field
     *            is equals to the inputted restaurantId
     */
    public Restaurant getRestaurantCopyById(String restaurantId) {
        Restaurant restaurant = null;
        for (Restaurant r : restaurants) // iterates over all of the restaurants
        {
            if (r.getId().equals(restaurantId)) // checks if the current restaurant has the specified restaurantId
            {
                restaurant = r;
            }
        }
        return new Restaurant(restaurant);
    }

    /**
     * gets the user with the inputted userId
     *
     * @param userId is a non-null String
     *
     * @return a User such that the user's userId field is equal to the inputted userId
     */
    private User getUserById(String userId)
    {
        User user = null;
        for (User u : users) // iterates over all of the users
        {
            if (u.getUserId().equals(userId)) // checks if the current user is the specified user
            {
                user = u;
            }
        }
        return user;
    }

    /**
     * Fetches a reference to a copy of the user with the given userId
     *
     * @param userId is a non-null String
     *
     * @return a User such that the user's userId field is equal to the inputted userId
     */
    public User getUserCopyById(String userId) {
        User user = null;
        for (User u : users) // iterates over all of the users
        {
            if (u.getUserId().equals(userId)) // checks if the current user is the specified user
            {
                user = u;
            }
        }
        return new User(user);
    }

    /**
     * gets the review with the inputted reviewId
     *
     * @param reviewId the ID of the desired review
     *
     * @return a Review such that the review's reviewId field is equal to the inputted reviewId
     */
    private Review getReviewById(String reviewId)
    {
        Review review = null;
        for (Review r : reviews) // iterates over all of the reviews
        {
            if (r.getReviewId().equals(reviewId)) // checks if the current review has the specified reviewId
            {
               review = r;
            }
        }
        return review;
    }

    /**
     * Fetches a reference a copy of the review with the specified reviewId
     *
     * @param reviewId the ID of the desired review
     *
     * @return a copy of the review with matching review ID
     */
    public Review getReviewCopyById(String reviewId) {
        Review review = null;
        for (Review r : reviews) // iterates over all of the reviews
        {
            if (r.getReviewId().equals(reviewId)) // checks if the current review has the specified reviewId
            {
                review = r;
            }
        }
        return new Review(review);
    }

    /**
     * Perform a structured query and return the set of objects that matches the
     * query
     *
     * @param queryString
     *
     * @return the set of objects that matches the query
     */
    @Override
    public Set getMatches(String queryString)
    {
        return null;
    }

    /**
     * Groups restaurants into k clusters such that each restaurant in the cluster is closer to the cluster's centre than any other cluster centre
     *
     * @param k number of clusters to create (0 < k <= number of objects)
     *
     * @return a list of set of restaurants, where each set represents a group of restaurants in the same cluster. The number of clusters is equal to k
     */
    public List<Set<Restaurant>> kMeansClusters_list(int k)
    {
        List<Coordinate> centroids = new ArrayList<>();
        double minLat = -90;
        double maxLat = 90;
        double minLong = -180;
        double maxLong = 180;
        boolean noCentroidChanges = false;

        //if k is 1, just return a set of all restaurants
        if(k == 1) {
            HashSet<Restaurant> restaurantSet = new HashSet<>(restaurants);
            List<Set<Restaurant>> clusteredRestaurants = new ArrayList<>();
            clusteredRestaurants.add(restaurantSet);
            return clusteredRestaurants;
        }

        //generate random centroids
        for(int i = 0; i < k; i++) {
            //note: the following 2 statements are based off of https://stackoverflow.com/questions/3680637/generate-a-random-double-in-a-range
            double x = ThreadLocalRandom.current().nextDouble(minLong, maxLong);
            double y = ThreadLocalRandom.current().nextDouble(minLat, maxLat);
            Coordinate randomCentroid = new Coordinate(x, y);
            centroids.add(randomCentroid);
        }

        Map<Restaurant, Coordinate> clusterMapping = new HashMap<>();

        //when centroid locations are not changed, we know we have finished
        while(!noCentroidChanges) {
            //1. group the restaurants into clusters
            for (Restaurant restaurant : restaurants) {
                //initialize clusterMapping to the first centroid if this is first the first run through this loop
                if (!clusterMapping.containsKey(restaurant)) {
                    clusterMapping.put(restaurant, centroids.get(0));
                }
                //get restaurant coordinates
                Coordinate restaurantCoordinate = new Coordinate(restaurant.getLongitude(), restaurant.getLatitude());
                //get distance between restaurant and centroid
                double minDistance = Coordinate.getDistanceBetweenPoints(restaurantCoordinate, clusterMapping.get(restaurant));
                //check each other centroid. If distance from restaurant to centroid is less than the current minDistance, remap the restaurant
                for (int i = 0; i < centroids.size(); i++) {
                    //don't need to recompute distance if restaurant already mapped to this centroid
                    if (!clusterMapping.get(restaurant).equals(centroids.get(i))) {
                        double curDistance = Coordinate.getDistanceBetweenPoints(restaurantCoordinate, centroids.get(i));
                        if (curDistance < minDistance) {
                            minDistance = curDistance;
                            clusterMapping.put(restaurant, centroids.get(i));
                        }
                    }
                }
            }

            //2. If empty clusters exist, take the largest cluster and map half of them to the empty cluster
            Set<Coordinate> centroidSet = new HashSet<> (clusterMapping.values());
            while(centroidSet.size() != k) {
                int maxClusterSize = 0;
                int largestClusterIndex = 0;

                //obtain the empty centroid
                Coordinate emptyCentroid = centroids.get(0);
                for(Coordinate centroid: centroids) {
                    if(!centroidSet.contains(centroid)) {
                        emptyCentroid = centroid;
                    }
                }

                //find index of largest cluster
                for(Coordinate centroid: centroids) {
                    int curSize = 0;
                    for (Restaurant restaurant : restaurants) {
                        if(clusterMapping.get(restaurant).equals(centroid)) {
                            curSize++;
                        }
                    }
                    if(curSize > maxClusterSize) {
                        maxClusterSize = curSize;
                        largestClusterIndex = centroids.indexOf(centroid);
                    }
                }

                //get restaurants mapped to this cluster
                List<Restaurant> largestClusterRestaurants = new ArrayList<>();
                for(Restaurant restaurant: restaurants) {
                    if(clusterMapping.get(restaurant).equals(centroids.get(largestClusterIndex))) {
                        largestClusterRestaurants.add(restaurant);
                    }
                }

                //map half of these restaurants to the empty cluster
                for(int i = 0; i < maxClusterSize / 2; i++) {
                    Restaurant curRestaurant = largestClusterRestaurants.get(i);
                    clusterMapping.replace(curRestaurant, emptyCentroid);
                }

                //update centroidSet
                centroidSet = new HashSet<> (clusterMapping.values());
            }

            noCentroidChanges = true;
            //3. compute new centroid for each cluster based on average location of restaurants in the cluster
            for (int i = 0; i < centroids.size(); i++) {
                //obtain all restaurants mapped to centroids.get(i)
                Set<Restaurant> restaurantByCentroid = new HashSet<>();
                for(Restaurant restaurant: restaurants) {
                    if(clusterMapping.get(restaurant).equals(centroids.get(i))) {
                        restaurantByCentroid.add(restaurant);
                    }
                }

                //centroids shouldn't be empty, but check just in case
                if(!restaurantByCentroid.isEmpty()) {
                    //compute average location for each restaurant in the cluster
                    int numRestaurants = 0;
                    double averageX = 0;
                    double averageY = 0;
                    for (Restaurant restaurant : restaurantByCentroid) {
                        averageX += restaurant.getLongitude();
                        averageY += restaurant.getLatitude();
                        numRestaurants++;
                    }
                    averageX = averageX / numRestaurants;
                    averageY = averageY / numRestaurants;

                    //if average location is new, update the centroid location and set noCentroidChanges flag
                    if (averageX != centroids.get(i).getX() || averageY != centroids.get(i).getY()) {
                        centroids.get(i).setX(averageX);
                        centroids.get(i).setY(averageY);
                        noCentroidChanges = false;
                    }
                }
            }
        }

        //fill set list of sets with each cluster (one set per cluster)
        List<Set<Restaurant>> clusteredRestaurants = new ArrayList<>();
        for(Coordinate centroid: centroids) {
            HashSet<Restaurant> restaurantByCentroid = new HashSet<>();
            for(Restaurant restaurant: restaurants) {
                if(clusterMapping.get(restaurant).equals(centroid)) {
                    restaurantByCentroid.add(restaurant);
                }
            }
            clusteredRestaurants.add(restaurantByCentroid);
        }
        return clusteredRestaurants;
    }

    /**
     * Cluster objects into k clusters using k-means clustering
     *
     * @param k number of clusters to create (0 < k <= number of objects)
     *
     * @return a String, in JSON format, that represents the clusters
     */
    @Override
    public String kMeansClusters_json(int k)
    {
       List<Set<Restaurant>> clusteredRestaurants = kMeansClusters_list(k);
       return convertListToJson(clusteredRestaurants);
    }

    /**
     * Generates a function for the inputted user using their past reviews to predict their rating of a restaurant
     *
     * @param user is a non-null String
     *
     * @return a ToDoubleBiFunction that takes a MP5Db and String as arguments that predicts the inputted users rating
     *             of the restaurant whose businessId is represented by the String input
     */
    @Override
    public ToDoubleBiFunction<MP5Db, String> getPredictorFunction(String user)
    {
        int j;
        double Sxx = 0, Syy = 0, Sxy = 0, meanx, meany, a, b, r_squared;
        List<Review> reviews = getReviewsByUserId(user); // list of all reviews left by the specified user
        List<Integer> ratings = new ArrayList<>(); // list of all the users ratings
        List<Integer> prices = new ArrayList<>(); // list of the prices of the restaurants the user reviewed
        for (Review r : reviews) // iterates over all of the user's reviews
        {
            ratings.add(r.getStars()); // adds the current reviews star rating to the list of ratings
            prices.add(getRestaurantById(r.getBusinessId()).getPrice()); // adds the current review's restaurant's price to the list of prices
        }
        int sum = 0;
        for (Integer i : prices) // iterates over the prices
        {
            sum += i; // adds up all of the prices
        }
        meanx = sum / prices.size(); // computes the average price
        sum = 0;
        for (Integer i : ratings) // iterates over all the ratings
        {
            sum += i; // adds up all of the ratings
        }
        meany = sum / ratings.size(); // computes the average rating
        for (Integer i : prices) // iterates over all the prices
        {
            Sxx += Math.pow(i - meanx, 2); // computes the sum of the squares of the current element minus the average price
        }
        for (Integer i : ratings) // iterates over all of the ratings
        {
            Syy += Math.pow(i - meany, 2); // computes the sum of the squares of the current element minus the average rating
        }
        for (j = 0; j < prices.size(); j++) // iterates over the lists
        {
            Sxy += (prices.get(j) - meanx) * (ratings.get(j) - meany); // computes the sum of the current price minus the average price times the current rating minus the average rating
        }
        b = Sxy / Sxx; // computes the regression coefficient b
        a = meany - (b * meanx); // computes the regression coefficient a
        if (Sxx == 0 || Syy == 0) // throws a runtime exception if the predictor function will end up dividing by zero
        {
            throw new RuntimeException();
        }
        ToDoubleBiFunction<MP5Db, String> predictRatings = (DB, restID) -> // creates the prediction function
        {
            YelpDB database = (YelpDB) DB;
            int x = database.getRestaurantById(restID).getPrice();
            double y = a + (b * x);
            if (y < 1) // if the result is less than 1, set it to 1
            {
                y = 1;
            }
            else if (y > 5) // if the result is greater than 5, set it to 5
            {
                y = 5;
            }
            return y;
        };
        return predictRatings; // returns the function
    }

    /**
     * converts a list of sets of Restaurants to a String in Json format (suitable for voroni.json)
     *
     * @param clusters is a non null List of Sets of Restaurants
     *
     * @return a String that is a json representation of the restaurants in the List of Sets
     */
    private static String convertListToJson(List<Set<Restaurant>> clusters)
    {
        int i;
        List<JsonObject> entries = new ArrayList<>();
        for (i = 0; i < clusters.size(); i++)
        {
            for (Restaurant r : clusters.get(i))
            {
                JsonObject entry = Json.createObjectBuilder().add("x", r.getLongitude()).add("y", r.getLatitude())
                                                             .add("name", r.getName()).add("cluster", i)
                                                             .add("weight", 5.0).build();
                entries.add(entry);
            }
        }
        String start = "[";
        List<String> result = new ArrayList<>();
        List<String> strings = new ArrayList<>();
        for (i = 0; i < entries.size(); i++)
        {
            if (i == 0)
            {
                String s = start.concat(entries.get(i).toString());
                strings.add(s.concat(", "));
            }
            else if (i == entries.size() - 1)
            {
                String s = strings.get(strings.size() - 1).concat(entries.get(i).toString());
                result.add(s.concat("]"));
                break;
            }
            else
            {
                String s = strings.get(strings.size() - 1).concat(entries.get(i).toString());
                strings.add(s.concat(", "));
            }
        }
        return result.get(0);
    }

    /**
     * converts a restaurant object to a string in json format
     *
     * @param restaurant is a non null Restaurant object
     *
     * @return a String that is a json representation of the restaurant
     */
    private static String convertRestaurantToJson(Restaurant restaurant)
    {
        String neighborhood = formatListForJson(restaurant.getNeighborhood());
        String categories = formatListForJson(restaurant.getCategories());
        String schools = formatListForJson(restaurant.getSchools());
        JsonObject entry = Json.createObjectBuilder().add("open", restaurant.getOpen()).add("url", restaurant.getUrl())
                                                     .add("longitude", restaurant.getLongitude()).add("neighborhoods", neighborhood)
                                                     .add("business_id", restaurant.getId()).add("name", restaurant.getName())
                                                     .add("categories", categories).add("state", restaurant.getState())
                                                     .add("type", restaurant.getType()).add("stars", restaurant.getRating())
                                                     .add("city", restaurant.getCity()).add("full_address", restaurant.getFullAddress())
                                                     .add("review_count", restaurant.getReviewCount()).add("photo_url", restaurant.getPhotoUrl())
                                                     .add("schools", schools).add("latitude", restaurant.getLatitude()).add("price", restaurant.getPrice()).build();
        String s = entry.toString();
        int neighborhoodsLength = 15; // converts string to proper json format
        int categoriesLength = 12;
        int schoolsLength = 9;
        int i1 = s.indexOf("\"neighborhoods\":");
        String s1 = s.substring(0, i1 + neighborhoodsLength + 1);
        int i2 = s.indexOf("]");
        String s2 = s.substring(i1 + neighborhoodsLength + 2, i2 + 1);
        int i3 = s.indexOf("\"categories\":");
        String s3 = s.substring(i2 + 2, i3 + categoriesLength + 1);
        int i4 = s.indexOf("]", i3);
        String s4 = s.substring(i3 + categoriesLength + 2, i4 + 1);
        int i5 = s.indexOf("\"schools\":");
        String s5 = s.substring(i4 + 2, i5 + schoolsLength + 1);
        int i6 = s.indexOf("]", i5);
        String s6 = s.substring(i5 + schoolsLength + 2, i6 + 1);
        int i7 = s.indexOf("}");
        String s7 = s.substring(i6 + 2, i7 + 1);
        String s12 = s1.concat(s2);
        String s123 = s12.concat(s3);
        String s1234 = s123.concat(s4);
        String s12345 = s1234.concat(s5);
        String s123456 = s12345.concat(s6);
        String result = s123456.concat(s7);
        int i8 = result.indexOf("full_address");
        String s8 = result.substring(0, i8 - 1);
        String s9 = result.substring(i8 - 1, result.indexOf(",", i8));
        String s10 = result.substring(result.indexOf(",", i8), result.indexOf("}") + 1);
        String s11 = s8.replaceAll("\\\\", "");
        String s13 = s10.replaceAll("\\\\", "");
        String s119 = s11.concat(s9);
        return s119.concat(s13);
    }

    private static String formatListForJson(List<String> list)
    {
        int count = 0;
        String q = "\"";
        String a = list.toString();
        String a1 = a.substring(0, 1);
        List<String> result = new ArrayList<>();
        String f1 = a1.concat(q);
        if (list.size() == 1)
        {
            int i1 = a.indexOf("]");
            String a2 = a.substring(1, i1);
            String a3 = a.substring(i1, i1 + 1);
            String f2 = f1.concat(a2);
            String f3 = f2.concat(q);
            result.add(f3.concat(a3));
        }
        else
        {
            result.add(a1);
            for (int i = 0; i < list.size() - 1; i++)
            {
                int i1 = a.indexOf(",", result.get(result.size() - 1).length());
                String a2 = a.substring(result.get(result.size() - 1).length() - i, i1);
                String f2 = q.concat(a2);
                String f3 = result.get(result.size() - 1).concat(f2);
                String f4 = f3.concat(q);
                String f5 = f4.concat(",");
                result.add(f5);
                count = i;
            }
            int i1 = a.indexOf("]");
            String a2 = a.substring(result.get(result.size() - 1).length() - count - 1, i1);
            String f2 = q.concat(a2);
            String f3 = result.get(result.size() - 1).concat(f2);
            String f4 = f3.concat(q);
            String f5 = f4.concat("]");
            result.add(f5);
        }
        return result.get(result.size() - 1);
    }

    /**
     * creates a new Restaurant and adds it to the set of restaurants
     *
     * @param url is a non null String
     *
     * @param businessId is a non null String
     *
     * @param name is a non null String
     *
     * @param state is a non null String
     *
     * @param type is a non null String
     *
     * @param city is a non null String
     *
     * @param fullAddress is a non null String
     *
     * @param photoUrl is a non null String
     *
     * @param longitude is a double value between -180 and 180
     *
     * @param latitude is a double value between -90 and 90
     *
     * @param stars is a double value between 0 and 5
     *
     * @param reviewCount is an int value
     *
     * @param price is an int value between 1 and 5
     *
     * @param neighborhood is a non null List of Strings
     *
     * @param schools is a non null List of Strings
     *
     * @param categories is a non null List of Strings
     *
     * @param open is a boolean value
     *
     * effects: creates a Restaurant Object and adds it to the set of restaurants
     */
    public void addRestaurant(String url, String businessId, String name, String state, String type, String city, String fullAddress, String photoUrl,
                              double longitude, double latitude, double stars, int reviewCount, int price, List<String> neighborhood,
                              List<String> schools, List<String> categories, boolean open)
    {
        Restaurant restaurant = new Restaurant(url, businessId, name, state, type, city, fullAddress, photoUrl, longitude, // creates a new restaurant
                latitude, stars, reviewCount, price, neighborhood, schools, categories, open);
        restaurants.add(restaurant); // add the restaurant to the set of restaurants
    }

    /**
     * adds the input restaurant to the database
     *
     * @param restaurant a reference to a valid restaurant object
     */
    public void addRestaurant(Restaurant restaurant)
    {
        restaurants.add(restaurant);
    }

    /**
     * creates a new Users and adds it to the set of users
     *
     * @param url is a non null String
     *
     * @param type is a non null String
     *
     * @param userId is a non null String
     *
     * @param name is a non null String
     *
     * @param reviewCount is a int value
     *
     * @param averageStars is a double value between 0 and 5
     *
     * @param votes is a Map of Strings to Integers
     *
     * effects: creates a User Object and adds it to the set of users
     */
    public void addUser(String url, String type, String userId, String name, int reviewCount, double averageStars, Map<String, Integer> votes)
    {

        User user = new User(url, type, userId, name, reviewCount, averageStars, votes); // creates a new user
        users.add(user); // add the user to the set of users
    }

    /**
     * creates a new Review and adds it to the set of reviews
     *
     * @param type is a non null String
     *
     * @param businessId is a non null String
     *
     * @param reviewId is a non null String
     *
     * @param text is a non null String
     *
     * @param userId is a non null String
     *
     * @param date is a non nul String
     *
     * @param stars is an int value between 0 and 5
     *
     * @param votes is a Map of Strings to Integers
     *
     * effects: creates a Review Object and adds it to the set of reviews
     */
    public void addReview(String type, String businessId, String reviewId, String text, String userId, String date, int stars, Map<String, Integer> votes)
    {
        Review review = new Review( type, businessId, reviewId, text, userId, date, stars, votes); // creates a new review
        reviews.add(review); // adds the review to the set of reviews
    }

    /**
     * creates all Restaurant, User and Review Objects from the instances file
     *
     */
    public void addAllFromFile()
    {
        addAllRestaurants();
        addAllReviews();
        addAllUsers();
    }

    /**
     * parses the json restaurants file
     *
     */
    private void addAllRestaurants()
    {
        int i;
        try
        {
            File file = new File(restaurantFile); // creates a file using the restaurant File
            FileReader reader = new FileReader(file); // creates a reader for the restaurant file
            BufferedReader bufferedReader = new BufferedReader(reader); // creates a buffered reader to reader the restaurant file line by line
            String line;
            while ((line = bufferedReader.readLine()) != null) // iterate until there are no lines left in the restaurant file
            {
                boolean array = false;
                List<String> stringVars = new ArrayList<>(); // list to hold all of the String arguments for the Restaurant constructor
                List<String> intVarNames = new ArrayList<>(Arrays.asList("review_count", "price")); // list to hold the names of the integer arguments
                List<Integer> intVars = new ArrayList<>(); // list to hold the integer arguments for the Restaurant constructor
                List<String> doubleVarNames = new ArrayList<>(Arrays.asList("longitude", "latitude", "stars")); // list to hold the names of the double arguments
                List<Double> doubleVars = new ArrayList<>(); // list to hold the double arguments for the Restaurant constructor
                List<List<String>> lists = new ArrayList<>(); // list to hold the list arguments for the Restaurant constructor
                List<String> booleanVarValues = new ArrayList<>(); // list to hold the String representations of the boolean arguments for the Restaurant Constructor
                List<String> keys = new ArrayList<>(); // list to hold the keys of the Json file
                JsonParser parser = Json.createParser(new StringReader(line)); // creates a json parser for the current line of the json file
                while (parser.hasNext()) // iterates over each element of the current line of the Json file
                {
                    JsonParser.Event event = parser.next(); // creates a json event and gives it the value of the current element of the json file
                    switch (event) // switches based on the current json event
                    {
                        case KEY_NAME: // if the event is a key
                        {
                            keys.add(parser.getString()); // add the key to the list of keys
                            break;
                        }
                        case VALUE_STRING: // if the event is a string value
                        {
                            if (array) // if the current element is in an array
                            {
                                lists.get(lists.size() - 1).add(parser.getString()); // add the string to the current list in the list of lists
                            }
                            else
                            {
                                stringVars.add(parser.getString()); // add the string to the list of string arguments
                            }
                            break;
                        }
                        case VALUE_TRUE: // if the event is true
                        {
                            booleanVarValues.add("true"); // add the string true to the list of string booleans
                            break;
                        }
                        case START_ARRAY: // if the event is the start of an array
                        {
                            array = true;
                            lists.add(new ArrayList<>()); // add a list to the list of lists
                            break;
                        }
                        case END_ARRAY: // if the event is the end of an array
                        {
                            array = false;
                            break;
                        }
                        case VALUE_NUMBER: // if the event is a number value
                        {
                            if (intVarNames.contains(keys.get(keys.size() - 1))) // if the previous key is in the list of integer argument names
                            {
                                intVars.add(parser.getInt()); // add the integer value to the list of integer arguments
                            }
                            else if (doubleVarNames.contains(keys.get(keys.size() - 1))) // if the previous key is in the list of double argument names
                            {
                                BigDecimal val = parser.getBigDecimal(); // create a BigDecimal to extract the number value from the event (cannot extract double values)
                                doubleVars.add(val.doubleValue()); // convert the BigDecimal into a double and add it to the list of double argument
                            }
                            break;
                        }
                        default: // if the current event is non of the above, do nothing
                        {}
                    }
                }
                boolean[] booleanVars = new boolean[booleanVarValues.size()]; // create a boolean array the size of the list of Sting booleans
                for (i = 0; i < booleanVarValues.size(); i++) // iterates over the list of boolean strings
                {
                     booleanVars[i] = true;
                }
                addRestaurant(stringVars.get(0), stringVars.get(1), stringVars.get(2), stringVars.get(3), stringVars.get(4), stringVars.get(5), // adds all of the arguments to the addRestaurants method
                        stringVars.get(6), stringVars.get(7), doubleVars.get(0), doubleVars.get(1), doubleVars.get(2), intVars.get(0),
                        intVars.get(1), lists.get(0), lists.get(1), lists.get(2), booleanVars[0]);
            }
        }
        catch(Exception e) // if any exceptions are thrown by the readers
        {
            System.out.println("You were my brother Anakin! I loved you! You were supposed to bring balance to the force, not leave it in darkness. (Restaurant parser error)");
        }
    }

    /**
     * parses the json user file
     *
     */
    private void addAllUsers()
    {
        try
        {
            File file = new File(userFile); // creates a file using the user file
            FileReader reader = new FileReader(file); // creates a reader for the user file
            BufferedReader bufferedReader = new BufferedReader(reader); // creates a buffered reader for the user file to read the file line by line
            String line;
            while ((line = bufferedReader.readLine()) != null) // iterates over each line of the user file
            {
                int i = 0;
                boolean array = false;
                List<String> stringVars = new ArrayList<>(); // list to store all of the string arguments for the User constructor
                List<String> intVarNames = new ArrayList<>(Arrays.asList("review_count")); // list of all the names of the integer arguments
                List<Integer> intVars = new ArrayList<>(); // list of the integer arguments for the User constructor
                List<String> doubleVarNames = new ArrayList<>(Arrays.asList("average_stars")); // list of all the names of the double arguments
                List<Double> doubleVars = new ArrayList<>(); // list of the double arguments for the User constructor
                List<Map<String, Integer>> maps = new ArrayList<>(); // list of all the map arguments for the user constructor
                List<String> keys = new ArrayList<>(); // list to hold all of the keys of the json file
                JsonParser parser = Json.createParser(new StringReader(line)); // creates the parser for the current lin of the json file
                while (parser.hasNext()) // iterates over each element of the current line of the json file
                {
                    JsonParser.Event event = parser.next(); // creates a new json event and initializes it to the current element of the json file
                    switch (event) // switches based on the current event
                    {
                        case KEY_NAME: // if the event is a key
                        {
                            keys.add(parser.getString()); // add the key to the list of keys
                            break;
                        }
                        case VALUE_STRING: // if the event is a string value
                        {
                            stringVars.add(parser.getString()); // add a string to the list of string argument
                            break;
                        }
                        case START_OBJECT: // if the event is the start of an object
                        {
                            i++; // ensures that the START_OBJECT event at the beginning of the line doesn't trigger a map creation
                            if (i > 1)
                            {
                                array = true;
                                maps.add(new HashMap<>()); // adds a new map to the list of maps
                            }
                            break;
                        }
                        case END_OBJECT: // if the event is the end of an object
                        {
                            array = false;
                            break;
                        }
                        case VALUE_NUMBER: // if the event is a number value
                        {
                            if (array) // if the number is in an array
                            {
                                maps.get(maps.size() - 1).put(keys.get(keys.size() - 1), parser.getInt()); // add the previous key and the number value to the current map
                            }
                            else if (intVarNames.contains(keys.get(keys.size() - 1))) // if the previous key is in the list of integer argument names
                            {
                                intVars.add(parser.getInt()); // add the integer value to the list of integer argument
                            }
                            else if (doubleVarNames.contains(keys.get(keys.size() - 1))) // if the previous hey is in the list of double argument names
                            {
                                BigDecimal val = parser.getBigDecimal(); // create a BigDecimal to extract the number value from the event (cannot extract double values)
                                doubleVars.add(val.doubleValue()); // convert the BigDecimal into a double and add it to the list of double argument
                            }
                            break;
                        }
                        default: // if the event is not one of the above
                        {}
                    }
                }
                addUser(stringVars.get(0), stringVars.get(1), stringVars.get(2), stringVars.get(3), intVars.get(0), doubleVars.get(0), maps.get(0)); // calls the addUser method with the appropriate arguments
            }
        }
        catch(Exception e) // if an exception is thrown
        {
            System.out.println( "I don't like sand. It’s coarse and rough and irritating - not like you. You’re soft and smooth. (User parser error)");
        }
    }

    /**
     * parses the json review file
     *
     */
    private void addAllReviews()
    {
        try
        {
            File file = new File(reviewFile); // creates a file for the review file
            FileReader reader = new FileReader(file); // creates a reader for the review file
            BufferedReader bufferedReader = new BufferedReader(reader); // creates a buffered reader to read the review file one line at a time
            String line;
            while ((line = bufferedReader.readLine()) != null) // iterates over each line of the json file
            {
                int i = 0;
                boolean array = false;
                List<String> stringVars = new ArrayList<>(); // list of the string arguments for the Review constructor
                List<Integer> intVars = new ArrayList<>(); // list of the integer arguments
                List<Map<String, Integer>> maps = new ArrayList<>(); // list of the map arguments for the Review constructor
                List<String> keys = new ArrayList<>(); // list of the keys of the json file
                JsonParser parser = Json.createParser(new StringReader(line)); // create a parser for the current line of the json file
                while (parser.hasNext()) // iterates over each element in current line of the json file
                {
                    JsonParser.Event event = parser.next(); // creates a json event and initializes it to the current element of the json parer
                    switch (event) // switches based on the json event
                    {
                        case KEY_NAME: // if the event is a key
                        {
                            keys.add(parser.getString()); // add the key to the list of keys
                            break;
                        }
                        case VALUE_STRING: // if the event is a string value
                        {
                            stringVars.add(parser.getString()); // add the string to the list of string arguments
                            break;
                        }
                        case START_OBJECT: // if the event is the start of an object
                        {
                            i++; // ensures that the START_OBJECT event generated by the start of the line is ignored
                            if (i > 1)
                            {
                                array = true;
                                maps.add(new HashMap<>()); // adds a new map to the list of map arguments
                            }
                            break;
                        }
                        case END_OBJECT: // if the event is the end of an object
                        {
                            array = false;
                            break;
                        }
                        case VALUE_NUMBER: // if the event is a number value
                        {
                            if (array) // if the number is in an array
                            {
                                maps.get(maps.size() - 1).put(keys.get(keys.size() - 1), parser.getInt()); // adds the previous key and the number value to the current map
                            }
                            else
                            {
                                intVars.add(parser.getInt()); // adds the number value to the list of integer values
                            }
                            break;
                        }
                        default: // if the event is not any of the above, do nothing
                        {}
                    }
                }
                addReview(stringVars.get(0), stringVars.get(1), stringVars.get(2), stringVars.get(3), stringVars.get(4), stringVars.get(5), intVars.get(0), maps.get(0)); // calls the addReview method with the appropriate arguments
            }
        }
        catch(Exception e)
        {
            System.out.println("I slaughtered them... and not just the men, but the women, and the children! (Review parser error)");
        }
    }

    /**
     * Searches the database for the restaurant with a matching business id and returns a json String representing that restaurant
     *
     * @param businessId
     * @return a String representing the restaurant in json format
     */
    public String getRestaurant(String businessId) {
        Restaurant restaurant = getRestaurantById(businessId);
        if (restaurant == null)
        {
            return "ERR: NO_SUCH_RESTAURANT";
        }
        String result = convertRestaurantToJson(restaurant);
        int i8 = result.indexOf("full_address");
        String s8 = result.substring(0, i8 - 1);
        String s9 = result.substring(i8 - 1, result.indexOf(",", i8));
        String s10 = result.substring(result.indexOf(",", i8), result.indexOf("}") + 1);
        String s11 = s8.replaceAll("\\\\", "");
        String s13 = s10.replaceAll("\\\\", "");
        String s119 = s11.concat(s9);
        return s119.concat(s13);
    }

    /**
     * Adds a new user to the database
     *
     * @param userInfo, a String representing the name of the user in Json format, along with optionally additional content (which will be ignored)
     * @return a complete String representing the user, in Json format
     */
    public synchronized String addUser(String userInfo) {
        String name = new String();
        try
        {
            List<String> keys = new ArrayList<>(); // list of the keys of the json file
            JsonParser parser = Json.createParser(new StringReader(userInfo)); // create a parser for the current line of the json file
            while (parser.hasNext()) // iterates over each element in current line of the json file
            {
                JsonParser.Event event = parser.next(); // creates a json event and initializes it to the current element of the json parer
                switch (event) // switches based on the json event
                {
                    case KEY_NAME: // if the event is a key
                    {
                        keys.add(parser.getString()); // add the key to the list of keys
                        break;
                    }
                    case VALUE_STRING: // if the event is a string value
                    {
                        if (keys.get(keys.size() - 1).equals("name")) { // get the name
                            name = parser.getString();
                        } // get the name
                        break;
                    }
                    default: // if the event is not any of the above, do nothing
                    {
                    }
                }
            }
        }
        catch (Exception e)
        {
            return "ERR: INVALID_USER_STRING";
        }
        Map<String, Integer> votes = new HashMap<>();
        votes.put("funny", 0);
        votes.put("useful", 0);
        votes.put("cool", 0);
        Integer userId = newUserId;
        String url = new String("http://www.yelp.com/user_details?userid=" + userId);
        newUserId++;
        addUser(url, "user", userId.toString(), name, 0, 0.0, votes);

        JsonObject json = Json.createObjectBuilder().add("url", url).add("votes", Json.createObjectBuilder().add("funny", 0)
                .add("useful", 0).add("cool", 0).build()).add("review_count", 0).add("type", "user").add("user_id", userId.toString())
                .add("name", name).add("average_stars", 0.0).build();
        return json.toString();
    }

    /**
     * Gets a reference to the first user object with the specified userName
     *
     * @param userName the name of the user
     * @return a reference to the first user object with the specified userName. If there are no users with the specified name, throw an exception
     */
    public User getUserObject(String userName) throws IOException {
        for(User user: users) {
            if(user.getName().equals(userName)) {
                return new User(user);
            }
        }
        throw new IOException();
    }

    /**
     * Check if a restaurant exists in the database
     *
     * @param restaurantId a String representing a the businessId of the restaurant
     * @return true if the restaurant is found in the database; otherwise, returns false
     */
    public boolean containsRestaurant(String restaurantId) {
        for(Restaurant DBrestaurant: restaurants) {
            if(DBrestaurant.getId().equals(restaurantId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a user with the given name exists in the database. Two users can have the same name as long as their userId is unique
     *
     * @param name a String representing the name of the user
     * @return true if the user is found in the database; otherwise, returns false
     */
    public boolean containsUserName(String name) {
        for(User user: users) {
            if(user.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add a new restaurant to the database
     *
     * @param restaurantInfo a String representing valid restaurant information
     * @return a complete String representing the restaurant in json format
     */
    public synchronized String addRestaurant(String restaurantInfo) {
        Map<String, Object> jsonFields = new HashMap<>();
        Stack jsonStack = new Stack<String>();
        Queue arrayQueue = new LinkedList();
        boolean array = false;
        try {
            JsonParser parser = Json.createParser(new StringReader(restaurantInfo)); // creates a json parser for the current line of the json file
            while (parser.hasNext()) // iterates over each element of the current line of the Json file
            {
                JsonParser.Event event = parser.next(); // creates a json event and gives it the value of the current element of the json file
                switch (event) // switches based on the current json event
                {
                    case KEY_NAME: // if the event is a key
                    {
                        String test = parser.getString();
                        jsonStack.add(parser.getString()); // add the key to the list of keys
                        break;
                    }
                    case VALUE_STRING: // if the event is a string value
                    {
                        if (array) // if the current element is in an array
                        {
                            arrayQueue.add(parser.getString()); // add the string to the current list in the list of lists
                        }
                        else
                        {
                            if(!jsonStack.isEmpty()) jsonFields.put((String) jsonStack.pop(), parser.getString()); // add the string to the list of string arguments
                        }
                        break;
                    }
                    case VALUE_TRUE: // if the event is true
                    {
                        if(!jsonStack.isEmpty()) jsonFields.put((String) jsonStack.pop(), true); // add the string true to the list of string booleans
                        break;
                    }
                    case VALUE_FALSE:
                    {
                        if(!jsonStack.isEmpty()) jsonFields.put((String) jsonStack.pop(), false);
                        break;
                    }
                    case START_ARRAY: // if the event is the start of an array
                    {
                        array = true;
                        break;
                    }
                    case END_ARRAY: // if the event is the end of an array
                    {
                        List<Object> jsonArray = new ArrayList<>();
                        while(!arrayQueue.isEmpty()) {
                            jsonArray.add(arrayQueue.poll());
                        }
                        if(!jsonStack.isEmpty()) jsonFields.put((String) jsonStack.pop(), jsonArray);
                        array = false;
                        break;
                    }
                    case VALUE_NUMBER: // if the event is a number value
                    {
                        if(jsonStack.peek().equals("price") || jsonStack.peek().equals("review_count")) {
                            if(!jsonStack.isEmpty()) jsonFields.put((String) jsonStack.pop(), new Integer(parser.getInt()));
                        }
                        else if(!jsonStack.isEmpty()) jsonFields.put((String) jsonStack.pop(), parser.getBigDecimal().doubleValue());
                    }
                    default: // if the current event is none of the above, do nothing
                    {}
                }
            }
        }
        catch(Exception e) {
            return "ERR: INVALID_RESTAURANT_STRING";
        }
        //send an error message if the add request does not contain the required fields
        if(!jsonFields.containsKey("name") || !jsonFields.containsKey("state") || !jsonFields.containsKey("city") || !jsonFields.containsKey("full_address") || !jsonFields.containsKey("longitude") || !jsonFields.containsKey("latitude") || !jsonFields.containsKey("price") || !jsonFields.containsKey("neighborhoods") || !jsonFields.containsKey("schools") || !jsonFields.containsKey("categories")) {
            return "ERR: INVALID_RESTAURANT_STRING";
        }

        String url = "http://www.yelp.com/restaurant_details?restaurantid="+newRestaurantId;
        String businessId = ((Integer) newRestaurantId).toString();
        String type = "business";
        double stars = 0.0;
        String photoUrl = "http://www.yelp.com/restaurant_details?restaurantid="+newRestaurantId+"/ms.jpg";
        int reviewCount = 0;
        boolean open;
        newRestaurantId++;

        //if "open" field was included, use that as the open status
        if(jsonFields.containsKey("open")) {
            open = (boolean) jsonFields.get("open");
        }
        //otherwise assume the business is open
        else
        {
            open = true;
        }

        Restaurant restaurant = new Restaurant(url, businessId, (String) jsonFields.get("name"), (String) jsonFields.get("state"), type, (String) jsonFields.get("city"), (String) jsonFields.get("full_address"), photoUrl, (double) jsonFields.get("longitude"), stars, (double) jsonFields.get("latitude"), reviewCount, (int) jsonFields.get("price"), (List<String>) jsonFields.get("neighborhoods"), (List<String>) jsonFields.get("categories"), (List<String>) jsonFields.get("schools"), open);
        addRestaurant(restaurant);
        return convertRestaurantToJson(restaurant);
    }

    /**
     * Add a new review to the database
     *
     * @param reviewInfo a String representing valid review information
     * @return a complete String representing the review in json format
     */
    public synchronized String addReview(String reviewInfo)
    {
        try
        {
            int i = 0;
            boolean array = false;
            Map<String, String> StringVars = new HashMap<>();
            List<Integer> intVars = new ArrayList<>(); // list of the integer arguments
            List<Map<String, Integer>> maps = new ArrayList<>(); // list of the map arguments for the Review constructor
            List<String> keys = new ArrayList<>(); // list of the keys of the json file
            JsonParser parser = Json.createParser(new StringReader(reviewInfo)); // create a parser for the current line of the json file
            while (parser.hasNext()) // iterates over each element in current line of the json file
            {
                JsonParser.Event event = parser.next(); // creates a json event and initializes it to the current element of the json parer
                switch (event) // switches based on the json event
                {
                    case KEY_NAME: // if the event is a key
                    {
                        keys.add(parser.getString()); // add the key to the list of keys
                        break;
                    }
                    case VALUE_STRING: // if the event is a string value
                    {
                        switch(keys.get(keys.size() - 1)) // switches based on the most recent key event
                        {
                            case "business_id":
                            {
                                StringVars.put("business_id", parser.getString());
                                break;
                            }
                            case "date":
                            {
                                StringVars.put("date", parser.getString());
                                break;
                            }
                            case "user_id":
                            {
                                StringVars.put("user_id", parser.getString());
                                break;
                            }
                            case "text":
                            {
                                StringVars.put("text", parser.getString());
                                break;
                            }
                            default:
                            {
                                break;
                            }
                        }
                        break;
                    }
                    case START_OBJECT: // if the event is the start of an object
                    {
                        i++; // ensures that the START_OBJECT event generated by the start of the line is ignored
                        if (i > 1)
                        {
                            array = true;
                            maps.add(new HashMap<>()); // adds a new map to the list of map arguments
                        }
                        break;
                    }
                    case END_OBJECT: // if the event is the end of an object
                    {
                        array = false;
                        break;
                    }
                    case VALUE_NUMBER: // if the event is a number value
                    {
                        if (array) // if the number is in an array
                        {
                            maps.get(maps.size() - 1).put(keys.get(keys.size() - 1), parser.getInt()); // adds the previous key and the number value to the current map
                        }
                        else
                        {
                            intVars.add(parser.getInt()); // adds the number value to the list of integer values
                        }
                        break;
                    }
                    default: // if the event is not any of the above, do nothing
                    {}
                }
            }
            Integer intReviewId = newReviewId; // create a new review ID
            newReviewId++;
            String reviewId = intReviewId.toString();
            if (!validateRestaurant(StringVars.get("business_id"))) // check if the reviewed restaurant exists in the database
            {
                return "ERR: NO_SUCH_RESTAURANT";
            }
            if(!validateUser(StringVars.get("user_id"))) // check if the reviewing user exists in the database
            {
                return "ERR: NO_SUCH_USER";
            }
            addReview("review", StringVars.get("business_id"), reviewId, StringVars.get("text"), StringVars.get("user_id"), StringVars.get("date"), intVars.get(0), maps.get(0));
            JsonObject review = Json.createObjectBuilder().add("type", "review").add("business_id", StringVars.get("business_id"))
                                                          .add("votes", Json.createObjectBuilder().add("funny", maps.get(0).get("funny")).add("useful", maps.get(0).get("useful")).add("cool", maps.get(0).get("cool")).build())
                                                          .add("review_id", reviewId).add("text", StringVars.get("text")).add("stars", intVars.get(0))
                                                          .add("user_id", StringVars.get("user_id")).add("date", StringVars.get("date")).build();
            updateReviewData(getReviewById(reviewId)); // updates the review counts and ratings of the referenced user and business
            return review.toString();
        }
        catch(Exception e)
        {
            return "ERR: INVALID_REVIEW_STRING";
        }
    }

    /**
     * Updates fields of the restaurant and user associated with a review
     *
     * @param review is a review object
     */
    private void updateReviewData(Review review)
    {
        Restaurant restaurant = getRestaurantById(review.getBusinessId());
        restaurant.updateReviewCount(1); // adds one ot the review count of the reviewed restaurant
        User user = getUserById(review.getUserId());
        user.updateReviewCount(1); // adds one to the review count of the reviewing user
        int rSum = 0, rCount = 0, uSum = 0, uCount = 0;
        for (Review r : reviews) // iterates over all of the previous reviews and calculates the user's and business's average star rating
        {
            if (r.getBusinessId().equals(review.getBusinessId()))
            {
                rSum += r.getStars();
                rCount++;
            }
            if (r.getUserId().equals(review.getUserId()))
            {
                uSum += r.getStars();
                uCount++;
            }
        }
        double rating = rSum / rCount;
        user.updateAverageStars(uSum / uCount); // updates the users average star rating
        restaurant.updateRating(Math.round(rating * 2) / 2.0); // updates the business's average star rating, rounding to the nearest .5
    }

    /**
     * Checks if a restaurant ID is exists in the database
     *
     * @param businessId is a String representing a business_id
     */
    private boolean validateRestaurant(String businessId)
    {
        boolean valid = false;
        for (Restaurant r : restaurants)
        {
            if (r.getId().equals((businessId))) // checks if the inputted business ID matches any already in the database
            {
                valid = true;
                break;
            }
        }
        return valid;
    }

    /**
     * Checks if a user ID is exists in the database
     *
     * @param userId is a String representing a user_id
     */
    private boolean validateUser(String userId)
    {
        boolean valid = false;
        for (User u : users)
        {
            if (u.getUserId().equals((userId))) // checks if the inputted user ID matches any already in the database
            {
                valid = true;
                break;
            }
        }
        return valid;
    }
}

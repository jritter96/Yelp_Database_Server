package ca.ece.ubc.cpen221.mp5;

import ca.ece.ubc.cpen221.mp5.*;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.function.ToDoubleBiFunction;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) // forced order required to ensure the server thread is started up before any client threads
public class DataBaseTest
{
    @Test
    public void test1() // tests getPredictorFunction
    {
        YelpDB database = new YelpDB("data/restaurants.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        ToDoubleBiFunction<MP5Db, String> ratingPredictor = database.getPredictorFunction("fL8ujZ89qTyhbjr1Qz5aSg");
        double y = ratingPredictor.applyAsDouble(database, "6QZR4ToHKlse0yhqpU5ijg");
        assertEquals(1.0, y, 0.0000000000001);
    }

    @Test
    public void test11() // tests methods in business and restaurant
    {
        YelpDB database = new YelpDB("data/restaurants.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        Restaurant panchos = database.getRestaurantCopyById("7x0i0seshwLXBVAam16wuw");
        assertEquals("7x0i0seshwLXBVAam16wuw", panchos.getId());
        assertEquals("Pancho's", panchos.getName());
        assertEquals("business", panchos.getType());
        assertEquals(3.0, panchos.getRating(), 0.0000001);
        assertEquals(72, panchos.getReviewCount());
        List<String> categories = panchos.getCategories();
        assertEquals(3, categories.size());
        assertTrue(categories.contains("Food"));
        assertTrue(categories.contains("Mexican"));
        assertTrue(categories.contains("Restaurants"));
        assertEquals(2, panchos.getPrice());
        assertTrue(panchos.getOpen());
        Business panchosCopy = database.getRestaurantCopyById("7x0i0seshwLXBVAam16wuw");
        Business alborz = database.getRestaurantCopyById("HXni0_SFPT1jAoH-Sm78Jg");
        Business peppermintGrill = database.getRestaurantCopyById("FWadSZw0G7HsgKXq7gHTnw");
        assertEquals("Alborz", alborz.getName());
        assertEquals("Peppermint Grill", peppermintGrill.getName());
        assertTrue(panchos.equals(panchosCopy));
        assertFalse(panchos.equals(alborz));
        assertTrue(panchos.equals(panchos));
        assertFalse(peppermintGrill.equals(panchos));
        User user = database.getUserCopyById("wv24eKVAffKx3x40v_q-PQ");
        assertEquals("Omar A.", user.getName());
        assertFalse(panchos.equals(user));
        assertFalse(user.equals(panchos));
        assertEquals(panchos.hashCode(), panchosCopy.hashCode());
        assertEquals("http://www.yelp.com/biz/panchos-berkeley", panchos.getUrl());
        assertEquals("CA", panchos.getState());
        assertEquals("Berkeley", database.getRestaurantCopyById("7x0i0seshwLXBVAam16wuw").getCity());
        assertEquals("2521 Durant Ave\nSte B\nTelegraph Ave\nBerkeley, CA 94704", panchos.getFullAddress());
        assertEquals("http://s3-media2.ak.yelpcdn.com/bphoto/99A1-1AKURB1vyUOlYiEVQ/ms.jpg", panchos.getPhotoUrl());
        assertEquals(-122.2579783, panchos.getLongitude(), 0.0000000000000000000000001);
        assertEquals(37.8680781, panchos.getLatitude(), 0.0000000000000000000000001);
        List<String> panchosNeighborhoods = panchos.getNeighborhood();
        assertEquals(2, panchosNeighborhoods.size());
        assertTrue(panchosNeighborhoods.contains("Telegraph Ave"));
        assertTrue(panchosNeighborhoods.contains("UC Campus Area"));
        List<String> panchosSchools = panchos.getSchools();
        assertEquals(1, panchosSchools.size());
        assertTrue(panchosSchools.contains("University of California at Berkeley"));
        panchos.updateRating(2.5);
        panchos.updateReviewCount(1);
        assertEquals(2.5, panchos.getRating(), 0.0000001);
        assertEquals(73, panchos.getReviewCount());

    }
    @Test
    public void test12() // tests k means clustering

    {
        YelpDB database = new YelpDB("data/restaurants.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        Restaurant urbannTurbann = database.getRestaurantCopyById("-t6JanEk_V6VzctlTyr6eA");
        Restaurant zachsSnacks = database.getRestaurantCopyById("qjJDyQSoU4QgtSIyRkAZ6Q");
        assertEquals("Urbann Turbann", urbannTurbann.getName());
        assertEquals("Zach's Snacks", zachsSnacks.getName());
        Coordinate urbannTurbannLocation = new Coordinate(urbannTurbann.getLongitude(), urbannTurbann.getLatitude());
        Coordinate zachsSnacksLocation = new Coordinate(zachsSnacks.getLongitude(), zachsSnacks.getLatitude());
        assertFalse(urbannTurbannLocation.equals(zachsSnacksLocation));
        assertFalse(zachsSnacksLocation.equals(urbannTurbannLocation));
        Integer five = new Integer(5);
        assertFalse(urbannTurbannLocation.equals(five));
    }

    @Test
    public void test13() // tests k-means clustering
    {
        YelpDB database = new YelpDB("data/restaurants.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        int numClusters = 10;
        List<Set<Restaurant>> clusteredRestaurants = database.kMeansClusters_list(numClusters);
        List<Coordinate> centroids = new ArrayList<>();
        Map<Restaurant, Coordinate> clusterMappings = new HashMap<>();

        //get all cluster centroids and map restaurants too their centroid
        for (Set<Restaurant> cluster : clusteredRestaurants) {
            if (!cluster.isEmpty()) {
                double averageX = 0;
                double averageY = 0;
                int numRestaurants = 0;
                //get average position of all restaurants in a cluster and find the centroid
                for (Restaurant restaurant : cluster) {
                    averageX += restaurant.getLongitude();
                    averageY += restaurant.getLatitude();
                    numRestaurants++;
                }
                averageX = averageX / numRestaurants;
                averageY = averageY / numRestaurants;
                Coordinate centroid = new Coordinate(averageX, averageY);
                centroids.add(centroid);
                //map all the restaurants in this set to this centroid
                for (Restaurant restaurant : cluster) {
                    clusterMappings.put(restaurant, centroid);
                }
            }
        }

        //get all restaurants
        List<Restaurant> restaurants = new ArrayList<>();
        restaurants.addAll(clusterMappings.keySet());

        //check that each restaurant is closer to it's centroid than any other centroid
        for (Restaurant restaurant : restaurants) {
            Coordinate restaurantLocation = new Coordinate(restaurant.getLongitude(), restaurant.getLatitude());
            double minDistance = Coordinate.getDistanceBetweenPoints(restaurantLocation, clusterMappings.get(restaurant));
            for (Coordinate centroid : centroids) {
                if (!centroid.equals(clusterMappings.get(restaurant))) {
                    double otherDistance = Coordinate.getDistanceBetweenPoints(restaurantLocation, centroid);
                    assertTrue(minDistance < otherDistance);
                }
            }
        }

        //assert no empty clusters
        assertTrue(clusteredRestaurants.size() == numClusters);
        for (Set<Restaurant> cluster : clusteredRestaurants) {
            assertFalse(cluster.isEmpty());
        }
    }

    @Test
    public void test14() // tests that getPredictorFunction throws a runtime exception when the user isn't good
    {
        boolean exception = false;
        YelpDB database = new YelpDB("data/restaurants.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        try
        {
            ToDoubleBiFunction<MP5Db, String> ratingPredictor = database.getPredictorFunction("a_-YKfqF2gCghzblWciBcA");
        }
        catch(RuntimeException e)
        {
            exception = true;
        }
        assertTrue(exception);
    }

    @Test
    public void test15() // tests the methods in User
    {
        YelpDB database = new YelpDB("data/restaurants.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        User chester = database.getUserCopyById("ePerljemmmGHi-DAZ6ZELQ");
        assertEquals("ePerljemmmGHi-DAZ6ZELQ", chester.getUserId());
        assertEquals("Chester R.", chester.getName());
        assertEquals("user", chester.getType());
        assertEquals("http://www.yelp.com/user_details?userid=ePerljemmmGHi-DAZ6ZELQ", chester.getUrl());
        assertEquals(22, (int) chester.getVotes().get("funny"));
        assertEquals(28, (int) chester.getVotes().get("useful"));
        assertEquals(11, (int) chester.getVotes().get("cool"));
        assertEquals(68, chester.getReviewCount());
        assertEquals(3.39705882352941, chester.getAverageStars(), 0.00000000000001);
        chester.updateReviewCount(-1);
        chester.updateAverageStars(1.17343);
        assertEquals(67, chester.getReviewCount());
        assertEquals(1.17343, chester.getAverageStars(), 0.00000000000001);
        User anand = database.getUserCopyById("a_-YKfqF2gCghzblWciBcA");
        assertFalse(anand.equals(chester));
        assertFalse(chester.equals(3));
    }

    @Test
    public void test16() // tests the methods in Review
    {
        YelpDB database = new YelpDB("data/restaurants.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        Review r = database.getReviewCopyById("0f8QNSVSocn40zr1tSSGRw");
        assertEquals("wr3JF-LruJ9LBwQTuw7aUg", r.getUserId());
        assertEquals("1CBs84C-a-cuA3vncXVSAw", r.getBusinessId());
        assertEquals("review", r.getType());
        assertEquals("0f8QNSVSocn40zr1tSSGRw", r.getReviewId());
        assertEquals(0, (int) r.getVotes().get("funny"));
        assertEquals(0, (int) r.getVotes().get("useful"));
        assertEquals(0, (int) r.getVotes().get("cool"));
        assertEquals("Food here is very consistent and good for a quick lunch or dinner. I usually order the garlic bread and salad; however, their pizza, especially the bbq chicken, is pretty good too.", r.getText());
        assertEquals(4, r.getStars());
        assertEquals("2012-01-28", r.getDate());
        Review v = database.getReviewCopyById("mdmFPUq98OtP8BQ4fCs8wA");
        assertTrue(r.equals(r));
        assertFalse(r.equals(v));
        assertFalse(r.equals(4));
    }

    @Test
    public void test17() // tests the exception output generated by the restaurant parser
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        YelpDB database = new YelpDB("data/reviews.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        assertEquals("You were my brother Anakin! I loved you! You were supposed to bring balance to the force, not leave it in darkness. (Restaurant parser error)".replace("\n", "").replace("\r", ""), out.toString().replace("\n", "").replace("\r", ""));
    }

    @Test
    public void test18() // tests the exception output generated by the user parser
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        YelpDB database = new YelpDB("data/restaurants.json", "data/reviews.json", "data/restaurants.json");
        database.addAllFromFile();
        assertEquals("I don't like sand. It’s coarse and rough and irritating - not like you. You’re soft and smooth. (User parser error)".replace("\n", "").replace("\r", ""), out.toString().replace("\n", "").replace("\r", ""));
    }

    @Test
    public void test19() // tests the exception output generated by the review parser
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        YelpDB database = new YelpDB("data/restaurants.json", "data/users.json", "data/users.json");
        database.addAllFromFile();
        assertEquals( "I slaughtered them... and not just the men, but the women, and the children! (Review parser error)".replace("\n", "").replace("\r", ""), out.toString().replace("\n", "").replace("\r", ""));
    }

    @Test
    public void test2() // tests the getReviewsByID method
    {
        YelpDB database = new YelpDB("data/restaurants.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        List<Review> reviews = new ArrayList<>();
        reviews = database.getReviewsByUserId("ErJcnrBcG9-HXiB_gTp_zA");
        assertEquals(3, reviews.size());
    }

    @Test
    public void test21() // tests k-means clustering
    {
        YelpDB database = new YelpDB("data/restaurants.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        int numClusters = 1;
        List<Set<Restaurant>> clusteredRestaurants = database.kMeansClusters_list(numClusters);
        assertEquals(1, clusteredRestaurants.size());
        assertEquals(135, clusteredRestaurants.get(0).size());
    }

    @Test
    public void test22() // tests k-means json formatting
    {
        YelpDB database = new YelpDB("data/restaurants.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        String kMeansJson = database.kMeansClusters_json(10);
        assertEquals("[{\"x\":", kMeansJson.substring(0,6));
        assertEquals("}]", kMeansJson.substring(kMeansJson.length()-2, kMeansJson.length()));
    }

    @Test
    public void test23() // tests the getReviewsByBusinessID method
    {
        YelpDB database = new YelpDB("data/restaurants.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        List<Review> reviews = new ArrayList<>();
        reviews = database.getReviewsByBusinessId("1E2MQLWfwpsId185Fs2gWw");
        assertEquals(17396, reviews.size());
    }

    @Test
    public void test24() // tests getPredictorFunction with a result less than 1
    {
        YelpDB database = new YelpDB("data/restaurants.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        ToDoubleBiFunction<MP5Db, String> ratingPredictor = database.getPredictorFunction("QScfKdcxsa7t5qfE0Ev0Cw");
        double y = ratingPredictor.applyAsDouble(database, "gclB3ED6uk6viWlolSb_uA");
        assertEquals(1.0, y, 0.0000000000001);
    }

    @Test
    public void test25() // tests getRestaurant method
    {
        YelpDB database = new YelpDB("data/restaurants.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        String jsonRestaurant = database.getRestaurant("FWadSZw0G7HsgKXq7gHTnw");
        assertEquals("{\"open\": true, \"url\": \"http://www.yelp.com/biz/peppermint-grill-berkeley\", \"longitude\": -122.2598181, \"neighborhoods\": [\"UC Campus Area\"], \"business_id\": \"FWadSZw0G7HsgKXq7gHTnw\", \"name\": \"Peppermint Grill\", \"categories\": [\"American (Traditional)\", \"Restaurants\"], \"state\": \"CA\", \"type\": \"business\", \"stars\": 2.5, \"city\": \"Berkeley\", \"full_address\": \"2505 Hearst Ave\\nSte B\\nUC Campus Area\\nBerkeley, CA 94709\", \"review_count\": 16, \"photo_url\": \"http://s3-media1.ak.yelpcdn.com/assets/2/www/img/924a6444ca6c/gfx/blank_biz_medium.gif\", \"schools\": [\"University of California at Berkeley\"], \"latitude\": 37.8751965, \"price\": 2}".replaceAll("\\s+",""), jsonRestaurant.replaceAll("\\s+",""));
        assertTrue(jsonRestaurant.contains("[\"UC Campus Area\"]"));
        assertTrue(jsonRestaurant.contains("[\"American (Traditional)\",\"Restaurants\"]"));
        assertTrue(jsonRestaurant.contains("[\"University of California at Berkeley\"]"));
    }

    @Test
    public void test26() // tests getRestaurant method with invalid input
    {
        YelpDB database = new YelpDB("data/restaurants.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        String jsonRestaurant = database.getRestaurant("FWadSZw0G7HsgKXq7gHTn");
        String returnedString = database.getRestaurant(jsonRestaurant);
        assertEquals("ERR: NO_SUCH_RESTAURANT", returnedString);
    }

    @Test
    public void test27() //tests all methods in business
    {
        String jsonStringRestaurant = "{\"open\": false, \"url\": \"http://www.yelp.com/biz/peppermint-grill-berkeley\", \"longitude\": -122.2598181, \"neighborhoods\": [\"UC Campus Area\"], \"business_id\": \"FWadSZw0G7HsgKXq7gHTnw\", \"name\": \"Peppermint Grill\", \"categories\": [\"American (Traditional)\", \"Restaurants\"], \"state\": \"CA\", \"type\": \"business\", \"stars\": 2.5, \"city\": \"Berkeley\", \"full_address\": \"2505 Hearst Ave\\nSte B\\nUC Campus Area\\nBerkeley, CA 94709\", \"review_count\": 16, \"photo_url\": \"http://s3-media1.ak.yelpcdn.com/assets/2/www/img/924a6444ca6c/gfx/blank_biz_medium.gif\", \"schools\": [\"University of California at Berkeley\"], \"latitude\": 37.8751965, \"price\": 2}";
        YelpDB database = new YelpDB("data/small_restaurant_set.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        assertFalse(database.containsRestaurant("0"));
        String returnedString = database.addRestaurant(jsonStringRestaurant);
        assertTrue(database.containsRestaurant("0"));
        Restaurant peppermintGrill = database.getRestaurantCopyById("0");
        assertFalse(peppermintGrill.getOpen());
        assertEquals("http://www.yelp.com/restaurant_details?restaurantid=0", peppermintGrill.getUrl());
        assertEquals("0", peppermintGrill.getId());
        assertEquals("business", peppermintGrill.getType());
        assertEquals(0.0, peppermintGrill.getRating(), 00001);
        assertEquals("http://www.yelp.com/restaurant_details?restaurantid="+"0"+"/ms.jpg", peppermintGrill.getPhotoUrl());
        assertEquals(0, peppermintGrill.getReviewCount());
        assertEquals("Peppermint Grill", peppermintGrill.getName());
        assertEquals("CA", peppermintGrill.getState());
        assertEquals("Berkeley", peppermintGrill.getCity());
        assertEquals("2505 Hearst Ave\nSte B\nUC Campus Area\nBerkeley, CA 94709", peppermintGrill.getFullAddress());
        assertEquals(-122.2598181, peppermintGrill.getLongitude(), 0.00000000001);
        assertEquals(37.8751965, peppermintGrill.getLatitude(), 0.00000000000001);
        assertEquals(2, peppermintGrill.getPrice());
        assertEquals("[UC Campus Area]", peppermintGrill.getNeighborhood().toString());
        assertEquals("[University of California at Berkeley]", peppermintGrill.getSchools().toString());
        assertEquals("[American (Traditional), Restaurants]", peppermintGrill.getCategories().toString());
    }

    @Test
    public void test28() //tests addRestaurant method
    {
        String jsonStringRestaurant = "{\"longitude\": -122.2598181, \"neighborhoods\": [\"UC Campus Area\"], \"name\": \"Peppermint Grill\", \"categories\": [\"American (Traditional)\", \"Restaurants\"], \"state\": \"CA\", \"city\": \"Berkeley\", \"full_address\": \"2505 Hearst Ave\\nSte B\\nUC Campus Area\\nBerkeley, CA 94709\", \"schools\": [\"University of California at Berkeley\"], \"latitude\": 37.8751965, \"price\": 2}";
        YelpDB database = new YelpDB("data/small_restaurant_set.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        assertFalse(database.containsRestaurant("0"));
        String returnedString = database.addRestaurant(jsonStringRestaurant);
        assertTrue(database.containsRestaurant("0"));
        Restaurant peppermintGrill = database.getRestaurantCopyById("0");
        assertTrue(peppermintGrill.getOpen());
        assertEquals("http://www.yelp.com/restaurant_details?restaurantid=0", peppermintGrill.getUrl());
        assertEquals("0", peppermintGrill.getId());
        assertEquals("business", peppermintGrill.getType());
        assertEquals(0.0, peppermintGrill.getRating(), 00001);
        assertEquals("http://www.yelp.com/restaurant_details?restaurantid="+"0"+"/ms.jpg", peppermintGrill.getPhotoUrl());
        assertEquals(0, peppermintGrill.getReviewCount());
        assertEquals("Peppermint Grill", peppermintGrill.getName());
        assertEquals("CA", peppermintGrill.getState());
        assertEquals("Berkeley", peppermintGrill.getCity());
        assertEquals("2505 Hearst Ave\nSte B\nUC Campus Area\nBerkeley, CA 94709", peppermintGrill.getFullAddress());
        assertEquals(-122.2598181, peppermintGrill.getLongitude(), 0.00000000001);
        assertEquals(37.8751965, peppermintGrill.getLatitude(), 0.00000000000001);
        assertEquals(2, peppermintGrill.getPrice());
        assertEquals("[UC Campus Area]", peppermintGrill.getNeighborhood().toString());
        assertEquals("[University of California at Berkeley]", peppermintGrill.getSchools().toString());
        assertEquals("[American (Traditional), Restaurants]", peppermintGrill.getCategories().toString());
    }

    @Test
    public void test29() // tests addRestaurant method
    {
        String jsonStringRestaurant = "{\"open\": true, \"longitude\": -122.2598181, \"neighborhoods\": [\"UC Campus Area\"], \"name\": \"Peppermint Grill\", \"categories\": [\"American (Traditional)\", \"Restaurants\"], \"state\": \"CA\", \"city\": \"Berkeley\", \"full_address\": \"2505 Hearst Ave\\nSte B\\nUC Campus Area\\nBerkeley, CA 94709\", \"schools\": [\"University of California at Berkeley\"], \"latitude\": 37.8751965, \"price\": 2}";
        YelpDB database = new YelpDB("data/small_restaurant_set.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        String returnedString = database.addRestaurant(jsonStringRestaurant);
        Restaurant peppermintGrill = database.getRestaurantCopyById("0");
        assertTrue(peppermintGrill.getOpen());
    }

    @Test
    public void test3 () // tests addRestaurant method with invalid input
    {
        String jsonStringRestaurant = "{\"longitude\": -122.2598181, \"neighborhoods\": [\"UC Campus Area\"], \"categories\": [\"American (Traditional)\", \"Restaurants\"], \"state\": \"CA\", \"city\": \"Berkeley\", \"full_address\": \"2505 Hearst Ave\\nSte B\\nUC Campus Area\\nBerkeley, CA 94709\", \"schools\": [\"University of California at Berkeley\"], \"latitude\": 37.8751965, \"price\": 2}";
        YelpDB database = new YelpDB("data/small_restaurant_set.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        String returnedString = database.addRestaurant(jsonStringRestaurant);
        assertEquals("ERR: INVALID_RESTAURANT_STRING", returnedString);
        assertFalse(database.containsRestaurant("0"));
    }

    @Test
    public void test31 () // tests addRestaurant method with invalid input
    {
        String jsonStringRestaurant = "Don't you hate it when you're eating ice cream cake in the shower and it melts? Yeah, me neither.";
        YelpDB database = new YelpDB("data/small_restaurant_set.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        String returnedString = database.addRestaurant(jsonStringRestaurant);
        assertEquals("ERR: INVALID_RESTAURANT_STRING", returnedString);
        assertFalse(database.containsRestaurant("0"));
    }


    @Test
    public void test32() throws RuntimeException // tests addUser method
    {
        YelpDB database = new YelpDB("data/restaurants.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        assertFalse(database.containsUserName("Clive Bixby"));
        assertFalse(database.containsUserName("Swamp Donkey"));
        database.addUser("{\"url\": \"http://www.yelp.com/user_details?userid=_NH7Cpq3qZkByP5xR4gXog\", \"votes\": {\"funny\": 35, \"useful\": 21, \"cool\": 14}, \"review_count\": 29, \"type\": \"stupid\", \"user_id\": \"_NH7Cpq3qZkByP5xR4gXog\", \"name\": \"Clive Bixby\", \"average_stars\": 3.89655172413793}");
        database.addUser("{\"name\":\"Swamp Donkey\"}");
        assertTrue(database.containsUserName("Clive Bixby"));
        assertTrue(database.containsUserName("Swamp Donkey"));
        User bixby;
        User donkey;
        try {
            bixby = database.getUserObject("Clive Bixby");
            donkey = database.getUserObject("Swamp Donkey");
        }
        catch(IOException ioe) {
            throw new RuntimeException();
        }
        assertTrue(bixby.getUserId().equals("0"));
        assertEquals("http://www.yelp.com/user_details?userid=0", bixby.getUrl());
        assertEquals("user", bixby.getType());
        assertEquals("Clive Bixby", bixby.getName());
        assertEquals(0, bixby.getReviewCount());
        assertEquals(0, bixby.getAverageStars(), 0.00000000001);
        assertEquals("{useful=0, funny=0, cool=0}", bixby.getVotes().toString());
        assertTrue(donkey.getUserId().equals("1"));
    }

    @Test
    public void test33() // tests addUser method with invalid input
    {
        YelpDB database = new YelpDB("data/restaurants.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        String returnedString = database.addUser("{\"name\":\"Swamp Donkey\"} Eat my shorts");
        assertEquals("ERR: INVALID_USER_STRING", returnedString);
    }

    @Test
    public void test34() // tests addReview method
    {
        YelpDB database = new YelpDB("data/restaurants.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        String reviewData = "{ \"business_id\": \"sxIPX4ZAipVl3ZCkkqXqZw\", \"votes\": {\"cool\": 1, \"useful\": 0, \"funny\": 0}, \"text\": \"It's pretty cool I guess\", \"stars\": 3, \"user_id\": \"RHl_kHgK-koy58otORcwwA\", \"date\": \"2017-09-13\"}";
        String newReview = database.addReview(reviewData);
        assertEquals("It's pretty cool I guess", database.getReviewCopyById("0").getText());
    }

    @Test
    public void test35() // tests addReview method with invalid input
    {
        YelpDB database = new YelpDB("data/restaurants.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        String reviewData = "\"business_id\": \"sxIPX4ZAipVl3ZCkkqXqZw\", \"votes\": {\"cool\": 1, \"useful\": 0, \"funny\": 0}, \"text\": \"It's pretty cool I guess\", \"stars\": 3, \"user_id\": \"RHl_kHgK-koy58otORcwwA\", \"date\": \"2017-09-13\"}";
        String error = database.addReview(reviewData);
        assertEquals("ERR: INVALID_REVIEW_STRING", error);
    }

    @Test
    public void test36() // tests addReview method with invalid input
    {
        YelpDB database = new YelpDB("data/restaurants.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        String reviewData = "{\"business_id\": \"12341\", \"votes\": {\"cool\": 1, \"useful\": 0, \"funny\": 0}, \"text\": \"It's pretty cool I guess\", \"stars\": 3, \"user_id\": \"RHl_kHgK-koy58otORcwwA\", \"date\": \"2017-09-13\"}";
        String error = database.addReview(reviewData);
        assertEquals("ERR: NO_SUCH_RESTAURANT", error);
    }

    @Test
    public void test37() // tests addReview method with invalid input
    {
        YelpDB database = new YelpDB("data/restaurants.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        String reviewData = "{\"business_id\": \"sxIPX4ZAipVl3ZCkkqXqZw\", \"votes\": {\"cool\": 1, \"useful\": 0, \"funny\": 0}, \"text\": \"It's pretty cool I guess\", \"stars\": 3, \"user_id\": \"your_mom_goes_to_college\", \"date\": \"2017-09-13\"}";
        String error = database.addReview(reviewData);
        assertEquals("ERR: NO_SUCH_USER", error);
    }

    @Test
    public void test38() // tests server - client function/compatibility with addRestaurant query. Also activates the server
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        ServerThread serverThread = new ServerThread();
        ClientThread clientThread = new ClientThread();
        clientThread.start();
        serverThread.start();
        try
        {
            clientThread.join();
        }
        catch(Exception e)
        {}
        assertEquals("Start up successful\r\nServer running\r\nServer connected\r\nClient online\r\nEnter Query or exit to quit:\r\n{\"open\":true,\"url\":\"http://www.yelp.com/restaurant_details?restaurantid=0\",\"longitude\":-122.2598181,\"neighborhoods\":[\"UC Campus Area\"],\"business_id\":\"0\",\"name\":\"Peppermint Grill\",\"categories\":[\"American (Traditional)\",\"Restaurants\"],\"state\":\"CA\",\"type\":\"business\",\"stars\":0.0,\"city\":\"Berkeley\",\"full_address\":\"2505 Hearst Ave Ste B UC Campus Area Berkeley, CA 94709\",\"review_count\":0,\"photo_url\":\"http://www.yelp.com/restaurant_details?restaurantid=0/ms.jpg\",\"schools\":[\"University of California at Berkeley\"],\"latitude\":37.8751965,\"price\":2}\r\nEnter Query or exit to quit:\r\n", out.toString());
    }

    @Test(expected = RuntimeException.class)
    public void test39() throws RuntimeException // tests getUserObject method
    {
        YelpDB database = new YelpDB("data/restaurants.json", "data/reviews.json", "data/users.json");
        database.addAllFromFile();
        try {
            User gus = database.getUserObject("Gus Chiggens");
        }
        catch(IOException ioe) {
            throw new RuntimeException();
        }
    }

    @Test
    public void test4() // tests server function with invalid query
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        ClientErrorThread1 clientErrorThread1 = new ClientErrorThread1();
        clientErrorThread1.start();
        try
        {
            clientErrorThread1.join();
        }
        catch(Exception e)
        {}
        boolean equal = "Client online\r\nServer connected\r\nEnter Query or exit to quit:\r\nERR: ILLEGAL_REQUEST\r\nEnter Query or exit to quit:\r\n".equals(out.toString()) || "Client online\r\nEnter Query or exit to quit:\r\nServer connected\r\nERR: ILLEGAL_REQUEST\r\nEnter Query or exit to quit:\r\n".equals(out.toString());
        assertTrue(equal);
    }

    @Test
    public void test41() // tests server function with invalid query
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        ClientErrorThread2 clientErrorThread2 = new ClientErrorThread2();
        clientErrorThread2.start();
        try
        {
            clientErrorThread2.join();
        }
        catch(Exception e)
        {}
        boolean equal = "Client online\r\nServer connected\r\nEnter Query or exit to quit:\r\nERR: ILLEGAL_REQUEST\r\nEnter Query or exit to quit:\r\n".equals(out.toString()) || "Server connected\r\nClient online\r\nEnter Query or exit to quit:\r\nERR: ILLEGAL_REQUEST\r\nEnter Query or exit to quit:\r\n".equals(out.toString());
        assertTrue(equal);
    }

    @Test
    public void test42() // shuts down the server (and the program)
    {
        ServerKillThread serverKillThread = new ServerKillThread();
        serverKillThread.start();
        assertTrue(true);
    }
}

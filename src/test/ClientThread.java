package ca.ece.ubc.cpen221.mp5;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.NoSuchElementException;

public class ClientThread extends Thread
{
    @Override
    public void run()
    {
        String[] args = new String[1];
        args[0] = "4949";
        try
        {
            sleep(5650);
        }
        catch(Exception e)
        {
            System.out.println("ERR: CLIENT_THREAD_INTERRUPT");
        }
        String query1 = "ADDRESTAURANT {\"longitude\": -122.2598181, \"neighborhoods\": [\"UC Campus Area\"], \"name\": \"Peppermint Grill\", \"categories\": [\"American (Traditional)\", \"Restaurants\"], \"state\": \"CA\", \"city\": \"Berkeley\", \"full_address\": \"2505 Hearst Ave Ste B UC Campus Area Berkeley, CA 94709\", \"photo_url\": \"http://s3-media1.ak.yelpcdn.com/assets/2/www/img/924a6444ca6c/gfx/blank_biz_medium.gif\", \"schools\": [\"University of California at Berkeley\"], \"latitude\": 37.8751965, \"price\": 2}";
        int i8 = query1.indexOf("full_address");
        String s8 = query1.substring(0, i8 - 1);
        String s9 = query1.substring(i8 - 1, query1.indexOf(",", i8));
        String s10 = query1.substring(query1.indexOf(",", i8), query1.indexOf("}") + 1);
        String s11 = s8.replaceAll("\\\\", "");
        String s13 = s10.replaceAll("\\\\", "");
        String s119 = s11.concat(s9);
        String query = s119.concat(s13);
        try
        {
            InputStream queries = new ByteArrayInputStream(query.getBytes());
            System.setIn(queries);
        }
        catch(Exception e)
        {}
        boolean running = true;
        while(running)
        {
            try
            {
                YelpDBClient.main(args);
            }
            catch(NoSuchElementException e)
            {
                running = false;
            }
        }
    }
}

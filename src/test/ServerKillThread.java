package ca.ece.ubc.cpen221.mp5;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.NoSuchElementException;

public class ServerKillThread extends Thread
{
    @Override
    public void run()
    {
        String[] args = new String[1];
        args[0] = "4949";
        try
        {
            sleep(3000);
        }
        catch(Exception e)
        {
            System.out.println("ERR: CLIENT_THREAD_INTERRUPT");
        }
        String query = "KILL_SERVER";
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

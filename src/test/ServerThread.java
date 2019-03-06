package ca.ece.ubc.cpen221.mp5;

import static java.lang.Thread.sleep;

public class ServerThread extends Thread
{
    @Override
    public void run()
    {
        String[] args = new String[1];
        args[0] = "4949";
        YelpDBServer.main(args);
    }
}

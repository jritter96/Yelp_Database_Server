package ca.ece.ubc.cpen221.mp5;

import org.omg.PortableInterceptor.INACTIVE;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class YelpDBClient
{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    /**
     * Make a YelpDBClient and connect it to a server running on
     * hostname at the specified port.
     *
     * @throws IOException if can't connect
     */
    public YelpDBClient(String hostname, int port) throws IOException
    {
        socket = new Socket(hostname, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    /**
     * Send a request to the server. Requires this is "open".
     *
     * @param query is a String representing a request of the server
     * @throws IOException if network or server failure
     */
    public void sendRequest(String query) throws IOException
    {
        out.print(query + "\n");
        out.flush();
    }

    /**
     * Get a reply from the next request that was submitted.
     * Requires this is "open".
     *
     * @return A String representing the servers response
     * @throws IOException if network or server failure
     */
    public void getReply() throws IOException
    {
        String reply = in.readLine();
        if (reply == null)
        {
            throw new IOException("connection terminated unexpectedly");
        }
        System.out.println(reply);
    }

    /**
     * Closes the client's connection to the server.
     * This client is now "closed". Requires this is "open".
     *
     * @throws IOException if close fails
     */
    public void close() throws IOException
    {
        in.close();
        out.close();
        socket.close();
    }

    public static void main(String[] args)
    {
        try
        {
            Integer port = Integer.parseInt(args[0]);
            YelpDBClient client = new YelpDBClient("localhost", port);
            System.out.println("Client online");
            while(true)
            {
                Scanner scan = new Scanner(System.in);
                System.out.println("Enter Query or exit to quit:");
                String query = scan.nextLine();
                if (query.equals("exit"))
                {
                    client.close();
                }
                if (query.equals("KILL_SERVER"))
                {
                    client.sendRequest(query);
                    client.close();
                }
                client.sendRequest(query);
                client.getReply();
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
}

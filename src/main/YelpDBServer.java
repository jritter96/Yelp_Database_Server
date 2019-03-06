package ca.ece.ubc.cpen221.mp5;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

    public class YelpDBServer
    {
        private ServerSocket serverSocket;
        private static YelpDB yelpDatabase = new YelpDB("data/restaurants.json", "data/reviews.json", "data/users.json");

        /**
         * Make a YelpDBServer that listens for connections on port.
         *
         * @param port
         *            port number, requires 0 <= port <= 65535
         */
        public YelpDBServer(int port) throws IOException
        {
            serverSocket = new ServerSocket(port);
        }

        /**
         * Run the server, listening for connections and handling them.
         *
         * @throws IOException
         *             if the main server socket is broken
         */
        public void serve() throws IOException
        {
            System.out.println("Server running");
            while (true)
            {
                // block until a client connects
                final Socket socket = serverSocket.accept();
                System.out.println("Server connected");
                // create a new thread to handle that client
                Thread handler = new Thread(new Runnable()
                {
                    public void run()
                    {
                        try
                        {
                            try
                            {
                                handle(socket);
                            }
                            finally
                            {
                                socket.close();
                            }
                        }
                        catch (IOException ioe)
                        {}
                    }
                });
                // start the thread
                handler.start();
            }
        }

        /**
         * Handle one client connection. Returns when client disconnects.
         *
         * @param socket
         *            socket where client is connected
         * @throws IOException
         *             if connection encounters an error
         */
        private void handle(Socket socket) throws IOException
        {
            System.err.println("Client connected");
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            try
            {
                // each request is a single line containing a query
                for (String query = in.readLine(); query != null; query = in.readLine())
                {
                    out.println(parseQuery(query));
                }
            }
            finally
            {
                out.close();
                in.close();
            }
        }

        private static String parseQuery(String query)
        {
            String command = null, argument = null;
            try
            {
                command = query.substring(0, query.indexOf(" "));
                argument = query.substring(query.indexOf(" ") + 1);
            }
            catch (Exception e)
            {
                return "ERR: ILLEGAL_REQUEST";
            }
            switch(command)
            {
                case "GETRESTAURANT":
                {
                    return yelpDatabase.getRestaurant(argument);
                }
                case "ADDUSER":
                {
                    return yelpDatabase.addUser(argument);
                }
                case "ADDRESTAURANT":
                {
                    return yelpDatabase.addRestaurant(argument);
                }
                case "ADDREVIEW":
                {
                    return yelpDatabase.addReview(argument);
                }
                case "KILL_SERVER":
                {
                    System.exit(1);
                }
                default:
                {
                    return "ERR: ILLEGAL_REQUEST";
                }
            }
        }

        /**
         * Start a YelpDbServer running on the inputted port.
         */
        public static void main(String[] args)
        {
            Integer port = Integer.parseInt(args[0]);
            System.out.println("Start up successful");
            try
            {
                YelpDBServer server = new YelpDBServer(port);
                yelpDatabase.addAllFromFile();
                server.serve();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

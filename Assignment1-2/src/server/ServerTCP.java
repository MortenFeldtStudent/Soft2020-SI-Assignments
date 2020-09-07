package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("ALL")
public class ServerTCP {

    public static final int PORT = 6666;
    public static ServerSocket serverSocket = null; // Server gets found
    public static Socket openSocket = null;         // Server communicates with the client
    public static ExecutorService executor = Executors.newFixedThreadPool(5); //Threadpool

    public static Socket configureServer() throws IOException
    {
        // get server's own IP address
        String serverIP = InetAddress.getLocalHost().getHostAddress();
        System.out.println("Server ip: " + serverIP);

        // create a socket at the predefined port
        serverSocket = new ServerSocket(PORT);

        // Start listening and accepting requests on the serverSocket port, blocked until a request arrives
        openSocket = serverSocket.accept();
        System.out.println("Server accepts requests at: " + openSocket);

        return openSocket;
    }

    public static void connectClient(Socket openSocket) throws IOException
    {
        String request, response;

        // two I/O streams attached to the server socket:
        Scanner in;         // Scanner is the incoming stream (requests from a client)
        PrintWriter out;    // PrintWriter is the outcoming stream (the response of the server)
        in = new Scanner(openSocket.getInputStream());
        out = new PrintWriter(openSocket.getOutputStream(),true);
        // Parameter true ensures automatic flushing of the output buffer

        // Server keeps listening for request and reading data from the Client,
        // until the Client sends "stop" requests
        while(in.hasNextLine())
        {
            request = in.nextLine();

            if(request.equals("stop"))
            {
                out.println("Good bye, client!");
                System.out.println("Log: " + request + " command from client!");
                break;
            }
            else
            {
                // server responses
                response = new StringBuilder(request).reverse().toString();
                out.println(response);
                // Log response on the server's console, too
                System.out.println("Log: " + response);
            }
        }
    }

    public static class WorkingThread implements Runnable {

        @Override
        public void run() {
            try{
                connectClient(openSocket);
            }
            catch(Exception e)
            {
                System.out.println(" Connection fails: " + e);
            }
            finally
            {
                try {
                    openSocket.close();
                } catch (IOException e) {
                    System.out.println("Connection to client closed");
                }
            }
        }
    }

    public static void main(String[] args) {
        while(true){
            try {
                openSocket = configureServer();
                executor.submit(new WorkingThread());
                serverSocket.close();
            } catch (Exception e) {
                System.out.println(" Connection fails: " + e);
            }
        }
    }

}

package server;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServerUDP {
    private static final int serverPort = 7777;

    public static String message;

    // buffers for the messages
    private static byte[] dataIn = new byte[65507];
    private static byte[] dataOut = new byte[65507];

    // In UDP messages are encapsulated in packages and sent over sockets
    private static DatagramPacket requestPacket;
    private static DatagramPacket responsePacket;
    private static DatagramSocket serverSocket;


    public static void main(String[] args) throws Exception
    {
        DatagramPacket datagramPacket;
        try
        {
            String serverIP = InetAddress.getLocalHost().getHostAddress();
            // Opens socket for accepting requests
            serverSocket = new DatagramSocket(serverPort);
            while(true)
            {
                System.out.println("Server " + serverIP + " running ...");
                datagramPacket = receiveRequest();
                sendResponse(datagramPacket);
            }
        }
        catch(Exception e)
        {
            System.out.println(" Connection fails: " + e);
        }
        finally
        {
            serverSocket.close();
            System.out.println("Server port closed");
        }
    }

    public static void convertByteArrayToImage(byte[] input) throws IOException
    {
        //Original Code Source: https://www.tutorialspoint.com/How-to-convert-Byte-Array-to-Image-in-java
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input);
        BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
        ImageIO.write(bufferedImage, "jpg", new File("Images/code_new.jpg") );
    }

    public static DatagramPacket receiveRequest() throws IOException
    {
        requestPacket = new DatagramPacket(dataIn, dataIn.length);
        serverSocket.receive(requestPacket);
        convertByteArrayToImage(requestPacket.getData());
        System.out.println("Image created");
        return requestPacket;
    }

    public static void sendResponse(DatagramPacket datagramPacket) throws IOException
    {
        InetAddress clientIP;
        int clientPort;

        clientIP = datagramPacket.getAddress();
        clientPort = datagramPacket.getPort();
        System.out.println("Client port: " + clientPort);

        message = "Image created";
        System.out.println("Response: " + message);
        dataOut = message.getBytes();
        responsePacket = new DatagramPacket(dataOut, dataOut.length, clientIP, clientPort);
        serverSocket.send(responsePacket);
        System.out.println("Message sent back " + message);
    }
}

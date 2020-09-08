package client;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ClientUDP {
    // Client needs to know server identification, <IP:port>
    private static final int serverPort = 7777;

    // File variables
    public static final String FILE_PATH = "Images/code.jpg";
    public static final String FILE_FORMAT = "jpg";

    // buffers for the messages
    public static String message;
    private static byte[] dataIn = new byte[256];
    private static byte[] dataOut = new byte[256];

    // In UDP messages are encapsulated in packages and sent over sockets
    private static DatagramPacket requestPacket;
    private static DatagramPacket responsePacket;
    private static DatagramSocket clientSocket;

    public static void main(String[] args) throws IOException
    {
        clientSocket = new DatagramSocket();
        InetAddress serverIP = InetAddress.getByName(args[0]);
        System.out.println(serverIP);

        Scanner scan = new Scanner(System.in);
        System.out.println("Type message: ");

        while((message = scan.nextLine()) != null)
        {
            // Stop client if "stop" command entered
            if(message.equals("stop"))
            {
                System.exit(0);
            }
            sendRequest(serverIP);
            receiveResponse();
        }
        clientSocket.close();
    }

    public static byte[] convertImageToByteArray(String filePath, String fileFormat) throws IOException
    {
        //Original Code Source: https://www.tutorialspoint.com/How-to-convert-Image-to-Byte-Array-in-java
        BufferedImage bufferedImage = ImageIO.read(new File(filePath));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, fileFormat, byteArrayOutputStream );
        return byteArrayOutputStream.toByteArray();
    }

    public static void sendRequest(InetAddress serverIP) throws IOException
    {
        dataOut = convertImageToByteArray(FILE_PATH, FILE_FORMAT);
        requestPacket = new DatagramPacket(dataOut, dataOut.length, serverIP, serverPort);
        clientSocket.send(requestPacket);
    }

    public static void receiveResponse() throws IOException
    {
        responsePacket = new DatagramPacket(dataIn, dataIn.length);
        clientSocket.receive(responsePacket);
        String message = new String(responsePacket.getData(), 0, responsePacket.getLength());
        System.out.println("Response from Server: " + message);
    }
}

package server;

import javax.imageio.IIOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 34522;

    public static void main(String[] args) {
//        JsonApp app = new JsonApp();
//        app.run();

        try (ServerSocket server = new ServerSocket(PORT, 50, InetAddress.getByName(ADDRESS))) {
            System.out.println("Server started!");
            try (Socket socket = server.accept();
                 DataInputStream input = new DataInputStream(socket.getInputStream());
                 DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
                String msg = input.readUTF();
                System.out.println("Received: " + msg);

                Matcher matcher = Pattern.compile("\\d+").matcher(msg);
                int receivedNum = -1;
                if (matcher.find()) {
                    receivedNum = Integer.parseInt(matcher.group());
                }
                String msgToSent = String.format("A record # %s was sent!", receivedNum);
                output.writeUTF(msgToSent);
                System.out.println("Sent: " + msgToSent);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }
}

package client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Main {

    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 34522;

    @Parameter(names = "-t", description = "Type of request (set, get, delete or exit)")
    private String typeOfRequest;

    @Parameter(names = "-i", description = "Index of the cell array we have on the server (from 1 to 1000)")
    private int cellIndex;

    @Parameter(names = "-m", description = "Value set on the cell if user uses the 'set' command")
    private String value;

    public static void main(String[] args) {
        Main main = new Main();
        JCommander.newBuilder()
                        .addObject(main)
                        .build()
                        .parse(args);
        main.run();
    }

    private void run() {
        System.out.println("Client started!");
        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output  = new DataOutputStream(socket.getOutputStream())
        ) {
            output.writeUTF(getSimplifiedCommand());
            System.out.println("Sent: " + getSimplifiedCommand());

            String receivedMsg = input.readUTF();
            System.out.println("Received: " + receivedMsg);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getSimplifiedCommand() {
        return String.format("%s %s %s",
                typeOfRequest,
                !"exit".equals(typeOfRequest) ? cellIndex : "",
                "set".equals(typeOfRequest) ? value : "")
                .trim();
    }
}

package client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 34522;

    @Parameter(names = "-t", description = "Type of request (set, get, delete or exit)")
    private String type;

    @Parameter(names = "-k", description = "Key of our database map objects")
    private String key;

    @Parameter(names = "-v", description = "Value set on the map if user uses the 'set' command")
    private String value;

    @Parameter(names = "-in", description = "Name of a file located inside the folder /client/data")
    private String fileName;

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
            output.writeUTF(getJsonCommand());
            System.out.println("Sent: " + getJsonCommand());

            String receivedMsg = input.readUTF();
            System.out.println("Received: " + receivedMsg);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getJsonCommand() {
        if (fileName != null) {
            return getRequestFromFile(fileName);
        }
        ClientRequest request = new ClientRequest(type, key, value);
        return new Gson().toJson(request);
    }

    private String getRequestFromFile(String fileName) {
        String filePath = "JSON Database (Java)/task/src/client/data/" + fileName;
        try (Scanner fileScan = new Scanner(new File(filePath))) {
            if (fileScan.hasNext()) {
                return fileScan.nextLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}

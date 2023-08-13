package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 34522;
    private static volatile boolean isRunning;

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        System.out.println("Server started!");
        isRunning = true;
        JsonApp app = new JsonApp();
        Object lock = new Object();

        try (ServerSocket server = new ServerSocket(PORT, 50, InetAddress.getByName(ADDRESS))) {
            while (isRunning) {
                try {
                    // Set a timeout for accept() to allow checking the isRunning flag
                    server.setSoTimeout(1000);
                    Socket socket = server.accept();

                    executor.submit(() -> {
                        try (
                                DataInputStream input = new DataInputStream(socket.getInputStream());
                                DataOutputStream output = new DataOutputStream(socket.getOutputStream())
                        ){
                            String receivedCommand = input.readUTF();
                            output.writeUTF(app.runAndGetResponse(receivedCommand));

                            if (receivedCommand.contains("exit")) {
                                synchronized (lock) {
                                    isRunning = false;
                                }
                            }
                            socket.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

                } catch (IOException ex) {
                    if (!isRunning) {
                        break;
                    }
                }
            }
            executor.shutdown();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}

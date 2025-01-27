package chatapp;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ListView;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ServerController {

    private static final int PORT = 12345;
    private final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private ServerSocket serverSocket;

    @FXML
    private TextArea logArea;

    private ListView<String> userIpList; 

    @FXML
    private Button stopButton;
    

    public void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            log("Server started on port " + PORT);

            stopButton.setDisable(false);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) stopButton.getScene().getWindow();
            stage.setOnCloseRequest((WindowEvent event) -> {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    try {
                        serverSocket.close(); 
                    } catch (IOException e) {
                        log("Error stopping server: " + e.getMessage());
                    }
                }
                Platform.exit(); 
                System.exit(0); 
            });
        });
    }


    private void handleClient(Socket clientSocket) {
        try {
            DataInputStream inputFromClient = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream outputToClient = new DataOutputStream(clientSocket.getOutputStream());

            InetAddress ip = clientSocket.getLocalAddress();
            String clientIpAddress = ip.getHostAddress();

            addIpToLog(clientIpAddress); 
            updateUserListWithIp(clientIpAddress); 
            outputToClient.writeUTF("Connection successful!");

            while (true) {
                String message = inputFromClient.readUTF();
                log("Message from client: " + message);

                broadcastMessage("Client", message);
            }

        } catch (IOException e) {
            log("Error with client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUserListWithIp(String ipAddress) {
        Platform.runLater(() -> {
            userIpList.getItems().add("User (" + ipAddress + ")");
        });
    }

    public void log(String message) {
        Platform.runLater(() -> logArea.appendText(message + "\n"));
    }

    private void addIpToLog(String ipAddress) {
        Platform.runLater(() -> {
            logArea.appendText("Client connected with IP: " + ipAddress + "\n");
        });
    }

    private void broadcastMessage(String sender, String message) {
        synchronized (clients) {
            for (ClientHandler client : clients.values()) {
                client.sendMessage(sender + ": " + message);
            }
        }
    }

    @FXML
    private void stopServer(ActionEvent event) {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close(); 
                log("Server stopped.");
            }
        } catch (IOException e) {
            log("Error stopping server: " + e.getMessage());
        }
        Platform.exit(); 
        System.exit(0); 
    }

    
    private class ClientHandler {

        private final String nickname;
        private final Socket socket;
        private final BufferedReader in;
        private final PrintWriter out;

        public ClientHandler(String nickname, Socket socket, BufferedReader in, PrintWriter out) {
            this.nickname = nickname;
            this.socket = socket;
            this.in = in;
            this.out = out;
        }

        public void listenForMessages() {
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        if (message.startsWith("/quit")) {
                            disconnectClient(nickname);
                            break;
                        } else {
                            broadcastMessage(nickname, message);
                        }
                    }
                } catch (IOException e) {
                    log("Error with client " + nickname + ": " + e.getMessage());
                } finally {
                    disconnectClient(nickname);
                }
            }).start();
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }

    private void disconnectClient(String nickname) {
        synchronized (clients) {
            if (clients.containsKey(nickname)) {
                clients.remove(nickname);
                log(nickname + " disconnected.");
            }
        }
    }
}

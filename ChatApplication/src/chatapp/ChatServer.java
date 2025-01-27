package chatapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer extends Application {

    private static final int PORT = 12345;
    private final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private ServerSocket serverSocket;
    private ServerController serverController;  

    public static void main(String[] args) {
        launch(args); 
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Server.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chat Server");
        primaryStage.show();

        serverController = loader.getController();

        new Thread(this::startServer).start();
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            log("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            log("Error starting server: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String nickname = in.readLine();
            InetAddress inetAddress = clientSocket.getInetAddress();
            String ipAddress = inetAddress.getHostAddress(); 

            if (nickname == null || nickname.trim().isEmpty()) {
                clientSocket.close();
                return;
            }

            synchronized (clients) {
                if (clients.containsKey(nickname)) {
                    out.println("/error Nickname already taken. Choose another.");
                    clientSocket.close();
                    return;
                }
            }

            clients.put(nickname, new ClientHandler(nickname, clientSocket, in, out));
            broadcastClients();
            log(nickname + " connected from IP: " + ipAddress); 

            String message;
            while ((message = in.readLine()) != null) {
                handleClientMessage(nickname, message);
            }
        } catch (IOException e) {
            log("Error with client: " + e.getMessage());
        } finally {
            disconnectClient(clientSocket);
        }
    }

    private void handleClientMessage(String nickname, String message) {
        if (message.startsWith("/quit")) {
            disconnectClient(nickname);
        } else {
            synchronized (clients) {
                for (ClientHandler client : clients.values()) {
                    if (!client.getNickname().equals(nickname)) {
                        client.sendMessage(nickname + ": " + message);
                    }
                }
            }
        }
    }

    private void broadcastClients() {
        synchronized (clients) {
            String userList = String.join(",", clients.keySet());
            for (ClientHandler client : clients.values()) {
                client.sendMessage("/users " + userList);
            }
            log("Updated client list: " + userList);
        }
    }

    private void disconnectClient(Socket clientSocket) {
        synchronized (clients) {
            clients.values().removeIf(handler -> handler.getSocket().equals(clientSocket));
            broadcastClients();
        }
    }

    private void disconnectClient(String nickname) {
        synchronized (clients) {
            if (clients.containsKey(nickname)) {
                clients.remove(nickname);
                broadcastClients();
                log(nickname + " disconnected.");
            }
        }
    }

    private void log(String message) {
        if (serverController != null) {
            serverController.log(message); 
        }
    }

    private static class ClientHandler {
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

        public String getNickname() {
            return nickname;
        }

        public Socket getSocket() {
            return socket;
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }
}
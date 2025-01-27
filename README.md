# Chat Application

This is a multi-user chat application built using JavaFX. The application consists of a server and multiple clients. The (local)server manages messages from clients and broadcasts them to all connected clients.

## Features

-   **Multi-User Chat:** Multiple users can chat simultaneously.
-   **Username:** Each user needs to enter a username before joining the chat room.
-   **Server Log:** The server keeps a log of connection and message activities.
-   **Client Log:** Clients can keep track of messages and user status.
-   **User List:** A list of connected users is sent to clients and updated.
-   **Quit Command:** Users can leave the chat using the "/quit" command.
-   **IP Address Information:** When each client connects, their IP address information is sent to the server and printed in the logs.

## Project Structure

The project consists of the following main components:

-   **ChatClient.java:** The main class of the client application.
-   **ClientController.java:** Controls the FXML interface of the client application.
-   **client.fxml:** Defines the user interface of the client application.
-   **ChatServer.java:** The main class of the server application.
-   **ServerController.java:** Controls the FXML interface of the server application.
-   **Server.fxml:** Defines the user interface of the server application.

## How to Run?

1.  **Project Setup:**
    *   Import the project files (ChatClient.java, ClientController.java, client.fxml, ChatServer.java, ServerController.java, Server.fxml) into a Java development environment (IDE) (e.g., IntelliJ IDEA, Eclipse).
    *   Ensure that you have correctly added the JavaFX dependencies to the project.
2.  **Start the Server:**
    *   Run the `ChatServer.java` file. This will start the server application and begin binding to the specified port (default is 12345).
    *   When the server starts, the interface in the `Server.fxml` file will be displayed.
    *   **Important:** The server must be started first before clients can connect.
3.  **Start the Clients:**
    *   Run multiple `ChatClient.java` files separately. Enter a different username for each client.
    *   When the client starts, the interface in the `client.fxml` file will be displayed.
    *   Each client must enter a username and then click the "Connect" button to connect to the server.
    *   **Unlimited Clients:** The server allows an unlimited number of clients to connect simultaneously.

4.  **Join the Chat:**
    *   After connecting, clients can start chatting by typing a message into the message input field and clicking the "Send" button.
    *   Messages are sent to all connected clients.
    *   Users can click the "Leave Chat" button to exit the chat.

## Screenshots

**Server Interface:**
The server interface displays the log of connection and message activities.
![Screenshot 2025-01-28 001306](https://github.com/user-attachments/assets/c9cf9ccb-e1cb-40f5-88c5-8199ca5be548)

**Client Interface:**
The client interface displays messages, user statuses, and user input.
![image](https://github.com/user-attachments/assets/8e83c25a-b025-4cd0-9853-f86da77e32c6)
![image](https://github.com/user-attachments/assets/de41f99c-3c81-4538-acb6-dfd20c869bfc)


## Code Explanation

-   **ChatServer.java:**
    *   The `startServer()` method initializes the server socket and accepts incoming connections.
    *   The `handleClient()` method starts a new thread for each client and manages communication with the client.
    *   The `broadcastClients()` method broadcasts the user list to all connected clients.
    *   The `log()` method prints messages to the server logs.
    *   The `ClientHandler` class creates a thread for each client.

-   **ChatClient.java:**
    *   The `connectToServer()` method connects to the server and initializes input/output streams.
    *   The `sendMessage()` method sends messages to the server.
    *   The `listenForMessages()` method listens for messages from the server.
    *   The `closeConnection()` method closes the connection.
-   **ClientController.java:**
    *   The `connect()` method manages the connection process to the server.
    *    The `sendMessage()` method sends the user's typed message to the server.
    *   The `leaveChat()` method closes the connection and resets UI elements to default state.
    *   The `listenForMessages()` method processes incoming messages from the server.
    *   The `processMessage()` method handles different message types from the server (IP info, user list, normal message).

- **ServerController.java:**
    *   The `startServer()` method initializes the server socket and starts accepting clients.
    *   The `handleClient()` method starts a separate thread for each client.
    *   The `broadcastMessage()` method sends messages to all connected clients.
    *   The `log()` method writes messages to the server log area.
    *   The `stopServer()` method stops the server and terminates the program.
    *   The `ClientHandler` class creates a separate thread for each client and listens for incoming messages from the client.
    *   The `addIpToLog()` method adds the IP addresses of clients to the log area.
    *   The `updateUserListWithIp()` method adds the IP addresses of clients to the user list.

## Development Notes

-   **JavaFX:** JavaFX is used for the user interface. This allows for creating a rich user interface.
-   **Multi-threading:** The server uses multi-threading to manage multiple clients simultaneously. This improves performance and responsiveness.
-   **Socket Programming:** Communication between clients and the server is achieved using socket programming.
-   **Concurrency:** The application uses concurrent data structures to manage multi-access and ensure data consistency.
-   **User Experience:** The client interface is designed to provide a user-friendly experience.

## Contributing

Feel free to contribute to this project! Here are some things you can do:

-   Bug fixes
-   New features
-   Code optimizations
-   Documentation improvements

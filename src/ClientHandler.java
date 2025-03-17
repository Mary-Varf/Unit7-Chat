import java.io.*;
import java.net.*;

class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Enter your name: ");
            String originalName = in.readLine();
            if (originalName == null) {
                throw new IOException("Client disconnected before entering a name.");
            }
            clientName = ChatServer.getUniqueName(originalName);

            out.println("Welcome, " + clientName + "! You can now enter messages. Type 'EXIT' to leave.");
            System.out.println(clientName + " joined the chat.");
            ChatServer.broadcastMessage(clientName + " has joined the chat.", this);

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("EXIT")) {
                    break;
                }
                System.out.println(clientName + ": " + message);
                ChatServer.broadcastMessage(clientName + ": " + message, this);
            }
        }
        catch (IOException e) {
            System.out.println("Client " + clientName + " disconnected unexpectedly.");
        }
        finally {
            ChatServer.broadcastMessage(clientName + " has left the chat.", this);
            ChatServer.removeClient(this);
            try {
                socket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void sendMessage(String message) {
        out.println(message);
    }

    String getClientName() {
        return clientName;
    }
}

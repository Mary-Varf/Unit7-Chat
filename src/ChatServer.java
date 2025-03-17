import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 1;
    private static Set<ClientHandler> clients = Collections.synchronizedSet(new HashSet<>());
    private static Map<String, Integer> nameCounts = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat Server Started");
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler client = new ClientHandler(socket);
                clients.add(client);
                new Thread(client).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    static synchronized String getUniqueName(String name) {
        if (!nameCounts.containsKey(name)) {
            nameCounts.put(name, 1);
            return name;
        }
        else {
            int count = nameCounts.get(name) + 1;
            nameCounts.put(name, count);
            return name + "_" + count;
        }
    }

    static void broadcastMessage(String message, ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        }
    }

    static void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
            System.out.println(client.getClientName() + " has left the chat.");
        }
    }
}

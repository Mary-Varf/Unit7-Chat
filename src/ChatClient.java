import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 1;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.print(in.readLine());
            String name = userInput.readLine();
            out.println(name);
            System.out.println(in.readLine());

            Thread listener = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                }
                catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });
            listener.start();

            String input;
            while ((input = userInput.readLine()) != null) {
                if (input.equalsIgnoreCase("EXIT")) {
                    out.println("EXIT");
                    System.out.println("You have left the chat.");
                    break;
                }
                out.println(input);
            }
        }
        catch (IOException e) {
            System.out.println("Could not connect to server. Make sure the server is running.");
        }
    }
}

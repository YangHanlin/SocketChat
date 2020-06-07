import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class SocketChatServerCore {

    private SocketChatServerWindow window;

    private Map<String, ServerThread> clients;

    private int clientNum;

    private boolean toStop;

    private Thread serverThread;

    public SocketChatServerCore() {
        clients = new HashMap<String, ServerThread>();
        clientNum = 0;
        toStop = false;
    }

    public Map<String, ServerThread> getClients() {
        return clients;
    }

    public void setClients(Map<String, ServerThread> clients) {
        this.clients = clients;
    }

    public SocketChatServerWindow getWindow() {
        return window;
    }

    public void setWindow(SocketChatServerWindow window) {
        this.window = window;
    }

    public void start(int port) {
        SocketChatServerCore core = this;
        serverThread = new Thread() {
            @Override
            public void run() {
                try {
                    ServerSocket server = new ServerSocket(port);
                    sendMessage("System", "All", "Server starts listening on Port " + port);
                    while (!toStop) {
                        Socket socket = server.accept();
                        ServerThread thread = new ServerThread();
                        ++clientNum;
                        String clientName = "Client " + clientNum;
                        thread.setSocket(socket);
                        thread.setCore(core);
                        thread.setClientName(clientName);
                        clients.put(clientName, thread);
                        thread.start();
                        sendMessage("Special.Update name", clientName, clientName);
                        sendMessage("Special.Update list", "All", String.join("\n", clients.keySet()));
                        sendMessage("System", "All", clientName + " is now online");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        serverThread.start();
    }

    public void sendMessage(String sender, String receiver, String message) {
        System.out.println("[SERVER]MESSAGE, SENDER=" + sender + ", RECEIVER=" + receiver + ", MESSAGE=" + message);
        switch (receiver) {
            case "All":
                if (sender.startsWith("Special.")) {
                    String directive = sender.substring("Special.".length());
                    switch (directive) {
                        case "Update list":
                            window.updateTargetList(message.split("\n"));
                            break;
                        default:
                            System.err.println("Unsupported directive " + directive);
                    }
                } else {
                    window.showMessage(sender, message);
                }
                for (String client : clients.keySet()) {
                    clients.get(client).setOutput(sender + "\n" + message);
                }
                break;
            case "Server":
                window.showMessage(sender, message);
                break;
            default:
                if ("Server".equals(sender)) {
                    window.showMessage(sender, message);
                }
                clients.get(receiver).setOutput(sender + "\n" + message);
        }
    }
}

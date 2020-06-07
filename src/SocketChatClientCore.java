import java.io.IOException;
import java.net.Socket;

public class SocketChatClientCore {

    private SocketChatClientWindow window;

    private boolean toStop;

    private ClientThread clientThread;

    private String clientName;

    public SocketChatClientCore() {
        toStop = false;
    }

    public SocketChatClientWindow getWindow() {
        return window;
    }

    public void setWindow(SocketChatClientWindow window) {
        this.window = window;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void connect(String host, int port) {
        try {
            Socket socket = new Socket(host, port);
            clientThread = new ClientThread();
            clientThread.setCore(this);
            clientThread.setSocket(socket);
            clientThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String sender, String receiver, String message) {
        System.out.println("[CLIENT]MESSAGE, SENDER=" + sender + ", RECEIVER=" + receiver + ", MESSAGE=" + message);
        if (sender.startsWith("Special.")) {
            String directive = sender.substring("Special.".length());
            switch (directive) {
                case "Update list":
                    window.updateTargetList(message.split("\n"));
                    break;
                case "Update name":
                    setClientName(message);
                    window.setClientName(message);
                    break;
                default:
                    System.err.println("Unsupported directive " + directive);
            }
        } else if (sender.equals(clientName)) {
            clientThread.setOutput(receiver + "\n" + message);
        } else {
            window.showMessage(sender, message);
        }
    }
}

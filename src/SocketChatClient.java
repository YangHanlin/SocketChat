public class SocketChatClient {
    public static void main(String[] args) {
        SocketChatClientWindow window = new SocketChatClientWindow();
        SocketChatClientCore core = new SocketChatClientCore();
        window.setCore(core);
        core.setWindow(window);
        window.setVisible(true);
    }
}

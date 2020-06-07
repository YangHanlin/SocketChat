public class SocketChatServer {
    public static void main(String[] args) {
        SocketChatServerWindow window = new SocketChatServerWindow();
        SocketChatServerCore core = new SocketChatServerCore();
        window.setCore(core);
        core.setWindow(window);
        window.setVisible(true);
    }
}

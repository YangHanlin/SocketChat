import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ClientThread extends Thread {

    private SocketChatClientCore core;

    private Socket socket;

    private boolean toStop;

    private Queue<String> output;

    public ClientThread() {
        toStop = false;
        output = new ArrayDeque<>();
    }

    public SocketChatClientCore getCore() {
        return core;
    }

    public void setCore(SocketChatClientCore core) {
        this.core = core;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public boolean isToStop() {
        return toStop;
    }

    public void setToStop(boolean toStop) {
        this.toStop = toStop;
    }

    public String getOutput() {
        return output.poll();
    }

    public void setOutput(String output) {
        this.output.add(output);
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            while (!toStop) {
                if (!output.isEmpty()) {
                    outputStream.write(getOutput().getBytes(StandardCharsets.UTF_8));
                }
                outputStream.write(0);
                Vector<Byte> inputBuffer = new Vector<>();
                int temp;
                while ((temp = inputStream.read()) != -1) {
                    if (temp == 0) {
                        break;
                    }
                    inputBuffer.add((byte)temp);
                }
                byte[] inputBytes = new byte[inputBuffer.size()];
                for (int i = 0; i < inputBytes.length; ++i) {
                    inputBytes[i] = inputBuffer.elementAt(i);
                }
                String inputString = new String(inputBytes, StandardCharsets.UTF_8);
                int newlineIndex = inputString.indexOf('\n');
                if (newlineIndex != -1) {
                    String sender = inputString.substring(0, newlineIndex),
                            message = inputString.substring(newlineIndex + 1);
                    core.sendMessage(sender, core.getClientName(), message);
                }
            }
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

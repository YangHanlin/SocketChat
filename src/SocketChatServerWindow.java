import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class SocketChatServerWindow extends JFrame {

    private Ui ui;

    private SocketChatServerCore core;

    static private class Ui {

        public JLabel portLabel;
        public JTextField portField;
        public JButton startButton;
        public JTextArea messageArea;
        public JLabel messageLabel;
        public JTextField messageField;
        public JLabel targetLabel;
        public JComboBox<String> targetComboBox;
        public JButton sendButton;

        public Ui() {
            this.portLabel = new JLabel();
            this.portField = new JTextField();
            this.startButton = new JButton();
            this.messageArea = new JTextArea();
            this.messageLabel = new JLabel();
            this.messageField = new JTextField();
            this.targetLabel = new JLabel();
            this.targetComboBox = new JComboBox<String>();
            this.sendButton = new JButton();
        }

        public void setupUi(JFrame frame) {
            this.portLabel.setText("Port:");
            this.startButton.setText("Start");
            this.messageArea.setEditable(false);
            this.messageLabel.setText("Send");
            this.targetLabel.setText("to");
            this.sendButton.setText("Go");

            frame.setLayout(new BorderLayout(5, 5));
            frame.setSize(500, 400);
            frame.setTitle("Socket Chat (Server)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            Panel mainPanel = new Panel(new BorderLayout(0, 10));

            JScrollPane messageAreaPanel = new JScrollPane(messageArea);
            mainPanel.add(messageAreaPanel, BorderLayout.CENTER);

            Panel settingPanel = new Panel(new BorderLayout(5, 0));
            settingPanel.add(portLabel, BorderLayout.WEST);
            settingPanel.add(portField, BorderLayout.CENTER);
            settingPanel.add(startButton, BorderLayout.EAST);
            mainPanel.add(settingPanel, BorderLayout.NORTH);

            Panel messagePanel = new Panel(new BorderLayout(5, 0)),
                    messageRightPanel = new Panel(new BorderLayout(5, 0));
            messagePanel.add(messageLabel, BorderLayout.WEST);
            messagePanel.add(messageField, BorderLayout.CENTER);
            messageRightPanel.add(targetLabel, BorderLayout.WEST);
            messageRightPanel.add(targetComboBox, BorderLayout.CENTER);
            messageRightPanel.add(sendButton, BorderLayout.EAST);
            messagePanel.add(messageRightPanel, BorderLayout.EAST);
            mainPanel.add(messagePanel, BorderLayout.SOUTH);

            frame.add(mainPanel, BorderLayout.CENTER);
            for (String direction : new String[]{BorderLayout.NORTH, BorderLayout.EAST, BorderLayout.WEST, BorderLayout.SOUTH}) {
                frame.add(new Panel(), direction);
            }
        }
    }

    private void setupActions() {
        ui.startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int port;
                try {
                    port = Integer.parseInt(ui.portField.getText());
                } catch (NumberFormatException exception) {
                    exception.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Invalid port");
                    return;
                }
                core.start(port);
                ui.portField.setEditable(false);
                ui.startButton.setEnabled(false);
                ui.startButton.setText("Started");
            }
        });
        ui.sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = ui.messageField.getText();
                if (!message.isBlank()) {
                    core.sendMessage("Server", ui.targetComboBox.getSelectedItem().toString(), ui.messageField.getText());
                    ui.messageField.setText("");
                }
            }
        });
    }

    public SocketChatServerWindow() {
        this.ui = new Ui();
        ui.setupUi(this);
        setupActions();
        updateTargetList(new String[]{});
    }

    public SocketChatServerCore getCore() {
        return core;
    }

    public void setCore(SocketChatServerCore core) {
        this.core = core;
    }

    public void showMessage(String sender, String message) {
        ui.messageArea.append(sender + " " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n" + message + "\n\n");
        ui.messageArea.setCaretPosition(ui.messageArea.getDocument().getLength());
    }

    public void updateTargetList(String[] targets) {
        ui.targetComboBox.removeAllItems();
        ui.targetComboBox.addItem("All");
        for (String target : targets) {
            ui.targetComboBox.addItem(target);
        }
    }
}

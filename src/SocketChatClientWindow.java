import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class SocketChatClientWindow extends JFrame {

    private Ui ui;

    private SocketChatClientCore core;

    private String clientName;

    static private class Ui {

        public JLabel serverLabel;
        public JTextField serverField;
        public JLabel portLabel;
        public JTextField portField;
        public JButton connectButton;
        public JTextArea messageArea;
        public JLabel messageLabel;
        public JTextField messageField;
        public JLabel targetLabel;
        public JComboBox<String> targetComboBox;
        public JButton sendButton;

        public Ui() {
            this.serverLabel = new JLabel();
            this.serverField = new JTextField();
            this.portLabel = new JLabel();
            this.portField = new JTextField();
            this.connectButton = new JButton();
            this.messageArea = new JTextArea();
            this.messageLabel = new JLabel();
            this.messageField = new JTextField();
            this.targetLabel = new JLabel();
            this.targetComboBox = new JComboBox<String>();
            this.sendButton = new JButton();
        }

        public void setupUi(JFrame frame) {
            this.serverLabel.setText("Server:");
            this.portLabel.setText("Port:");
            this.portField.setPreferredSize(new Dimension(75, 0));
            this.connectButton.setText("Connect");
            this.messageArea.setEditable(false);
            this.messageLabel.setText("Send");
            this.targetLabel.setText("to");
            this.sendButton.setText("Go");

            frame.setLayout(new BorderLayout(5, 5));
            frame.setSize(500, 400);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            Panel mainPanel = new Panel(new BorderLayout(0, 10));

            JScrollPane scrollPane = new JScrollPane(messageArea);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            Panel settingPanel = new Panel(new BorderLayout(5, 0)),
                    settingRightPanel = new Panel(new BorderLayout(5, 0));
            settingPanel.add(serverLabel, BorderLayout.WEST);
            settingPanel.add(serverField, BorderLayout.CENTER);
            settingRightPanel.add(portLabel, BorderLayout.WEST);
            settingRightPanel.add(portField, BorderLayout.CENTER);
            settingRightPanel.add(connectButton, BorderLayout.EAST);
            settingPanel.add(settingRightPanel, BorderLayout.EAST);
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
        ui.connectButton.addActionListener(new ActionListener() {
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
                core.connect(ui.serverField.getText(), port);
                ui.serverField.setEditable(false);
                ui.portField.setEditable(false);
                ui.connectButton.setEnabled(false);
                ui.connectButton.setText("Connected");
            }
        });
        ui.sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = ui.messageField.getText();
                if (!message.isBlank()) {
                    core.sendMessage(clientName, ui.targetComboBox.getSelectedItem().toString(), message);
                    ui.messageField.setText("");
                }
            }
        });
    }

    public SocketChatClientWindow() {
        this.ui = new Ui();
        ui.setupUi(this);
        setClientName("Client");
        setupActions();
        updateTargetList(new String[]{});
    }

    public SocketChatClientCore getCore() {
        return core;
    }

    public void setCore(SocketChatClientCore core) {
        this.core = core;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
        setTitle("Socket Chat (" + clientName + ")");
    }

    public void showMessage(String sender, String message) {
        ui.messageArea.append(sender + " " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n" + message + "\n\n");
        ui.messageArea.setCaretPosition(ui.messageArea.getDocument().getLength());
    }

    public void updateTargetList(String[] targets) {
        ui.targetComboBox.removeAllItems();
        ui.targetComboBox.addItem("All");
        ui.targetComboBox.addItem("Server");
        for (String target : targets) {
            if (!target.equals(clientName)) {
                ui.targetComboBox.addItem(target);
            }
        }
    }
}

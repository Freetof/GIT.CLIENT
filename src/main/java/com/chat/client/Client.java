package main.java.com.chat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDateTime;

public class Client extends JFrame implements ActionListener {
    private final int ServerPort = 8888;
    private String name;
    private boolean isActive = true;

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    private JTextField jtextFieldInput;
    private JTextArea jtAreaOutput;

    private JTextField jTextField;


    public Client() throws Exception {
        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // establish the connection
        Socket socket = new Socket(ip, ServerPort);

        // obtaining input and out streams
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());

        createGui();
        createModalWindow();

        // readMessage thread
        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {

                while (isActive) {
                    try {
                        // read the message sent to this client
                        String msg = dataInputStream.readUTF();
                        System.out.println(msg);
                        addAreaFieldMessage(msg);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        });
        System.out.println("RUN");
        readMessage.start();

    }

    private String getClientName() {
        return name;
    }

    public void createGui() {
        jtextFieldInput = new JTextField(100);

        this.setTitle(getClientName());
        jtextFieldInput.addActionListener(this);
        jtAreaOutput = new JTextArea(50, 100);
        jtAreaOutput.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(jtAreaOutput,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        GridBagLayout gridBag = new GridBagLayout();
        Container contentPane = getContentPane();
        contentPane.setLayout(gridBag);
        GridBagConstraints gridCons1 = new GridBagConstraints();
        gridCons1.gridwidth = GridBagConstraints.REMAINDER;
        gridCons1.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(jtextFieldInput, gridCons1);
        GridBagConstraints gridCons2 = new GridBagConstraints();
        gridCons2.weightx = 1.0;
        gridCons2.weighty = 1.0;
        contentPane.add(scrollPane, gridCons2);
        this.pack();
        this.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent evt) {
        String text = jtextFieldInput.getText();
        addAreaFieldMessage(text);
        jtextFieldInput.setText("");
        sendMessage(text);
    }

    private void sendMessage(String text){
        try {
            // write on the output stream
            dataOutputStream.writeUTF(getClientName() + ":" + text);
        } catch (IOException e) {
            e.printStackTrace();
            isActive = false;
        }
    }

    private void addAreaFieldMessage(String message){
        jtAreaOutput.append(LocalDateTime.now() +" : "+ message + "\n");
    }

    private void createModalWindow() {
        JDialog dialog = new JDialog(this, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setBounds(132, 132, 300, 200);
        dialog.addWindowListener(closeWindow);
        JLabel label = new JLabel("Enter your name: ");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        jTextField = new JTextField(12);
        JButton button = new JButton("OK");
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                name = jTextField.getText();
                Client.super.setTitle("Logged in as : " + name);
                sendMessage("init_message");
                dialog.setVisible(false);
            }
        });
        Container contentPane = dialog.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(label, BorderLayout.NORTH);
        contentPane.add(jTextField, BorderLayout.CENTER);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout());
        jPanel.add(button);
        contentPane.add(jPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public static void main(String args[]) throws IOException {
        try {
            new Client();
        } catch (Exception ex) {
            System.out.println("Unexpected error occurs");
            ex.printStackTrace();
        }
    }

    private static WindowListener closeWindow = new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            e.getWindow().dispose();
        }
    };
} 
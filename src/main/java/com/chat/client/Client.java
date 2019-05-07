package main.java.com.chat.client;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    public Client(String name) {
        this.name = name;
    }

    final JPanel comboPanel = new JPanel();

    private static String name = "";

    public static String getName() {
        return name;
    }

    final static int ServerPort = 8888;

    public static void main(String args[]) throws UnknownHostException, IOException {

        if (name.equals("")) {
            System.out.println("enter the name:");
            Scanner sc = new Scanner(System.in);
            name = sc.next();
        }
        System.out.println(getName() + " is your name for this session\n");
        System.out.println("enter anything to confirm connection...");

        Scanner scn = new Scanner(System.in);

        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // establish the connection
        Socket s = new Socket(ip, ServerPort);

        // obtaining input and out streams
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        // sendMessage thread
        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                Client client = new Client(name);
                while (true) {
                    // read the message to deliver.
                    String msg = client.getName() + ":" + scn.nextLine();

                    try {
                        // write on the output stream
                        dos.writeUTF(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // readMessage thread
        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        // read the message sent to this client
                        String msg = dis.readUTF();
                        System.out.println(msg);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();

    }
} 
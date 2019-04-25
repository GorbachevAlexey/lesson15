package ru.sbt.lesson15.client;

import ru.sbt.lesson15.network.TCPConnection;
import ru.sbt.lesson15.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWondow extends JFrame implements ActionListener, TCPConnectionListener {
    private static final String IP_ADDR = "127.0.0.1";
    private static final int PORT = 8189;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientWondow::new);
    }

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickname = new JTextField("Nickname");
    private final JTextField fieldInput = new JTextField();
    private final JPanel panel = new JPanel();
    private final JButton buttonConnect = new JButton("Connect");
    private TCPConnection connection;

    private ClientWondow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null); //всегда посередине
        setAlwaysOnTop(true); //всегда поверх
        log.setEditable(false);//запрет редактирования
        log.setLineWrap(true);//автоперенос слов
        fieldInput.addActionListener(this); // для перехвата enter
        buttonConnect.addActionListener(this);
        panel.setLayout(new GridLayout(1, 2, 1, 1));
        panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        panel.add(fieldNickname);
        panel.add(buttonConnect);
        add(log, BorderLayout.CENTER);
        add(fieldInput, BorderLayout.SOUTH);
        add(panel, BorderLayout.NORTH);

        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (connection==null) {
            try {
                connection = new TCPConnection(this, IP_ADDR, PORT);
            } catch (IOException ex) {
                printMsg("Connection exception: " + ex);
            }
            buttonConnect.setText("Send");
        } else {

            String msg = fieldInput.getText();
            if (msg.equals("")) return;
            fieldInput.setText(null);
            connection.sendString(fieldNickname.getText() + ": " + msg);
        }
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMsg("Connection ready...");
    }

    @Override
    public void onReciveString(TCPConnection tcpConnection, String value) {
        printMsg(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMsg("Connection close");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMsg("Connection exception: " + e);
    }

    private synchronized void printMsg(String msg) {
        SwingUtilities.invokeLater(() -> {
            log.append(msg + "\n");
            log.setCaretPosition(log.getDocument().getLength());//принудительная прокрутка вниз
        });
    }
}

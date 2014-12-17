/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author insane
 */
public class AuthenticationForm extends JFrame {

    private JButton connect;
    private JTextField login;
    private JPasswordField password;
    private Socket client;

    public AuthenticationForm() {
        setSize(224, 207);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout(FlowLayout.LEFT));
        connect = new JButton("Увійти");
        connect.setPreferredSize(new Dimension(198, 24));
        login = new JTextField(15);
        password = new JPasswordField(15);

        connect.addActionListener((action) -> {
            try {
                client = new Socket(Constants.HOST, Constants.PORT);
                DataInputStream in = null;
                DataOutputStream out = null;
                in = new DataInputStream(client.getInputStream());
                out = new DataOutputStream(client.getOutputStream());

                out.writeUTF(login.getText());
                out.writeUTF(password.getText());
                File fileCfg = new File("settings.cfg");
                if (fileCfg.exists()) {
                    BufferedReader reader = new BufferedReader(new FileReader(fileCfg));
                    int shopId = Integer.parseInt(reader.readLine());
                    System.out.println("325345");
//                    JOptionPane.showMessageDialog(null, "325345!" + fileCfg.getAbsolutePath(), "Помилка!", JOptionPane.ERROR_MESSAGE);

                    out.writeInt(shopId);
                    reader.close();
                } else {
                    fileCfg.createNewFile();
                    FileWriter writer = new FileWriter(fileCfg);
                    writer.write("1");
                    writer.close();
//                    System.out.println("awdadwadawdwa");
//                                        JOptionPane.showMessageDialog(null, "awd!", "Помилка!", JOptionPane.ERROR_MESSAGE);

                    out.writeInt(1);
                }
                if (in.readInt() == Constants.AUTHENTICATION_SUCCESS) {
                    new MainFrame(client, in, out);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Неправильний логін або пароль!", "Помилка!", JOptionPane.ERROR_MESSAGE);

                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Неможливо підєднатись до сервера!", "Помилка!", JOptionPane.ERROR_MESSAGE);
            }
        });
        JPanel box = new JPanel();
        box.setLayout(new FlowLayout(FlowLayout.LEFT));
        box.setPreferredSize(new Dimension(200,130));
        box.setBorder(BorderFactory.createTitledBorder("Введіть дані"));
        box.add(new JLabel("Логін: "));
        box.add(login);
        box.add(new JLabel("Пароль: "));
        box.add(password);
        add(box);
        add(connect);
        setVisible(true);
    }

}

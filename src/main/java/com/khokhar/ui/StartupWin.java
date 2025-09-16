package com.khokhar.ui;
import com.khokhar.AppMode;
import com.khokhar.ViewModel;

import javax.swing.*;
import java.awt.*;

public class StartupWin extends JFrame {

    public StartupWin() {
        setTitle("Chat Launcher");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(220, 200);

        JLabel signatureLabel = new JLabel("  Made by Khokhar   ");
        Font signatureFont = new Font("Arial", Font.ITALIC, 11);
        signatureLabel.setFont(signatureFont);
        signatureLabel.setForeground(Color.GRAY);

        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JLabel nameLabel = new JLabel("Enter Your Name:");
        JTextField nameField = new JTextField(15);
        JButton hostBtn = new JButton("Host a Chat");
        JButton joinBtn = new JButton("Join a Chat");


        hostBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a name.");
            } else {
                ViewModel controller=new ViewModel(AppMode.HOST);
                controller.setUsername(name);
                if(controller.startHosting()){
                    dispose();
                }
            }
        });

        joinBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a name.");
            } else {
                ViewModel controller=new ViewModel(AppMode.CLIENT);
                controller.setUsername(name);
                new SearchWindow(controller);
                dispose();
            }
        });

        add(nameLabel);
        add(nameField);
        add(hostBtn);
        add(joinBtn);

        add(signatureLabel);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

package com.khokhar.ui;
import com.khokhar.Adaptor;
import com.khokhar.AppMode;
import com.khokhar.ViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StartupWin extends JFrame {

    public StartupWin() {
        ViewModel controller=new ViewModel();

        setTitle("Chat Launcher v2.0");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        //main pannel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        //adapter selection pannel row:1
        JPanel adapterPanel = new JPanel(new GridLayout(2, 1));
        JComboBox<Adaptor> adaptors = new JComboBox<>();
        for (Adaptor a : controller.getAdaptors()) {
            adaptors.addItem(a);
        }
        adapterPanel.add(new JLabel("Network Interface:"));
        adapterPanel.add(adaptors);
        if (adaptors.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Error: No active network interfaces found.\nPlease check your connection.",
                    "Network Error",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
        }

        //row:2 name input panel
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JTextField nameField = new JTextField(15);
        namePanel.add(new JLabel("Enter Your Name:"));
        namePanel.add(nameField);

        //row:3 Host/Join btns
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,30,0));
        JButton hostBtn = new JButton("Host a Chat");
        JButton joinBtn = new JButton("Join a Chat");
        buttonPanel.add(hostBtn);
        buttonPanel.add(joinBtn);

        //row:4 signature panel
        JPanel signaturePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel signatureLabel = new JLabel("Made by Khokhar");
        Font signatureFont = new Font("Arial", Font.ITALIC, 11);
        signatureLabel.setFont(signatureFont);
        signatureLabel.setForeground(Color.GRAY);
        signaturePanel.add(signatureLabel);

        //adding to main panel
        mainPanel.add(adapterPanel);
        mainPanel.add(Box.createVerticalStrut(10)); // Adds a 10px vertical gap
        mainPanel.add(namePanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(signaturePanel);

        this.add(mainPanel);

        //host btn action listener
        hostBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a name.");
            } else {
                controller.setSelectedAdaptor((Adaptor) adaptors.getSelectedItem());
                controller.setAppMode(AppMode.HOST);
                controller.setUsername(name);
                if(controller.startHosting()){
                    dispose();
                }
            }
        });

        //join btn action listener
        joinBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a name.");
            } else {
                controller.setSelectedAdaptor((Adaptor) adaptors.getSelectedItem());
                controller.setAppMode(AppMode.CLIENT);
                controller.setUsername(name);
                new SearchWindow(controller);
                dispose();
            }
        });


        pack();
        setLocationRelativeTo(null); // center the window on screen
        setVisible(true);
    }
}

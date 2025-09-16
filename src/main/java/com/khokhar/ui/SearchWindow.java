package com.khokhar.ui;

import com.khokhar.AppMode;
import com.khokhar.ViewModel;

import javax.jmdns.ServiceInfo;
import javax.swing.*;
import java.awt.*;
import java.net.Socket;

public class SearchWindow extends JFrame {
    public SearchWindow(ViewModel controller) {
        controller.startDiscovery();
        setTitle("Select server...");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300,400);
        setResizable(false);

        setLayout(new BorderLayout(10,10));

        JButton connect=new JButton("Connect");
        connect.setEnabled(false);
        JList<ServiceInfo> list=new JList<>(controller.getServers());
        list.addListSelectionListener((e)->{
            if(!list.isSelectionEmpty()){
                connect.setEnabled(true);
            }else{
                connect.setEnabled(false);
            }
        });
        connect.addActionListener((e)->{
            if(controller.connectToServer(list.getSelectedValue())){
                controller.stopDiscovery();
                dispose();
            }else {
                JOptionPane.showMessageDialog(null,"Failed to Connect!! \nPlease retry.");
            }
        });
        JButton back=new JButton("back");
        back.addActionListener((e)->{
            new StartupWin();
            dispose();
        });
        JPanel btns=new JPanel();
        btns.add(connect);
        btns.add(back);

        list.setCellRenderer(new ServiceInfoRenderer());
        JLabel foundLabel=new JLabel("Searching for servers...");
        add(foundLabel,BorderLayout.NORTH);
        add(new JScrollPane(list), BorderLayout.CENTER);
        add(btns,BorderLayout.SOUTH);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

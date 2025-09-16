package com.khokhar.ui;

import com.khokhar.AppMode;
import com.khokhar.ViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatWindow extends JFrame {
    AppMode appMode;
    JScrollPane messagesArea;
    JTextArea messagesTextArea;
    JList<String> usersList;
    JTextField messageTextField;
    JButton sendBtn;
    JPanel bottomArea;
    ViewModel controller;
    JPanel topArea;
    JLabel topLabel;
    JButton disconnectBtn;
    JPanel mainPannel;
    public ChatWindow(AppMode appMode,ViewModel viewModel) {
        controller=viewModel;
        this.appMode=appMode;
        display();
    }
    void display() {
        mainPannel=new JPanel(new BorderLayout(10,10));
        mainPannel.setBorder(new EmptyBorder(10,10,10,10));
        if(appMode==AppMode.HOST){
            setTitle("LocalChat|Server");
        }else{
            setTitle("LocalChat|Client: "+controller.getUsername());
        }
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 600);
//        setLayout(new BorderLayout(10, 10));

        if(appMode==AppMode.CLIENT){
            topLabel=new JLabel("Connected to Server: "+controller.getConnectedServerName());
            disconnectBtn=new JButton("Disconnect");
            disconnectBtn.addActionListener((e)->{
                int choice=JOptionPane.showConfirmDialog(this,"Are you sure you want to disconnect?","Confirm Disconnect",JOptionPane.YES_NO_OPTION);
                if(choice==JOptionPane.YES_OPTION){
                    controller.onDisconnect();
                }
            });
        }else{
            topLabel=new JLabel("Hosting server: "+controller.getUsername());
            disconnectBtn=new JButton("Shutdown Server");
            disconnectBtn.addActionListener((e)->{
                int choice=JOptionPane.showConfirmDialog(this,"Are you sure you want to shutdown server?","Confirm Shutdown",JOptionPane.YES_NO_OPTION);
                if(choice==JOptionPane.YES_OPTION){
                    controller.onDisconnect();
                }
            });
        }
        topArea=new JPanel(new BorderLayout(10,10));
        topArea.add(topLabel,BorderLayout.CENTER);
        topArea.add(disconnectBtn,BorderLayout.EAST);
        mainPannel.add(topArea,BorderLayout.NORTH);

        //CENTER: Chat History
        messagesTextArea = new JTextArea();
        messagesTextArea.setEditable(false);
        messagesArea = new JScrollPane(messagesTextArea);
        mainPannel.add(messagesArea, BorderLayout.CENTER);

        //  WEST: User List
        if(appMode==AppMode.HOST){
            JPanel leftPannel=new JPanel(new BorderLayout(10,10));
            usersList = new JList<>(controller.getConnectedUsers());
            JScrollPane userListScrollPane = new JScrollPane(usersList);
            userListScrollPane.setPreferredSize(new Dimension(100, 0));
            JLabel connUsersLabel=new JLabel("Connected Users:");
            leftPannel.add(connUsersLabel,BorderLayout.NORTH);
            leftPannel.add(userListScrollPane, BorderLayout.CENTER);
            mainPannel.add(leftPannel,BorderLayout.WEST);
        }

        ActionListener sendMessageAction=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = messageTextField.getText().trim();
                messageTextField.setText("");
                if (!msg.isEmpty()) {
                    controller.sendMessage(msg);
                }
            }
        };
        // SOUTH: Input Area
        bottomArea = new JPanel(new BorderLayout(10, 0));
        messageTextField = new JTextField();
        messageTextField.addActionListener(sendMessageAction);
        sendBtn = new JButton("Send");
        sendBtn.addActionListener(sendMessageAction);
        bottomArea.add(messageTextField, BorderLayout.CENTER);
        bottomArea.add(sendBtn, BorderLayout.EAST);
        mainPannel.add(bottomArea, BorderLayout.SOUTH);
        add(mainPannel);

        setLocationRelativeTo(null);
        setVisible(true);
    }
    public void addMessageToDisplay(String message){
        messagesTextArea.append(message+"\n\n");
    }
}

package com.khokhar.ui;

import com.khokhar.AppMode;
import com.khokhar.ViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;

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
    private JTextPane chatPane;
    private SimpleAttributeSet sentStyle;
    private SimpleAttributeSet receivedStyle;
    private SimpleAttributeSet privateStyle;
    public ChatWindow(AppMode appMode,ViewModel viewModel) {
        controller=viewModel;
        this.appMode=appMode;
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                        ChatWindow.this,
                        "Are you sure you want to exit?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    viewModel.onDisconnect();
                    System.exit(0);
                }
            }
        });
        display();
    }
    void display() {
        mainPannel=new JPanel(new BorderLayout(10,10));
        mainPannel.setBorder(new EmptyBorder(10,10,10,10));
        if(appMode==AppMode.HOST){
            setTitle("LocalChat v2.0|Server");
        }else{
            setTitle("LocalChat v2.0|Client: "+controller.getUsername());
        }
        setSize(500, 600);

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
        topLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        //CENTER: Chat History
//        messagesTextArea = new JTextArea();
//        messagesTextArea.setEditable(false);
//        messagesArea = new JScrollPane(messagesTextArea);
//        mainPannel.add(messagesArea, BorderLayout.CENTER);
        // === Replace the JTextArea ===
        chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPane.setMargin(new Insets(10, 10, 10, 10)); // Adds nice padding
        mainPannel.add(new JScrollPane(chatPane), BorderLayout.CENTER);

// === Define the style for SENT messages (right-aligned, blue) ===
        sentStyle = new SimpleAttributeSet();
        StyleConstants.setAlignment(sentStyle, StyleConstants.ALIGN_RIGHT);
        StyleConstants.setForeground(sentStyle, new Color(0, 100, 200)); // A nice blue
        StyleConstants.setBold(sentStyle, true);

// === Define the style for RECEIVED messages (left-aligned, black) ===
        receivedStyle = new SimpleAttributeSet();
        StyleConstants.setAlignment(receivedStyle, StyleConstants.ALIGN_LEFT);
        StyleConstants.setForeground(receivedStyle, Color.BLACK);

// === Define the style for PRIVATE messages (left-aligned, purple/italic) ===
        privateStyle = new SimpleAttributeSet();
        StyleConstants.setAlignment(privateStyle, StyleConstants.ALIGN_LEFT);
        StyleConstants.setForeground(privateStyle, Color.RED); // Purple
        StyleConstants.setItalic(privateStyle, true);


        //  WEST: User List
            JPanel leftPannel=new JPanel(new BorderLayout(10,10));
            usersList = new JList<>(controller.getConnectedUsers());

            usersList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int index = usersList.locationToIndex(e.getPoint());
                if (index != -1) {
                    String selectedItem = usersList.getModel().getElementAt(index);
                    if(selectedItem.equals("You"))return;
                    messageTextField.setText("/DM:"+selectedItem+":");
                    messageTextField.requestFocusInWindow();
                }
            }
        });
            JScrollPane userListScrollPane = new JScrollPane(usersList);
            usersList.setFont(new Font("SansSerif", Font.PLAIN, 12));
            usersList.setSelectionBackground(new Color(180, 210, 255));
            userListScrollPane.setPreferredSize(new Dimension(100, 0));
            JLabel connUsersLabel=new JLabel("Connected Users:");
            connUsersLabel.setBorder(new EmptyBorder(0, 5, 0, 0));
            leftPannel.add(connUsersLabel,BorderLayout.NORTH);
            leftPannel.add(userListScrollPane, BorderLayout.CENTER);
            mainPannel.add(leftPannel,BorderLayout.WEST);
        //}

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
    //public void addMessageToDisplay(String message){
        //messagesTextArea.append(message+"\n\n");
    //}
    public void displaySentMessage(String message) {
        // We prepend "You: " to our own messages to be clear
        addStyledText(message + "\n", sentStyle);
    }

    public void displayReceivedMessage(String message) {
        addStyledText(message + "\n", receivedStyle);
    }

    public void displayPrivateMessage(String message) {
        addStyledText(message + "\n", privateStyle);
    }

// --- A Private Helper Method to Do the Actual Work ---

    private void addStyledText(String text, SimpleAttributeSet style) {
        // Ensure this UI update runs on the correct thread (the EDT)
        SwingUtilities.invokeLater(() -> {
            StyledDocument doc = chatPane.getStyledDocument();
            try {
                // 1. Insert the text with its character style (color, bold, etc.)
                doc.insertString(doc.getLength(), text, style);

                // 2. Apply the alignment to the paragraph we just inserted.
                //    This is the key part that aligns the whole line of text.
                int start = doc.getLength() - text.length();
                doc.setParagraphAttributes(start, text.length(), style, false);

                // 3. Auto-scroll to the bottom to show the new message
                chatPane.setCaretPosition(doc.getLength());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

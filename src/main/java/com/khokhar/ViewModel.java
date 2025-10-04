package com.khokhar;

import com.khokhar.Service.*;
import com.khokhar.ui.ChatWindow;
import com.khokhar.ui.StartupWin;

import javax.jmdns.ServiceInfo;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ViewModel {
    ArrayList<Adaptor> adaptors;
    InetAddress selectedAdaptor;
    String username;
    String connectedServerName;
    ChatWindow view;
    AppMode appMode;
    SocServer serverSocket;
    DefaultListModel<ServiceInfo> discoveredServers;
    DefaultListModel<String> connectedUserListModel = new DefaultListModel<>();
    //for storing userlist to send to clients due to swing issues
    List<String> userList=new ArrayList<>();
    ServiceDiscoverer discoveryService;
    ServiceAnnouncer broadcaster;
    SocClient clientManager;
    public ViewModel(){
        adaptors= NetwoekInterfaces.getAdaptors();
    }
    //return available network adaptors
    public ArrayList<Adaptor> getAdaptors(){
        return adaptors;
    }

    //sets the user selected adaptor
    public void setSelectedAdaptor(Adaptor selectedItem) {
        selectedAdaptor=selectedItem.getAddress();
    }

    //starts discovery of servers
    public void startDiscovery() {
        discoveredServers=new DefaultListModel<ServiceInfo>();
        try{
        discoveryService=new ServiceDiscoverer(selectedAdaptor,this);
        discoveryService.startDiscovery();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //sets the username
    public void setUsername(String name) {
        username=name;
    }
    //returns username
    public String getUsername(){
        return username;
    }

    // return connected server name/username
    public String getConnectedServerName(){
        return connectedServerName;
    }

    // list of all available servers discovered so far
    public DefaultListModel<ServiceInfo> getServers() {
        return discoveredServers;
    }
    // when a server is found
    public void serviceFound(ServiceInfo info) {
        SwingUtilities.invokeLater(()->{
            if(!discoveredServers.contains(info)) discoveredServers.addElement(info);
        });
    }
    // when a server stops broadcasting
    public void serviceDeleted(ServiceInfo info) {
        SwingUtilities.invokeLater(()->{
            discoveredServers.removeElement(info);
        });
    }

    //establish connection to the selected server
    public boolean connectToServer(ServiceInfo selectedValue) {
        clientManager=new SocClient(selectedValue.getHostAddress(),username,this);
        if(clientManager.connect()){
            connectedServerName=selectedValue.getName();
            view=new ChatWindow(appMode,this);
            clientManager.startReceiving();
        }
        return true;
    }

    //sends the message
    public void sendMessage(String msg) {

        String header="";
        int msgIndex=0;
        if (msg.startsWith("/DM:")){
            msgIndex=msg.indexOf(':',4)+1;
            header=msg.substring(0,msgIndex);
            //msg+="    **private";
        }
        if (appMode==AppMode.CLIENT){
            if(msgIndex!=0){
                onMessageReceived(msg.substring(msgIndex),1);
                clientManager.sendToServer(header+username+": "+msg.substring(msgIndex));
            }else{
                clientManager.sendToServer(header+"/CHAT:"+username+": "+msg.substring(msgIndex));

            }
        }else{
            if (msgIndex!=0){
                if (serverSocket.onMessageReceived(header+username+": "+msg.substring(msgIndex))){
                    onMessageReceived(msg.substring(msgIndex),1);
                }
            }else {
                serverSocket.onMessageReceived(header + "/CHAT:" + username + ": " + msg.substring(msgIndex));
            }
        }

    }
    // runs when a message from server is received
    public void onMessageReceived(String serverResponse,int mode) {
        if(mode==1) view.displaySentMessage(serverResponse);
        else if (mode==2)view.displayReceivedMessage(serverResponse);
        else view.displayPrivateMessage(serverResponse);
    }

    //runs when the server disconnects
    public void onDisconnect() {
        if (appMode==AppMode.CLIENT){
            clientManager.close();
            JOptionPane.showMessageDialog(view,"Disconnected from server.","Disconnect",JOptionPane.INFORMATION_MESSAGE);
        }else{
            broadcaster.stopBroadcasting();
            serverSocket.shutdown();
        }
        view.dispose();
        new StartupWin();
    }

    //starts announcing the presence of the server
    public boolean startHosting() {
        broadcaster=new ServiceAnnouncer(username,selectedAdaptor);
        if(broadcaster.startBroadcasting()){
            view=new ChatWindow(appMode,this);
            serverSocket=new SocServer(this);
            serverSocket.start();
            return true;
        }
        return false;
    }
    //returns connected userlist
    public DefaultListModel<String> getConnectedUsers() {
        return connectedUserListModel;
    }
    // removes client username from userlist on disconnection
    public void removeClientName(String clientUsername) {
        userList.remove(clientUsername);
        SwingUtilities.invokeLater(()->{
            connectedUserListModel.removeElement(clientUsername);
        });
    }
    // adds client name to userlist on connection
    public boolean addClientName(String name) {
        if(name.equals("you")||name.equals("You"))return false;
        if (userList.contains(name))return false;
        userList.add(name);
        SwingUtilities.invokeLater(()->{
            connectedUserListModel.addElement(name);
        });
        return true;
    }
    // stops discovery
    public void stopDiscovery() {
        discoveryService.stopDiscovery();
    }

    //sets the app mode
    public void setAppMode(AppMode appMode) {
        this.appMode=appMode;
    }

    // returns String format of complete userlist for sending to clients
    public String getUserList() {
        StringBuilder sb=new StringBuilder(username);
            for(int i=0;i<userList.size();i++){
                sb.append('|');
                sb.append(userList.get(i));
            }
        return sb.toString();
    }

    // populates userlist at client side
    public void setUserList(String substring) {
        String[] users=substring.split("\\|");
        SwingUtilities.invokeLater(()->{
            connectedUserListModel.clear();
            for (int i=0;i<users.length;i++){
                if (users[i].equals(username)){
                    connectedUserListModel.addElement("You");
                    continue;
                }
                connectedUserListModel.addElement(users[i]);
            }
        });

    }
}

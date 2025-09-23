package com.khokhar;

import com.khokhar.Service.*;
import com.khokhar.ui.ChatWindow;
import com.khokhar.ui.StartupWin;

import javax.jmdns.ServiceInfo;
import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;

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
    ServiceDiscoverer discoveryService;
    ServiceAnnouncer broadcaster;
    SocClient clientManager;
    public ViewModel(){
        adaptors= NetwoekInterfaces.getAdaptors();
    }
    public ArrayList<Adaptor> getAdaptors(){
        return adaptors;
    }
    public void setSelectedAdaptor(Adaptor selectedItem) {
        selectedAdaptor=selectedItem.getAddress();
    }
//    public ViewModel(AppMode appMode){
//        this.appMode=appMode;
//    }
    public void startDiscovery() {
        discoveredServers=new DefaultListModel<ServiceInfo>();
        try{
        discoveryService=new ServiceDiscoverer(selectedAdaptor,this);
        discoveryService.startDiscovery();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setUsername(String name) {
        username=name;
    }
    public String getUsername(){
        return username;
    }
    public String getConnectedServerName(){
        return connectedServerName;
    }


    public DefaultListModel<ServiceInfo> getServers() {
        return discoveredServers;
    }

    public void serviceFound(ServiceInfo info) {
        SwingUtilities.invokeLater(()->{
            if(!discoveredServers.contains(info)) discoveredServers.addElement(info);
        });
    }

    public void serviceDeleted(ServiceInfo info) {
        SwingUtilities.invokeLater(()->{
            discoveredServers.removeElement(info);
        });
    }

    public boolean connectToServer(ServiceInfo selectedValue) {
        clientManager=new SocClient(selectedValue.getHostAddress(),username);
        if(clientManager.connect()){
            connectedServerName=selectedValue.getName();
            view=new ChatWindow(appMode,this);
            clientManager.startReceiving(this);
        }
        return true;
    }

    public void sendMessage(String msg) {

        if (appMode==AppMode.CLIENT){
            //view.addMessageToDisplay("you: "+msg);
            clientManager.sendToServer(username+": "+msg);
        }else{
            serverSocket.broadcastMessage(username+": "+msg);
        }

    }

    public void onMessageReceived(String serverResponse) {
        view.addMessageToDisplay(serverResponse);
    }

    public void onDisconnect() {
        if (appMode==AppMode.CLIENT){
            clientManager.disconnect();
            JOptionPane.showMessageDialog(view,"Disconnected from server.","Disconnect",JOptionPane.INFORMATION_MESSAGE);
        }else{
            broadcaster.stopBroadcasting();
            serverSocket.shutdown();

        }
        view.dispose();
        new StartupWin();
    }

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

    public DefaultListModel<String> getConnectedUsers() {
        return connectedUserListModel;
    }

    public void removeClientName(String clientUsername) {
        SwingUtilities.invokeLater(()->{
            connectedUserListModel.removeElement(clientUsername);
        });
    }

    public void addClientName(String msg) {
        SwingUtilities.invokeLater(()->{
            connectedUserListModel.addElement(msg);
        });
    }

    public void stopDiscovery() {
        discoveryService.stopDiscovery();
    }

    public void setAppMode(AppMode appMode) {
        this.appMode=appMode;
    }


}

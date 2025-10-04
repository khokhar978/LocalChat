package com.khokhar.Service;

import com.khokhar.ViewModel;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocServer {
    private final Map<String, PrintWriter> clientWriters = new ConcurrentHashMap<>();
    private final List<Socket> clientSockets=new ArrayList<>();
    private final ViewModel controller;
    private ServerSocket serverSocket;
    public SocServer(ViewModel viewModel){
        this.controller=viewModel;
    }
    public void start(){

        new Thread(()->{
            try {
                serverSocket=new ServerSocket(9999);
                while(true){
                    Socket clientSocket= serverSocket.accept();
                    PrintWriter out=new PrintWriter(clientSocket.getOutputStream(),true);
                    clientSockets.add(clientSocket);
//                    clientWriters.add(out);
                    new Thread(new ClientHandler(out,clientSocket,this)).start();
                }
            } catch (IOException e) {
            }
        }).start();

    }

    // broadcasts message to all clients
    public void broadcastMessage(String message) {
        for(PrintWriter out:clientWriters.values()){
            out.println(message);
        }
    }
    // remove method client if username already exist
    public void removeClient(String uname,Socket s){
        clientSockets.remove(s);
        clientWriters.remove(uname);
    }

    // removes client when it disconnects
    public void removeClient(PrintWriter out, Socket s, String clientUsername) {
        clientWriters.remove(clientUsername,out);
        clientSockets.remove(s);
        controller.removeClientName(clientUsername);
        broadcastMessage("/USERLIST:"+controller.getUserList());
        onMessageReceived("/INFO:'"+clientUsername+"' disconnected...");
    }

    public boolean onUsernameReceived(String msg,PrintWriter out) {
        if(controller.addClientName(msg)){
            clientWriters.put(msg,out);
            broadcastMessage("/USERLIST:"+controller.getUserList());
            onMessageReceived("/INFO:'"+msg+"' connected...");
            return true;
        }
        return false;
    }

    public void shutdown() {
        for(PrintWriter out:clientWriters.values()){
            out.close();
        }
        for (Socket s:clientSockets){
            try {
                s.close();
            } catch (IOException e) {

            }
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
        }
    }
    //whenever a message from any client/server is received process it
    public boolean onMessageReceived(String msg) {
        if (msg.startsWith("/CHAT:")){
            String temp=msg.substring(6);
            if(controller.getUsername().equals(temp.substring(0,temp.indexOf(':')))){
                controller.onMessageReceived(temp.substring(controller.getUsername().length()+1),1);
            }else controller.onMessageReceived(msg.substring(6),2);
            broadcastMessage(msg);
            return true;
        } else if (msg.startsWith("/DM:")) {
            String temp=msg.substring(4);
            String targetDevice=temp.substring(0,temp.indexOf(':'));
            return forwardMessage(targetDevice,msg);
        } else if (msg.startsWith("/INFO:")) {
            controller.onMessageReceived(msg.substring(6).trim(),2);
            broadcastMessage(msg);
            return true;
        }
        return false;
    }
    // forwards the private message to the targeted client
    private boolean forwardMessage(String targetDevice, String message) {
        if (targetDevice.equals(controller.getUsername())){
            controller.onMessageReceived(message.substring(4+controller.getUsername().length()+1),3);
            return true;
        }
        PrintWriter out=clientWriters.getOrDefault(targetDevice,null);
        if (out==null)return false;
        else{
            out.println(message);
            return true;
        }
    }
}

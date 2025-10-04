package com.khokhar.Service;

import com.khokhar.ViewModel;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SocClient {
    private ViewModel controller;
    private String ip;
    private Socket s;
    private String username;
    private PrintWriter out;
    private int port = 9999;
    Thread rec;
    public SocClient(String hostAddresses,String clientUsername,ViewModel viewModel ) {
        this.ip = hostAddresses;
        this.username=clientUsername;
        this.controller=viewModel;
    }
    public boolean connect(){
        try {
            s = new Socket(ip, port);
            out =new PrintWriter(s.getOutputStream(),true);
            out.println(username);
            return true;
        }catch(Exception e){
            return false;
        }
    }
    public void startReceiving(){
        rec=new Thread(new Receiver(s,this));
        rec.start();

    }
    public void sendToServer(String message){
        out.println(message);
    }

    public void onDisconnect() {
        controller.onDisconnect();

    }
    public void close(){
        out.close();
        try {
            s.close();
        } catch (IOException e) {

        }
    }

    public void OnResponseReceived(String serverResponse) {
        if (serverResponse.startsWith("/CHAT:")){
            String temp=serverResponse.substring(6);
            if(controller.getUsername().equals(temp.substring(0,temp.indexOf(':')))){
                controller.onMessageReceived(temp.substring(controller.getUsername().length()+1),1);
            }else controller.onMessageReceived(serverResponse.substring(6),2);
        } else if (serverResponse.startsWith("/USERLIST:")) {
            controller.setUserList(serverResponse.substring(10));
        } else if (serverResponse.startsWith("/DM:")) {
            controller.onMessageReceived(serverResponse.substring(4+controller.getUsername().length()+1),3);
        } else if (serverResponse.startsWith("/INFO:")) {
            controller.onMessageReceived(serverResponse.substring(6),2);
        }
    }
}

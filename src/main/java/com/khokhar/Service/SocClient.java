package com.khokhar.Service;

import com.khokhar.ViewModel;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SocClient {
    private String ip;
    private Socket s;
    private String username;
    private PrintWriter out;
    private int port = 9999;
    Thread rec;
    public SocClient(String hostAddresses,String clientUsername) {
        this.ip = hostAddresses;
        this.username=clientUsername;
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
    public void startReceiving(ViewModel viewModel){
        rec=new Thread(new Receiver(s,viewModel));
        rec.start();

    }
    public void sendToServer(String message){
        out.println(message);
    }

    public void disconnect() {
        out.close();
        try {
            s.close();
        } catch (IOException e) {

        }
    }
}

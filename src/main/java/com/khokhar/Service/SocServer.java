package com.khokhar.Service;

import com.khokhar.ViewModel;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SocServer {
 private final List<PrintWriter> clientWriters =new CopyOnWriteArrayList<>();
 private List<Socket> clientSockets=new ArrayList<>();
    private ViewModel controller;
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
                    clientWriters.add(out);
                    new Thread(new ClientHandler(out,clientSocket,this)).start();

                }
            } catch (IOException e) {
            }
        }).start();

    }
    public void broadcastMessage(String message) {
        controller.onMessageReceived(message);
        for(PrintWriter out:clientWriters){
            out.println(message);
        }

    }
    public void removeClient(PrintWriter out, Socket s, String clientUsername) {
        clientWriters.remove(out);
        clientSockets.remove(s);
        controller.removeClientName(clientUsername);
        broadcastMessage("'"+clientUsername+"' disconnected...");
    }

    public void onUsernameReceived(String msg) {
        controller.addClientName(msg);
        broadcastMessage("'"+msg+"' connected...");

    }

    public void shutdown() {
        for(PrintWriter out:clientWriters){
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
}

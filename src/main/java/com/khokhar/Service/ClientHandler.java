package com.khokhar.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private PrintWriter out;
    private Socket s;
    String clientUsername;
    private SocServer serverSocket;

    public ClientHandler(PrintWriter writer,Socket socket,SocServer serverManager){
        this.out=writer;
        this.s=socket;
        this.serverSocket=serverManager;
    }
    @Override
    public void run() {

        try {
            BufferedReader reader=new BufferedReader(new InputStreamReader(s.getInputStream()));
            String msg= reader.readLine();
            clientUsername=msg;
            serverSocket.onUsernameReceived(msg);
            while((msg=reader.readLine())!=null){
                serverSocket.broadcastMessage(msg);
            }
            reader.close();
        } catch (IOException e) {
        }finally{
            serverSocket.removeClient(out,s,clientUsername);
            try {
                s.close();
            } catch (IOException e) {
            }
        }

    }
}

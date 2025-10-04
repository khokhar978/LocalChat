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
    private boolean duplicateName;

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
            duplicateName=serverSocket.onUsernameReceived(msg,out);
            if(duplicateName){
                while((msg=reader.readLine())!=null){
                    if(!serverSocket.onMessageReceived(msg)){
                        out.println("/CHAT:Error in message");
                    }
                }
            }else{
                serverSocket.removeClient(clientUsername,s);// just remove the socket and writer as username already exists
            }
            reader.close();
        } catch (IOException e) {
        }finally{
            if (duplicateName){// remove client username only if it was successfully connected
                serverSocket.removeClient(out,s,clientUsername);
            }
            try {
                s.close();
            } catch (IOException e) {
            }
        }

    }
}

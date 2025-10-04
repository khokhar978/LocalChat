package com.khokhar.Service;

import com.khokhar.ViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLOutput;

public class Receiver implements Runnable {
    private final Socket clientSocket;
    private SocClient clienManager;

    public Receiver(Socket socket, SocClient clienManager) {
        this.clientSocket = socket;
        this.clienManager=clienManager;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String serverResponse;

            while ((serverResponse = in.readLine()) != null) {
                clienManager.OnResponseReceived(serverResponse);
            }
            in.close();
            clienManager.onDisconnect();
        } catch (SocketException e) {
        } catch (IOException e) {
            clienManager.onDisconnect();
        }
    }
}

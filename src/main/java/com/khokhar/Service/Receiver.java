package com.khokhar.Service;

import com.khokhar.ViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class Receiver implements Runnable {
    private final Socket clientSocket;
    private ViewModel controller;

    public Receiver(Socket socket, ViewModel viewModel) {
        this.clientSocket = socket;
        controller=viewModel;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String serverResponse;

            while ((serverResponse = in.readLine()) != null) {
                controller.onMessageReceived(serverResponse);
            }
            controller.onDisconnect();
        } catch (SocketException e) {
        } catch (IOException e) {
            controller.onDisconnect();
            System.out.println("An error occurred in the receiver: " + e.getMessage());
        }
    }
}

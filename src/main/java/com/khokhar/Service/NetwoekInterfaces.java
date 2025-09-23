package com.khokhar.Service;

import com.khokhar.Adaptor;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;

public class NetwoekInterfaces {
    public static ArrayList<Adaptor> getAdaptors(){
        ArrayList<Adaptor> adaptors=new ArrayList<>();
        try {
            for(NetworkInterface iface: Collections.list(NetworkInterface.getNetworkInterfaces())){
                if (iface.isUp()&&!iface.isVirtual()&&!iface.isLoopback()){
                    for(InetAddress addr:Collections.list(iface.getInetAddresses())){
                        if (addr instanceof Inet4Address){
                            adaptors.add(new Adaptor(iface.getDisplayName(), addr));
                        }
                    }
                }
            }
        } catch (SocketException e) {
        }
            return adaptors;
    }
}

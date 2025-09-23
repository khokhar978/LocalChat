package com.khokhar.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class ServiceAnnouncer {

    private JmDNS jmdns;
    private ServiceInfo serviceInfo;
    private final String SERVICE_TYPE = "_mychat._tcp.local.";
    private InetAddress inetaddress;

    public ServiceAnnouncer(String serviceName,InetAddress inetAddress) {
        this.inetaddress=inetAddress;
        this.serviceInfo = ServiceInfo.create(SERVICE_TYPE, serviceName, 5353, "A local chat server.");
    }

    public boolean startBroadcasting() {
        try {
            jmdns = JmDNS.create(inetaddress);
            jmdns.registerService(serviceInfo);
        } catch (IOException e) {
            return false;
        }
        return true;
    }


    public void stopBroadcasting() {
        if (jmdns != null) {
            jmdns.unregisterAllServices();
            try {
                jmdns.close();
            } catch (IOException e) {
            }
            jmdns = null;
        }
    }
}
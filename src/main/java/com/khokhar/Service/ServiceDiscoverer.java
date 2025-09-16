package com.khokhar.Service;

import com.khokhar.ViewModel;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

public class ServiceDiscoverer {

    private JmDNS jmdns;
    private ServiceListener listener;
    private final String SERVICE_TYPE = "_mychat._tcp.local.";
    private InetAddress inetaddress;
    private ViewModel controller;
    public ServiceDiscoverer(InetAddress selectedAddress, ViewModel viewmodel) {
        this.inetaddress=selectedAddress;
        this.controller=viewmodel;
    }


    public void startDiscovery() throws IOException {
        jmdns = JmDNS.create(inetaddress);

        listener = new ServiceListener() {
            @Override
            public void serviceAdded(ServiceEvent event) {
                jmdns.requestServiceInfo(event.getType(), event.getName());
            }

            @Override
            public void serviceResolved(ServiceEvent event) {
                controller.serviceFound(event.getInfo());
            }

            @Override
            public void serviceRemoved(ServiceEvent event) {
                controller.serviceDeleted(event.getInfo());
            }
        };

        jmdns.addServiceListener(SERVICE_TYPE, listener);
    }

    public void stopDiscovery(){
        if (jmdns != null) {
            jmdns.removeServiceListener(SERVICE_TYPE, listener);
            try {
                jmdns.close();
            } catch (IOException e) {
            }
            jmdns = null;
        }
    }
}
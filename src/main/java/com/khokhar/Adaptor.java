package com.khokhar;

import java.net.InetAddress;

public class Adaptor {
    private final String displayName;
    private final InetAddress address;

    public Adaptor(String displayName, InetAddress address) {
        this.displayName = displayName;
        this.address = address;
    }

    public InetAddress getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return displayName + " (" + address.getHostAddress() + ")";
    }
}

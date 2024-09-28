package com.danikula.videocache;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

class IgnoreHostProxySelector extends ProxySelector {
    private static final List<Proxy> NO_PROXY_LIST = Arrays.asList(new Proxy[]{Proxy.NO_PROXY});
    private final ProxySelector defaultProxySelector;
    private final String hostToIgnore;
    private final int portToIgnore;

    IgnoreHostProxySelector(ProxySelector defaultProxySelector2, String hostToIgnore2, int portToIgnore2) {
        this.defaultProxySelector = (ProxySelector) Preconditions.checkNotNull(defaultProxySelector2);
        this.hostToIgnore = (String) Preconditions.checkNotNull(hostToIgnore2);
        this.portToIgnore = portToIgnore2;
    }

    static void install(String hostToIgnore2, int portToIgnore2) {
        ProxySelector.setDefault(new IgnoreHostProxySelector(ProxySelector.getDefault(), hostToIgnore2, portToIgnore2));
    }

    public List<Proxy> select(URI uri) {
        return this.hostToIgnore.equals(uri.getHost()) && this.portToIgnore == uri.getPort() ? NO_PROXY_LIST : this.defaultProxySelector.select(uri);
    }

    public void connectFailed(URI uri, SocketAddress address, IOException failure) {
        this.defaultProxySelector.connectFailed(uri, address, failure);
    }
}

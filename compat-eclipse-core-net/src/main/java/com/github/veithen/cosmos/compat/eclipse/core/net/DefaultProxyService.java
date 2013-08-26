package com.github.veithen.cosmos.compat.eclipse.core.net;

import java.net.URI;
import java.util.Locale;

import org.eclipse.core.net.proxy.IProxyChangeListener;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.CoreException;

final class DefaultProxyService implements IProxyService {
    @Override
    public boolean isSystemProxiesEnabled() {
        return true;
    }

    @Override
    public void setSystemProxiesEnabled(boolean enabled) {
        throw new UnsupportedOperationException("Proxy configuration is not mutable");
    }

    @Override
    public boolean hasSystemProxies() {
        return System.getProperty("http.proxyHost") != null || System.getProperty("https.proxyHost") != null;
    }

    @Override
    public boolean isProxiesEnabled() {
        return hasSystemProxies();
    }

    @Override
    public void setProxiesEnabled(boolean enabled) {
        throw new UnsupportedOperationException("Proxy configuration is not mutable");
    }

    @Override
    public IProxyData[] select(URI uri) {
        IProxyData proxy;
        String protocol = uri.getScheme();
        if (protocol.equals("http") || protocol.equals("https")) {
            // TODO: need to check non proxy hosts
            String host = System.getProperty(protocol + ".proxyHost");
            if (host != null && host.length() > 0) {
                String portString = System.getProperty(protocol + ".proxyPort");
                int port;
                if (portString != null && portString.length() > 0) {
                    port = Integer.parseInt(portString);
                } else if (protocol.equals("https")) {
                    port = 443;
                } else {
                    port = 80;
                }
                // Note: there are no system properties to specify the username/password for HTTP proxy servers
                proxy = new ProxyData(protocol.toUpperCase(Locale.ENGLISH), host, port, null, null);
            } else {
                proxy = null;
            }
        } else {
            proxy = null;
        }
        return proxy == null ? new IProxyData[0] : new IProxyData[] { proxy };
    }

    @Override
    public IProxyData[] getProxyData() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public IProxyData[] getProxyDataForHost(String host) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public IProxyData getProxyData(String type) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public IProxyData getProxyDataForHost(String host, String type) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void setProxyData(IProxyData[] proxies) throws CoreException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String[] getNonProxiedHosts() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void setNonProxiedHosts(String[] hosts) throws CoreException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void addProxyChangeListener(IProxyChangeListener listener) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void removeProxyChangeListener(IProxyChangeListener listener) {
        throw new UnsupportedOperationException("Not implemented");
    }
}

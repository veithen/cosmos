package com.github.veithen.cosmos.wagon;

import java.net.URI;

import org.eclipse.core.net.proxy.IProxyChangeListener;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.CoreException;

public class ProxyService implements IProxyService {
    public void addProxyChangeListener(IProxyChangeListener arg0) {
        throw new UnsupportedOperationException();
    }

    public String[] getNonProxiedHosts() {
        throw new UnsupportedOperationException();
    }

    public IProxyData[] getProxyData() {
        throw new UnsupportedOperationException();
    }

    public IProxyData getProxyData(String arg0) {
        throw new UnsupportedOperationException();
    }

    public IProxyData[] getProxyDataForHost(String arg0) {
        throw new UnsupportedOperationException();
    }

    public IProxyData getProxyDataForHost(String arg0, String arg1) {
        throw new UnsupportedOperationException();
    }

    public boolean hasSystemProxies() {
        throw new UnsupportedOperationException();
    }

    public boolean isProxiesEnabled() {
        throw new UnsupportedOperationException();
    }

    public boolean isSystemProxiesEnabled() {
        throw new UnsupportedOperationException();
    }

    public void removeProxyChangeListener(IProxyChangeListener arg0) {
        throw new UnsupportedOperationException();
    }

    public IProxyData[] select(URI arg0) {
        throw new UnsupportedOperationException();
    }

    public void setNonProxiedHosts(String[] arg0) throws CoreException {
        throw new UnsupportedOperationException();
    }

    public void setProxiesEnabled(boolean arg0) {
        throw new UnsupportedOperationException();
    }

    public void setProxyData(IProxyData[] arg0) throws CoreException {
        throw new UnsupportedOperationException();
    }

    public void setSystemProxiesEnabled(boolean arg0) {
        throw new UnsupportedOperationException();
    }
}

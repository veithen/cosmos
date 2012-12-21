package com.github.veithen.cosmos.wagon;

import java.net.URI;

import org.eclipse.core.net.proxy.IProxyChangeListener;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.CoreException;

public class ProxyService implements IProxyService {
    @Override
    public void addProxyChangeListener(IProxyChangeListener arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getNonProxiedHosts() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IProxyData[] getProxyData() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IProxyData getProxyData(String arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IProxyData[] getProxyDataForHost(String arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IProxyData getProxyDataForHost(String arg0, String arg1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasSystemProxies() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isProxiesEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSystemProxiesEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeProxyChangeListener(IProxyChangeListener arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IProxyData[] select(URI arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNonProxiedHosts(String[] arg0) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setProxiesEnabled(boolean arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setProxyData(IProxyData[] arg0) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSystemProxiesEnabled(boolean arg0) {
        throw new UnsupportedOperationException();
    }
}

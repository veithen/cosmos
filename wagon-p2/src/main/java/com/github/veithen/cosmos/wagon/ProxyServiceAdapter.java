package com.github.veithen.cosmos.wagon;

import java.net.URI;
import java.util.Locale;

import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.eclipse.core.net.proxy.IProxyChangeListener;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.CoreException;

public final class ProxyServiceAdapter implements IProxyService {
    private final WagonManager wagonManager;

    public ProxyServiceAdapter(WagonManager wagonManager) {
        this.wagonManager = wagonManager;
    }

    @Override
    public boolean isSystemProxiesEnabled() {
        return false;
    }

    @Override
    public void setSystemProxiesEnabled(boolean enabled) {
        throw new UnsupportedOperationException("Proxy configuration is not mutable");
    }

    @Override
    public boolean hasSystemProxies() {
        return false;
    }

    @Override
    public boolean isProxiesEnabled() {
        return wagonManager.getProxy("http") != null || wagonManager.getProxy("https") != null;
    }

    @Override
    public void setProxiesEnabled(boolean enabled) {
        throw new UnsupportedOperationException("Proxy configuration is not mutable");
    }

    @Override
    public IProxyData[] select(URI uri) {
        String protocol = uri.getScheme();
        ProxyInfo info = wagonManager.getProxy(protocol);
        if (info != null) {
            // TODO: check non proxy hosts
            return new IProxyData[] { new ProxyDataAdapter(protocol.toUpperCase(Locale.ENGLISH), info) };
        } else {
            return new IProxyData[0];
        }
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

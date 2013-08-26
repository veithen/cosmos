package com.github.veithen.cosmos.wagon;

import org.apache.maven.wagon.proxy.ProxyInfo;
import org.eclipse.core.net.proxy.IProxyData;

public final class ProxyDataAdapter implements IProxyData {
    private final String type;
    private final ProxyInfo info;

    public ProxyDataAdapter(String type, ProxyInfo info) {
        this.type = type;
        this.info = info;
    }
    
    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getHost() {
        return info.getHost();
    }

    @Override
    public void setHost(String host) {
        info.setHost(host);
    }

    @Override
    public int getPort() {
        return info.getPort();
    }

    @Override
    public void setPort(int port) {
        info.setPort(port);
    }

    @Override
    public String getUserId() {
        return info.getUserName();
    }

    @Override
    public void setUserid(String userid) {
        info.setUserName(userid);
    }

    @Override
    public String getPassword() {
        return info.getPassword();
    }

    @Override
    public void setPassword(String password) {
        info.setPassword(password);
    }

    @Override
    public boolean isRequiresAuthentication() {
        return info.getUserName() != null;
    }

    @Override
    public void disable() {
        throw new UnsupportedOperationException("Not implemented");
    }
}

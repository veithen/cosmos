/*-
 * #%L
 * Cosmos
 * %%
 * Copyright (C) 2012 - 2018 Andreas Veithen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.veithen.cosmos.compat.eclipse.core.net;

import java.net.URI;
import java.util.Locale;

import org.eclipse.core.net.proxy.IProxyChangeListener;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.CoreException;
import org.osgi.service.component.annotations.Component;

@Component(service={IProxyService.class}, xmlns="http://www.osgi.org/xmlns/scr/v1.1.0")
public final class DefaultProxyService implements IProxyService {
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

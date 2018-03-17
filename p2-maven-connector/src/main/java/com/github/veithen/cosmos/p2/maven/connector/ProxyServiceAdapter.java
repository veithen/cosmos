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
package com.github.veithen.cosmos.p2.maven.connector;

import java.net.URI;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.eclipse.aether.repository.Proxy;
import org.eclipse.core.net.proxy.IProxyChangeListener;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.CoreException;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
        service=IProxyService.class,
        property={Constants.SERVICE_RANKING + ":Integer=1"},
        xmlns="http://www.osgi.org/xmlns/scr/v1.1.0")
public class ProxyServiceAdapter implements IProxyService {
    private PlexusContainer plexusContainer;
    private ProxyHolder proxyHolder;
    
    @Reference
    private void setPlexusContainer(PlexusContainer plexusContainer) {
        this.plexusContainer = plexusContainer;
    }
    
    @Activate
    private void activate() throws ComponentLookupException {
        proxyHolder = plexusContainer.lookup(ProxyHolder.class);
    }
    
    @Override
    public void setProxiesEnabled(boolean enabled) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isProxiesEnabled() {
        return proxyHolder.getCurrentProxy() != null;
    }

    @Override
    public boolean hasSystemProxies() {
        return false;
    }

    @Override
    public void setSystemProxiesEnabled(boolean enabled) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSystemProxiesEnabled() {
        return false;
    }

    @Override
    public IProxyData[] getProxyData() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IProxyData[] select(URI uri) {
        String protocol = uri.getScheme();
        if (protocol.equals("http") || protocol.equals("https")) {
            Proxy proxy = proxyHolder.getCurrentProxy();
            if (proxy != null) {
                return new IProxyData[] { new ProxyDataAdapter(proxy) };
            }
        }
        return new IProxyData[0];
    }

    @Override
    public IProxyData[] getProxyDataForHost(String host) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IProxyData getProxyData(String type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IProxyData getProxyDataForHost(String host, String type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setProxyData(IProxyData[] proxies) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getNonProxiedHosts() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNonProxiedHosts(String[] hosts) throws CoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addProxyChangeListener(IProxyChangeListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeProxyChangeListener(IProxyChangeListener listener) {
        throw new UnsupportedOperationException();
    }
}

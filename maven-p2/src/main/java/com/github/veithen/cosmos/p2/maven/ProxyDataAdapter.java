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
package com.github.veithen.cosmos.p2.maven;

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

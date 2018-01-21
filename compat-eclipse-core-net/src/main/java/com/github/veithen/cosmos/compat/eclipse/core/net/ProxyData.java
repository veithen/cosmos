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

import org.eclipse.core.net.proxy.IProxyData;

final class ProxyData implements IProxyData {
    private final String type;
    private final String host;
    private final int port;
    private final String userid;
    private final String password;

    ProxyData(String type, String host, int port, String userid, String password) {
        this.type = type;
        this.host = host;
        this.port = port;
        this.userid = userid;
        this.password = password;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void setHost(String host) {
        throw new UnsupportedOperationException("Proxy configuration is not mutable");
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void setPort(int port) {
        throw new UnsupportedOperationException("Proxy configuration is not mutable");
    }

    @Override
    public String getUserId() {
        return userid;
    }

    @Override
    public void setUserid(String userid) {
        throw new UnsupportedOperationException("Proxy configuration is not mutable");
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        throw new UnsupportedOperationException("Proxy configuration is not mutable");
    }

    @Override
    public boolean isRequiresAuthentication() {
        return userid != null;
    }

    @Override
    public void disable() {
        throw new UnsupportedOperationException("Not implemented");
    }
}

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
package com.github.veithen.cosmos.p2.maven.plugin.importer;

import java.util.List;
import java.util.Locale;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.crypto.DefaultSettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.eclipse.core.net.proxy.IProxyData;

import com.github.veithen.cosmos.p2.maven.ProxyDataProvider;

final class MavenSessionProxyDataProvider implements ProxyDataProvider {
    private final MavenSession session;
    private final SettingsDecrypter settingsDecrypter;

    MavenSessionProxyDataProvider(MavenSession session, SettingsDecrypter settingsDecrypter) {
        this.session = session;
        this.settingsDecrypter = settingsDecrypter;
    }

    @Override
    public IProxyData getProxyData(String protocol) {
        MavenExecutionRequest request = session.getRequest();
        if (request == null) {
            return null;
        }
        List<Proxy> proxies = request.getProxies();
        if (proxies == null) {
            return null;
        }
        for (Proxy proxy : proxies) {
            if (proxy.isActive() && protocol.equalsIgnoreCase(proxy.getProtocol())) {
                return new MavenSessionProxyDataAdapter(
                        protocol.toUpperCase(Locale.ENGLISH),
                        settingsDecrypter.decrypt(new DefaultSettingsDecryptionRequest(proxy)).getProxy());
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        return session.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MavenSessionProxyDataProvider && ((MavenSessionProxyDataProvider)obj).session.equals(session);
    }
}

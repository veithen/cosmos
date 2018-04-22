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
package com.github.veithen.cosmos.osgi.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

final class BundleManager {
    private final BundleImpl[] bundles;
    private final Map<String,BundleImpl> bundlesBySymbolicName = new HashMap<String,BundleImpl>();
    private final Map<URL,Bundle> bundlesByUrl = new HashMap<URL,Bundle>();

    /**
     * Maps exported packages to their corresponding bundles.
     */
    private final Map<String,BundleImpl> packageMap = new HashMap<String,BundleImpl>();

    BundleManager() throws BundleException {
        final List<BundleImpl> bundles = new ArrayList<>();
        // Add a system bundle
        // TODO: this should implement org.osgi.framework.launch.Framework
        BundleImpl systemBundle = new BundleImpl(this, 0, Constants.SYSTEM_BUNDLE_SYMBOLICNAME, new Attributes(), null);
        bundles.add(systemBundle);
        ResourceUtil.processResources("META-INF/MANIFEST.MF", new ResourceProcessor() {
            @Override
            public void process(URL url, InputStream in) throws IOException, BundleException {
                Manifest manifest = new Manifest(in);
                Attributes attrs = manifest.getMainAttributes();
                String symbolicName = attrs.getValue("Bundle-SymbolicName");
                if (symbolicName == null) {
                    return;
                }
                // Remove the "singleton" attribute
                int idx = symbolicName.indexOf(';');
                if (idx != -1) {
                    symbolicName = symbolicName.substring(0, idx);
                }
                URL rootUrl;
                try {
                    rootUrl = new URL(url, "..");
                } catch (MalformedURLException ex) {
                    throw new BundleException("Unexpected exception", ex);
                }
                // There cannot be any bundle listeners yet, so no need to call BundleListeners
                BundleImpl bundle = new BundleImpl(BundleManager.this, bundles.size(), symbolicName, attrs, rootUrl);
                bundles.add(bundle);
                bundlesBySymbolicName.put(symbolicName, bundle);
                bundlesByUrl.put(bundle.getLocationUrl(), bundle);
                String exportPackage = attrs.getValue("Export-Package");
                if (exportPackage != null) {
                    Element[] elements;
                    try {
                        elements = Element.parseHeaderValue(exportPackage);
                    } catch (ParseException ex) {
                        throw new BundleException("Unable to parse Export-Package header", BundleException.MANIFEST_ERROR, ex);
                    }
                    for (Element element : elements) {
                        // TODO: what if the same package is exported by multiple bundles??
                        packageMap.put(element.getValue(), bundle);
                    }
                }
            }
        });
        this.bundles = bundles.toArray(new BundleImpl[bundles.size()]);
    }

    void initialize(BundleContextFactory bundleContextFactory) throws BundleException {
        for (BundleImpl bundle : bundles) {
            bundle.initialize(bundleContextFactory);
        }
        Patcher.injectBundles(bundlesByUrl);
    }

    BundleImpl[] getBundles() {
        return bundles.clone();
    }
    
    Bundle getBundle(String symbolicName) {
        return bundlesBySymbolicName.get(symbolicName);
    }
    
    BundleImpl getBundleByPackage(String pkg) {
        return packageMap.get(pkg);
    }
    
    BundleImpl getBundle(long id) {
        return id < bundles.length ? bundles[(int)id] : null;
    }
    
    void fireBundleEvent(BundleImpl bundleImpl, int type) {
        BundleEvent event = new BundleEvent(type, bundleImpl);
        for (BundleImpl bundle : bundles) {
            bundle.distributeBundleEvent(event);
        }
    }
}

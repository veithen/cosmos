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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.xml.XMLParserActivator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.veithen.cosmos.osgi.runtime.internal.InternalLoggerFactory;

public final class CosmosRuntime {
    private static final Logger logger = LoggerFactory.getLogger(CosmosRuntime.class);

    private static CosmosRuntime instance;

    private final Properties properties = new Properties();
    private final BundleManager bundleManager;
    private final ServiceRegistry serviceRegistry;
    
    private CosmosRuntime() throws BundleException {
        bundleManager = new BundleManager();
        serviceRegistry = new ServiceRegistry();
        BundleContextFactory bundleContextFactory = new BundleContextFactory(this, bundleManager, serviceRegistry);
        bundleManager.initialize(bundleContextFactory);
        loadProperties("META-INF/cosmos.properties");
        if (logger.isDebugEnabled()) {
            loadProperties("META-INF/cosmos-debug.properties");
            logger.debug(String.format("Properties: %s", properties));
        }
        AbstractBundle systemBundle = bundleManager.getBundle(0);
        registerSAXParserFactory(systemBundle);
        registerDocumentBuilderFactory(systemBundle);
        systemBundle.getBundleContext().registerService(PackageAdmin.class, new PackageAdminImpl(bundleManager), null);
        systemBundle.getBundleContext().registerService(Logger.class, logger, null);
        InternalLoggerFactory internalLoggerFactory = new InternalLoggerFactoryImpl();
        systemBundle.getBundleContext().registerService(InternalLoggerFactory.class, internalLoggerFactory, null);
        systemBundle.getBundleContext().registerService(LogService.class, new LogServiceFactory(internalLoggerFactory), null);
        final Set<Bundle> autostartBundles = new HashSet<>();
        for (AbstractBundle bundle : bundleManager.getBundles()) {
            if ("true".equals(bundle.getHeaderValue("Cosmos-AutoStart"))) {
                autostartBundles.add(bundle);
            }
        }
        ResourceUtil.processResources("META-INF/cosmos-autostart-bundles.list", new ResourceProcessor() {
            @Override
            public void process(URL url, InputStream in) throws IOException, BundleException {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        Bundle bundle = bundleManager.getBundle(line);
                        if (bundle == null) {
                            throw new BundleException(String.format("Bundle %s listed in %s not found", line, url));
                        }
                        autostartBundles.add(bundle);
                    }
                }
            }
        });
        Bundle scrBundle = bundleManager.getBundle("org.apache.felix.scr");
        if (scrBundle != null) {
            // Always auto-start the Declarative Services implementation if it's available. Do this
            // immediately so that declarative services are registered before other bundles start
            // (Not all bundles are designed to handle services becoming available after they
            // start).
            scrBundle.start();
        }
        for (Bundle bundle : autostartBundles) {
            bundle.start();
        }
    }

    private void registerSAXParserFactory(Bundle bundle) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        Hashtable<String,Object> props = new Hashtable<String,Object>();
        new XMLParserActivator().setSAXProperties(factory, props);
        bundle.getBundleContext().registerService(SAXParserFactory.class, factory, props);
    }
    
    private void registerDocumentBuilderFactory(Bundle bundle) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Hashtable<String,Object> props = new Hashtable<String,Object>();
        new XMLParserActivator().setDOMProperties(factory, props);
        bundle.getBundleContext().registerService(DocumentBuilderFactory.class, factory, props);
    }
    
    private void loadProperties(String resourceName) throws BundleException {
        ResourceUtil.processResources(resourceName, new ResourceProcessor() {
            @Override
            public void process(URL url, InputStream in) throws IOException {
                properties.load(in);
            }
        });
    }
    
    public static synchronized CosmosRuntime getInstance() throws BundleException {
        if (instance == null) {
            Patcher.patch();
            instance = new CosmosRuntime();
        }
        return instance;
    }
    
    String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            System.getProperty(key);
        }
        if (value == null && logger.isDebugEnabled()) {
            logger.debug("No value for property " + key);
        }
        return value;
    }
    
    public <T> T getService(Class<T> clazz) {
        ServiceReference<T> ref = serviceRegistry.getServiceReference(clazz.getName(), null, clazz);
        // TODO: need a system/framework bundle here
        return ref == null ? null : ((ServiceReferenceImpl<T>)ref).getService(null);
    }
    
    public void dispose() {
        AbstractBundle[] bundles = bundleManager.getBundles();
        List<BundleImpl> bundlesToStop = new ArrayList<>(bundles.length);
        for (AbstractBundle bundle : bundles) {
            if (bundle instanceof BundleImpl && bundle.getState() == Bundle.ACTIVE) {
                bundlesToStop.add((BundleImpl)bundle);
            }
        }
        Collections.sort(bundlesToStop, new Comparator<BundleImpl>() {
            @Override
            public int compare(BundleImpl o1, BundleImpl o2) {
                return o2.getStartOrder() - o1.getStartOrder();
            }
        });
        for (BundleImpl bundle : bundlesToStop) {
            try {
                bundle.stop();
            } catch (BundleException ex) {
                logger.warn(String.format("Failed to stop bundle %s", bundle.getSymbolicName()), ex);
            }
        }
        synchronized (CosmosRuntime.class) {
            instance = null;
        }
    }
}

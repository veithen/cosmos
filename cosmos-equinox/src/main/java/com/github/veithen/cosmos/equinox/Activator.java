/*-
 * #%L
 * Cosmos
 * %%
 * Copyright (C) 2012 - 2022 Andreas Veithen
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
package com.github.veithen.cosmos.equinox;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Dictionary;
import java.util.Hashtable;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.osgi.signedcontent.SignedContentFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.veithen.cosmos.equinox.datalocation.LocationImpl;
import com.github.veithen.cosmos.equinox.signedcontent.DummySignedContentFactory;

public class Activator implements BundleActivator {
    private static final Logger logger = LoggerFactory.getLogger(Activator.class);

    // Instantiate this eagerly so that running the shutdown hook doesn't trigger
    // NoClassDefFoundError if the class loader is closed.
    private static final FileVisitor<Path> deletingFileVisitor =
            new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                        throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            };

    private Path instanceArea;
    private Thread shutdownHook;

    @Override
    public void start(BundleContext context) throws Exception {
        if ("true".equals(context.getProperty("cosmos.equinox.disableSignatureValidation"))) {
            context.registerService(
                    SignedContentFactory.class, new DummySignedContentFactory(), null);
        }
        if ("true".equals(context.getProperty("cosmos.equinox.createTempInstanceArea"))) {
            try {
                instanceArea = Files.createTempDirectory("osgi.instance");
                shutdownHook =
                        new Thread() {
                            @Override
                            public void run() {
                                deleteInstanceArea();
                            }
                        };
                Runtime.getRuntime().addShutdownHook(shutdownHook);
                LocationImpl location = new LocationImpl(null, null, false);
                URL url = instanceArea.toUri().toURL();
                location.set(url, false);
                Dictionary<String, Object> properties = new Hashtable<>();
                properties.put(Location.SERVICE_PROPERTY_TYPE, Location.INSTANCE_AREA_TYPE);
                properties.put(Location.SERVICE_PROPERTY_URL, url.toExternalForm());
                context.registerService(Location.class, location, properties);
            } catch (IOException ex) {
                throw new RuntimeException(
                        "Unable to create temporary directory for instance data", ex);
            }
        }
    }

    private void deleteInstanceArea() {
        try {
            Files.walkFileTree(instanceArea, deletingFileVisitor);
        } catch (IOException ex) {
            logger.error(String.format("Failed to delete directory %s", instanceArea), ex);
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (instanceArea != null) {
            deleteInstanceArea();
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
        }
    }
}

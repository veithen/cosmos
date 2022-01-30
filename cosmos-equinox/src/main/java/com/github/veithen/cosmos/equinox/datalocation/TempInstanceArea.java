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
package com.github.veithen.cosmos.equinox.datalocation;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = {Location.class},
        property = {"type=osgi.instance.area"},
        xmlns = "http://www.osgi.org/xmlns/scr/v1.1.0")
public final class TempInstanceArea extends AbstractLocation {
    private static final Logger logger = LoggerFactory.getLogger(TempInstanceArea.class);

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

    private Path defaultDirectory;
    private URL defaultUrl;
    private Thread shutdownHook;

    public TempInstanceArea() {
        super(null, false);
    }

    @Deactivate
    private void deactivate() {
        if (defaultDirectory != null) {
            delete();
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
        }
    }

    @Override
    public synchronized URL getDefault() {
        if (defaultUrl == null) {
            try {
                defaultDirectory = Files.createTempDirectory("osgi.instance");
                shutdownHook =
                        new Thread() {
                            @Override
                            public void run() {
                                delete();
                            }
                        };
                Runtime.getRuntime().addShutdownHook(shutdownHook);
                defaultUrl = defaultDirectory.toUri().toURL();
            } catch (IOException ex) {
                throw new RuntimeException(
                        "Unable to create temporary directory for instance data", ex);
            }
        }
        return defaultUrl;
    }

    private void delete() {
        try {
            Files.walkFileTree(defaultDirectory, deletingFileVisitor);
        } catch (IOException ex) {
            logger.error(String.format("Failed to delete directory %s", defaultDirectory), ex);
        }
    }
}

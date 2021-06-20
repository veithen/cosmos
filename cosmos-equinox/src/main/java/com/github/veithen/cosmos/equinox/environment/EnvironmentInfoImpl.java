/*-
 * #%L
 * Cosmos
 * %%
 * Copyright (C) 2012 - 2021 Andreas Veithen
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
package com.github.veithen.cosmos.equinox.environment;

import java.io.File;

import org.eclipse.osgi.service.environment.EnvironmentInfo;
import org.osgi.service.component.annotations.Component;

@Component(
        service = {EnvironmentInfo.class},
        xmlns = "http://www.osgi.org/xmlns/scr/v1.1.0")
public class EnvironmentInfoImpl implements EnvironmentInfo {
    @Override
    public String[] getCommandLineArgs() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getFrameworkArgs() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getNonFrameworkArgs() {
        // Prevent the Equinox framework from accessing files in ~/.eclipse.
        // TODO: find a better way to do this
        return new String[] {
            "-eclipse.keyring",
            new File(System.getProperty("java.io.tmpdir"), "secure_storage").toString()
        };
    }

    @Override
    public String getOSArch() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getNL() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getOS() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getWS() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean inDebugMode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean inDevelopmentMode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProperty(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String setProperty(String key, String value) {
        throw new UnsupportedOperationException();
    }
}

/*-
 * #%L
 * Cosmos
 * %%
 * Copyright (C) 2012 - 2024 Andreas Veithen
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

import org.osgi.framework.Bundle;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.packageadmin.RequiredBundle;

@SuppressWarnings("deprecation")
final class PackageAdminImpl implements PackageAdmin {
    private final BundleManager bundleManager;

    PackageAdminImpl(BundleManager bundleManager) {
        this.bundleManager = bundleManager;
    }

    @Override
    public ExportedPackage[] getExportedPackages(Bundle bundle) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ExportedPackage[] getExportedPackages(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ExportedPackage getExportedPackage(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void refreshPackages(Bundle[] bundles) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean resolveBundles(Bundle[] bundles) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RequiredBundle[] getRequiredBundles(String symbolicName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle[] getBundles(String symbolicName, String versionRange) {
        Bundle bundle = bundleManager.getBundle(symbolicName);
        if (bundle == null) {
            return null;
        } else if (versionRange != null) {
            throw new UnsupportedOperationException();
        } else {
            return new Bundle[] {bundle};
        }
    }

    @Override
    public Bundle[] getFragments(Bundle bundle) {
        return null;
    }

    @Override
    public Bundle[] getHosts(Bundle bundle) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle getBundle(@SuppressWarnings("rawtypes") Class clazz) {
        return bundleManager.getBundle(clazz);
    }

    @Override
    public int getBundleType(Bundle bundle) {
        return 0;
    }
}

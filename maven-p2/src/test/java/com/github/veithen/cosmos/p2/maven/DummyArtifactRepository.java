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

import java.io.OutputStream;
import java.net.URI;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.internal.p2.metadata.ArtifactKey;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.IQueryable;
import org.eclipse.equinox.p2.repository.IRunnableWithProgress;
import org.eclipse.equinox.p2.repository.artifact.IArtifactDescriptor;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRequest;

public class DummyArtifactRepository implements IArtifactRepository {
    public static final IArtifactRepository INSTANCE = new DummyArtifactRepository();

    private DummyArtifactRepository() {}

    @Override
    public URI getLocation() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getVersion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProvider() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, String> getProperties() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProperty(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IProvisioningAgent getProvisioningAgent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isModifiable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String setProperty(String key, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String setProperty(String key, String value, IProgressMonitor monitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getAdapter(Class adapter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IQueryResult<IArtifactKey> query(IQuery<IArtifactKey> query, IProgressMonitor monitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IArtifactDescriptor createArtifactDescriptor(IArtifactKey key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IArtifactKey createArtifactKey(String classifier, String id, Version version) {
        return new ArtifactKey(classifier, id, version);
    }

    @Override
    public void addDescriptor(IArtifactDescriptor descriptor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addDescriptor(IArtifactDescriptor descriptor, IProgressMonitor monitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addDescriptors(IArtifactDescriptor[] descriptors) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addDescriptors(IArtifactDescriptor[] descriptors, IProgressMonitor monitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(IArtifactDescriptor descriptor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(IArtifactKey key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IStatus getArtifact(IArtifactDescriptor descriptor, OutputStream destination,
            IProgressMonitor monitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IStatus getRawArtifact(IArtifactDescriptor descriptor, OutputStream destination,
            IProgressMonitor monitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IArtifactDescriptor[] getArtifactDescriptors(IArtifactKey key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IStatus getArtifacts(IArtifactRequest[] requests, IProgressMonitor monitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OutputStream getOutputStream(IArtifactDescriptor descriptor) throws ProvisionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IQueryable<IArtifactDescriptor> descriptorQueryable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAll(IProgressMonitor monitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeDescriptor(IArtifactDescriptor descriptor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeDescriptor(IArtifactDescriptor descriptor, IProgressMonitor monitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeDescriptor(IArtifactKey key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeDescriptor(IArtifactKey key, IProgressMonitor monitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeDescriptors(IArtifactDescriptor[] descriptors) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeDescriptors(IArtifactDescriptor[] descriptors, IProgressMonitor monitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeDescriptors(IArtifactKey[] keys) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeDescriptors(IArtifactKey[] keys, IProgressMonitor monitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IStatus executeBatch(IRunnableWithProgress runnable, IProgressMonitor monitor) {
        throw new UnsupportedOperationException();
    }
}

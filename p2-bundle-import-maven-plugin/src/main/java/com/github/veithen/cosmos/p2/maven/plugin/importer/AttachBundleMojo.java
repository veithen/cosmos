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

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name="attach-bundle")
public class AttachBundleMojo extends AbstractImportMojo {
    @Parameter(property="project.artifact")
    private Artifact projectArtifact;

    @Parameter(defaultValue="${project.build.directory}/${project.build.finalName}.jar", required=true)
    private File outputLocation;

    @Override
    protected File getOutputLocation() {
        return outputLocation;
    }

    @Override
    protected void processArtifact(File file) {
        projectArtifact.setFile(file);
    }
}

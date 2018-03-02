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
package com.github.veithen.cosmos.maven;

import java.io.File;

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

@Mojo(name="attach-sources")
public class AttachSourcesMojo extends AbstractImportMojo {
    @Component
    private MavenProjectHelper projectHelper;

    @Parameter(property="project")
    private MavenProject project;

    @Parameter(defaultValue="${project.build.directory}/${project.build.finalName}-sources.jar", required=true)
    private File outputLocation;

    @Override
    protected String transformBundleId(String bundleId) {
        return bundleId + ".source";
    }

    @Override
    protected File getOutputLocation() {
        return outputLocation;
    }

    @Override
    protected void processArtifact(File file) {
        projectHelper.attachArtifact(project, file, "sources");
    }
}

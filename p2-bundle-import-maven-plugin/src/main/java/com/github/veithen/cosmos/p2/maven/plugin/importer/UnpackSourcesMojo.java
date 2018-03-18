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

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;

@Mojo(name="unpack-sources", defaultPhase=LifecyclePhase.GENERATE_SOURCES)
public class UnpackSourcesMojo extends AbstractImportMojo {
    @Component(hint="jar")
    private UnArchiver jarUnArchiver;

    @Parameter(property="project", required=true, readonly=true)
    private MavenProject project;

    @Parameter(defaultValue="${project.build.directory}/bundle-sources", required=true)
    private File outputDirectory;

    @Parameter
    private String[] includes = new String[] { "**/*.java" };

    @Parameter
    private String[] excludes;

    @Override
    protected String transformBundleId(String bundleId) {
        return bundleId + ".source";
    }

    @Override
    protected File getOutputLocation() {
        return null;
    }

    @Override
    protected void processArtifact(File file) {
        outputDirectory.mkdirs();
        jarUnArchiver.setSourceFile(file);
        IncludeExcludeFileSelector selector = new IncludeExcludeFileSelector();
        selector.setIncludes(includes);
        selector.setExcludes(excludes);
        jarUnArchiver.setFileSelectors(new FileSelector[] { selector });
        jarUnArchiver.setDestDirectory(outputDirectory);
        jarUnArchiver.extract();
        project.getCompileSourceRoots().add(outputDirectory.getPath());
    }
}

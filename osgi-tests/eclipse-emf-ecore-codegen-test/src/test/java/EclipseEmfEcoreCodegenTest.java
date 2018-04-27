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
import java.io.File;
import java.util.HashMap;

import org.eclipse.core.internal.resources.ProjectDescription;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.codegen.ecore.generator.Generator;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.veithen.cosmos.osgi.testing.CosmosRunner;

@RunWith(CosmosRunner.class)
public class EclipseEmfEcoreCodegenTest {
    @Test
    public void test() throws Exception {
        ResourceSet set = new ResourceSetImpl();
        Resource res = set.getResource(URI.createFileURI(new File("src/test/ecore/My.genmodel").getAbsolutePath()), true);
        res.load(new HashMap<>());
        GenModel genmodel = (GenModel)res.getAllContents().next();
        genmodel.reconcile();
        IProgressMonitor progressMonitor = new NullProgressMonitor();
        File outputDirectory = new File("target/out");
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject project = root.getProject("out");
        ProjectDescription projectDescription = new ProjectDescription();
        projectDescription.setName(project.getName());
        projectDescription.setLocationURI(outputDirectory.toURI());
        project.create(projectDescription, progressMonitor);
        project.open(progressMonitor);
        Generator gen = new Generator();
        gen.setInput(genmodel);
        genmodel.setCanGenerate(true);
        genmodel.setModelDirectory("/out");
        Monitor monitor = new BasicMonitor.Printing(System.out);
        gen.generate(genmodel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE, monitor);
    }
}

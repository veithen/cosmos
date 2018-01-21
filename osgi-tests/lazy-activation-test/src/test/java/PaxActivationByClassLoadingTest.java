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
import static org.junit.Assert.assertEquals;
import static org.ops4j.pax.exam.CoreOptions.frameworkProperty;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.MavenUtils.asInProject;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import bundle2.MyService;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class PaxActivationByClassLoadingTest {
    @Inject
    private BundleContext bundleContext;
    
    @Configuration
    public static Option[] configuration() {
        return options(
                mavenBundle().groupId("com.github.veithen.cosmos").artifactId("bundle1").version(asInProject()).start(false),
                mavenBundle().groupId("com.github.veithen.cosmos").artifactId("bundle2").version(asInProject()).start(false),
                junitBundles(),
                frameworkProperty("foo").value("bar"));
    }
    
    @Test
    public void test() throws Exception {
        Bundle bundle1 = OSGiUtil.getBundle(bundleContext, "bundle1");
        Bundle bundle2 = OSGiUtil.getBundle(bundleContext, "bundle2");
        
        // Bundles with lazy activation policy are in state STARTING if they have
        // not yet been activated.
        assertEquals(Bundle.STARTING, bundle1.getState());
        assertEquals(Bundle.RESOLVED, bundle2.getState());
        
        bundle2.start();
        MyService myService = (MyService)bundleContext.getService(bundleContext.getServiceReference(MyService.class.getName()));
        assertEquals("bar", myService.getProperty("foo"));
        
        // bundle1 should now have been activated automatically
        assertEquals(Bundle.ACTIVE, bundle1.getState());
    }
}

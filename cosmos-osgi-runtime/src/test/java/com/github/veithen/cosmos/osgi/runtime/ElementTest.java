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
package com.github.veithen.cosmos.osgi.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

import org.junit.Test;

import com.github.veithen.cosmos.osgi.runtime.Element;

public class ElementTest {
    @Test
    public void test() throws Exception {
        Map<String,Element[]> headers = new HashMap<String,Element[]>();
        InputStream in = ElementTest.class.getResourceAsStream("manifest1");
        try {
            Manifest manifest = new Manifest(in);
            for (Map.Entry<?,?> entry : manifest.getMainAttributes().entrySet()) {
                headers.put(((Name)entry.getKey()).toString(), Element.parseHeaderValue((String)entry.getValue()));
            }
        } finally {
            in.close();
        }
        assertHeaderValue(headers.get("Bundle-Activator"), "org.eclipse.osgi.framework.internal.core.SystemBundleActivator", false, false);
        assertHeaderValue(headers.get("Bundle-DocUrl"), "http://www.eclipse.org", false, false);
        
        Element[] elements = headers.get("Bundle-SymbolicName");
        assertHeaderValue(elements, "org.eclipse.osgi", false, true);
        assertEquals("true", elements[0].getDirective("singleton"));
        
        elements = headers.get("Bundle-RequiredExecutionEnvironment");
        assertEquals(2, elements.length);
        assertEquals("J2SE-1.5", elements[0].getValue());
        assertEquals("OSGi/Minimum-1.2", elements[1].getValue());
        
        elements = headers.get("Export-Package");
        assertTrue(elements.length > 1);
        assertEquals("org.eclipse.osgi.event", elements[0].getValue());
        assertEquals("1.0", elements[0].getAttribute("version"));
    }
    
    private static void assertHeaderValue(Element[] elements, String value, boolean hasAttributes, boolean hasDirectives) throws Exception {
        assertEquals(1, elements.length);
        Element element = elements[0];
        assertEquals(value, element.getValue());
        assertEquals(hasAttributes, element.hasAttributes());
        assertEquals(hasDirectives, element.hasDirectives());
    }
}

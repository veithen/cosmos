package com.github.veithen.cosmos.solstice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

import org.junit.Assert;
import org.junit.Test;

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

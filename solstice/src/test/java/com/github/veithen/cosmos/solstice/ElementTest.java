package com.github.veithen.cosmos.solstice;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

import org.junit.Test;

public class ElementTest {
    @Test
    public void test() throws Exception {
        Map<String,List<Element>> headers = new HashMap<String,List<Element>>();
        InputStream in = ElementTest.class.getResourceAsStream("manifest1");
        try {
            Manifest manifest = new Manifest(in);
            for (Map.Entry<?,?> entry : manifest.getMainAttributes().entrySet()) {
                headers.put(((Name)entry.getKey()).toString(), Arrays.asList(Element.parseHeaderValue((String)entry.getValue())));
            }
        } finally {
            in.close();
        }
        System.out.println(headers);
    }
}

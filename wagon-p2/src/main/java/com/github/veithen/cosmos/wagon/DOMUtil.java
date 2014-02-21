package com.github.veithen.cosmos.wagon;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

public final class DOMUtil {
    private static final DocumentBuilder documentBuilder;
    
    static {
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new Error(ex);
        }
    }
    
    private DOMUtil() {}
    
    public static Document createDocument() {
        return documentBuilder.newDocument();
    }
}

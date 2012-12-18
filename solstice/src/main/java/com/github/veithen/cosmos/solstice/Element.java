package com.github.veithen.cosmos.solstice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleException;

final class Element {
    private String[] values;
    private Map<String,String> attributes;
    private Map<String,String> directives;

    private Element(String[] values, Map<String,String> attributes, Map<String,String> directives) {
        this.values = values;
        this.attributes = attributes;
        this.directives = directives;
    }
    
    static Element[] parseHeaderValue(String value) throws ParseException {
        Tokenizer tokenizer = new Tokenizer(value);
        List<Element> elements = new ArrayList<Element>();
        while (true) {
            List<String> values = new ArrayList<String>();
            Map<String,String> attributes = new HashMap<String,String>();
            Map<String,String> directives = new HashMap<String,String>();
            while (true) {
                String token = tokenizer.getToken(";,=:");
                int c = tokenizer.getChar();
                switch (c) {
                    case '=':
                        attributes.put(token, tokenizer.getString(";,"));
                        c = tokenizer.getChar();
                        break;
                    case ':':
                        if (tokenizer.skipIf('=')) {
                            directives.put(token, tokenizer.getString(";,"));
                        } else {
                            values.add(token + ":" + tokenizer.getToken(";,"));
                        }
                        c = tokenizer.getChar();
                        break;
                    default:
                        values.add(token);
                }
                if (c != ';') {
                    elements.add(new Element(values.toArray(new String[values.size()]), attributes, directives));
                    if (c == -1) {
                        return elements.toArray(new Element[elements.size()]);
                    } else {
                        break;
                    }
                }
            }
        }
    }
    
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        for (String value : values) {
            if (buffer.length() > 0) {
                buffer.append("; ");
            }
            buffer.append(value);
        }
        for (Map.Entry<String,String> attribute : attributes.entrySet()) {
            if (buffer.length() > 0) {
                buffer.append("; ");
            }
            buffer.append(attribute.getKey());
            buffer.append("=");
            buffer.append(attribute.getValue());
        }
        for (Map.Entry<String,String> directive : directives.entrySet()) {
            if (buffer.length() > 0) {
                buffer.append("; ");
            }
            buffer.append(directive.getKey());
            buffer.append(":=");
            buffer.append(directive.getValue());
        }
        return buffer.toString();
    }

    String getValue() throws BundleException {
        if (values.length == 1) {
            return values[0];
        } else {
            throw new BundleException("Expected only a single header value");
        }
    }
    
    boolean hasAttributes() {
        return !attributes.isEmpty();
    }
    
    boolean hasDirectives() {
        return !directives.isEmpty();
    }

    String getAttribute(String name) {
        return attributes.get(name);
    }

    String getDirective(String name) {
        return directives.get(name);
    }
}

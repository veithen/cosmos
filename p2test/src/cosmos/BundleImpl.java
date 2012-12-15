package cosmos;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

public class BundleImpl implements Bundle {
    private final Runtime runtime;
    private final long id;
    private final String symbolicName;
    private final Attributes attrs;
    private final URL rootUrl;
    private final File data;
    private int state;
    private BundleContextImpl context;

    public BundleImpl(Runtime runtime, long id, String symbolicName, Attributes attrs, URL rootUrl, File data) {
        this.runtime = runtime;
        this.id = id;
        this.symbolicName = symbolicName;
        this.attrs = attrs;
        this.rootUrl = rootUrl;
        this.data = data;
        state = Bundle.RESOLVED;
    }

    public Runtime getRuntime() {
        return runtime;
    }

    public long getBundleId() {
        return id;
    }

    public String getSymbolicName() {
        return symbolicName;
    }

    public int getState() {
        return state;
    }

    public Dictionary<String,String> getHeaders() {
        Hashtable<String,String> headers = new Hashtable<String,String>();
        for (Map.Entry<Object,Object> entry : attrs.entrySet()) {
            headers.put(((Name)entry.getKey()).toString(), (String)entry.getValue());
        }
        return headers;
    }

    public Dictionary<String,String> getHeaders(String locale) {
        // TODO
        return getHeaders();
    }

    public void start(int options) throws BundleException {
        throw new UnsupportedOperationException();
    }

    public void start() throws BundleException {
        System.out.println("Starting bundle " + symbolicName + " ...");
        runtime.fireBundleEvent(this, BundleEvent.STARTING);
        String activatorClassName = attrs.getValue("Bundle-Activator");
        if (activatorClassName != null) {
            BundleActivator activator;
            try {
                activator = (BundleActivator)Class.forName(activatorClassName).newInstance();
            } catch (Exception ex) {
                throw new BundleException("Failed to instantiate bundle activator " + activatorClassName, ex);
            }
            context = new BundleContextImpl(this);
            try {
                activator.start(context);
            } catch (Exception ex) {
                throw new BundleException("Failed to start bundle " + symbolicName, ex);
            }
        }
        state = Bundle.ACTIVE;
        runtime.fireBundleEvent(this, BundleEvent.STARTED);
    }

    public void stop(int options) throws BundleException {
        throw new UnsupportedOperationException();
    }

    public void stop() throws BundleException {
        throw new UnsupportedOperationException();
    }

    public int compareTo(Bundle o) {
        throw new UnsupportedOperationException();
    }

    public void update(InputStream input) throws BundleException {
        throw new UnsupportedOperationException();
    }

    public void update() throws BundleException {
        throw new UnsupportedOperationException();
    }

    public void uninstall() throws BundleException {
        throw new UnsupportedOperationException();
    }

    public URL getEntry(String path) {
        // TODO: not correct; need to return null if entry doesn't exist
        try {
            return new URL(rootUrl, path);
        } catch (MalformedURLException ex) {
            return null;
        }
    }

    public Enumeration<String> getEntryPaths(String path) {
        throw new UnsupportedOperationException();
    }

    public Enumeration<URL> findEntries(String path, String filePattern, boolean recurse) {
        if (!recurse && filePattern.indexOf('*') == -1 && filePattern.indexOf('?') == -1) {
            Vector<URL> entries = new Vector<URL>();
            URL entry = getEntry(path + "/" + filePattern);
            if (entry != null) {
                entries.add(entry);
            }
            return entries.elements();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public String getLocation() {
        throw new UnsupportedOperationException();
    }

    public ServiceReference<?>[] getRegisteredServices() {
        throw new UnsupportedOperationException();
    }

    public ServiceReference<?>[] getServicesInUse() {
        throw new UnsupportedOperationException();
    }

    public boolean hasPermission(Object permission) {
        throw new UnsupportedOperationException();
    }

    public URL getResource(String name) {
        throw new UnsupportedOperationException();
    }

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }

    public Enumeration<URL> getResources(String name) throws IOException {
        throw new UnsupportedOperationException();
    }

    public long getLastModified() {
        // We never modify bundles, so we may as well return 0 here
        return 0;
    }

    public BundleContext getBundleContext() {
        return context;
    }

    public Map<X509Certificate,List<X509Certificate>> getSignerCertificates(int signersType) {
        throw new UnsupportedOperationException();
    }

    public Version getVersion() {
        throw new UnsupportedOperationException();
    }

    public <A> A adapt(Class<A> type) {
        throw new UnsupportedOperationException();
    }

    public File getDataFile(String filename) {
        return filename == null ? data : new File(data, filename);
    }
}

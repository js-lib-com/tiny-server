package js.tiny.server.servlet;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

public class ServletContextImpl implements ServletContext {
	private final Map<String, Object> attributes;

	public ServletContextImpl() {
		this.attributes = new HashMap<>();
	}

	@Override
	public String getContextPath() {
		// tiny server does not support multiple contexts; always root
		return "";
	}

	@Override
	public ServletContext getContext(String uripath) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getMajorVersion() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getMinorVersion() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getEffectiveMajorVersion() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getEffectiveMinorVersion() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getMimeType(String file) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> getResourcePaths(String path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public URL getResource(String path) throws MalformedURLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public RequestDispatcher getNamedDispatcher(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Servlet getServlet(String name) throws ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<Servlet> getServlets() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<String> getServletNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void log(String msg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void log(Exception exception, String msg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void log(String message, Throwable throwable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRealPath(String path) {
		return null;
	}

	@Override
	public String getServerInfo() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getInitParameter(String name) {
		return null;
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return Collections.emptyEnumeration();
	}

	@Override
	public boolean setInitParameter(String name, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		Iterator<String> iterator = attributes.keySet().iterator();
		return new Enumeration<String>() {
			@Override
			public String nextElement() {
				return iterator.next();
			}

			@Override
			public boolean hasMoreElements() {
				return iterator.hasNext();
			}
		};
	}

	@Override
	public void setAttribute(String name, Object object) {
		attributes.put(name, object);
	}

	@Override
	public void removeAttribute(String name) {
		attributes.remove(name);
	}

	@Override
	public String getServletContextName() {
		// tiny server does not support multiple contexts; always root
		return "";
	}

	@Override
	public Dynamic addServlet(String servletName, String className) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Dynamic addServlet(String servletName, Servlet servlet) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ServletRegistration getServletRegistration(String servletName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, ? extends ServletRegistration> getServletRegistrations() {
		throw new UnsupportedOperationException();
	}

	@Override
	public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className) {
		throw new UnsupportedOperationException();
	}

	@Override
	public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
		throw new UnsupportedOperationException();
	}

	@Override
	public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public FilterRegistration getFilterRegistration(String filterName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionCookieConfig getSessionCookieConfig() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addListener(String className) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends EventListener> void addListener(T t) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addListener(Class<? extends EventListener> listenerClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public JspConfigDescriptor getJspConfigDescriptor() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ClassLoader getClassLoader() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void declareRoles(String... roleNames) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getVirtualServerName() {
		throw new UnsupportedOperationException();
	}
}

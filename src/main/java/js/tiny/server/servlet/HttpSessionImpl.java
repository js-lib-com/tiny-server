package js.tiny.server.servlet;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

@SuppressWarnings("deprecation")
public class HttpSessionImpl implements HttpSession {
	private final String ID;
	private final long creationTime;

	private final Map<String, Object> attributes;

	public HttpSessionImpl(String ID) {
		this.ID = ID;
		this.creationTime = System.currentTimeMillis();
		this.attributes = Collections.synchronizedMap(new HashMap<>());
	}

	@Override
	public long getCreationTime() {
		return creationTime;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public long getLastAccessedTime() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ServletContext getServletContext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getMaxInactiveInterval() {
		throw new UnsupportedOperationException();
	}

	@Override
	public HttpSessionContext getSessionContext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	@Override
	public Object getValue(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return new StringsEnumeration(attributes.keySet());
	}

	@Override
	public String[] getValueNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
	}

	@Override
	public void putValue(String name, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeAttribute(String name) {
		attributes.remove(name);
	}

	@Override
	public void removeValue(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void invalidate() {
		attributes.clear();
	}

	@Override
	public boolean isNew() {
		return false;
	}
}

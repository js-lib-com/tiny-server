package com.jslib.tiny.server.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;
import com.jslib.api.log.Log;
import com.jslib.api.log.LogFactory;
import com.jslib.util.Strings;

public class HttpServletRequestImpl implements HttpServletRequest {
	private static final Log log = LogFactory.getLog(HttpServletRequestImpl.class);

	private static final int CR = '\r';
	private static final int LF = '\n';
	private static final int EOF = -1;

	private final ServletContext context;

	private final SessionManager sessionManager;

	/** Bytes stream containing entire HTTP request: start line, headers, empty line separator and body. */
	private final BufferedInputStream stream;

	private final String remoteAddr;
	private final Map<String, List<String>> headers;
	private final Map<String, String> parameters;
	private final Map<String, Object> attributes;

	private final String method;
	private final String requestURI;
	private final String queryString;

	private Cookie[] cookies;
	private String sessionID;
	private AsyncContext asyncContext;

	public HttpServletRequestImpl(ServletContext context, SessionManager sessionManager, InputStream inputStream, String remoteAddress) throws IOException {
		log.trace("HttpServletRequestImpl(ServletContext, SessionManager, InputStream, String)");
		this.context = context;
		this.sessionManager = sessionManager;
		this.stream = new BufferedInputStream(inputStream);
		this.remoteAddr = remoteAddress;

		this.headers = new HashMap<>();
		this.parameters = new HashMap<>();
		this.attributes = new HashMap<>();

		String line = readLine();
		if (line == null) {
			throw new IOException("Peer closed.");
		}

		int beginIndex = line.indexOf(' ') + 1;
		this.method = line.substring(0, beginIndex - 1);

		int endIndex = line.lastIndexOf(' ');
		if (endIndex < beginIndex) {
			endIndex = line.length();
		}
		String requestURI = line.substring(beginIndex, endIndex);
		int queryStringSeparatorPosition = requestURI.lastIndexOf('?');
		if (queryStringSeparatorPosition != -1) {
			requestURI = requestURI.substring(0, queryStringSeparatorPosition);
			this.queryString = requestURI.substring(queryStringSeparatorPosition + 1);
		} else {
			this.queryString = null;
		}
		this.requestURI = requestURI;

		// TODO: update headers parser to consider values continuing on next line (starts with white space)
		while ((line = readLine()) != null && !line.isEmpty()) {
			int separatorIndex = line.indexOf(':');
			String name = line.substring(0, separatorIndex).trim();
			List<String> values = Strings.split(line.substring(separatorIndex + 1).trim(), ',');

			List<String> existingValues = headers.get(name);
			if (existingValues == null) {
				headers.put(name, values);
				continue;
			}
			existingValues.addAll(values);
		}

		log.debug("Request URI: {http_request}", requestURI);
		if (this.queryString != null) {
			for (String pair : this.queryString.split("&")) {
				final int valueSeparatorPosition = pair.indexOf("=");
				final String name = URLDecoder.decode(pair.substring(0, valueSeparatorPosition), "UTF-8");
				final String value = URLDecoder.decode(pair.substring(valueSeparatorPosition + 1), "UTF-8");
				parameters.put(name, value);
			}
		}
	}

	private String readLine() throws IOException {
		int c = stream.read();
		if (c == EOF) {
			return null;
		}
		StringBuilder line = new StringBuilder();
		boolean foundCR = false;

		LINE_LOOP: for (;;) {
			switch (c) {
			case EOF:
				break LINE_LOOP;

			case CR:
				foundCR = true;
				break;

			case LF:
				if (foundCR) {
					break LINE_LOOP;
				}
				line.append(LF);
				break;

			default:
				if (foundCR) {
					line.append(CR);
					foundCR = false;
				}
				line.append((char) c);
			}
			c = stream.read();
		}

		return line.toString();
	}

	@Override
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return new StringsEnumeration(attributes.keySet());
	}

	@Override
	public String getCharacterEncoding() {
		return "UTF-8";
	}

	@Override
	public void setCharacterEncoding(String characterEncoding) throws UnsupportedEncodingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getContentLength() {
		return (int) getContentLengthLong();
	}

	@Override
	public long getContentLengthLong() {
		String contentLength = getHeader(Headers.CONTENT_LENGTH);
		return contentLength != null ? Long.parseLong(contentLength) : 0L;
	}

	@Override
	public String getContentType() {
		return getHeader(Headers.CONTENT_TYPE);
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return new ServletInputStreamImpl(stream, getContentLengthLong());
	}

	@Override
	public String getParameter(String name) {
		return parameters.get(name);
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return new StringsEnumeration(parameters.keySet());
	}

	@Override
	public String[] getParameterValues(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getProtocol() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getScheme() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getServerName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getServerPort() {
		throw new UnsupportedOperationException();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRemoteAddr() {
		return remoteAddr;
	}

	@Override
	public String getRemoteHost() {
		return remoteAddr;
	}

	@Override
	public void setAttribute(String name, Object o) {
		attributes.put(name, o);
	}

	@Override
	public void removeAttribute(String name) {
		attributes.remove(name);
	}

	@Override
	public Locale getLocale() {
		String locale = getHeader(Headers.ACCEPT_LANGUAGE);
		if (locale == null) {
			return Locale.getDefault();
		}
		List<Locale.LanguageRange> locales = Locale.LanguageRange.parse(locale);
		if (locales.isEmpty()) {
			return Locale.getDefault();
		}
		return Locale.forLanguageTag(locales.get(0).getRange());
	}

	@Override
	public Enumeration<Locale> getLocales() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSecure() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRealPath(String path) {
		return context.getRealPath(path);
	}

	@Override
	public int getRemotePort() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getLocalName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getLocalAddr() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getLocalPort() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ServletContext getServletContext() {
		return context;
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
		asyncContext = new AsyncContextImpl(servletRequest, servletResponse);
		return asyncContext;
	}

	@Override
	public boolean isAsyncStarted() {
		return asyncContext != null;
	}

	@Override
	public boolean isAsyncSupported() {
		return true;
	}

	@Override
	public AsyncContext getAsyncContext() {
		return asyncContext;
	}

	@Override
	public DispatcherType getDispatcherType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getAuthType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cookie[] getCookies() {
		if (cookies != null) {
			return cookies;
		}

		List<Cookie> cookies = new ArrayList<>();
		List<String> cookieHeaders = headers.get(Headers.COOKIE);
		if (cookieHeaders != null) {
			for (String cookiesList : cookieHeaders) {
				for (String nameValuePair : Strings.split(cookiesList, ';')) {
					int valueSeparatorPosition = nameValuePair.indexOf('=');
					if (valueSeparatorPosition == -1) {
						log.debug("Invalid cookie name-value pair |{cookie_value}|. Ignore it.", nameValuePair);
						continue;
					}
					final String name = nameValuePair.substring(0, valueSeparatorPosition).trim();
					final String value = valueSeparatorPosition < nameValuePair.length() ? nameValuePair.substring(valueSeparatorPosition + 1) : "";
					if (SessionManager.SESSION_COOKIE.equals(name)) {
						log.debug("Session ID on cookie: {cookie_session}", value);
						this.sessionID = value;
					}
					cookies.add(new Cookie(name, value));
				}
			}
		}

		this.cookies = cookies.toArray(new Cookie[0]);
		return this.cookies;
	}

	@Override
	public long getDateHeader(String name) {
		String value = getHeader(name);
		if (value == null) {
			return -1L;
		}

		long result = Headers.parseDate(value);
		if (result != -1L) {
			return result;
		}
		throw new IllegalArgumentException(value);
	}

	@Override
	public String getHeader(String name) {
		List<String> values = headers.get(name);
		return values != null ? values.size() > 0 ? values.get(0) : null : null;
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		List<String> values = headers.get(name);
		return values != null ? new StringsEnumeration(values) : Collections.emptyEnumeration();
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		return new StringsEnumeration(headers.keySet());
	}

	@Override
	public int getIntHeader(String name) {
		String value = getHeader(name);
		return value != null ? Integer.parseInt(value) : -1;
	}

	@Override
	public String getMethod() {
		return method;
	}

	@Override
	public String getPathInfo() {
		return requestURI.startsWith("/rest/") ? requestURI.substring(5) : null;
	}

	@Override
	public String getPathTranslated() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getContextPath() {
		return context.getContextPath();
	}

	@Override
	public String getQueryString() {
		return queryString;
	}

	@Override
	public String getRemoteUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUserInRole(String role) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRequestedSessionId() {
		return sessionID;
	}

	@Override
	public String getRequestURI() {
		return requestURI;
	}

	@Override
	public StringBuffer getRequestURL() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getServletPath() {
		throw new UnsupportedOperationException();
	}

	@Override
	public HttpSession getSession(boolean create) {
		if (sessionID == null) {
			sessionID = sessionManager.generateSessionID();
		}
		return sessionManager.getSession(sessionID, create);
	}

	@Override
	public HttpSession getSession() {
		return sessionManager.getSession(sessionID);
	}

	@Override
	public String changeSessionId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return sessionManager.hasSession(sessionID);
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return true;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		return false;
	}

	@Override
	public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void login(String username, String password) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void logout() throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Part getPart(String name) throws IOException, ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
		throw new UnsupportedOperationException();
	}
}

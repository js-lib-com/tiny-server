package com.jslib.tiny.server.servlet;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.jslib.api.log.Log;
import com.jslib.api.log.LogFactory;
import com.jslib.tiny.server.CT;
import com.jslib.util.Params;
import com.jslib.util.Strings;

public class HttpServletResponseImpl implements HttpServletResponse {
	private static final Log log=LogFactory.getLog(HttpServletResponseImpl.class);
	
	private static final String HTTP_VERSION = "HTTP/1.1";
	private static final String LWS = " ";
	private static final String CRLF = "\r\n";

	private final HttpServletRequest request;
	private final BufferedOutputStream outputStream;
	private final Map<String, List<String>> headers;
	private final List<Cookie> cookies;

	private IWriterListener writerListener;

	private int statusCode;
	private boolean commited;

	public HttpServletResponseImpl(HttpServletRequest request, OutputStream outputStream) {
		this.request = request;
		this.outputStream = outputStream instanceof BufferedOutputStream ? (BufferedOutputStream) outputStream : new BufferedOutputStream(outputStream);
		this.headers = new HashMap<String, List<String>>();
		this.cookies = new ArrayList<>();
	}

	@Override
	public String getCharacterEncoding() {
		return "UTF-8";
	}

	@Override
	public String getContentType() {
		return getHeader(Headers.CONTENT_TYPE);
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (!commited) {
			commit();
		}
		return new ServletOutputStreamImpl(outputStream);
	}

	public void setWriterListener(IWriterListener writerListener) {
		this.writerListener = writerListener;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (!commited) {
			commit();
		}
		if (writerListener != null) {
			return new PrintWriterEx(new OutputStreamWriter(outputStream, "UTF-8"), writerListener);
		}
		return new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"));
	}

	@Override
	public void setCharacterEncoding(String charset) {
		Params.EQ(charset, "UTF-8", "Charset");
	}

	@Override
	public void setContentLength(int len) {
		setHeader(Headers.CONTENT_LENGTH, Integer.toString(len));
	}

	@Override
	public void setContentLengthLong(long len) {
		setHeader(Headers.CONTENT_LENGTH, Long.toString(len));
	}

	@Override
	public void setContentType(String type) {
		setHeader(Headers.CONTENT_TYPE, type);
	}

	@Override
	public void setBufferSize(int size) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getBufferSize() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void flushBuffer() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void resetBuffer() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCommitted() {
		return commited;
	}

	@Override
	public void reset() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLocale(Locale loc) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Locale getLocale() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addCookie(Cookie cookie) {
		if (isCommitted()) {
			return;
		}
		cookies.add(cookie);
	}

	@Override
	public boolean containsHeader(String name) {
		return headers.containsKey(name);
	}

	@Override
	public String encodeURL(String url) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String encodeRedirectURL(String url) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String encodeUrl(String url) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String encodeRedirectUrl(String url) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendError(int statusCode) throws IOException {
		this.statusCode = statusCode;
		commit();
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDateHeader(String name, long date) {
		setHeader(name, Headers.formatDate(date));
	}

	@Override
	public void addDateHeader(String name, long date) {
		addHeader(name, Headers.formatDate(date));
	}

	@Override
	public void setHeader(String name, String value) {
		List<String> values = new ArrayList<>();
		values.add(value);
		headers.put(name, values);
	}

	@Override
	public void addHeader(String name, String value) {
		List<String> values = headers.get(name);
		if (values == null) {
			setHeader(name, value);
			return;
		}
		values.add(value);
	}

	@Override
	public void setIntHeader(String name, int value) {
		setHeader(name, Integer.toString(value));
	}

	@Override
	public void addIntHeader(String name, int value) {
		addHeader(name, Integer.toString(value));
	}

	@Override
	public void setStatus(int statusCode) {
		this.statusCode = statusCode;
	}

	@Override
	public void setStatus(int statusCode, String statusMessage) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getStatus() {
		return statusCode;
	}

	@Override
	public String getHeader(String name) {
		List<String> values = headers.get(name);
		return values != null ? values.size() > 0 ? values.get(0) : null : null;
	}

	@Override
	public Collection<String> getHeaders(String name) {
		List<String> values = headers.get(name);
		return values != null ? values : Collections.emptyList();
	}

	@Override
	public Collection<String> getHeaderNames() {
		return headers.keySet();
	}

	private void commit() throws IOException {
		// commit pre-processing: ensure status code and prepare session cookie
		if (statusCode == 0) {
			statusCode = 200;
		}
		String sessionID = request.getRequestedSessionId();
		if (sessionID != null) {
			Cookie cookie = new Cookie(SessionManager.SESSION_COOKIE, sessionID);
			cookie.setPath("/");
			cookie.setSecure(true);
			addCookie(cookie);
		}
		
		// mark response as commited
		commited = true;

		// write status line
		write(HTTP_VERSION);
		write(LWS);
		write(Integer.toString(statusCode));
		String statusMessage = CT.statusMessage(statusCode);
		if (statusMessage != null) {
			write(LWS);
			write(statusMessage);
		}
		write(CRLF);

		// write headers
		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			write(entry.getKey());
			write(": ");
			write(Strings.join(entry.getValue(), ", "));
			write(CRLF);
		}

		// write cookies
		for (Cookie cookie : cookies) {
			write("Set-Cookie: ");
			write(Headers.formatCookie(cookie));
			log.debug("Set-Cookie: {cookies}", Headers.formatCookie(cookie));
			write(CRLF);
		}

		// write empty line to mark headers end
		write(CRLF);

		outputStream.flush();
	}

	private void write(String string) throws IOException {
		outputStream.write(string.getBytes());
	}
}

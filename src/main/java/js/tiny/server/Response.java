package js.tiny.server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP response. Sample response format.
 *
 * <pre>
 * HTTP/1.1 200 OKCRLF
 * Content-Type: application/json;charset=UTF-8CRLF
 * Content-Length: 618CRLF
 * Connection: close
 * CRLF
 * value
 * </pre>
 *
 * @author Iulian Rotaru
 */
public final class Response {
	private static final String HTTP_VERSION = "HTTP/1.1";
	private static final String LWS = " ";
	private static final String CRLF = "\r\n";

	private final BufferedOutputStream stream;

	private ResponseStatus status;
	private Map<String, String> headers = new HashMap<String, String>();
	private boolean commited;

	public Response(OutputStream stream) throws IOException {
		this.stream = new BufferedOutputStream(stream);
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public void setContentType(ContentType contentType) {
		headers.put("Content-Type", contentType.value());
	}

	public void setContentLength(long length) {
		headers.put("Content-Length", Long.toString(length));
	}

	public void setHeader(String name, String value) {
		headers.put(name, value);
	}

	public OutputStream getOutputStream() throws IOException {
		if (!commited) {
			commit();
		}
		return stream;
	}

	public void flush() throws IOException {
		if (!commited) {
			commit();
		}
		stream.flush();
	}

	/**
	 * For response status and headers serialization used bytes stream.
	 *
	 * @throws IOException
	 */
	private void commit() throws IOException {
		commited = true;

		// write status line
		write(HTTP_VERSION);
		write(LWS);
		write(status.value());
		write(CRLF);

		// write headers
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			write(entry.getKey());
			write(": ");
			write(entry.getValue());
			write(CRLF);
		}

		// write empty line to mark headers end
		write(CRLF);

		stream.flush();
	}

	private void write(String string) throws IOException {
		stream.write(string.getBytes());
	}

	public boolean isCommitted() {
		return commited;
	}
}
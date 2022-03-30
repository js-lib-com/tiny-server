package js.tiny.server.servlet;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

final class ServletInputStreamImpl extends ServletInputStream {
	private final InputStream stream;
	private final long contentLength;
	
	private long contentOffset;

	public ServletInputStreamImpl(InputStream stream, long contentLength) {
		super();
		this.stream = stream instanceof BufferedInputStream ? (BufferedInputStream) stream : new BufferedInputStream(stream);
		this.contentLength = contentLength;
		this.contentOffset = 0;
	}

	@Override
	public int read() throws IOException {
		if(contentOffset++ >= contentLength) {
			return -1;
		}
		return stream.read();
	}

	@Override
	public void close() throws IOException {
		stream.close();
	}

	// --------------------------------------------------------------------------------------------
	// Asynchronous mode for Servlet 3.1 -- unused in current implementation

	@Override
	public boolean isFinished() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isReady() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setReadListener(ReadListener readListener) {
		throw new UnsupportedOperationException();
	}
}

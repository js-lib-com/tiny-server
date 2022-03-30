package js.tiny.server.servlet;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

final class ServletOutputStreamImpl extends ServletOutputStream {
	private final BufferedOutputStream stream;

	public ServletOutputStreamImpl(OutputStream stream) {
		this.stream = stream instanceof BufferedOutputStream ? (BufferedOutputStream) stream : new BufferedOutputStream(stream);
	}

	@Override
	public void write(int b) throws IOException {
		stream.write(b);
	}

	@Override
	public void flush() throws IOException {
		stream.flush();
	}

	@Override
	public void close() throws IOException {
		stream.close();
	}

	// --------------------------------------------------------------------------------------------
	// Asynchronous mode for Servlet 3.1 -- unused in current implementation

	@Override
	public boolean isReady() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setWriteListener(WriteListener writeListener) {
		throw new UnsupportedOperationException();
	}
}

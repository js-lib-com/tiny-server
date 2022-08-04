package js.tiny.server.servlet;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.io.Writer;

import com.jslib.api.log.Log;
import com.jslib.api.log.LogFactory;

class PrintWriterEx extends PrintWriter {
	private static final Log log = LogFactory.getLog(PrintWriterEx.class);

	private final IWriterListener listener;

	private boolean error;

	public PrintWriterEx(Writer writer, IWriterListener listener) {
		super(writer);
		this.listener = listener;
	}

	public void write(int c) {
		try {
			out.write(c);
		} catch (InterruptedIOException e) {
			Thread.currentThread().interrupt();
		} catch (IOException e) {
			listener.onError(e);
			log.error(e);
			error = true;
		}
	}

	public void write(char buf[], int off, int len) {
		try {
			out.write(buf, off, len);
		} catch (InterruptedIOException e) {
			Thread.currentThread().interrupt();
		} catch (IOException e) {
			listener.onError(e);
			log.error(e);
			error = true;
		}
	}

	public void write(String s, int off, int len) {
		try {
			out.write(s, off, len);
		} catch (InterruptedIOException e) {
			Thread.currentThread().interrupt();
		} catch (IOException e) {
			listener.onError(e);
			log.error(e);
			error = true;
		}
	}

	public void println() {
		try {
			out.write("\r\n");
			out.flush();
		} catch (InterruptedIOException e) {
			Thread.currentThread().interrupt();
		} catch (IOException e) {
			listener.onError(e);
			log.error(e);
			error = true;
		}
	}

	@Override
	public boolean checkError() {
		if (out != null) {
			flush();
		}
		return error;
	}

	@Override
	public void flush() {
		try {
			out.flush();
		} catch (IOException e) {
			listener.onError(e);
			log.error(e);
			error = true;
		}
	}

	public void close() {
		try {
			if (out == null) {
				return;
			}
			out.close();
			out = null;
		} catch (IOException e) {
			listener.onError(e);
			log.error(e);
			error = true;
		}
	}
}

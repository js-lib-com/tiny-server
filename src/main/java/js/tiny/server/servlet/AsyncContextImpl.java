package js.tiny.server.servlet;

import java.io.IOException;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import js.log.Log;
import js.log.LogFactory;
import js.util.Params;

public class AsyncContextImpl implements AsyncContext, IWriterListener {
	private static final Log log = LogFactory.getLog(AsyncContextImpl.class);

	private final ServletRequest request;
	private final ServletResponse response;

	private long timeout;
	private AsyncListener listener;

	public AsyncContextImpl(ServletRequest request, ServletResponse response) {
		log.trace("AsyncContextImpl(ServletRequest, ServletResponse)");
		this.request = request;
		this.response = response;
		if (response instanceof HttpServletResponseImpl) {
			((HttpServletResponseImpl)response).setWriterListener(this);
		}
	}

	@Override
	public ServletRequest getRequest() {
		return request;
	}

	@Override
	public ServletResponse getResponse() {
		return response;
	}

	@Override
	public boolean hasOriginalRequestAndResponse() {
		return false;
	}

	@Override
	public void dispatch() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void dispatch(String path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void dispatch(ServletContext context, String path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void complete() {
		try {
			response.getWriter().close();
		} catch (IOException e) {
			log.error(e);
			try {
				listener.onError(new AsyncEvent(this, e));
			} catch (IOException asyncException) {
				log.error(asyncException);
			}
		}

		if (listener != null) {
			try {
				listener.onComplete(new AsyncEvent(this));
			} catch (IOException e) {
				log.error(e);
			}
		}
	}

	@Override
	public void start(Runnable run) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addListener(AsyncListener listener) {
		Params.notNull(listener, "Asynchronous listener");
		this.listener = listener;
		try {
			this.listener.onStartAsync(new AsyncEvent(this));
		} catch (IOException e) {
			log.error(e);
		}
	}

	@Override
	public void addListener(AsyncListener listener, ServletRequest servletRequest, ServletResponse servletResponse) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends AsyncListener> T createListener(Class<T> clazz) throws ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	@Override
	public long getTimeout() {
		return timeout;
	}

	@Override	
	public void onError(Throwable t) {
		if (listener != null) {
			try {
				listener.onError(new AsyncEvent(this, t));
			} catch (IOException e) {
				log.error(e);
			}
		}
	}
}

package js.tiny.server.servlet;

class AsyncContextException extends RuntimeException {
	private static final long serialVersionUID = -1529197628182041468L;

	public AsyncContextException() {
		super();
	}

	public AsyncContextException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AsyncContextException(String message, Throwable cause) {
		super(message, cause);
	}

	public AsyncContextException(String message) {
		super(message);
	}

	public AsyncContextException(Throwable cause) {
		super(cause);
	}
}

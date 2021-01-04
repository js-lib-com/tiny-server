package js.tiny.server;

import js.lang.Event;
import js.log.Log;
import js.log.LogFactory;

/**
 * HTTP lite server.
 *
 * @author Iulian Rotaru
 */
public class HttpServer implements IServletFactory {
	private static final Log log = LogFactory.getLog(HttpServer.class);

	private final IConnector connector;

	/**
	 * Storage for file resources. This HTTP server just keep its reference but actual implementation is created outside this
	 * server scope.
	 */
	private final IStorage storage;

	private final IContainer container;

	private final IEventsManager eventsManager;

	public HttpServer(IStorage storage, IContainer container, int port) {
		log.trace("HttpServer(Storage,IContainer,int)");
		this.connector = new HttpConnector(this, port);
		this.storage = storage;
		this.container = container;
		this.eventsManager = new EventsManager();
	}

	public HttpServer(IStorage storage, int port) {
		log.trace("HttpServer(Storage,int)");
		this.connector = new HttpConnector(this, port);
		this.storage = storage;
		this.container = null;
		this.eventsManager = new EventsManager();
	}

	public HttpServer(IContainer container, int port) {
		log.trace("HttpServer(IContainer,int)");
		this.connector = new HttpConnector(this, port);
		this.storage = null;
		this.container = container;
		this.eventsManager = new EventsManager();
	}

	public void start() {
		connector.start();
		log.info("HTTP server started.");
	}

	public void stop() {
		connector.stop();
        log.info("HTTP server stopped.");
	}

	public void pushEvent(Event event) {
		eventsManager.pushEvent(event);
	}

	@Override
	public IServlet createServlet(RequestType requestType) {
		switch (requestType) {
		case FILE:
			if (storage == null) {
				throw new IllegalStateException("File request but document root not defined.");
			}
			return new FileServlet(storage);

		case RMI:
			if (container == null) {
				throw new IllegalStateException("HTTP-RMI request but container not defined.");
			}
			return new RmiServlet(container);

		case EVENTS:
			return new EventsServlet(eventsManager);

		default:
			throw new UnsupportedOperationException("Not supported request type " + requestType);
		}
	}
}

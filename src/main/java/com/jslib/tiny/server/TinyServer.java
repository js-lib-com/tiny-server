package com.jslib.tiny.server;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.inject.Inject;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.jslib.api.log.Log;
import com.jslib.api.log.LogFactory;
import com.jslib.container.servlet.TinyContainer;
import com.jslib.tiny.server.servlet.HttpServletRequestImpl;
import com.jslib.tiny.server.servlet.HttpServletResponseImpl;
import com.jslib.tiny.server.servlet.ServletConfigImpl;
import com.jslib.tiny.server.servlet.ServletContextImpl;
import com.jslib.tiny.server.servlet.SessionManager;
import com.jslib.util.Files;

public class TinyServer implements Runnable {
	private static final Log log = LogFactory.getLog(TinyServer.class);

	private static final int RECEIVE_TIMEOUT = 10000;
	private static final int SHUTDOWN_TIMEOUT = 30000;

	private final ServletContext servletContext;
	private final ServletFactory servletFactory;
	private final SessionManager sessionManager;
	private final int port;

	private final TinyContainer container;
	private final ServletContextListener contextListener;
	private final ServletRequestListener requestListener;

	private volatile boolean running;

	@Inject
	public TinyServer(int port) {
		log.trace("TinyServer(int)");
		this.servletContext = new ServletContextImpl();
		this.servletFactory = new ServletFactory();
		this.sessionManager = new SessionManager();
		this.port = port;

		this.container = new TinyContainer();
		this.container.bind(TinyServer.class).instance(this).build();
		this.container.bind(Manager.class).build();

		this.contextListener = container;
		this.sessionManager.setSessionListener(container);
		this.requestListener = container;
	}

	public void stop() {
		if (!running) {
			return;
		}

		log.trace("stop()");
		Socket socket = null;
		try {
			running = false;
			socket = new Socket(InetAddress.getByName("127.0.0.1"), port);
		} catch (Exception e) {
			log.error(e);
		} finally {
			close(socket);
		}
	}

	@SuppressWarnings("resource")
	@Override
	public void run() {
		log.debug("Start HTTP connector thread |%s|", Thread.currentThread());

		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(InetAddress.getByName("0.0.0.0"), port));
		} catch (IOException e) {
			log.error(e);
			log.debug("Error creating sync socket. Stop HTTP connector thread |%s|.", Thread.currentThread());
			return;
		}
		log.debug("Listen on |%s:%d| for HTTP requests.", serverSocket.getInetAddress().getHostAddress(), port);

		ServletContextEvent event = new ServletContextEvent(servletContext);
		contextListener.contextInitialized(event);

		final AtomicLong threadIndex = new AtomicLong();
		final ThreadFactory threadFactory = runnable -> new Thread(runnable, "Request Thread #" + threadIndex.getAndIncrement());
		ThreadPoolExecutor executor = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), threadFactory);

		running = true;
		for (;;) {
			try {
				final Socket socket = serverSocket.accept();
				if (!running) {
					log.debug("Got shutdown command. Break HTTP connector loop.");
					socket.close();
					break;
				}

				executor.submit(() -> service(socket));

			} catch (Throwable t) {
				log.dump("Fatal error on HTTP connector.", t);
			}
		}

		executor.shutdown();
		try {
			executor.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			log.error(e);
		}
		contextListener.contextDestroyed(event);

		try {
			serverSocket.close();
		} catch (IOException e) {
			log.error(e);
		}
		log.debug("Exit HTTP connector thread.");
	}

	private void service(Socket socket) {
		log.trace("service(Socket)");
		String remoteAddress = socket.getRemoteSocketAddress().toString();
		log.debug("Remote address: %s", remoteAddress);
		LogFactory.getLogContext().put("ip", remoteAddress);

		HttpServletRequest request = null;
		HttpServletResponse response = null;
		ServletRequestEvent requestEvent = null;

		long start = System.nanoTime();
		try {
			socket.setSoTimeout(RECEIVE_TIMEOUT);
			request = new HttpServletRequestImpl(servletContext, sessionManager, socket.getInputStream(), remoteAddress);
			requestEvent = new ServletRequestEvent(servletContext, request);
			requestListener.requestInitialized(requestEvent);

			for (Cookie cookie : request.getCookies()) {
				log.debug("Cookie: %s: %s", cookie.getName(), cookie.getValue());
			}

			response = new HttpServletResponseImpl(request, socket.getOutputStream());

			Servlet servlet = servletFactory.createServlet(request.getRequestURI());
			ServletConfig config = new ServletConfigImpl(servletContext);
			servlet.init(config);
			servlet.service(request, response);

		} catch (FileNotFoundException notFound) {
			log.warn("Resource not found: " + notFound.getMessage());
			if (response != null) {
				try {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
				} catch (IOException sendFail) {
					log.error(sendFail);
				}
				return;
			}
		} catch (Throwable throwable) {
			log.dump("Error processing request.", throwable);
			exception(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, throwable);
			return;
		} finally {
			requestListener.requestDestroyed(requestEvent);
			if (!request.isAsyncStarted()) {
				close(response);
				close(socket);
			}
		}

		// at this point request is guaranteed to be initialized
		// if request initialization fails for some reason there is exception that does return
		log.info("Request %s processed in %.02f msec.", request.getRequestURI(), (System.nanoTime() - start) / 1000000.0);
	}

	// ---------------------------------------------------------------------------------------------
	// UTILITY METHODS

	private static void close(ServletResponse response) {
		if (response != null) {
			try {
				response.getOutputStream().close();
			} catch (IOException e) {
				log.error(e);
			}
		}
	}

	private static void close(Socket socket) {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
	}

	private static void exception(HttpServletResponse response, int statusCode, Throwable throwable) {
		if (response == null || response.isCommitted()) {
			return;
		}
		response.setStatus(statusCode);

		StringWriter writer = new StringWriter();
		throwable.printStackTrace(new PrintWriter(writer));
		String trace = writer.toString();

		response.setContentType("text/plain; charset=UTF-8");
		response.setContentLength(trace.getBytes().length);

		try {
			Files.copy(new ByteArrayInputStream(trace.getBytes()), response.getOutputStream());
		} catch (IOException e) {
			log.error(e);
		}
	}
}

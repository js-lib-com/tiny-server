package js.tiny.server;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

public class Bootstrap {
	private static final int DEFAULT_PORT = 9999;
	private static final int STOP_CONNECTION_TIMEOUT = 2000;
	private static final int SHUTDOWN_HOOK_TIMEOUT = 30000;

	public static void main(String... arguments) {
		if (arguments.length == 0) {
			System.out.println("Bootstrap start | stop | run");
			return;
		}

		try {
			for (int i = 1; i < arguments.length; ++i) {
				String argument = arguments[i];
				int beginIndex = 0;
				while (argument.charAt(beginIndex) == '-') {
					++beginIndex;
				}
				if (beginIndex > 0) {
					argument = argument.substring(beginIndex);
				}

				int separatorIndex = argument.indexOf('=');
				if (separatorIndex == -1 || separatorIndex == argument.length() - 1) {
					continue;
				}

				final String key = argument.substring(0, separatorIndex);
				final String value = argument.substring(separatorIndex + 1);
				System.setProperty(key, value);
			}

			Bootstrap bootstrap = new Bootstrap();
			switch (arguments[0]) {
			case "start":
				bootstrap.start();
				break;

			case "stop":
				bootstrap.stop();
				break;

			case "run":
				bootstrap.run();
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private final int port;

	private Bootstrap() {
		String port = System.getProperty("port");
		this.port = port != null ? Integer.parseInt(port) : DEFAULT_PORT;
	}

	private void start() {
		TinyServer server = new TinyServer(port);
		Runtime.getRuntime().addShutdownHook(new ShutdownHook(server));
		server.run();
	}

	private void stop() throws IOException {
		try {
			URL url = new URL(String.format("http://127.0.0.1:%d/rest/manager/shutdown", port));
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(STOP_CONNECTION_TIMEOUT);
			con.setRequestMethod("POST");
			con.getResponseCode();
		} catch (SocketTimeoutException | ConnectException e) {
			System.out.println("Tiny Server is not running.");
		}
	}

	private void run() throws IOException, InterruptedException {
		TinyServer server = new TinyServer(port);

		Thread thread = new Thread(server);
		thread.setDaemon(true);
		thread.start();

		System.in.read();

		server.stop();
		thread.join();
	}

	private static class ShutdownHook extends Thread implements Runnable {
		private final TinyServer server;
		private final Thread mainThread;

		public ShutdownHook(TinyServer server) {
			this.server = server;
			this.mainThread = Thread.currentThread();
			this.setName("Shutdown Hook");
			this.setDaemon(false);
		}

		@Override
		public void run() {
			// server stop sends shutdown packet from this hook thread
			server.stop();
			// but at this point server's loop is possible to still run in the main thread

			// main thread is locked on server's loop; wait for its termination
			try {
				mainThread.join(SHUTDOWN_HOOK_TIMEOUT, 0);
			} catch (InterruptedException ignored) {
			}
		}
	}
}

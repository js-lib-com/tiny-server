package js.tiny.server;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import js.lang.Event;
import js.net.client.EventStreamClient;

public class HttpServerTest {
	public static class Notification implements Event {
		private int id;

		public Notification() {
		}

		public Notification(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		@Override
		public String toString() {
			return "Notification [id=" + id + "]";
		}
	}

	public static class Service {
		public Service() {
		}

		public String Hello(String user) {
			return String.format("Hello %s!", user);
		}
	}

	@Test
	public void file() throws IOException {
		IStorage storage = new FileStorage("D:/runtime/kids-cademy/webapps/site/");
		HttpServer server = new HttpServer(storage, 9999);
		server.start();
		System.in.read();
	}

	public void rmi() throws IOException {
		IContainer container = new RmiContainer();
		container.addMapping("Demo.Service", Service.class);
		HttpServer server = new HttpServer(container, 9999);
		server.start();
		System.in.read();
	}

	public void events() throws Exception {
		IContainer container = new RmiContainer();
		container.addMapping("Demo.Service", Service.class);
		HttpServer server = new HttpServer(container, 9999);
		server.start();

		URL eventStreamURL = new URL("http://localhost:8080/echo/server.event");
		try (EventStreamClient client = new EventStreamClient(eventStreamURL)) {
			client.addMapping(Notification.class);
			client.await(event -> System.out.println(event));

			for (int i = 0; i < 100; ++i) {
				server.pushEvent(new Notification(i));
			}
		}

		System.in.read();
	}
}

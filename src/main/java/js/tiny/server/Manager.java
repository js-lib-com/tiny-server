package js.tiny.server;

import jakarta.ejb.Remote;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Singleton
@Startup
@Remote
@Path("/manager")
public class Manager {
	private final TinyServer server;

	@Inject
	public Manager(TinyServer server) {
		this.server = server;
	}

	@POST
	@Path("/shutdown")
	public void shutdown() {
		server.stop();
	}
}

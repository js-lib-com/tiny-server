package js.tiny.server;

import java.io.IOException;

public interface IStorage {
	IResource getResource(String requestURI) throws IOException;
}

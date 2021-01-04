package js.tiny.server;

import java.io.FileNotFoundException;
import java.io.IOException;

public class DefaultStorage implements IStorage {
    @Override
    public IResource getResource(String requestURI) throws IOException {
        throw new FileNotFoundException(requestURI);
    }
}

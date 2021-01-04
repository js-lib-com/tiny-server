package js.tiny.server;

import java.io.Closeable;
import java.io.InputStream;

public interface IResource extends Closeable {
    ContentType getContentType();

    long getContentLength();

    InputStream getInputStream();
}

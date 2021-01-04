package js.tiny.server;

import java.io.IOException;
import java.io.OutputStream;

import js.log.Log;
import js.log.LogFactory;

public class FileServlet implements IServlet {
	private static final Log log = LogFactory.getLog(FileServlet.class);

	/**
	 * The size of buffer used by copy operations.
	 */
	private static final int BUFFER_SIZE = 4 * 1024;

	private final IStorage storage;

	public FileServlet(IStorage storage) {
		log.trace("FileServlet(Storage)");
		this.storage = storage;
	}

	@Override
	public void service(Request request, Response response) throws IOException {
		log.trace("service(Request,Response)");

		try (IResource resource = storage.getResource(request.getRequestURI())) {
			response.setStatus(ResponseStatus.OK);
			response.setContentType(resource.getContentType());
			response.setContentLength(resource.getContentLength());

			OutputStream outputStream = response.getOutputStream();
			byte[] buffer = new byte[BUFFER_SIZE];
			int length;
			while ((length = resource.getInputStream().read(buffer)) != -1) {
				outputStream.write(buffer, 0, length);
			}
			response.flush();
		}
	}
}

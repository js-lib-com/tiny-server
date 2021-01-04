package js.tiny.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import js.util.Files;

public class FileStorage implements IStorage {
	private final File baseDir;

	public FileStorage(String baseDir) {
		this.baseDir = new File(baseDir);
	}

	@Override
	public IResource getResource(String requestURI) throws IOException {
		File file = new File(baseDir, requestURI.substring(1).replace('/', File.separatorChar));
		if (!file.exists()) {
			throw new FileNotFoundException(file.getPath());
		}
		InputStream stream = new BufferedInputStream(new FileInputStream(file));
		ContentType contetType = ContentType.forExtension(Files.getExtension(file));
		return new Resource(stream, contetType, file.length());
	}

	private static class Resource implements IResource {
		private final InputStream stream;
		private final ContentType contentType;
		private final long contentLength;

		public Resource(InputStream stream, ContentType contentType, long contentLength) {
			this.stream = stream;
			this.contentType = contentType;
			this.contentLength = contentLength;
		}

		@Override
		public void close() throws IOException {
			stream.close();
		}

		@Override
		public long getContentLength() {
			return contentLength;
		}

		@Override
		public ContentType getContentType() {
			return contentType;
		}

		@Override
		public InputStream getInputStream() {
			return stream;
		}
	}
}

package js.tiny.server;

import js.util.Files;

public enum ContentType {
	/**
	 * Neutral value
	 */
	NONE(""),

	/**
	 *
	 */
	TEXT_PLAIN("text/plain; charset=UTF-8"),

	/**
	 *
	 */
	TEXT_HTML("text/html; charset=UTF-8"),

	/**
	 *
	 */
	TEXT_CSS("text/css; charset=UTF-8"),

	/**
	 *
	 */
	TEXT_JS("text/javascript; charset=UTF-8"),

	/**
	 *
	 */
	TEXT_XML("text/xml; charset=UTF-8"),

	/**
	 *
	 */
	TEXT_CSV("text/csv; charset=UTF-8"),

	/**
	 *
	 */
	MULTIPART_FORM("multipart/form-data"),

	/**
	 *
	 */
	MULTIPART_MIXED("multipart/mixed"),

	/**
	 *
	 */
	URLENCODED_FORM("application/x-www-form-urlencoded; charset=UTF-8"),

	/**
	 *
	 */
	APPLICATION_JSON("application/json; charset=UTF-8"),

	/**
	 *
	 */
	APPLICATION_PDF("application/pdf"),

	/**
	 *
	 */
	APPLICATION_STREAM("application/octet-stream"),

	/**
	 *
	 */
	IMAGE_PNG("image/png"),

	/**
	 *
	 */
	IMAGE_JPEG("image/jpeg"),

	/**
	 *
	 */
	IMAGE_GIF("image/gif"),

	/**
	 *
	 */
	IMAGE_TIFF("image/tiff"),

	/**
	 *
	 */
	IMAGE_SVG("image/svg+xml"),

	TEXT_EVENT_STREAM("text/event-stream; charset=UTF-8");

	private String value;

	private ContentType(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

	public static ContentType forFilePath(String filePath) {
		return forExtension(Files.getExtension(filePath));
	}

	public static ContentType forExtension(String extension) {
		switch (extension) {
		case "htm":
		case "html":
			return ContentType.TEXT_HTML;

		case "png":
			return ContentType.IMAGE_PNG;

		case "css":
			return ContentType.TEXT_CSS;

		case "js":
			return ContentType.TEXT_JS;

		case "jpg":
		case "jpeg":
			return ContentType.IMAGE_PNG;

		case "gif":
			return ContentType.IMAGE_GIF;

		default:
			return ContentType.NONE;
		}
	}
}

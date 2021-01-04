package js.tiny.server;

enum RequestType {
	/**
	 * Neutral value.
	 */
	NONE,

	/**
	 * Request for a generic file.
	 */
	FILE,

	/**
	 * HTTP-RMI request.
	 */
	RMI,

	/**
	 * Server-Sent Events.
	 */
	EVENTS
}

class RequestTypeFactory {
	public static RequestType valueOf(Request request) {
		String requestURI = request.getRequestURI();
		if (requestURI.endsWith(".rmi")) {
			return RequestType.RMI;
		}
		if (requestURI.startsWith("/events/")) {
			return RequestType.EVENTS;
		}
		return RequestType.FILE;
	}
}

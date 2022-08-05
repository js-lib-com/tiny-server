package com.jslib.tiny.server;

import java.util.HashMap;
import java.util.Map;

public class CT {
	private static final Map<Integer, String> MESSAGES = new HashMap<>();
	static {
		MESSAGES.put(100, "Continue");
		MESSAGES.put(101, "Switching Protocols");
		MESSAGES.put(200, "OK");
		MESSAGES.put(201, "Created");
		MESSAGES.put(202, "Accepted");
		MESSAGES.put(203, "Non-Authoritative Information");
		MESSAGES.put(204, "No Content");
		MESSAGES.put(205, "Reset Content");
		MESSAGES.put(206, "Partial Content");
		MESSAGES.put(300, "Multiple Choices");
		MESSAGES.put(301, "Moved Permanently");
		MESSAGES.put(302, "Found");
		MESSAGES.put(303, "See Other");
		MESSAGES.put(304, "Not Modified");
		MESSAGES.put(305, "Use Proxy");
		MESSAGES.put(307, "Temporary Redirect");
		MESSAGES.put(400, "Bad Request");
		MESSAGES.put(401, "Unauthorized");
		MESSAGES.put(402, "Payment Required");
		MESSAGES.put(403, "Forbidden");
		MESSAGES.put(404, "Not Found");
		MESSAGES.put(405, "Method Not Allowed");
		MESSAGES.put(406, "Not Acceptable");
		MESSAGES.put(407, "Proxy Authentication Required");
		MESSAGES.put(408, "Request Time-out");
		MESSAGES.put(409, "Conflict");
		MESSAGES.put(410, "Gone");
		MESSAGES.put(411, "Length Required");
		MESSAGES.put(412, "Precondition Failed");
		MESSAGES.put(413, "Request Entity Too Large");
		MESSAGES.put(414, "Request-URI Too Large");
		MESSAGES.put(415, "Unsupported Media Type");
		MESSAGES.put(416, "Requested range not satisfiable");
		MESSAGES.put(417, "Expectation Failed");
		MESSAGES.put(500, "Internal Server Error");
		MESSAGES.put(501, "Not Implemented");
		MESSAGES.put(502, "Bad Gateway");
		MESSAGES.put(503, "Service Unavailable");
		MESSAGES.put(504, "Gateway Time-out");
		MESSAGES.put(505, "HTTP Version not supported");
	}

	public static String statusMessage(int statusCode) {
		return MESSAGES.getOrDefault(statusCode, "Unknown code");
	}
}

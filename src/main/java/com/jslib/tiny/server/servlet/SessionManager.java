package com.jslib.tiny.server.servlet;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import com.jslib.api.log.Log;
import com.jslib.api.log.LogFactory;
import com.jslib.util.Strings;

public class SessionManager {
	public static final String SESSION_COOKIE = "JSESSIONID";
	
	private static final Log log = LogFactory.getLog(SessionManager.class);

	private final Map<String, HttpSession> sessions;

	private HttpSessionListener sessionListener;

	public SessionManager() {
		log.trace("SessionManager()");
		this.sessions = new HashMap<>();
	}

	public void setSessionListener(HttpSessionListener sessionListener) {
		this.sessionListener = sessionListener;
	}

	public HttpSession getSession(String sessionID) {
		return sessions.get(sessionID);
	}

	public HttpSession getSession(String sessionID, boolean create) {
		HttpSession session = sessions.get(sessionID);
		if (session == null && create) {
			session = new HttpSessionImpl(sessionID);
			sessions.put(sessionID, session);
			if (sessionListener != null) {
				HttpSessionEvent event = new HttpSessionEvent(session);
				sessionListener.sessionCreated(event);
			}
		}
		return session;
	}

	public String generateSessionID() {
		return Strings.md5(Strings.UUID());
	}

	public boolean hasSession(String sessionID) {
		return sessions.containsKey(sessionID);
	}
}

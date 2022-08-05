package com.jslib.tiny.server.jndi;

import javax.naming.NamingException;

import com.jslib.util.Strings;

public class JndiException extends NamingException {
	private static final long serialVersionUID = -7596710558564755693L;

	public JndiException(String message, Object... arguments) {
		super(String.format(message, arguments(arguments)));
	}

	private static Object[] arguments(Object[] arguments) {
		for (int i = 0; i < arguments.length; ++i) {
			if (arguments[i] instanceof Exception) {
				Exception exception = (Exception) arguments[i];
				arguments[i] = Strings.concat(exception.getClass().getCanonicalName(), ": ", exception.getMessage());
			}
			if (arguments[i] instanceof Class) {
				arguments[i] = ((Class<?>) arguments[i]).getCanonicalName();
			}
		}
		return arguments;
	}
}

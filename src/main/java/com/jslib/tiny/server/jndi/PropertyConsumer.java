package com.jslib.tiny.server.jndi;

import javax.naming.NamingException;

@FunctionalInterface
public interface PropertyConsumer {

	void accept(String name, String value) throws NamingException;

}

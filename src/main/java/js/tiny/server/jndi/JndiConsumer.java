package js.tiny.server.jndi;

import javax.naming.NamingException;

@FunctionalInterface
public interface JndiConsumer {

	void accept(String name, String value) throws NamingException;

}

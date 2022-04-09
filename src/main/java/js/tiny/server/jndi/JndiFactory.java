package js.tiny.server.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

public class JndiFactory implements InitialContextFactory {
	public static final String GLOBAL_ENV = "java:global/env";
	public static final String COMP_ENV = "java:comp/env";

	private static volatile Context initialContext;

	@Override
	public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
		if (initialContext == null) {
			synchronized (JndiFactory.class) {
				if (initialContext == null) {
					ConfigurationDirectory confDir = new ConfigurationDirectory();
					initialContext = new JndiContext("java:", confDir);
					initialContext.createSubcontext(GLOBAL_ENV);

					JndiContext compContext = (JndiContext) initialContext.createSubcontext(COMP_ENV);
					if(confDir.exists()) {
						SystemProperties properties = new SystemProperties(confDir);
						compContext.setSystemProperties(properties);
						properties.forEach((name, value) -> compContext.bind(name, value));
					}
				}
			}
		}
		return initialContext;
	}
}

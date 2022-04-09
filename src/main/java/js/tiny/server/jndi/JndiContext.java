package js.tiny.server.jndi;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import js.log.Log;
import js.log.LogFactory;
import js.util.Params;
import js.util.Strings;

public class JndiContext implements Context {
	private static final Log log = LogFactory.getLog(JndiContext.class);

	private final String contextName;
	private final ConfigurationDirectory confDir;
	private final Map<String, Object> bindings;

	private SystemProperties systemProperties;

	public JndiContext(String contextName, ConfigurationDirectory confDir) {
		this.contextName = contextName;
		this.confDir = confDir;
		this.bindings = new HashMap<>();
	}

	public void setSystemProperties(SystemProperties systemProperties) {
		this.systemProperties = systemProperties;
	}

	@Override
	public Object lookup(Name name) throws NamingException {
		return lookup(name.toString());
	}

	@Override
	public Object lookup(String name) throws NamingException {
		Params.notNullOrEmpty(name, "Name");
		log.debug("Lookup JNDI |%s|.", name);

		if (name.startsWith(JndiFactory.GLOBAL_ENV)) {
			Context context = (Context) bindings.get(JndiFactory.GLOBAL_ENV);
			if (name.length() == JndiFactory.GLOBAL_ENV.length()) {
				return context;
			}
			return context.lookup(name.substring(JndiFactory.GLOBAL_ENV.length() + 1));
		}

		if (name.startsWith(JndiFactory.COMP_ENV)) {
			Context context = (Context) bindings.get(JndiFactory.COMP_ENV);
			if (name.length() == JndiFactory.COMP_ENV.length()) {
				return context;
			}
			return context.lookup(name.substring(JndiFactory.COMP_ENV.length() + 1));
		}

		if (name.contains(":")) {
			throw new JndiException("Unsupported context for JNDI name |%s|.", name);
		}

		Object object = bindings.get(name);
		if (object != null) {
			return object;
		}

		// at this point we need to create a resource object using properties from configuration directory
		log.debug("Create resource object |%s|.", name);
		if (!confDir.exists()) {
			throw new JndiException("Missing configuration directory. Fail to create resource object for |%s|.", name);
		}

		// jdbc/db -> jdbc-db.properties
		String resourceProperties = Strings.concat(name.replace('/', '-').toLowerCase(), ".properties");
		File resourcePropertiesFile = confDir.getFile(resourceProperties);
		if (!resourcePropertiesFile.exists()) {
			throw new JndiException("Missing resource properties |%s|.", resourcePropertiesFile);
		}

		ResourceFactory resourceFactory = new ResourceFactory(systemProperties, resourcePropertiesFile);
		Object resource = resourceFactory.getResource();
		bind(resourceFactory.getName(), resource);
		return resource;
	}

	@Override
	public void bind(Name name, Object object) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void bind(String name, Object object) throws NamingException {
		if (bindings.put(name, object) != null) {
			throw new NameAlreadyBoundException(name);
		}
		log.debug("Bind JNDI name |%s| to |%s|.", name, object);
	}

	@Override
	public void rebind(Name name, Object object) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rebind(String name, Object object) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void unbind(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void unbind(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rename(Name oldName, Name newName) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rename(String oldName, String newName) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void destroySubcontext(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void destroySubcontext(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Context createSubcontext(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Context createSubcontext(String name) throws NamingException {
		Context context = new JndiContext(name, confDir);
		if (bindings.put(name, context) != null) {
			throw new NameAlreadyBoundException(name);
		}
		log.debug("Create JNDI subcontext |%s|.", name);
		return context;
	}

	@Override
	public Object lookupLink(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object lookupLink(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NameParser getNameParser(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NameParser getNameParser(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Name composeName(Name name, Name prefix) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String composeName(String name, String prefix) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object addToEnvironment(String propName, Object propVal) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object removeFromEnvironment(String propName) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Hashtable<?, ?> getEnvironment() throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getNameInNamespace() throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return contextName;
	}
}

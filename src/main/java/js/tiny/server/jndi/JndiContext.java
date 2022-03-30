package js.tiny.server.jndi;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import js.log.Log;
import js.log.LogFactory;
import js.util.Strings;

public class JndiContext implements Context {
	private static final Log log = LogFactory.getLog(JndiContext.class);

	private final Map<String, Object> bindings;

	private SystemProperties systemProperties;
	
	public JndiContext() {
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
		if (name.length() > JndiFactory.GLOBAL_ENV.length() && name.startsWith(JndiFactory.GLOBAL_ENV)) {
			Context context = (Context) bindings.get(JndiFactory.GLOBAL_ENV);
			return context.lookup(name.substring(JndiFactory.GLOBAL_ENV.length() + 1));
		}
		
		if (name.length() > JndiFactory.COMP_ENV.length() && name.startsWith(JndiFactory.COMP_ENV)) {
			// java:comp/env/server.name -> server.name
			// java:comp/env/jdbc/db -> jdbc/db
			String compName = name.substring(JndiFactory.COMP_ENV.length() + 1);

			JndiContext compContext = (JndiContext) bindings.get(JndiFactory.COMP_ENV);
			Object object = compContext.bindings.get(compName);
			if (object != null) {
				return object;
			}

			// jdbc/db -> /META-INF/jdbc-db.properties
			String resourceProperties = Strings.concat("/META-INF/", compName.replace('/', '-').toLowerCase(), ".properties");
			if (getClass().getResource(resourceProperties) == null) {
				throw new NameNotFoundException(name);
			}

			ResourceFactory resourceFactory = new ResourceFactory(compContext.systemProperties, resourceProperties);
			Object resource = resourceFactory.getResource();
			compContext.bind(resourceFactory.getName(), resource);
			return resource;
		}

		Object object = bindings.get(name);
		if (object == null) {
			throw new NameNotFoundException(name);
		}
		return object;
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
		Context context = new JndiContext();
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
}

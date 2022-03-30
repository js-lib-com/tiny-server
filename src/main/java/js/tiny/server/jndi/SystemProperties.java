package js.tiny.server.jndi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;

/**
 * System property, or simple environment entry as named by EJB specification, is a configuration parameter used to customize
 * the business logic. The system property values may be one of the following Java types: String, Character, Integer, Boolean,
 * Double, Byte, Short, Long, and Float.
 * 
 * @author Iulian Rotaru
 */
class SystemProperties {
	private static final String RESOURCE_PATH = "/META-INF/system.properties";

	private final Map<String, String> properties;

	public SystemProperties() throws NamingException {
		this.properties = new HashMap<>();

		InputStream stream = getClass().getResourceAsStream(RESOURCE_PATH);
		if (stream == null) {
			// TODO: discover global JNDI or a registry service deployed on a managed context like application server or
			// micro-services private cloud
			return;
		}

		String line;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				}

				int valueSeparatorPosition = line.indexOf('=');
				if (valueSeparatorPosition == -1 || valueSeparatorPosition == line.length() - 1) {
					continue;
				}

				String key = line.substring(0, valueSeparatorPosition).trim();
				String value = line.substring(valueSeparatorPosition + 1).trim();
				String systemValue = System.getProperty(key);
				properties.put(key, systemValue != null ? systemValue : value);
			}
		} catch (IOException e) {
			throw new JndiException("Fail to load system properties from |%s|. Root cause: %s", RESOURCE_PATH, e);
		}
	}

	public void forEach(JndiConsumer consumer) throws NamingException {
		for (String name : properties.keySet()) {
			consumer.accept(name, properties.get(name));
		}
	}

	public Map<String, String> getProperties() {
		return properties;
	}
}

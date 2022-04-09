package js.tiny.server.jndi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
	private static final String PROPERTIES_PATH = "system.properties";

	private final Properties properties;

	public SystemProperties(ConfigurationDirectory confDir) throws NamingException {
		this.properties = new Properties();

		File propertiesFile = confDir.getFile(PROPERTIES_PATH);
		if (propertiesFile == null) {
			return;
		}

		String line;
		try (BufferedReader reader = new BufferedReader(new FileReader(propertiesFile))) {
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				}

				int valueSeparatorPosition = line.indexOf('=');
				if (valueSeparatorPosition == -1 || valueSeparatorPosition == line.length() - 1) {
					continue;
				}

				final String key = line.substring(0, valueSeparatorPosition).trim();
				final String value = line.substring(valueSeparatorPosition + 1).trim();
				properties.put(key, value);
			}
		} catch (IOException e) {
			throw new JndiException("Fail to load system properties from |%s|. Root cause: %s", propertiesFile, e);
		}
	}

	public void forEach(PropertyConsumer consumer) throws NamingException {
		for (String name : properties.keySet()) {
			consumer.accept(name, properties.get(name));
		}
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	private static class Properties extends HashMap<String, String> {
		private static final long serialVersionUID = -3458641557733720672L;

		@Override
		public String get(Object keyObject) {
			if (keyObject == null) {
				return super.get(keyObject);
			}
			final String key = (String) keyObject;

			// db-user -> DB_USER
			String value = System.getenv(key.replace('-', '_').toUpperCase());
			if (value != null) {
				return value;
			}

			value = System.getProperty(key);
			if (value != null) {
				return value;
			}

			return super.get(key);
		}

		@Override
		public String getOrDefault(Object key, String defaultValue) {
			String value = get(key);
			return value != null ? value : defaultValue;
		}
	}
}

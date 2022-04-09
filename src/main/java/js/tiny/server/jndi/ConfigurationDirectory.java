package js.tiny.server.jndi;

import java.io.File;

import javax.naming.NamingException;

import js.log.Log;
import js.log.LogFactory;

public class ConfigurationDirectory {
	private static final Log log = LogFactory.getLog(ConfigurationDirectory.class);

	private static final String CONF_DIR = "CONF_DIR";

	private final File confDir;

	public ConfigurationDirectory() throws NamingException {
		String confDirProperty = System.getProperty(CONF_DIR);
		log.debug("Configuration directory: %s", confDirProperty);
		if (confDirProperty == null) {
			confDir = null;
			return;
		}

		confDir = new File(confDirProperty);
		if (!confDir.exists()) {
			throw new JndiException("Missing configuration directory |%s|.", confDirProperty);
		}
	}

	public boolean exists() {
		return confDir != null;
	}

	public File getFile(String filePath) {
		if (confDir == null) {
			throw new IllegalStateException("Attempt to read configuration property from not existing directory.");
		}
		return new File(confDir, filePath);
	}
}

package com.jslib.tiny.server.jndi;

import java.io.File;

import javax.naming.NamingException;

import com.jslib.api.log.Log;
import com.jslib.api.log.LogFactory;

/**
 * Directory holding application configuration files. Configuration files can be retrieved via relative path using
 * {@link #getFile(String)}; configuration directory is not mandatory and user should ensure it actually exists before
 * attempting to retrieve any configuration file - see {@link #exists()}.
 * <p>
 * Configuration directory absolute path may be set on system property named <code>CONFIG_DIR</code> before creating an
 * instance of this class.
 * 
 * @author Iulian Rotaru
 */
class ConfigDir {
	private static final Log log = LogFactory.getLog(ConfigDir.class);

	private static final String CONFIG_DIR = "CONFIG_DIR";

	private final File configDir;

	public ConfigDir() throws NamingException {
		String confDirProperty = System.getProperty(CONFIG_DIR);
		log.debug("Configuration directory: %s", confDirProperty);
		if (confDirProperty == null) {
			configDir = null;
			return;
		}

		configDir = new File(confDirProperty);
		if (!configDir.exists()) {
			throw new JndiException("Missing configuration directory |%s|.", confDirProperty);
		}
	}

	public boolean exists() {
		return configDir != null;
	}

	public File getFile(String filePath) {
		if (configDir == null) {
			throw new IllegalStateException("Attempt to read configurations from not existing directory.");
		}
		File file = new File(configDir, filePath);
		if (!file.exists()) {
			throw new IllegalStateException("Attempt to read not existing configuration file: " + file);
		}
		return file;
	}
}

package com.jslib.tiny.server.jndi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.naming.NamingException;

import com.jslib.converter.Converter;
import com.jslib.converter.ConverterException;
import com.jslib.converter.ConverterRegistry;
import com.jslib.lang.NoSuchBeingException;
import com.jslib.util.Classes;
import com.jslib.util.Strings;

public class ResourceFactory {
	private final Converter converter;

	private final File propertiesFile;
	private final Properties properties;

	public ResourceFactory(Variables variables, File propertiesFile) throws NamingException {
		this.converter = ConverterRegistry.getConverter();
		this.propertiesFile = propertiesFile;
		this.properties = new Properties();

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

				String key = line.substring(0, valueSeparatorPosition).trim();
				String value = Strings.injectVariables(line.substring(valueSeparatorPosition + 1).trim(), variables.getProperties());
				properties.put(key, value);
			}
		} catch (IOException e) {
			throw new JndiException("Fail to load resource properties from |%s|. Root cause: %s", propertiesFile, e);
		}
	}

	public String getName() throws NamingException {
		return property("name");
	}

	public Object getResource() throws NamingException {
		String resourceType = property("type");
		Object resource;
		try {
			resource = Classes.newInstance(resourceType);
		} catch (NoSuchBeingException e) {
			throw new JndiException("Resource class not found |%s|.", resourceType);
		}

		properties((name, value) -> {
			Method setter = setter(resource, name);
			try {
				setter.invoke(resource, converter.asObject(value, setter.getParameterTypes()[0]));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | ConverterException e) {
				throw new JndiException("Fail to set property |%s| on resource |%s|. Root cause: %s", name, resource.getClass(), e);
			}
		});
		return resource;
	}

	private Method setter(Object resource, String name) throws NamingException {
		List<Method> setters = new ArrayList<>();

		for (Method method : resource.getClass().getMethods()) {
			if (method.getName().equals(Strings.getMethodAccessor("set", name)) && method.getParameterCount() == 1) {
				setters.add(method);
			}
		}

		if (setters.isEmpty()) {
			throw new JndiException("No setter found for property |%s| on resource |%s|,", name, resource.getClass());
		}
		if (setters.size() == 1) {
			return setters.get(0);
		}

		// handle setter overloading: choose more specialized one
		Collections.sort(setters, (m1, m2) -> {
			if (m1.getParameterTypes()[0].equals(String.class)) {
				return m2.getParameterTypes()[1].equals(String.class) ? 0 : -1;
			}
			if (m2.getParameterTypes()[0].equals(String.class)) {
				return m1.getParameterTypes()[0].equals(String.class) ? 0 : 1;
			}
			return 0;
		});

		return setters.get(0);
	}

	private String property(String propertyName) throws NamingException {
		String property = (String) properties.get(propertyName);
		if (property == null) {
			throw new JndiException("Missing property |%s| from resource properties |%s|.", propertyName, propertiesFile);
		}
		return property.trim();
	}

	private void properties(PropertyConsumer consumer) throws NamingException {
		for (Object name : properties.keySet()) {
			if (excludes(name)) {
				continue;
			}
			consumer.accept((String) name, ((String) properties.get(name)).trim());
		}
	}

	private boolean excludes(Object name) {
		return "name".equals(name) || "type".equals(name);
	}
}

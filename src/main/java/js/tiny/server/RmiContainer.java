package js.tiny.server;

import java.util.HashMap;
import java.util.Map;

public class RmiContainer implements IContainer {
	private final Map<String, Class<?>> mappings = new HashMap<>();

	private final Map<Class<?>, Object> instances = new HashMap<>();

	private final Object instancesLock = new Object();

	public void addMapping(String typeName, Class<?> type) {
		mappings.put(typeName, type);
	}

	public Class<?> getMappedType(String typeName) {
		return mappings.get(typeName);
	}

	public Object getInstance(Class<?> type) throws Exception {
		Object instance = instances.get(type);
		if (instance == null) {
			synchronized (instancesLock) {
				if (instance == null) {
					instance = type.newInstance();
					instances.put(type, instance);
				}
			}
		}
		return instance;
	}
}

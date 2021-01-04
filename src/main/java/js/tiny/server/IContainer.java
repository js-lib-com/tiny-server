package js.tiny.server;

public interface IContainer {
	void addMapping(String typeName, Class<?> type);

	Class<?> getMappedType(String typeName);

	Object getInstance(Class<?> type) throws Exception;
}

package js.tiny.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import js.json.Json;
import js.log.Log;
import js.log.LogFactory;
import js.util.Classes;
import js.util.Types;

public class RmiServlet implements IServlet {
	private static final Log log = LogFactory.getLog(RmiServlet.class);

	private static final Map<String, Method> methodsCache = new HashMap<>();

	private final IContainer container;
	private final Json json;

	public RmiServlet(IContainer container) {
		log.trace("RmiServlet(IContainer)");
		this.container = container;
		this.json = Classes.loadService(Json.class);
	}

	@Override
	public void service(Request request, Response response) throws Exception {
		String requestURI = request.getRequestURI();
		int pathSeparatorIndex = requestURI.lastIndexOf('/');
		int extensionSeparatorIndex = requestURI.lastIndexOf('.');
		String className = className(requestURI.substring(0, pathSeparatorIndex));
		String methodName = requestURI.substring(pathSeparatorIndex + 1, extensionSeparatorIndex);

		Class<?> managedClass = container.getMappedType(className);
		if (managedClass == null) {
			log.error("Missing managed class |%s|.", className);
			return;
		}

		Method method = methodsCache.get(request.getRequestURI());
		if (method == null) {
			try {
				method = Classes.findMethod(managedClass, methodName);
			} catch (NoSuchMethodException e) {
				log.error("Missing managed method |%s#%s|.", className, methodName);
			}
			methodsCache.put(request.getRequestURI(), method);
		}

		Object managedInstance = container.getInstance(managedClass);
		Class<?> returnType = method.getReturnType();
		Object value = method.invoke(managedInstance, getArguments(request.getStream(), method.getParameterTypes()));

		response.setHeader("Connection", "close");

		if (Types.isVoid(returnType)) {
			response.setStatus(ResponseStatus.NO_CONTENT);
			response.setContentLength(0L);
			response.flush();
			return;
		}

		byte[] body = json.stringify(value).getBytes("UTF-8");
		response.setStatus(ResponseStatus.OK);
		response.setContentType(ContentType.APPLICATION_JSON);
		response.setContentLength(body.length);
		response.getOutputStream().write(body);
		response.flush();
	}

	private Object[] getArguments(InputStream stream, Class<?>[] parameterTypes) throws IOException {
		if (parameterTypes.length == 0) {
			return new Object[0];
		}
		return json.parse(new InputStreamReader(stream), parameterTypes);
	}

	private static String className(String match) {
		StringBuilder className = new StringBuilder();
		char separator = '.';
		char c = match.charAt(1);
		for (int i = 1;;) {
			if (c == '/') {
				c = separator;
			}
			className.append(c);

			if (++i == match.length()) {
				break;
			}
			c = match.charAt(i);
			if (Character.isUpperCase(c)) {
				separator = '$';
			}
		}
		return className.toString();
	}
}

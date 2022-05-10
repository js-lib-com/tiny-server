package js.tiny.server;

import java.io.FileNotFoundException;

import jakarta.servlet.Servlet;
import js.tiny.container.rest.RestServlet;
import js.tiny.container.rmi.HttpRmiServlet;

public class ServletFactory {
	public Servlet createServlet(String requestURI) throws FileNotFoundException {
		if (requestURI.endsWith(".rmi")) {
			return new HttpRmiServlet();
		}
		if (requestURI.startsWith("/rest/")) {
			return new RestServlet();
		}
		throw new FileNotFoundException(requestURI);
	}
}

package js.tiny.server;

import java.io.FileNotFoundException;

import javax.servlet.Servlet;

import js.tiny.container.net.HttpRmiServlet;
import js.tiny.container.rest.RestServlet;

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

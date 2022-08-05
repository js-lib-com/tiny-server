package com.jslib.tiny.server;

import java.io.FileNotFoundException;

import com.jslib.tiny.container.rest.RestServlet;
import com.jslib.tiny.container.rmi.HttpRmiServlet;

import jakarta.servlet.Servlet;

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

package com.jslib.tiny.server.servlet;

import java.io.IOException;

import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;

public class AsyncListenerImpl implements AsyncListener {
	@Override
	public void onComplete(AsyncEvent event) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onTimeout(AsyncEvent event) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onError(AsyncEvent event) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onStartAsync(AsyncEvent event) throws IOException {
		throw new UnsupportedOperationException();
	}
}

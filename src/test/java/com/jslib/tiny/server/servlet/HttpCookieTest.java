package com.jslib.tiny.server.servlet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.HttpCookie;
import java.util.List;

import org.junit.Test;

public class HttpCookieTest {
	@Test
	public void GivenSingleSetCookie_WhenParse_Then() {
		// given
		String header = "userName=Tom Joad; Domain=js-lib.com; Path=/users; Max-Age=1800; Expires=Sun, 13-Mar-2022 05:32:14 GMT; SameSite=None";

		// when
		List<HttpCookie> cookies = HttpCookie.parse(header);

		// then
		assertThat(cookies, notNullValue());
		assertThat(cookies.size(), equalTo(1));

		HttpCookie cookie = cookies.get(0);
		assertThat(cookie.getName(), equalTo("userName"));
		assertThat(cookie.getValue(), equalTo("Tom Joad"));
		assertThat(cookie.getDomain(), equalTo("js-lib.com"));
		assertThat(cookie.getPath(), equalTo("/users"));
		assertThat(cookie.getMaxAge(), equalTo(1800L));
		assertThat(cookie.hasExpired(), equalTo(false));
	}
	
	@Test
	public void GivenMultipleCookies_WhenParse_Then() {
		// given
		String header = "userName=Tom Joad; profession=Land Worker";

		// when
		List<HttpCookie> cookies = HttpCookie.parse(header);

		// then
		assertThat(cookies, notNullValue());
		assertThat(cookies.size(), equalTo(1));
	}
}

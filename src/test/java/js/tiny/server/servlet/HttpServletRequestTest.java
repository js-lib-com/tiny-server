package js.tiny.server.servlet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HttpServletRequestTest {
	@Mock
	private ServletContext context;
	@Mock
	private SessionManager sessionManager;

	private HttpServletRequestImpl request;

	@Before
	public void beforeTest() throws IOException {
	}

	@Test
	public void Given_WhenGetCookies_Then() throws IOException {
		// given
		InputStream stream = stream("GET /\r\nCookie: userName=Chaitanya; JSESSIONID=6d648ab0872da3315b8adf598081b96b\r\n\r\n");
		request = new HttpServletRequestImpl(context, sessionManager, stream, "localhost");

		// when
		Cookie[] cookies = request.getCookies();

		// then
		assertThat(cookies, notNullValue());
		assertThat(cookies.length, equalTo(2));
	}

	@Test
	public void Given_When_Then() {
		// given

		// when

		// then
	}

	private InputStream stream(String http) {
		return new ByteArrayInputStream(http.getBytes());
	}
}

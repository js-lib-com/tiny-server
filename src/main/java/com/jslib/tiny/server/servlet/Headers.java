package com.jslib.tiny.server.servlet;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import jakarta.servlet.http.Cookie;

class Headers {
	public static final String ACCEPT_LANGUAGE = "Accept-Language";
	public static final String CONTENT_LENGTH = "Content-Length";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String COOKIE = "Cookie";
	public static final String SET_COOKIE = "Set-Cookie";

	// simple date format is not thread safe
	private static final ThreadLocal<Dates> datesTLS = ThreadLocal.withInitial(() -> new Dates());

	public static long parseDate(String value) {
		Dates dates = datesTLS.get();
		long date = -1;
		for (int i = 0; (date == -1) && (i < dates.formats.length); i++) {
			try {
				date = dates.formats[i].parse(value).getTime();
			} catch (ParseException ignored) {
			}
		}
		return date;
	}

	public static String formatDate(long value) {
		Dates dates = datesTLS.get();
		// official format is first in array
		return dates.formats[0].format(new Date(value));
	}

	private static class Dates {
		private static final String RFC5322 = "EEE, dd MMM yyyy HH:mm:ss z";
		private static final String RFC850 = "EEEEEE, dd-MMM-yy HH:mm:ss zzz";
		private static final String ASCTIME = "EEE MMMM d HH:mm:ss yyyy";

		// official format is first in array
		DateFormat[] formats = new DateFormat[] { //
				new SimpleDateFormat(RFC5322), //
				new SimpleDateFormat(RFC850), //
				new SimpleDateFormat(ASCTIME) //
		};
	}

	// --------------------------------------------------------------------------------------------

	private static final String COOKIE_DATE_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss z";
	private static final ThreadLocal<DateFormat> COOKIE_DATE_FORMAT = ThreadLocal.withInitial(() -> {
		DateFormat df = new SimpleDateFormat(COOKIE_DATE_PATTERN, Locale.US);
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		return df;
	});

	private static final String ANCIENT_DATE;
	static {
		ANCIENT_DATE = COOKIE_DATE_FORMAT.get().format(new Date(10000));
	}

	public static String formatCookie(Cookie cookie) {
		StringBuffer header = new StringBuffer();

		header.append(cookie.getName());
		header.append('=');
		String value = cookie.getValue();
		if (value != null && value.length() > 0) {
			header.append(value);
		}

		int maxAge = cookie.getMaxAge();
		if (maxAge > -1) {
			// Negative Max-Age is equivalent to no Max-Age
			header.append("; Max-Age=");
			header.append(maxAge);

			// Microsoft IE and Microsoft Edge don't understand Max-Age so send
			// expires as well. Without this, persistent cookies fail with those
			// browsers. See http://tomcat.markmail.org/thread/g6sipbofsjossacn

			header.append("; Expires=");
			// To expire immediately we need to set the time in past
			if (maxAge == 0) {
				header.append(ANCIENT_DATE);
			} else {
				COOKIE_DATE_FORMAT.get().format(new Date(System.currentTimeMillis() + maxAge * 1000L), header, new FieldPosition(0));
			}
		}

		String domain = cookie.getDomain();
		if (domain != null && domain.length() > 0) {
			header.append("; Domain=");
			header.append(domain);
		}

		String path = cookie.getPath();
		if (path != null && path.length() > 0) {
			header.append("; Path=");
			header.append(path);
		}

		if (cookie.getSecure()) {
			header.append("; Secure");
		}
		if (cookie.isHttpOnly()) {
			header.append("; HttpOnly");
		}

		header.append("; SameSite=None");
				
		return header.toString();
	}
}

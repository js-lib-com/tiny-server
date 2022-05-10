package js.tiny.server.servlet;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

class StringsEnumeration implements Enumeration<String> {
	private final Iterator<String> iterator;

	public StringsEnumeration(Collection<String> names) {
		this.iterator = names.iterator();
	}

	@Override
	public boolean hasMoreElements() {
		return iterator.hasNext();
	}

	@Override
	public String nextElement() {
		return iterator.next();
	}
}

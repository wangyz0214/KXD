package kxd.util.memcached;

import java.io.IOException;

public class MemCachedException extends IOException {
	private static final long serialVersionUID = -5283961559628153679L;

	public MemCachedException() {
		super();
	}

	public MemCachedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public MemCachedException(String arg0) {
		super(arg0);
	}

	public MemCachedException(Throwable arg0) {
		super(arg0);
	}

}

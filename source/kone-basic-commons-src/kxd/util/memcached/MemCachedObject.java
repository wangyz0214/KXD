package kxd.util.memcached;

import java.io.InputStream;
import java.io.OutputStream;

public interface MemCachedObject {
	public int encodedSize();

	public void encode(OutputStream stream);

	public void decode(InputStream stream);
}

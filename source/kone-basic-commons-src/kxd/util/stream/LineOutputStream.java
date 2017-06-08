package kxd.util.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class LineOutputStream extends OutputStream {
	ArrayList<byte[]> lines = new ArrayList<byte[]>();
	byte[] buffer;
	int pos = 0;
	byte endChar = '\n';
	boolean writeDisabled = false;

	public LineOutputStream() {
	}

	public LineOutputStream(byte endChar) {
		this.endChar = endChar;
	}

	@Override
	public void write(int b) throws IOException {
		if (writeDisabled)
			throw new IOException("Stream is disabled to write!");
		if (b == endChar) {
			byte[] buf = new byte[pos];
			if (buffer != null)
				System.arraycopy(buffer, 0, buf, 0, pos);
			lines.add(buf);
			pos = 0;
			buffer = null;
			return;
		} else {
			if (buffer == null) {
				buffer = new byte[1024];
				pos = 0;
			} else if ((pos + 1) >= buffer.length) {
				byte buf[] = new byte[buffer.length + 1024];
				System.arraycopy(buffer, 0, buf, 0, buffer.length);
				buffer = null;
				buffer = buf;
			}
		}
		buffer[pos++] = (byte) b;
	}

	public byte getEndChar() {
		return endChar;
	}

	public void setEndChar(byte endChar) {
		this.endChar = endChar;
	}

	/**
	 * 获取所有行数据，调用了这个方法之后，流不能再写入
	 * 
	 */
	public ArrayList<byte[]> getLines() {
		if (!writeDisabled) {
			writeDisabled = true;
			if (buffer != null) {
				byte[] buf = new byte[pos];
				System.arraycopy(buffer, 0, buf, 0, pos);
				lines.add(buf);
				buffer = null;
			}
		}
		return lines;
	}

}

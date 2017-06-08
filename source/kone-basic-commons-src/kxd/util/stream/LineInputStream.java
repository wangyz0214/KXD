package kxd.util.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class LineInputStream extends InputStream {
	ArrayList<byte[]> lines = new ArrayList<byte[]>();
	byte[] curLine = null;
	int line = 0;
	int pos = 0;
	byte endChar = '\n';

	public LineInputStream(ArrayList<byte[]> lines) {
		this(lines, (byte) '\n');
	}

	public LineInputStream(ArrayList<byte[]> lines, byte endChar) {
		this.lines = lines;
		this.endChar = endChar;
	}

	@Override
	public int read() throws IOException {
		if (curLine == null) {
			if (line >= lines.size())
				return -1;
			curLine = lines.get(line);
			line++;
		}
		int r;
		if (pos < curLine.length)
			r = curLine[pos++];
		else {
			r = endChar;
			curLine = null;
			pos = 0;
		}
		return r;
	}

	public byte getEndChar() {
		return endChar;
	}

	public void setEndChar(byte endChar) {
		this.endChar = endChar;
	}

	/**
	 * 获取所有行数据
	 * 
	 */
	public ArrayList<byte[]> getLines() {
		return lines;
	}

}

package kxd.engine.scs.invoice.template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kxd.util.StringUnit;
import kxd.util.stream.Stream;

public class Column implements Fieldable {
	private static final long serialVersionUID = 1L;
	/**
	 * 宽度
	 */
	private int label_width;
	/**
	 * 对齐方式:left-左对齐，center-居中，right-右对齐
	 */
	private String label_align;
	/**
	 * 填充字符
	 */
	private byte label_fillchar = ' ';
	/**
	 * 值宽度
	 */
	private int value_width;
	/**
	 * 值对齐方式:left-左对齐，center-居中，right-右对齐
	 */
	private String value_align;
	/**
	 * 值填充字符
	 */
	private byte value_fillchar = ' ';
	/**
	 * 正则
	 */
	private String regexp;
	/**
	 * 字段总行数
	 */
	private int wordbreak_lines = 0;
	private int left;

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getLabel_width() {
		return label_width;
	}

	public void setLabel_width(int label_width) {
		this.label_width = label_width;
	}

	public String getLabel_align() {
		return label_align;
	}

	public void setLabel_align(String label_align) {
		this.label_align = label_align;
	}

	public byte getLabel_fillchar() {
		return label_fillchar;
	}

	public void setLabel_fillchar(byte label_fillchar) {
		this.label_fillchar = label_fillchar;
	}

	public int getValue_width() {
		return value_width;
	}

	public void setValue_width(int value_width) {
		this.value_width = value_width;
	}

	public String getValue_align() {
		return value_align;
	}

	public void setValue_align(String value_align) {
		this.value_align = value_align;
	}

	public byte getValue_fillchar() {
		return value_fillchar;
	}

	public void setValue_fillchar(byte value_fillchar) {
		this.value_fillchar = value_fillchar;
	}

	public String getRegexp() {
		return regexp;
	}

	public void setRegexp(String regexp) {
		this.regexp = regexp;
	}

	public int getWordbreak_lines() {
		return wordbreak_lines;
	}

	public void setWordbreak_lines(int wordbreak_lines) {
		this.wordbreak_lines = wordbreak_lines;
	}

	@Override
	public void readData(Stream stream) throws IOException {
		left = stream.readInt(false, 3000);
		label_align = stream.readPacketByteString(3000);
		value_align = stream.readPacketByteString(3000);
		label_width = stream.readInt(false, 3000);
		value_width = stream.readInt(false, 3000);
		label_fillchar = stream.readByte(3000);
		value_fillchar = stream.readByte(3000);
		regexp = stream.readPacketShortString(false, 3000);
		wordbreak_lines = stream.readByte(3000);
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		stream.writeInt(left, false, 3000);
		stream.writePacketByteString(label_align, 3000);
		stream.writePacketByteString(value_align, 3000);
		stream.writeInt(label_width, false, 3000);
		stream.writeInt(value_width, false, 3000);
		stream.writeByte(label_fillchar, 3000);
		stream.writeByte(value_fillchar, 3000);
		stream.writePacketShortString(regexp, false, 3000);
		stream.writeByte((byte) wordbreak_lines, 3000);
	}

	@Override
	public int getDataType() {
		return OBJ_COLUMN;
	}

	public void format(String label, String value, List<String> outList)
			throws Exception {
		List<String> ls = new ArrayList<String>(), vs = new ArrayList<String>();
		StringUnit.wordBreak(label, label_align, label_fillchar, label_width,
				0, ls);
		StringUnit.wordBreak(value, value_align, value_fillchar, value_width,
				0, vs);
		int c = ls.size();
		if (c > vs.size())
			c = vs.size();
		String leftstr = StringUnit.fillChar((byte) ' ', left);
		for (int i = 0; i < c; i++) {
			outList.add(leftstr + ls.get(i) + vs.get(i));
			if (wordbreak_lines > 0 && outList.size() >= wordbreak_lines)
				return;
		}
		if (c < vs.size()) {
			String f = StringUnit.fillChar(label_fillchar, label_width);
			for (int i = c; i < vs.size(); i++) {
				outList.add(leftstr + f + vs.get(i));
				if (wordbreak_lines > 0 && outList.size() >= wordbreak_lines)
					return;
			}
		} else if (c < ls.size()) {
			String f = StringUnit.fillChar(value_fillchar, value_width);
			for (int i = c; i < ls.size(); i++) {
				outList.add(leftstr + ls.get(i) + f);
				if (wordbreak_lines > 0 && outList.size() >= wordbreak_lines)
					return;
			}
		}
	}
}

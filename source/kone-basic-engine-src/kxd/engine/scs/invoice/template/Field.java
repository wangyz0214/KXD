package kxd.engine.scs.invoice.template;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import kxd.util.StringUnit;
import kxd.util.stream.Stream;

public class Field implements Fieldable {
	private static final long serialVersionUID = 4396761268701483746L;
	/**
	 * 字段名称
	 */
	private String name;
	/**
	 * 字段描述
	 */
	private String desp;
	/**
	 * 宽度
	 */
	private int width;
	/**
	 * 对齐方式:left-左对齐，center-居中，right-右对齐
	 */
	private String align = "left";
	/**
	 * 填充字符
	 */
	private byte fillchar = ' ';
	/**
	 * 字段值
	 */
	private String value = "";
	/**
	 * 正则
	 */
	private String regexp;
	/**
	 * 字段总行数
	 */
	private int wordbreak_lines = 0;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.trim();
	}

	public String getDesp() {
		return desp;
	}

	public void setDesp(String desp) {
		this.desp = desp;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public void setFillchar(String fillchar) {
		this.fillchar = (byte) fillchar.charAt(0);
	}

	public byte getFillchar() {
		return fillchar;
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

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setFillchar(byte fillchar) {
		this.fillchar = fillchar;
	}

	@Override
	public void readData(Stream stream) throws IOException {
		name = stream.readPacketByteString(3000);
		desp = stream.readPacketByteString(3000);
		width = stream.readInt(false, 3000);
		align = stream.readPacketByteString(3000);
		fillchar = stream.readByte(3000);
		value = stream.readPacketShortString(false, 3000);
		regexp = stream.readPacketShortString(false, 3000);
		wordbreak_lines = stream.readByte(3000);
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		stream.writePacketByteString(name, 3000);
		stream.writePacketByteString(desp, 3000);
		stream.writeInt(width, false, 3000);
		stream.writePacketByteString(align, 3000);
		stream.writeByte(fillchar, 3000);
		stream.writePacketShortString(value, false, 3000);
		stream.writePacketShortString(regexp, false, 3000);
		stream.writeByte((byte) wordbreak_lines, 3000);
	}

	@Override
	public int getDataType() {
		return OBJ_FIELD;
	}

	public void format(Map<String, String> params, List<String> outList)
			throws Exception {
		String str;
		if (name == null || name.isEmpty())
			str = value;
		else
			str = params.get(name);
		if (str == null)
			str = "";
		StringUnit.wordBreak(str, align, fillchar, width, wordbreak_lines,
				outList);
	}

}

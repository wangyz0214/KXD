package kxd.engine.scs.invoice.template;

import java.io.IOException;
import java.util.List;

import kxd.engine.cache.beans.sts.CachedTerm;
import kxd.engine.helper.TermHelper;
import kxd.remote.scs.beans.adinfo.EditedPrintAd;
import kxd.util.StringUnit;
import kxd.util.stream.Stream;

public class AdRow implements Fieldable, Rowable {
	private static final long serialVersionUID = 4396761268701483746L;
	/**
	 * 广告分类ID
	 */
	private int adcategoryid = 0;
	/**
	 * 对齐方式:left-左对齐，center-居中，right-右对齐
	 */
	private String align = "left";
	/**
	 * 填充字符
	 */
	private byte fillchar = ' ';
	/**
	 * 宽度
	 */
	private int width;
	/**
	 * 字段值长度超过width时的换行数
	 */
	private int wordbreak_lines;

	public int getAdcategoryid() {
		return adcategoryid;
	}

	public void setAdcategoryid(int adcategoryid) {
		this.adcategoryid = adcategoryid;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public byte getFillchar() {
		return fillchar;
	}

	public void setFillchar(byte fillchar) {
		this.fillchar = fillchar;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getWordbreak_lines() {
		return wordbreak_lines;
	}

	public void setWordbreak_lines(int wordbreak_lines) {
		this.wordbreak_lines = wordbreak_lines;
	}

	@Override
	public void readData(Stream stream) throws IOException {
		adcategoryid = stream.readInt(false, 3000);
		width = stream.readInt(false, 3000);
		align = stream.readPacketByteString(3000);
		fillchar = stream.readByte(3000);
		wordbreak_lines = stream.readByte(3000);
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		stream.writeInt(adcategoryid, false, 3000);
		stream.writeInt(width, false, 3000);
		stream.writePacketByteString(align, 3000);
		stream.writeByte(fillchar, 3000);
		stream.writeByte((byte) wordbreak_lines, 3000);
	}

	@Override
	public int getDataType() {
		return OBJ_ADROW;
	}

	public void format(CachedTerm term, List<String> outList) throws Exception {
		EditedPrintAd ad = term == null ? null : TermHelper.getPrintAd(term,
				adcategoryid);
		String str = null;
		if (ad != null) {
			str = ad.getContent();
		}
		if (str == null)
			StringUnit.wordBreak("", align, fillchar, width, wordbreak_lines,
					outList);
		else {
			String[] sa = StringUnit.split(str, "\n");
			for (int i = 0; i < sa.length; i++)
				StringUnit.wordBreak(sa[i].trim(), align, fillchar, width,
						wordbreak_lines, outList);
		}
	}

}

package kxd.engine.scs.invoice.template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kxd.util.StringUnit;
import kxd.util.stream.Stream;

public class Row extends TemplateObjectList<Field> implements Rowable {
	private static final long serialVersionUID = 1;
	/**
	 * 左边距--保留暂未使用
	 */
	private int left = 0;
	/**
	 * 样式--保留暂未使用
	 */
	private String style = "";

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	@Override
	public void readData(Stream stream) throws IOException {
		left = stream.readInt(false, 3000);
		style = stream.readPacketShortString(false, 3000);
		super.readData(stream);
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		stream.writeInt(left, false, 3000);
		stream.writePacketShortString(style, false, 3000);
		super.writeData(stream);
	}

	@Override
	public Field newItemInstance(int type) {
		return new Field();
	}

	@Override
	public int getDataType() {
		return OBJ_ROW;
	}

	public void format(Map<String, String> params, List<String> outList)
			throws Exception {
		String leftstr = StringUnit.fillChar((byte) ' ', left);
		List<List<String>> cols = new ArrayList<List<String>>();
		int maxLines = 0;
		for (int i = 0; i < getItems().size(); i++) {
			List<String> ls = new ArrayList<String>();
			getItems().get(i).format(params, ls);
			cols.add(ls);
			if (ls.size() > maxLines)
				maxLines = ls.size();
		}
		for (int i = 0; i < cols.size(); i++) {
			List<String> ls = cols.get(i);
			if (ls.size() < maxLines) {
				String f = StringUnit.fillChar((byte) ' ', getItems().get(i)
						.getWidth());
				for (int j = 0; j < maxLines - ls.size(); j++) {
					ls.add(f);
				}
			}
		}
		for (int i = 0; i < maxLines; i++) {
			String str = leftstr;
			for (int j = 0; j < cols.size(); j++)
				str += cols.get(j).get(i);
			outList.add(leftstr + str);
		}
	}
}

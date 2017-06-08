package kxd.engine.scs.invoice.template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kxd.util.StringUnit;
import kxd.util.stream.Stream;

public class Detail extends Columns {
	private static final long serialVersionUID = 1;
	int rows;
	int rowDistances;
	int left;

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getRowDistances() {
		return rowDistances;
	}

	public void setRowDistances(int rowDistances) {
		this.rowDistances = rowDistances;
	}

	@Override
	public int getDataType() {
		return OBJ_DETAIL;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	@Override
	public void readData(Stream stream) throws IOException {
		left = stream.readInt(false, 3000);
		rows = stream.readInt(false, 3000);
		rowDistances = stream.readInt(false, 3000);
		super.readData(stream);
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		stream.writeInt(left, false, 3000);
		stream.writeInt(rows, false, 3000);
		stream.writeInt(rowDistances, false, 3000);
		super.writeData(stream);
	}

	// 从左至右，多列格式化
	public void format(List<String[]> details, List<String> outList)
			throws Exception {
		String leftstr = StringUnit.fillChar((byte) ' ', left);
		int colIndex = 0;
		Column column = getItems().get(0);
		String columnfill = StringUnit.fillChar((byte) ' ',
				column.getValue_width() + column.getLabel_width());
		List<List<String>> cols = new ArrayList<List<String>>();
		List<String> cs = new ArrayList<String>();
		cols.add(cs);
		List<String> ls = new ArrayList<String>();
		for (String[] v : details) {
			ls.clear();
			column.format(v[0], v[1], ls);
			if (cs.size() + ls.size() > rows) {
				colIndex++;
				int len = rows - cs.size();
				if (colIndex >= getItems().size()) { // 没有多余的列
					for (int i = 0; i < len; i++)
						cs.add(ls.get(i));
					break;
				} else { // 填满空行后，另起一行
					for (int i = 0; i < len; i++)
						cs.add(columnfill);
					column = getItems().get(colIndex);
					columnfill = StringUnit.fillChar((byte) ' ',
							column.getValue_width() + column.getLabel_width());
					ls.clear();
					column.format(v[0], v[1], ls);
					cs = new ArrayList<String>();
					cols.add(cs);
				}
			}
			cs.addAll(ls);
			if (cs.size() < rows - rowDistances) { // 添加行间距
				for (int i = 0; i < rowDistances; i++)
					cs.add(columnfill);
			}
		}
		while (cs.size() < rows)
			cs.add(columnfill);
		for (int i = 0; i < rows; i++) {
			String str = "";
			for (int j = 0; j < cols.size(); j++)
				str += cols.get(j).get(i);
			outList.add(leftstr + str);
		}
	}
}

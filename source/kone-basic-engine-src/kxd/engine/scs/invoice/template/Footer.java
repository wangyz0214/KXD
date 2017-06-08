package kxd.engine.scs.invoice.template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kxd.engine.cache.beans.sts.CachedTerm;
import kxd.util.stream.Stream;

public class Footer extends TemplateObjectList<Rowable> {
	private static final long serialVersionUID = 1;
	int rows;

	@Override
	public int getDataType() {
		return OBJ_FOOTER;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	@Override
	public Rowable newItemInstance(int type) {
		switch (type) {
		case OBJ_ADROW:
			return new AdRow();
		case OBJ_ROW:
			return new Row();
		default:
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public void readData(Stream stream) throws IOException {
		rows = stream.readInt(false, 3000);
		super.readData(stream);
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		stream.writeInt(rows, false, 3000);
		super.writeData(stream);
	}

	public void format(CachedTerm term, Map<String, String> params,
			List<String> outList) throws Exception {
		List<String> ls = new ArrayList<String>();
		for (Rowable o : getItems()) {
			if (o instanceof Row) {
				((Row) o).format(params, ls);
			} else
				((AdRow) o).format(term, ls);
		}
		while (ls.size() > rows)
			ls.remove(ls.size() - 1);
		while (ls.size() < rows)
			ls.add("");
		outList.addAll(ls);
	}

}

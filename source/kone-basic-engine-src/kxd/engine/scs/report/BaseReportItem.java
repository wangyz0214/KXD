package kxd.engine.scs.report;

import java.io.IOException;

import kxd.util.IdableObject;
import kxd.util.ListItem;
import kxd.util.Streamable;
import kxd.util.stream.Stream;

/**
 * 基于报表项接口的一个基本实现
 * 
 * @author zhaom
 * 
 * @param <N>
 *            报表名称项
 */
abstract public class BaseReportItem<N extends Streamable> extends
		ListItem<Integer> implements ReportItem<N>, Streamable {
	private static final long serialVersionUID = 1L;
	protected N nameItem;

	@Override
	public N getNameItem() {
		return nameItem;
	}

	@Override
	public void setNameItem(N item) {
		nameItem = item;
	}

	@Override
	public String getText() {
		return nameItem.toString();
	}

	@Override
	public void setText(String text) {
	}

	@Override
	protected String toDisplayLabel() {
		return getText();
	}

	@Override
	public IdableObject<Integer> createObject() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getIdString() {
		return Integer.toString(getId());
	}

	@Override
	public void setIdString(String id) {
		setId(Integer.valueOf(id));
	}

	@Override
	public void readData(Stream stream) throws IOException {
		nameItem.readData(stream);
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		nameItem.writeData(stream);
	}

}

package kxd.engine.scs.report.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import kxd.engine.scs.report.BaseReportItem;
import kxd.engine.scs.report.MonthReportList;
import kxd.engine.scs.report.ReportItem;
import kxd.engine.scs.report.ReportList;
import kxd.remote.scs.util.AppException;
import kxd.util.Streamable;
import kxd.util.stream.Stream;

public class TermPrintStatItem<N extends Streamable> extends BaseReportItem<N> {
	private static final long serialVersionUID = 1L;
	long count = 0, lines = 0, prevMonthCount = 0, prevMonthLines = 0;
	String countRate, linesRate;
	List<TermPrintStatField> items = new ArrayList<TermPrintStatField>();
	HashMap<Short, TermPrintStatField> itemMap = new HashMap<Short, TermPrintStatField>();

	public TermPrintStatItem(Collection<Short> businessList) {
		for (Short o : businessList) {
			TermPrintStatField item = new TermPrintStatField();
			item.setId(o);
			itemMap.put(o, item);
			items.add(item);
		}
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public long getPrevMonthCount() {
		return prevMonthCount;
	}

	public void setPrevMonthCount(long prevMonthCount) {
		this.prevMonthCount = prevMonthCount;
	}

	public long getLines() {
		return lines;
	}

	public void setLines(long lines) {
		this.lines = lines;
	}

	public long getPrevMonthLines() {
		return prevMonthLines;
	}

	public void setPrevMonthLines(long prevMonthLines) {
		this.prevMonthLines = prevMonthLines;
	}

	public String getCountRate() {
		return countRate;
	}

	public void setCountRate(String countRate) {
		this.countRate = countRate;
	}

	public String getLinesRate() {
		return linesRate;
	}

	public void setLinesRate(String rate) {
		this.linesRate = rate;
	}

	public List<TermPrintStatField> getItems() {
		return items;
	}

	public HashMap<Short, TermPrintStatField> getItemMap() {
		return itemMap;
	}

	/**
	 * @param data
	 *            统计数据数组：ident,month,printtype,count1,line1,count2,line2 ,...
	 */
	@Override
	public void addData(ReportList<?, ?> list, Object[] data) {
		MonthReportList<?, ?> ls = (MonthReportList<?, ?>) list;
		int month = Integer.valueOf(data[1].toString());
		long c = 0;
		long m = 0;
		TermPrintStatField item = itemMap
				.get(Short.valueOf(data[2].toString()));
		if (item == null)
			throw new AppException("找不到打印类型[id=" + data[2] + "]");
		for (int i = 3; i < data.length; i += 2) {
			c += Integer.valueOf(data[i].toString());
			m += Integer.valueOf(data[i + 1].toString());
		}
		if (month == ls.getMonth()) {
			item.setLines(item.getLines() + m);
			item.setCount(item.getCount() + c);
			count += c;
			lines += m;
		} else {
			prevMonthCount += c;
			prevMonthLines += m;
		}
	}

	@Override
	public void addData(ReportList<?, ?> list, ReportItem<N> item) {
		TermPrintStatItem<N> st = (TermPrintStatItem<N>) item;
		for (int i = 0; i < st.items.size(); i++) {
			TermPrintStatField f = st.items.get(i);
			TermPrintStatField f1 = items.get(i);
			f1.setLines(f.getLines() + f1.getLines());
			f1.setCount(f.getCount() + f1.getCount());
		}
		count += st.count;
		prevMonthCount += st.prevMonthCount;
		lines += st.lines;
		prevMonthLines += st.prevMonthLines;
	}

	@Override
	public void complele(ReportList<?, ?> list) {
		if (getPrevMonthCount() > 0)
			setCountRate(Long.toString((getCount() - getPrevMonthCount()) * 100
					/ getPrevMonthCount())
					+ "%");
		else
			setCountRate("-");
		if (getPrevMonthLines() > 0)
			setLinesRate(Long.toString((getLines() - getPrevMonthLines()) * 100
					/ getPrevMonthLines())
					+ "%");
		else
			setLinesRate("-");
	}

	@Override
	public void readData(Stream stream) throws IOException {
		super.readData(stream);
		count = stream.readLong(3000);
		lines = stream.readLong(3000);
		prevMonthCount = stream.readLong(3000);
		prevMonthLines = stream.readLong(3000);
		countRate = stream.readPacketByteString(3000);
		linesRate = stream.readPacketByteString(3000);
		items.clear();
		itemMap.clear();
		int c = stream.readInt(false, 3000);
		for (int i = 0; i < c; i++) {
			TermPrintStatField o = new TermPrintStatField();
			o.readData(stream);
			items.add(o);
			itemMap.put(o.getId(), o);
		}
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		super.writeData(stream);
		stream.writeLong(count, 3000);
		stream.writeLong(lines, 3000);
		stream.writeLong(prevMonthCount, 3000);
		stream.writeLong(prevMonthLines, 3000);
		stream.writePacketByteString(countRate, 3000);
		stream.writePacketByteString(linesRate, 3000);
		stream.writeInt(items.size(), false, 3000);
		for (TermPrintStatField o : items) {
			o.writeData(stream);
		}
	}
}

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

public class PageClickStatItem<N extends Streamable> extends BaseReportItem<N> {
	private static final long serialVersionUID = 1L;
	long count = 0, prevMonthCount = 0;
	String countRate;
	List<PageClickStatField> items = new ArrayList<PageClickStatField>();
	HashMap<Integer, PageClickStatField> itemMap = new HashMap<Integer, PageClickStatField>();

	public PageClickStatItem(Collection<Integer> businessList) {
		for (Integer o : businessList) {
			PageClickStatField item = new PageClickStatField();
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

	public String getCountRate() {
		return countRate;
	}

	public void setCountRate(String countRate) {
		this.countRate = countRate;
	}

	public HashMap<Integer, PageClickStatField> getItemMap() {
		return itemMap;
	}

	public Collection<PageClickStatField> getItems() {
		return items;
	}

	/**
	 * @param data
	 *            统计数据数组：ident,month,businessid,count1,count2,...,count31
	 * 
	 */
	@Override
	public void addData(ReportList<?, ?> list, Object[] data) {
		MonthReportList<?, ?> ls = (MonthReportList<?, ?>) list;
		int month = Integer.valueOf(data[1].toString());
		long c = 0;
		PageClickStatField item = itemMap.get(Integer.valueOf(data[2]
				.toString()));
		if (item == null)
			throw new AppException("找不到业务分类[id=" + data[2] + "]");
		for (int i = 3; i < data.length; i++) {
			c += Integer.valueOf(data[i].toString());
		}
		if (month == ls.getMonth()) {
			item.setCount(item.getCount() + c);
			count += c;
		} else {
			prevMonthCount += c;
		}
	}

	@Override
	public void addData(ReportList<?, ?> list, ReportItem<N> item) {
		PageClickStatItem<N> st = (PageClickStatItem<N>) item;
		for (int i = 0; i < st.items.size(); i++) {
			PageClickStatField f = st.items.get(i);
			PageClickStatField f1 = items.get(i);
			f1.setCount(f.getCount() + f1.getCount());
		}
		count += st.count;
		prevMonthCount += st.prevMonthCount;
	}

	@Override
	public void complele(ReportList<?, ?> list) {
		if (getPrevMonthCount() > 0)
			setCountRate(Long.toString((getCount() - getPrevMonthCount()) * 100
					/ getPrevMonthCount())
					+ "%");
		else
			setCountRate("-");
	}

	@Override
	public void readData(Stream stream) throws IOException {
		super.readData(stream);
		count = stream.readLong(3000);
		prevMonthCount = stream.readLong(3000);
		countRate = stream.readPacketByteString(3000);
		items.clear();
		itemMap.clear();
		int c = stream.readInt(false, 3000);
		for (int i = 0; i < c; i++) {
			PageClickStatField o = new PageClickStatField();
			o.readData(stream);
			items.add(o);
			itemMap.put(o.getId(), o);
		}
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		super.writeData(stream);
		stream.writeLong(count, 3000);
		stream.writePacketByteString(countRate, 3000);
		stream.writeInt(items.size(), false, 3000);
		for (PageClickStatField o : items) {
			o.writeData(stream);
		}
	}

}

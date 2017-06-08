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

public class BusinessStatItem<N extends Streamable> extends BaseReportItem<N> {
	private static final long serialVersionUID = 1L;
	long count = 0, money = 0, prevMonthCount = 0, prevMonthMoney = 0;
	String countRate, moneyRate;
	List<BusinessStatField> items = new ArrayList<BusinessStatField>();
	HashMap<Integer, BusinessStatField> itemMap = new HashMap<Integer, BusinessStatField>();

	public BusinessStatItem(Collection<Integer> businessList) {
		for (Integer o : businessList) {
			BusinessStatField item = new BusinessStatField();
			item.setId(o);
			itemMap.put(o, item);
			items.add(item);
		}
	}

	public BusinessStatItem() {

	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public long getMoney() {
		return money;
	}

	public void setMoney(long money) {
		this.money = money;
	}

	public long getPrevMonthCount() {
		return prevMonthCount;
	}

	public void setPrevMonthCount(long prevMonthCount) {
		this.prevMonthCount = prevMonthCount;
	}

	public long getPrevMonthMoney() {
		return prevMonthMoney;
	}

	public void setPrevMonthMoney(long prevMonthMoney) {
		this.prevMonthMoney = prevMonthMoney;
	}

	public String getCountRate() {
		return countRate;
	}

	public void setCountRate(String countRate) {
		this.countRate = countRate;
	}

	public String getMoneyRate() {
		return moneyRate;
	}

	public void setMoneyRate(String moneyRate) {
		this.moneyRate = moneyRate;
	}

	public HashMap<Integer, BusinessStatField> getItemMap() {
		return itemMap;
	}

	/**
	 * @param data
	 *            统计数据数组：ident,orgid,month,businessid,count1,money1,count2,
	 *            money2,...,count31 ,money31
	 */
	@Override
	public void addData(ReportList<?, ?> list, Object[] data) {
		MonthReportList<?, ?> ls = (MonthReportList<?, ?>) list;
		int month = Integer.valueOf(data[2].toString());
		long c = 0;
		long m = 0;
		BusinessStatField item = itemMap
				.get(Integer.valueOf(data[3].toString()));
		if (item == null)
			throw new AppException("找不到业务分类[id=" + data[2] + "]");
		for (int i = 4; i < data.length; i += 2) {
			c += Integer.valueOf(data[i].toString());
			m += Integer.valueOf(data[i + 1].toString());
		}
		if (month == ls.getMonth()) {
			item.setAmount(item.getAmount() + m);
			item.setCount(item.getCount() + c);
			count += c;
			money += m;
		} else {
			prevMonthCount += c;
			prevMonthMoney += m;
		}
	}

	@Override
	public void addData(ReportList<?, ?> list, ReportItem<N> item) {
		BusinessStatItem<N> st = (BusinessStatItem<N>) item;
		for (int i = 0; i < st.items.size(); i++) {
			BusinessStatField f = st.items.get(i);
			BusinessStatField f1 = items.get(i);
			f1.setAmount(f.getAmount() + f1.getAmount());
			f1.setCount(f.getCount() + f1.getCount());
		}
		count += st.count;
		prevMonthCount += st.prevMonthCount;
		money += st.money;
		prevMonthMoney += st.prevMonthMoney;
	}

	@Override
	public void complele(ReportList<?, ?> list) {
		if (getPrevMonthCount() > 0)
			setCountRate(Long.toString((getCount() - getPrevMonthCount()) * 100
					/ getPrevMonthCount())
					+ "%");
		else
			setCountRate("-");
		if (getPrevMonthMoney() > 0)
			setMoneyRate(Long.toString((getMoney() - getPrevMonthMoney()) * 100
					/ getPrevMonthMoney())
					+ "%");
		else
			setMoneyRate("-");
	}

	public List<BusinessStatField> getItems() {
		return items;
	}

	@Override
	public void readData(Stream stream) throws IOException {
		super.readData(stream);
		count = stream.readLong(3000);
		money = stream.readLong(3000);
		prevMonthCount = stream.readLong(3000);
		prevMonthMoney = stream.readLong(3000);
		countRate = stream.readPacketByteString(3000);
		moneyRate = stream.readPacketByteString(3000);
		int c = stream.readInt(false, 3000);
		items.clear();
		itemMap.clear();
		for (int i = 0; i < c; i++) {
			BusinessStatField o = new BusinessStatField();
			o.readData(stream);
			items.add(o);
			itemMap.put(o.getId(), o);
		}
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		super.writeData(stream);
		stream.writeLong(count, 3000);
		stream.writeLong(money, 3000);
		stream.writeLong(prevMonthCount, 3000);
		stream.writeLong(prevMonthMoney, 3000);
		stream.writePacketByteString(countRate, 3000);
		stream.writePacketByteString(moneyRate, 3000);
		stream.writeInt(items.size(), false, 3000);
		for (BusinessStatField o : items) {
			o.writeData(stream);
		}
	}
}

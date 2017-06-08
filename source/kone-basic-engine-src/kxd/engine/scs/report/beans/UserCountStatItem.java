package kxd.engine.scs.report.beans;

import java.io.IOException;

import kxd.engine.helper.beans.IdentOrg;
import kxd.engine.scs.report.BaseReportItem;
import kxd.engine.scs.report.MonthDayReportList;
import kxd.engine.scs.report.ReportItem;
import kxd.engine.scs.report.ReportList;
import kxd.util.stream.Stream;

public class UserCountStatItem extends BaseReportItem<IdentOrg> {
	private static final long serialVersionUID = 1L;
	long toDayCount = 0, curMonthCount = 0, prevMonthCount = 0;
	String rate;

	public long getToDayCount() {
		return toDayCount;
	}

	public void setToDayCount(long toDayCount) {
		this.toDayCount = toDayCount;
	}

	public long getCurMonthCount() {
		return curMonthCount;
	}

	public void setCurMonthCount(long curMonthCount) {
		this.curMonthCount = curMonthCount;
	}

	public long getPrevMonthCount() {
		return prevMonthCount;
	}

	public void setPrevMonthCount(long prevMonthCount) {
		this.prevMonthCount = prevMonthCount;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	/**
	 * @param data
	 *            统计数据数组：ident,month,count1,count2,...,count31
	 */
	@Override
	public void addData(ReportList<?, ?> list, Object[] data) {
		MonthDayReportList<?, ?> ls = (MonthDayReportList<?, ?>) list;
		int month = Integer.valueOf(data[1].toString());
		long c = 0;
		for (int i = 1; i < data.length - 1; i++) {
			// 从第2个数据开始为日期统计项
			int cc = Integer.valueOf(data[i + 1].toString());
			c += cc;
			if (i == ls.getDay() && month == ls.getMonth())
				toDayCount += cc;
		}
		if (month == ls.getMonth())
			curMonthCount += c;
		else
			prevMonthCount += c;
	}

	@Override
	public void addData(ReportList<?, ?> list, ReportItem<IdentOrg> item) {
		UserCountStatItem o = (UserCountStatItem) item;
		this.curMonthCount += o.curMonthCount;
		this.prevMonthCount += o.prevMonthCount;
		this.toDayCount += o.toDayCount;
	}

	@Override
	public void complele(ReportList<?, ?> list) {
		if (prevMonthCount > 0)
			setRate(Long.toString((curMonthCount - prevMonthCount) * 100
					/ prevMonthCount)
					+ "%");
		else
			setRate("-");
	}

	@Override
	public void readData(Stream stream) throws IOException {
		super.readData(stream);
		toDayCount = stream.readLong(3000);
		curMonthCount = stream.readLong(3000);
		prevMonthCount = stream.readLong(3000);
		rate = stream.readPacketByteString(3000);
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		super.writeData(stream);
		stream.writeLong(toDayCount, 3000);
		stream.writeLong(curMonthCount, 3000);
		stream.writeLong(prevMonthCount, 3000);
		stream.writePacketByteString(rate, 3000);
	}

}

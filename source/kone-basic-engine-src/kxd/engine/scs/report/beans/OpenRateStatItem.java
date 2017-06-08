package kxd.engine.scs.report.beans;

import java.io.IOException;

import kxd.engine.scs.report.BaseReportItem;
import kxd.engine.scs.report.MonthDayReportList;
import kxd.engine.scs.report.ReportItem;
import kxd.engine.scs.report.ReportList;
import kxd.engine.scs.report.TermNameItem;
import kxd.util.stream.Stream;

public class OpenRateStatItem extends BaseReportItem<TermNameItem> {
	private static final long serialVersionUID = 1L;
	long needOpenTime = 0, realOpenTime = 0, busyTime = 0, idleTime = 0;
	long prevMonthOpenTime = 0;
	String openRate, cycleRate;

	public long getNeedOpenTime() {
		return needOpenTime;
	}

	public void setNeedOpenTime(long needOpenTime) {
		this.needOpenTime = needOpenTime;
	}

	public long getRealOpenTime() {
		return realOpenTime;
	}

	public void setRealOpenTime(long realOpenTime) {
		this.realOpenTime = realOpenTime;
	}

	public long getBusyTime() {
		return busyTime;
	}

	public void setBusyTime(long busyTime) {
		this.busyTime = busyTime;
	}

	public long getIdleTime() {
		return idleTime;
	}

	public void setIdleTime(long idleTime) {
		this.idleTime = idleTime;
	}

	public long getPrevMonthOpenTime() {
		return prevMonthOpenTime;
	}

	public void setPrevMonthOpenTime(long prevMonthOpenTime) {
		this.prevMonthOpenTime = prevMonthOpenTime;
	}

	public String getOpenRate() {
		return openRate;
	}

	public void setOpenRate(String openRate) {
		this.openRate = openRate;
	}

	public String getCycleRate() {
		return cycleRate;
	}

	public void setCycleRate(String cycleRate) {
		this.cycleRate = cycleRate;
	}

	/**
	 * @param data
	 *            统计数据数组：ident,month,idle_time1,busy_time1,...,
	 */
	@Override
	public void addData(ReportList<?, ?> list, Object[] data) {
		MonthDayReportList<?, ?> ls = (MonthDayReportList<?, ?>) list;
		int month = Integer.valueOf(data[1].toString());
		long c = 0;
		long c1 = 0;
		for (int i = 2; i < data.length - 1; i += 2) {
			// 从第2个数据开始为日期统计项
			c += Integer.valueOf(data[i].toString());
			c1 += Integer.valueOf(data[i + 1].toString());
		}
		if (month == ls.getMonth()) {
			realOpenTime += c + c1;
			idleTime += c;
			busyTime += c1;
		} else
			prevMonthOpenTime += c + c1;
	}

	@Override
	public void addData(ReportList<?, ?> list, ReportItem<TermNameItem> item) {
		OpenRateStatItem o = (OpenRateStatItem) item;
		this.needOpenTime += o.needOpenTime;
		this.realOpenTime += o.realOpenTime;
		this.prevMonthOpenTime = o.prevMonthOpenTime;
		this.busyTime += o.busyTime;
		this.idleTime += o.idleTime;
	}

	@Override
	public void complele(ReportList<?, ?> list) {
		if (prevMonthOpenTime > 0)
			setCycleRate(Long.toString((realOpenTime - prevMonthOpenTime) * 100
					/ prevMonthOpenTime)
					+ "%");
		else
			setCycleRate("-");
		if (needOpenTime > 0)
			setOpenRate(Long.toString((realOpenTime) * 100 / needOpenTime)
					+ "%");
		else
			setOpenRate("-");
	}

	@Override
	public void readData(Stream stream) throws IOException {
		super.readData(stream);
		needOpenTime = stream.readLong(3000);
		realOpenTime = stream.readLong(3000);
		busyTime = stream.readLong(3000);
		idleTime = stream.readLong(3000);
		prevMonthOpenTime = stream.readLong(3000);
		openRate = stream.readPacketByteString(3000);
		cycleRate = stream.readPacketByteString(3000);
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		super.writeData(stream);
		stream.writeLong(needOpenTime, 3000);
		stream.writeLong(realOpenTime, 3000);
		stream.writeLong(busyTime, 3000);
		stream.writeLong(idleTime, 3000);
		stream.writeLong(prevMonthOpenTime, 3000);
		stream.writePacketByteString(openRate, 3000);
		stream.writePacketByteString(cycleRate, 3000);
	}

}

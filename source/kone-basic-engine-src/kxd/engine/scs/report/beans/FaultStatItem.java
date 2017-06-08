package kxd.engine.scs.report.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import kxd.engine.scs.report.BaseReportItem;
import kxd.engine.scs.report.MonthDayReportList;
import kxd.engine.scs.report.ReportItem;
import kxd.engine.scs.report.ReportList;
import kxd.engine.scs.report.TermNameItem;
import kxd.remote.scs.util.AppException;
import kxd.util.stream.Stream;

public class FaultStatItem extends BaseReportItem<TermNameItem> {
	private static final long serialVersionUID = 1L;
	List<FaultStatField> items = new ArrayList<FaultStatField>();
	HashMap<Integer, FaultStatField> itemMap = new HashMap<Integer, FaultStatField>();

	public FaultStatItem(Collection<Integer> list) {
		for (Integer o : list) {
			FaultStatField item = new FaultStatField();
			item.setFaultType(o);
			itemMap.put(o, item);
			items.add(item);
		}
	}

	public FaultStatItem() {
	}

	FaultStatField1 faultCount = new FaultStatField1(),
			faultTime = new FaultStatField1(),
			responseTime = new FaultStatField1(),
			restoreTime = new FaultStatField1();

	public FaultStatField1 getFaultCount() {
		return faultCount;
	}

	public void setFaultCount(FaultStatField1 faultCount) {
		this.faultCount = faultCount;
	}

	public FaultStatField1 getFaultTime() {
		return faultTime;
	}

	public void setFaultTime(FaultStatField1 faultTime) {
		this.faultTime = faultTime;
	}

	public FaultStatField1 getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(FaultStatField1 responseTime) {
		this.responseTime = responseTime;
	}

	public FaultStatField1 getRestoreTime() {
		return restoreTime;
	}

	public void setRestoreTime(FaultStatField1 restoreTime) {
		this.restoreTime = restoreTime;
	}

	public List<FaultStatField> getItems() {
		return items;
	}

	/**
	 * @param data
	 *            统计数据数组：termid,month,faulttype,fault_count_1,fault_time_1,
	 *            response_time_1 , restore_time_...,
	 */
	@Override
	public void addData(ReportList<?, ?> list, Object[] data) {
		MonthDayReportList<?, ?> ls = (MonthDayReportList<?, ?>) list;
		int month = Integer.valueOf(data[1].toString());
		long fc = 0;
		long ft = 0;
		long rt = 0;
		long rs = 0;
		for (int i = 3; i < data.length; i += 4) {
			// 从第2个数据开始为日期统计项
			fc += Integer.valueOf(data[i].toString());
			ft += Integer.valueOf(data[i + 1].toString());
			rt += Integer.valueOf(data[i + 2].toString());
			rs += Integer.valueOf(data[i + 3].toString());
		}
		if (month == ls.getMonth()) {
			int type = Integer.valueOf(data[2].toString());
			FaultStatField field = itemMap.get(type);
			if (field == null)
				throw new AppException("找不到故障类型[" + type + "]");
			field.setCount(fc);
			faultCount.setCount(faultCount.getCount() + fc);
			faultTime.setCount(faultCount.getCount() + ft);
			responseTime.setCount(faultCount.getCount() + rt);
			restoreTime.setCount(faultCount.getCount() + rs);
		} else {
			faultCount.setPrevMonthCount(faultCount.getPrevMonthCount() + fc);
			faultTime.setPrevMonthCount(faultCount.getPrevMonthCount() + ft);
			responseTime.setPrevMonthCount(faultCount.getPrevMonthCount() + rt);
			restoreTime.setPrevMonthCount(faultCount.getPrevMonthCount() + rs);
		}
	}

	@Override
	public void addData(ReportList<?, ?> list, ReportItem<TermNameItem> item) {
		FaultStatItem o = (FaultStatItem) item;
		faultCount.setCount(faultCount.getCount() + o.faultCount.getCount());
		faultTime.setCount(faultTime.getCount() + o.faultTime.getCount());
		responseTime.setCount(responseTime.getCount()
				+ o.responseTime.getCount());
		restoreTime.setCount(restoreTime.getCount() + o.restoreTime.getCount());

		faultCount.setPrevMonthCount(faultCount.getPrevMonthCount()
				+ o.faultCount.getPrevMonthCount());
		faultTime.setPrevMonthCount(faultTime.getPrevMonthCount()
				+ o.faultTime.getPrevMonthCount());
		responseTime.setPrevMonthCount(responseTime.getPrevMonthCount()
				+ o.responseTime.getPrevMonthCount());
		restoreTime.setPrevMonthCount(restoreTime.getPrevMonthCount()
				+ o.restoreTime.getPrevMonthCount());
	}

	@Override
	public void complele(ReportList<?, ?> list) {
		if (faultCount.getPrevMonthCount() > 0)
			faultCount
					.setRate(Long.toString((faultCount.getCount() - faultCount
							.getPrevMonthCount())
							* 100
							/ faultCount.getPrevMonthCount()) + "%");
		else
			faultCount.setRate("-");
		if (faultTime.getPrevMonthCount() > 0)
			faultTime
					.setRate(Long.toString((faultTime.getCount() - faultTime
							.getPrevMonthCount())
							* 100
							/ faultTime.getPrevMonthCount()) + "%");
		else
			faultTime.setRate("-");
		if (responseTime.getPrevMonthCount() > 0)
			responseTime
					.setRate(Long.toString((responseTime.getCount() - responseTime
							.getPrevMonthCount())
							* 100
							/ responseTime.getPrevMonthCount())
							+ "%");
		else
			responseTime.setRate("-");
		if (restoreTime.getPrevMonthCount() > 0)
			restoreTime
					.setRate(Long.toString((restoreTime.getCount() - restoreTime
							.getPrevMonthCount())
							* 100
							/ restoreTime.getPrevMonthCount())
							+ "%");
		else
			restoreTime.setRate("-");
	}

	@Override
	public void readData(Stream stream) throws IOException {
		super.readData(stream);
		items.clear();
		itemMap.clear();
		int c = stream.readInt(false, 3000);
		for (int i = 0; i < c; i++) {
			FaultStatField o = new FaultStatField();
			o.readData(stream);
			items.add(o);
			itemMap.put(o.getFaultType(), o);
		}
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		super.writeData(stream);
		stream.writeInt(items.size(), false, 3000);
		for (FaultStatField o : items) {
			o.writeData(stream);
		}
	}

}

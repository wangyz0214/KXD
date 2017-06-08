package kxd.engine.scs.report.beans;

import java.io.IOException;

import kxd.engine.scs.report.BaseReportItem;
import kxd.engine.scs.report.ReportItem;
import kxd.engine.scs.report.ReportList;
import kxd.util.DataUnit;
import kxd.util.Streamable;
import kxd.util.stream.Stream;

public class ReceivableStatItem<N extends Streamable> extends BaseReportItem<N> {
	private static final long serialVersionUID = 1L;
	long count = 0, money = 0, successCount = 0, successMoney = 0,
			errorCount = 0, errorMoney = 0, refundCount = 0, refundMoney = 0,
			cancelRefundCount = 0, cancelRefundMoney = 0;

	public ReceivableStatItem() {
	}

	public long getMoney() {
		return money;
	}

	public void setMoney(long money) {
		this.money = money;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public long getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(long successCount) {
		this.successCount = successCount;
	}

	public long getSuccessMoney() {
		return successMoney;
	}

	public void setSuccessMoney(long successMoney) {
		this.successMoney = successMoney;
	}

	public long getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(long errorCount) {
		this.errorCount = errorCount;
	}

	public long getErrorMoney() {
		return errorMoney;
	}

	public void setErrorMoney(long errorMoney) {
		this.errorMoney = errorMoney;
	}

	public long getRefundCount() {
		return refundCount;
	}

	public void setRefundCount(long refundCount) {
		this.refundCount = refundCount;
	}

	public long getRefundMoney() {
		return refundMoney;
	}

	public void setRefundMoney(long refundMoney) {
		this.refundMoney = refundMoney;
	}

	public long getCancelRefundCount() {
		return cancelRefundCount;
	}

	public void setCancelRefundCount(long cancelRefundCount) {
		this.cancelRefundCount = cancelRefundCount;
	}

	public long getCancelRefundMoney() {
		return cancelRefundMoney;
	}

	public void setCancelRefundMoney(long cancelRefundMoney) {
		this.cancelRefundMoney = cancelRefundMoney;
	}

	/**
	 * @param data
	 *            统计数据数组：ident,orgid,count,
	 */
	@Override
	public void addData(ReportList<?, ?> list, Object[] data) {
		successCount += Integer.valueOf(data[1].toString());
		successMoney += Integer.valueOf(data[2].toString());
		errorCount += Integer.valueOf(data[3].toString());
		errorMoney += Integer.valueOf(data[4].toString());
		refundCount += Integer.valueOf(data[5].toString());
		refundMoney += Integer.valueOf(data[6].toString());
		cancelRefundCount += Integer.valueOf(data[7].toString());
		cancelRefundMoney += Integer.valueOf(data[8].toString());
	}

	@Override
	public void addData(ReportList<?, ?> list, ReportItem<N> item) {
		ReceivableStatItem<N> st = (ReceivableStatItem<N>) item;
		successCount += st.successCount;
		errorCount += st.errorCount;
		successMoney += st.successMoney;
		errorMoney += st.errorMoney;
		refundMoney += st.refundMoney;
		cancelRefundMoney += st.cancelRefundMoney;
		refundCount += st.refundCount;
		cancelRefundCount += st.cancelRefundCount;
	}

	@Override
	public void complele(ReportList<?, ?> list) {
		money = successMoney + errorMoney;
		count = successCount + errorCount;
	}

	public long getTotalMoney() {
		return money - refundMoney + cancelRefundMoney;
	}

	public String getFaultRate() {
		if (count == 0)
			return "-";
		else {
			return DataUnit.fenToYuan((int) (errorCount * 10000 / count)) + "%";
		}
	}

	@Override
	public void readData(Stream stream) throws IOException {
		super.readData(stream);
		count = stream.readLong(3000);
		money = stream.readLong(3000);
		successCount = stream.readLong(3000);
		successMoney = stream.readLong(3000);
		errorCount = stream.readLong(3000);
		errorMoney = stream.readLong(3000);
		refundCount = stream.readLong(3000);
		refundMoney = stream.readLong(3000);
		cancelRefundCount = stream.readLong(3000);
		cancelRefundMoney = stream.readLong(3000);
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		super.writeData(stream);
		stream.writeLong(count, 3000);
		stream.writeLong(money, 3000);
		stream.writeLong(successCount, 3000);
		stream.writeLong(successMoney, 3000);
		stream.writeLong(errorCount, 3000);
		stream.writeLong(errorMoney, 3000);
		stream.writeLong(refundCount, 3000);
		stream.writeLong(refundMoney, 3000);
		stream.writeLong(cancelRefundCount, 3000);
		stream.writeLong(cancelRefundMoney, 3000);
	}
}

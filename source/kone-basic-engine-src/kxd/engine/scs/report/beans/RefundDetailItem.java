package kxd.engine.scs.report.beans;

import java.io.IOException;
import java.util.Date;

import kxd.engine.scs.report.BaseReportItem;
import kxd.engine.scs.report.ReportItem;
import kxd.engine.scs.report.ReportList;
import kxd.remote.scs.util.emun.RefundStatus;
import kxd.util.Streamable;
import kxd.util.stream.Stream;

/**
 * 退款明细项
 * 
 * @author zhaom
 * 
 * @param <N>
 */
public class RefundDetailItem<N extends Streamable> extends BaseReportItem<N> {
	private static final long serialVersionUID = 1L;
	private String termGlide, tradeGlide, userNo;
	private Date refundTime;
	private int tradeCodeId;
	private int amount;
	private RefundStatus refundResult;
	private boolean canceled;

	public RefundDetailItem() {
	}

	/**
	 * @param data
	 *            数组：termcode,orgid,termglide,tradeglide,userno,tradecodeid,
	 *            amount,refundtime,refundresult
	 */
	@Override
	public void addData(ReportList<?, ?> list, Object[] data) {
		termGlide = data[2].toString();
		tradeGlide = (String) data[3];
		userNo = (String) data[4];
		tradeCodeId = Integer.valueOf(data[5].toString());
		refundTime = (Date) data[6];
		amount = Integer.valueOf(data[7].toString());
		refundResult = RefundStatus
				.valueOf(Integer.valueOf(data[8].toString()));
		canceled = Integer.valueOf(data[9].toString()) != 0;
	}

	@Override
	public void addData(ReportList<?, ?> list, ReportItem<N> item) {
	}

	@Override
	public void complele(ReportList<?, ?> list) {
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getTermGlide() {
		return termGlide;
	}

	public void setTermGlide(String termGlide) {
		this.termGlide = termGlide;
	}

	public String getTradeGlide() {
		return tradeGlide;
	}

	public void setTradeGlide(String tradeGlide) {
		this.tradeGlide = tradeGlide;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public int getTradeCodeId() {
		return tradeCodeId;
	}

	public void setTradeCodeId(int tradeCodeId) {
		this.tradeCodeId = tradeCodeId;
	}

	public Date getRefundTime() {
		return refundTime;
	}

	public void setRefundTime(Date refundTime) {
		this.refundTime = refundTime;
	}

	public RefundStatus getRefundResult() {
		return refundResult;
	}

	public void setRefundResult(RefundStatus refundResult) {
		this.refundResult = refundResult;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	@Override
	public void readData(Stream stream) throws IOException {
		super.readData(stream);
		termGlide = stream.readPacketByteString(3000);
		tradeGlide = stream.readPacketByteString(3000);
		userNo = stream.readPacketByteString(3000);
		refundTime = stream.readLongDateTime(3000);
		tradeCodeId = stream.readInt(false, 3000);
		amount = stream.readInt(false, 3000);
		refundResult = RefundStatus.valueOf(stream.readByte(3000));
		canceled = stream.readBoolean(3000);
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		super.writeData(stream);
		stream.writePacketByteString(termGlide, 3000);
		stream.writePacketByteString(tradeGlide, 3000);
		stream.writePacketByteString(userNo, 3000);
		stream.writeLongDateTime(refundTime, 3000);
		stream.writeInt(tradeCodeId, false, 3000);
		stream.writeInt(amount, false, 3000);
		stream.writeByte((byte) refundResult.getValue(), 3000);
		stream.writeBoolean(canceled, 3000);
	}

}

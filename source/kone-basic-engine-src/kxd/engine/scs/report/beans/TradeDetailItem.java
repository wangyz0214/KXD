package kxd.engine.scs.report.beans;

import java.io.IOException;
import java.util.Date;

import kxd.engine.scs.report.BaseReportItem;
import kxd.engine.scs.report.ReportItem;
import kxd.engine.scs.report.ReportList;
import kxd.remote.scs.util.emun.PayStatus;
import kxd.remote.scs.util.emun.RecStatus;
import kxd.remote.scs.util.emun.RefundStatus;
import kxd.remote.scs.util.emun.TradeStatus;
import kxd.util.Streamable;
import kxd.util.stream.Stream;

/**
 * 交易明细项
 * 
 * @author zhaom
 * 
 * @param <N>
 */
public class TradeDetailItem<N extends Streamable> extends BaseReportItem<N> {
	private static final long serialVersionUID = 1L;
	private String termGlide, tradeGlide, userNo, tradeResult;
	private Date tradeTime, optionTime;
	private int tradeCodeId;
	private long amount;
	private PayStatus payStatus;
	private TradeStatus tradeStatus;
	private RefundStatus refundStatus;
	private RecStatus recStatus;
	private int optionCode;

	public TradeDetailItem() {
	}

	/**
	 * @param data
	 *            数组：termcode,orgid,termglide,tradeglide,userno,tradecodeid,
	 *            paystatus,
	 *            tradestatus,refund_status,rec_status,tradetime,amount
	 *            ,resultinfo
	 */
	@Override
	public void addData(ReportList<?, ?> list, Object[] data) {
		termGlide = data[2].toString();
		tradeGlide = (String) data[3];
		userNo = (String) data[4];
		tradeCodeId = Integer.valueOf(data[5].toString());
		payStatus = PayStatus.valueOfIntString(data[6]);
		tradeStatus = TradeStatus.valueOfIntString(data[7]);
		refundStatus = RefundStatus.valueOfIntString(data[8]);
		recStatus = RecStatus.valueOfIntString(data[9]);
		tradeTime = (Date) data[10];
		amount = Long.valueOf(data[11].toString());
		tradeResult = (String) data[12];
		optionTime = (Date) data[13];
		optionCode = Integer.valueOf(data[14] == null ? "0" : data[14]
				.toString());
	}

	@Override
	public void addData(ReportList<?, ?> list, ReportItem<N> item) {
	}

	@Override
	public void complele(ReportList<?, ?> list) {
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
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

	public PayStatus getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(PayStatus payStatus) {
		this.payStatus = payStatus;
	}

	public TradeStatus getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(TradeStatus tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public RefundStatus getRefundStatus() {
		return refundStatus;
	}

	public void setRefundStatus(RefundStatus refundStatus) {
		this.refundStatus = refundStatus;
	}

	public RecStatus getRecStatus() {
		return recStatus;
	}

	public void setRecStatus(RecStatus recStatus) {
		this.recStatus = recStatus;
	}

	public Date getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
	}

	public String getTradeResult() {
		return tradeResult;
	}

	public void setTradeResult(String tradeResult) {
		this.tradeResult = tradeResult;
	}

	public Date getOptionTime() {
		return optionTime;
	}

	public void setOptionTime(Date optionTime) {
		this.optionTime = optionTime;
	}

	public int getOptionCode() {
		return optionCode;
	}

	public void setOptionCode(int optionCode) {
		this.optionCode = optionCode;
	}

	@Override
	public void readData(Stream stream) throws IOException {
		super.readData(stream);
		termGlide = stream.readPacketByteString(3000);
		tradeGlide = stream.readPacketByteString(3000);
		userNo = stream.readPacketByteString(3000);
		tradeCodeId = stream.readInt(false, 3000);
		payStatus = PayStatus.valueOf(stream.readInt(false, 3000));
		tradeStatus = TradeStatus.valueOf(stream.readInt(false, 3000));
		refundStatus = RefundStatus.valueOf(stream.readInt(false, 3000));
		recStatus = RecStatus.valueOf(stream.readInt(false, 3000));
		tradeTime = stream.readLongDateTime(3000);
		amount = stream.readLong(3000);
		tradeResult = stream.readPacketByteString(3000);
		optionTime = stream.readLongDateTime(3000);
		optionCode = stream.readInt(false, 3000);
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		super.writeData(stream);
		stream.writePacketByteString(termGlide, 3000);
		stream.writePacketByteString(tradeGlide, 3000);
		stream.writePacketByteString(userNo, 3000);
		stream.writeInt(tradeCodeId, false, 3000);
		stream.writeInt(payStatus.getValue(), false, 3000);
		stream.writeInt(tradeStatus.getValue(), false, 3000);
		stream.writeInt(refundStatus.getValue(), false, 3000);
		stream.writeInt(recStatus.getValue(), false, 3000);
		stream.writeLongDateTime(tradeTime, 3000);
		stream.writeLong(amount, 3000);
		stream.writePacketByteString(tradeResult, 3000);
		stream.writeLongDateTime(optionTime, 3000);
		stream.writeInt(optionCode, false, 3000);
	}
}

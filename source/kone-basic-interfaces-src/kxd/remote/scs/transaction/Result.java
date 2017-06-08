package kxd.remote.scs.transaction;

import java.io.Serializable;
import java.util.Date;

import kxd.json.JSONException;
import kxd.json.JSONObject;
import kxd.remote.scs.util.emun.PayStatus;
import kxd.remote.scs.util.emun.TradePhase;
import kxd.remote.scs.util.emun.TradeResult;
import kxd.remote.scs.util.emun.TradeStatus;
import kxd.util.DataUnit;
import kxd.util.StringUnit;

import org.apache.log4j.Logger;

/**
 * 交易结果。与Response不同，Response是对终端的响应，而交易结果用于系统记录交易日志及统计之用
 * 
 * @author 赵明
 * 
 */
public class Result implements Serializable {

	private static final long serialVersionUID = 1711185823846598350L;

	private TradeResult result;

	private TradePhase phase = TradePhase.BEFORE_PAY;
	private TradeStatus tradeStatus = TradeStatus.NOT_TRADE;
	private PayStatus payStatus = PayStatus.NOT_PAY;
	private Date submitTime;
	private String resultInfo = "";
	private String tradeGlide = "";
	private String bankGlide = "";
	private String extData0 = "";
	private String extData1 = "";
	private String extData2 = "";
	private String extData3 = "";
	private String extData4 = "";
	private boolean tradeDetailInserted = false;
	private Date tradeStartTime = new Date(), tradeEndTime;

	Result() {
		super();
	}

	public Result(TradeResult result) {

		super();
		this.result = result;
		resultInfo = result.toString();
	}

	public Result copy() {
		Result r = new Result();
		r.result = result;
		r.phase = phase;
		r.tradeStatus = tradeStatus;
		r.payStatus = payStatus;
		r.submitTime = submitTime;
		r.resultInfo = resultInfo;
		r.tradeGlide = tradeGlide;
		r.bankGlide = bankGlide;
		r.extData0 = extData0;
		r.extData1 = extData1;
		r.extData2 = extData2;
		r.extData3 = extData3;
		r.extData4 = extData4;
		r.tradeStartTime = tradeStartTime;
		r.tradeEndTime = tradeEndTime;
		return r;
	}

	public Result(Request request) throws TradeError {
		this(TradeResult.valueOf(request.getParameterIntDef("traderesult", 1)));
		setBankGlide(request.getParameterDef("bankglide", ""));

		setTradeGlide(request.getParameterDef("tradeglide", ""));

		String[] extData = StringUnit.split(
				request.getParameterDef("extdata", ""), ";");

		if (extData.length > 0)
			setExtData0(extData[0].trim());

		if (extData.length > 1)
			setExtData1(extData[1].trim());

		if (extData.length > 2)
			setExtData2(extData[2].trim());

		if (extData.length > 3)
			setExtData3(extData[3].trim());

		if (extData.length > 4)
			setExtData4(extData[4].trim());

		phase = TradePhase.valueOf(request.getParameterIntDef("tradephase", 0));
		tradeStatus = TradeStatus.valueOf(request.getParameterIntDef(
				"tradestatus", 0));
		payStatus = PayStatus.valueOf(request
				.getParameterIntDef("paystatus", 0));
		setResultInfo(request.getParameterDef("resultinfo", ""));
	}

	public Result(TradeResult result, String resultInfo) {
		super();
		this.result = result;
		this.resultInfo = resultInfo;
	}

	public TradeResult getResult() {

		return result;
	}

	public void setResult(TradeResult result) {

		this.result = result;
		resultInfo = result.toString();
	}

	public String getTradeGlide() {

		return tradeGlide;
	}

	public void setTradeGlide(String tradeGlideNo) {

		this.tradeGlide = tradeGlideNo;
	}

	public String getBankGlide() {

		return bankGlide;
	}

	public void setBankGlide(String bankGlideNo) {

		this.bankGlide = bankGlideNo;
	}

	public String getExtData0() {

		return extData0;
	}

	public void setExtData0(String extData0) {

		this.extData0 = extData0;
	}

	public String getExtData1() {

		return extData1;
	}

	public void setExtData1(String extData1) {

		this.extData1 = extData1;
	}

	public String getExtData2() {

		return extData2;
	}

	public void setExtData2(String extData2) {

		this.extData2 = extData2;
	}

	public String getExtData3() {

		return extData3;
	}

	public void setExtData3(String extData3) {

		this.extData3 = extData3;
	}

	public String getExtData4() {

		return extData4;
	}

	public void setExtData4(String extData4) {

		this.extData4 = extData4;
	}

	public String getResultInfo() {

		return resultInfo;
	}

	public void setResultInfo(String resultInfo) {

		this.resultInfo = resultInfo;
	}

	/**
	 * 返回16进制编码的JSON字符串
	 * 
	 * @param termGlide
	 * @param bankCardNo
	 * @return
	 * @throws JSONException
	 */
	public String toJSONString(String termGlide, String bankCardNo,
			boolean submitted) throws JSONException {

		JSONObject o = new JSONObject();
		o.put("termglide", termGlide);
		o.put("traderesult", result.getValue());
		o.put("tradeglide", tradeGlide);
		o.put("bankglide", bankGlide);
		o.put("bankcardno", bankCardNo);
		String extData;

		if (extData4.length() > 0)
			extData = extData0 + " | " + extData1 + " | " + extData2 + " | "
					+ extData3 + " | " + extData4;

		else if (extData3.length() > 0)
			extData = extData0 + " | " + extData1 + " | " + extData2 + " | "
					+ extData3;

		else if (extData2.length() > 0)
			extData = extData0 + " | " + extData1 + " | " + extData2;

		else if (extData1.length() > 0)
			extData = extData0 + " | " + extData1;

		else
			extData = extData0;

		o.put("extdata", extData);
		o.put("submitted", submitted ? "true" : "false");
		return DataUnit.bytesToHex(o.toString().getBytes());
	}

	public TradePhase getPhase() {
		return phase;
	}

	public void setPhase(TradePhase phase) {
		this.phase = phase;
	}

	public Date getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}

	public TradeStatus getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(TradeStatus tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public PayStatus getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(PayStatus payStatus) {
		this.payStatus = payStatus;
	}

	public void debug(Logger logger, String prefix) {
		if (logger == null)
			return;
		logger.debug(prefix + " - Result{paystatus:"
				+ Integer.toString(payStatus.getValue()) + " tradestatus:"
				+ Integer.toString(payStatus.getValue()) + " tradephase:"
				+ Integer.toString(phase.getValue()) + " traderesult:"
				+ Integer.toString(result.getValue()) + " resultinfo:"
				+ resultInfo + " tradeglide:" + tradeGlide + " bankGlide:"
				+ bankGlide + " ext0:" + extData0 + " ext1:" + extData1
				+ " ext2:" + extData2 + " ext3:" + extData3 + " ext4:"
				+ extData4 + "}");
	}

	public boolean isTradeDetailInserted() {
		return tradeDetailInserted;
	}

	public void setTradeDetailInserted(boolean tradeDetailInserted) {
		this.tradeDetailInserted = tradeDetailInserted;
	}

	public Date getTradeStartTime() {
		return tradeStartTime;
	}

	public void setTradeStartTime(Date tradeStartTime) {
		this.tradeStartTime = tradeStartTime;
	}

	public Date getTradeEndTime() {
		return tradeEndTime;
	}

	public void setTradeEndTime(Date tradeEndTime) {
		this.tradeEndTime = tradeEndTime;
	}
}

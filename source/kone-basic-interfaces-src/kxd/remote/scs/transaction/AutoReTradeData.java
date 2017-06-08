package kxd.remote.scs.transaction;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 自动重做交易的数据<br>
 * 后台自动重做交易，是指由于网络原因，终端的交易未能及时提交至服务器，或交易超时后导致交易失败，为了提高交易
 * 成功率，后台做一线程定时扫描这类交易记录，提取出来后，再自动重做交易。
 */
public class AutoReTradeData implements Serializable {
	private static final long serialVersionUID = 1L;

	BigDecimal termGlide;

	int termid;
	int orgid;
	String tradeAmount;
	String tradeTime;
	String tradeService;
	String tradeCode;
	String userNo;
	String bankCardNo;
	String amount;
	String tradeGlide;
	String bankGlide;
	String redoParams;

	public BigDecimal getTermGlide() {
		return termGlide;
	}

	public void setTermGlide(BigDecimal termGlide) {
		this.termGlide = termGlide;
	}

	public int getTermid() {
		return termid;
	}

	public void setTermid(int termid) {
		this.termid = termid;
	}

	public int getOrgid() {
		return orgid;
	}

	public void setOrgid(int orgid) {
		this.orgid = orgid;
	}

	public String getTradeService() {
		return tradeService == null ? "" : tradeService;
	}

	public void setTradeService(String tradeService) {
		this.tradeService = tradeService;
	}

	public String getTradeCode() {
		return tradeCode == null ? "" : tradeCode;
	}

	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
	}

	public String getUserNo() {
		return userNo == null ? "" : userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getBankCardNo() {
		return bankCardNo == null ? "" : bankCardNo;
	}

	public void setBankCardNo(String bankCardNo) {
		this.bankCardNo = bankCardNo;
	}

	public String getAmount() {
		return amount == null ? "0" : amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getTradeGlide() {
		return tradeGlide == null ? "" : tradeGlide;
	}

	public void setTradeGlide(String tradeGlide) {
		this.tradeGlide = tradeGlide;
	}

	public String getBankGlide() {
		return bankGlide == null ? "" : bankGlide;
	}

	public void setBankGlide(String bankGlide) {
		this.bankGlide = bankGlide;
	}

	public String getTradeTime() {
		return tradeTime == null ? "" : tradeTime;
	}

	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}

	public String getTradeAmount() {
		return tradeAmount == null ? "" : tradeAmount;
	}

	public void setTradeAmount(String tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	public String getRedoParams() {
		return redoParams == null ? "" : redoParams;
	}

	public void setRedoParams(String redoParams) {
		this.redoParams = redoParams;
	}

}

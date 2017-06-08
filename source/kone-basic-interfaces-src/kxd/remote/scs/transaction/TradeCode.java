package kxd.remote.scs.transaction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;

import kxd.util.StringUnit;

public class TradeCode implements Serializable {

	private static final long serialVersionUID = -4553253333982147657L;
	/**
	 * 交易代码
	 */
	String tradeCode;
	/**
	 * 交易时间
	 */
	Date tradeTime;
	/**
	 * 终端ID
	 */
	int termId;
	/**
	 * 机构ID
	 */
	int orgId;
	/**
	 * 交易服务名称
	 */
	String service;
	/**
	 * 交易流水号,当前终端,当前日期的唯一ID
	 */
	BigDecimal termGlide;
	/**
	 * 用户号码
	 */
	String userno;
	/**
	 * 实交金额,以分为单位
	 */
	int amount;
	/**
	 * 应交金额,以分为单位
	 */
	int tradeAmount;
	/**
	 * 银行卡号
	 */
	String bankCardNo;
	/**
	 * 交易代码ID
	 */
	Integer tradeCodeId;
	/**
	 * 是否统计
	 */
	private boolean stated = false;
	/**
	 * 是否记录日志
	 */
	private boolean logged = false;
	/**
	 * 用户城市编码
	 */
	private String userCityCode = "";
	private String redoParams;

	public Integer getTradeCodeId() {

		return tradeCodeId;
	}

	public void setTradeCodeId(Integer tradeCodeId) {

		this.tradeCodeId = tradeCodeId;
	}

	public TradeCode() {
	}

	public TradeCode(Request request) throws TradeError {
		try {
			termId = request.getParameterInt("termid");
			tradeCode = request.getParameter("tradecode");
			service = request.getParameter("service");
			userCityCode = request.getParameterDef("citycode", "");
			String str = request.getParameterDef("termglide", "");

			if (!str.isEmpty()) {
				termGlide = new BigDecimal(str);
				tradeTime = request.getParameterDateTime("tradetime",
						"yyyyMMddHHmmss");
				bankCardNo = request.getParameterDef("bankcardno", "");
				userno = request.getParameter("userno");
				amount = request.getParameterInt("amount");
				tradeAmount = request.getParameterInt("tradeamount");
			} else {
				termGlide = null;
				userno = request.getParameterDef("userno", "");
			}
			Iterator<String> en = request.params.keySet().iterator();
			redoParams = "";
			while (en.hasNext()) {
				String n = en.next();
				if (!(n.equalsIgnoreCase("userip")
						|| n.equalsIgnoreCase("executor")
						|| n.equalsIgnoreCase("tradecode")
						|| n.equalsIgnoreCase("service")
						|| n.equalsIgnoreCase("termid")
						|| n.equalsIgnoreCase("termglide")
						|| n.equalsIgnoreCase("tradetime")
						|| n.equalsIgnoreCase("bankcardno")
						|| n.equalsIgnoreCase("userno")
						|| n.equalsIgnoreCase("amount") || n
							.equalsIgnoreCase("tradeamount"))) {
					if (!redoParams.isEmpty()) {
						redoParams += "&";
					}
					redoParams += n + "=" + request.params.get(n);
				}
			}
		} catch (Throwable e) {
			throw new TradeError("PE", StringUnit.getExceptionMessage(e));
		}
	}

	public boolean isTrade() {
		return termGlide != null;
	}

	public String getUserno() {
		return userno;
	}

	public void setUserno(String userno) {
		this.userno = userno;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getTradeAmount() {
		return tradeAmount;
	}

	public void setTradeAmount(int tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	public String getTradeCode() {
		return tradeCode;
	}

	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
	}

	public Date getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
	}

	public int getTermId() {
		return termId;
	}

	public void setTermId(int termId) {
		this.termId = termId;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public BigDecimal getTermGlide() {
		return termGlide;
	}

	public void setTermGlide(BigDecimal termGlide) {
		this.termGlide = termGlide;
	}

	public boolean isStated() {
		return stated;
	}

	public void setStated(boolean stated) {
		this.stated = stated;
	}

	public boolean isLogged() {
		return logged;
	}

	public void setLogged(boolean logged) {
		this.logged = logged;
	}

	public String getBankCardNo() {
		return bankCardNo;
	}

	public void setBankCardNo(String bankCardNo) {
		this.bankCardNo = bankCardNo;
	}

	public int getOrgId() {
		return orgId;
	}

	public void setOrgId(int orgId) {
		this.orgId = orgId;
	}

	public String getUserCityCode() {
		return userCityCode;
	}

	public void setUserCityCode(String userCityCode) {
		this.userCityCode = userCityCode;
	}

	public String getRedoParams() {
		return redoParams;
	}

	public void setRedoParams(String redoParams) {
		this.redoParams = redoParams;
	}
}

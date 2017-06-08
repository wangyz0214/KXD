package kxd.engine.cache.beans.sts;

import java.util.Date;

import kxd.remote.scs.util.emun.AlarmStatus;
import kxd.remote.scs.util.emun.TermStatus;
import kxd.util.DateTime;
import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class MonitoredCachedTerm extends ListItem<Integer> {
	private static final long serialVersionUID = 7864312922148907172L;
	/**
	 * 终端编码
	 */
	private String termCode;
	/**
	 * 终端描述
	 */
	private String termDesp;
	/**
	 * 出厂编码
	 */
	private String manufNo;
	/**
	 * 地址
	 */
	private String address;
	/**
	 * 工作密钥
	 */
	private String workKey;
	/**
	 * 联系人
	 */
	private String contacter;
	/**
	 * IP
	 */
	private String ip;
	/**
	 * 开始使用时间
	 */
	private Date startTime;
	/**
	 * 最后在线时间
	 */
	private Date lastInlineTime;
	/**
	 * 终端状态
	 */
	private TermStatus status;
	/**
	 * 终端告警状态
	 */
	private AlarmStatus alarmStatus;
	/**
	 * 终端区号
	 */
	private String areaCode;
	/**
	 * 一日的运行时间
	 */
	private short dayRunTime;
	/**
	 * 开机时间
	 */
	private String openTime;
	/**
	 * 关机时间
	 */
	private String closeTime;
	/**
	 * 终端唯一编码
	 */
	private String guid;
	private String appDesp, orgDesp, termTypeDesp, manufDesp;
	private Integer orgId;

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
		logger.debug(prefix + "appDesp: " + appDesp + ";");
		logger.debug(prefix + "orgDesp: " + orgDesp + ";");
		logger.debug(prefix + "termTypeDesp: " + termTypeDesp + ";");
		logger.debug(prefix + "manufDesp: " + manufDesp + ";");
		logger.debug(prefix + "orgId: " + orgId + ";");
	}

	public MonitoredCachedTerm() {
		super();
	}

	public MonitoredCachedTerm(Integer id) {
		super(id);
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof MonitoredCachedTerm))
			return;
		MonitoredCachedTerm d = (MonitoredCachedTerm) src;
		orgId = d.orgId;
		termTypeDesp = d.termTypeDesp;
		orgDesp = d.orgDesp;
		appDesp = d.appDesp;
		termCode = d.termCode;
		termDesp = d.termDesp;
		address = d.address;
		alarmStatus = d.alarmStatus;
		areaCode = d.areaCode;
		closeTime = d.closeTime;
		contacter = d.contacter;
		dayRunTime = d.dayRunTime;
		guid = d.guid;
		ip = d.ip;
		lastInlineTime = d.lastInlineTime;
		manufNo = d.manufNo;
		openTime = d.openTime;
		startTime = d.startTime;
		status = d.status;
		workKey = d.workKey;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new MonitoredCachedTerm();
	}

	public String getAppDesp() {
		return appDesp;
	}

	public void setAppDesp(String appDesp) {
		this.appDesp = appDesp;
	}

	public String getOrgDesp() {
		return orgDesp;
	}

	public void setOrgDesp(String orgDesp) {
		this.orgDesp = orgDesp;
	}

	public String getTermTypeDesp() {
		return termTypeDesp;
	}

	public void setTermTypeDesp(String termTypeDesp) {
		this.termTypeDesp = termTypeDesp;
	}

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public String getManufDesp() {
		return manufDesp;
	}

	public void setManufDesp(String manufDesp) {
		this.manufDesp = manufDesp;
	}

	public String getTermCode() {
		return termCode;
	}

	public void setTermCode(String termCode) {
		this.termCode = termCode;
	}

	public String getTermDesp() {
		return termDesp;
	}

	public void setTermDesp(String termDesp) {
		this.termDesp = termDesp;
	}

	public String getManufNo() {
		return manufNo;
	}

	public void setManufNo(String manufNo) {
		this.manufNo = manufNo;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getWorkKey() {
		return workKey;
	}

	public void setWorkKey(String workKey) {
		this.workKey = workKey;
	}

	public String getContacter() {
		return contacter;
	}

	public void setContacter(String contacter) {
		this.contacter = contacter;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getLastInlineTime() {
		return lastInlineTime;
	}

	public void setLastInlineTime(Date lastInlineTime) {
		this.lastInlineTime = lastInlineTime;
	}

	public TermStatus getStatus() {
		return status;
	}

	public void setStatus(TermStatus status) {
		this.status = status;
	}

	public AlarmStatus getAlarmStatus() {
		return alarmStatus;
	}

	public void setAlarmStatus(AlarmStatus alarmStatus) {
		this.alarmStatus = alarmStatus;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public short getDayRunTime() {
		return dayRunTime;
	}

	public void setDayRunTime(short dayRunTime) {
		this.dayRunTime = dayRunTime;
	}

	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

	public String getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(String closeTime) {
		this.closeTime = closeTime;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	@Override
	public String getIdString() {
		return getId().toString();
	}

	@Override
	public void setIdString(String id) {
		setId(Integer.valueOf(id));
	}

	@Override
	public String toDisplayLabel() {
		return termDesp;
	}

	@Override
	public String getText() {
		return termDesp;
	}

	@Override
	public void setText(String text) {
		termDesp = text;
	}

	/**
	 * 获取在线状态
	 * 
	 * @param curTime
	 *            当前时间
	 * @return 0 - 离线；1 - 在线；-1 - 尚未注册激活
	 */
	public int getOnlineStatus(DateTime curTime) {
		if (lastInlineTime == null)
			return -1;
		return curTime.minutesBetween(new DateTime(lastInlineTime)) < 10 ? 1
				: 0;
	}

	/**
	 * 获取在线状态
	 * 
	 * @param curTime
	 *            当前时间
	 * @return 0 - 离线；1 - 在线；-1 - 尚未注册激活
	 */
	public int getOnlineStatus() {
		return getOnlineStatus(new DateTime());
	}

}

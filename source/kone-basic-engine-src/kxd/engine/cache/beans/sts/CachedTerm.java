package kxd.engine.cache.beans.sts;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.naming.NamingException;

import kxd.engine.cache.beans.CachedIntegerObject;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.util.emun.AlarmStatus;
import kxd.remote.scs.util.emun.SettlementType;
import kxd.remote.scs.util.emun.TermStatus;
import kxd.util.DateTime;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 缓存终端数据
 * 
 * @author zhaom
 * 
 */
public class CachedTerm extends CachedIntegerObject {
	private static final long serialVersionUID = 1L;
	private String termCode;
	private String termDesp;
	private String areaCode;
	private String manufNo;
	private String address;
	private String contacter;
	private short dayRunTime;
	private String openTime;
	private String closeTime;
	private String guid;
	private int termTypeId;
	private int appId;
	private int orgId;
	private Integer bankTermId;
	private CachedTermType termType;
	private CachedApp app;
	private CachedOrg org;
	private CachedBankTerm bankTerm;
	private TermStatus status;
	private AlarmStatus alarmStatus;
	private long lastInlineTime;
	private long startTime;
	private String workKey;
	private String loginSessionID;
	private long lastLoginTime;
	private String extField0;
	private String extField1;
	private String extField2;
	private String extField3;
	private String extField4;
	private SettlementType settlementType;
	private CopyOnWriteArrayList<Integer> parentOrgIds = new CopyOnWriteArrayList<Integer>();

	private String ip;
	
	public CachedTerm() {
		super();
	}

	public CachedTerm(int id) {
		super(id);
	}

	public CachedTerm(Integer id, boolean isNullValue) {
		super(id, isNullValue);
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setTermCode(stream.readPacketByteString(3000));
		setTermDesp(stream.readPacketByteString(3000));
		setTermTypeId(stream.readInt(false, 3000));
		setAppId(stream.readInt(false, 3000));
		setBankTermId(stream.readInt(false, 3000));
		int c = stream.readInt(false, 3000);
		parentOrgIds.clear();
		for (int i = 0; i < c; i++) {
			parentOrgIds.add(stream.readInt(false, 3000));
		}
		setOrgId(parentOrgIds.get(0));
		setAlarmStatus(AlarmStatus.valueOf(stream.readByte(3000)));
		setLastLoginTime(stream.readLong(3000));
		setLastInlineTime(stream.readLong(3000));
		setLoginSessionID(stream.readPacketByteString(3000));
		setStatus(TermStatus.valueOf(stream.readByte(3000)));
		setAddress(stream.readPacketByteString(3000));
		setAreaCode(stream.readPacketByteString(3000));
		setOpenTime(stream.readPacketByteString(3000));
		setCloseTime(stream.readPacketByteString(3000));
		setContacter(stream.readPacketByteString(3000));
		setDayRunTime(stream.readShort(false, 3000));
		setGuid(stream.readPacketByteString(3000));
		setManufNo(stream.readPacketByteString(3000));
		setStartTime(stream.readLong(3000));
		setWorkKey(stream.readPacketByteString(3000));
		extField0 = stream.readPacketByteString(3000);
		extField1 = stream.readPacketByteString(3000);
		extField2 = stream.readPacketByteString(3000);
		extField3 = stream.readPacketByteString(3000);
		extField4 = stream.readPacketByteString(3000);
		settlementType = SettlementType.valueOf(stream.readByte(3000));
		setIp(stream.readPacketByteString(3000));//add by jurstone 20120611
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writePacketByteString(getTermCode(), 3000);
		stream.writePacketByteString(getTermDesp(), 3000);
		stream.writeInt(getTermTypeId(), false, 3000);
		stream.writeInt(getAppId(), false, 3000);
		if (bankTermId == null)
			stream.writeInt(-1, false, 3000);
		else
			stream.writeInt(getBankTermId(), false, 3000);
		stream.writeInt(parentOrgIds.size(), false, 3000);
		for (int i = 0; i < parentOrgIds.size(); i++)
			stream.writeInt(parentOrgIds.get(i), false, 3000);
		stream.writeByte((byte) getAlarmStatus().getValue(), 3000);
		stream.writeLong(getLastLoginTime(), 3000);
		stream.writeLong(getLastInlineTime(), 3000);
		stream.writePacketByteString(getLoginSessionID(), 3000);
		stream.writeByte((byte) getStatus().getValue(), 3000);
		stream.writePacketByteString(getAddress(), 3000);
		stream.writePacketByteString(getAreaCode(), 3000);
		stream.writePacketByteString(getOpenTime(), 3000);
		stream.writePacketByteString(getCloseTime(), 3000);
		stream.writePacketByteString(getContacter(), 3000);
		stream.writeShort(getDayRunTime(), false, 3000);
		stream.writePacketByteString(getGuid(), 3000);
		stream.writePacketByteString(getManufNo(), 3000);
		stream.writeLong(getStartTime(), 3000);
		stream.writePacketByteString(getWorkKey(), 3000);
		stream.writePacketByteString(extField0, 3000);
		stream.writePacketByteString(extField1, 3000);
		stream.writePacketByteString(extField2, 3000);
		stream.writePacketByteString(extField3, 3000);
		stream.writePacketByteString(extField4, 3000);
		stream.writeByte((byte) settlementType.getValue(), 3000);
		stream.writePacketByteString(getIp(), 3000);//add by jurstone 20120611
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedTerm) {
			CachedTerm o = (CachedTerm) src;
			setOrgId(o.orgId);
			parentOrgIds.clear();
			parentOrgIds.addAll(o.parentOrgIds);
			setTermCode(o.getTermCode());
			setTermDesp(o.getTermDesp());
			setTermTypeId(o.getTermTypeId());
			setAppId(o.getAppId());
			setBankTermId(o.getBankTermId());
			setAlarmStatus(o.getAlarmStatus());
			setLastInlineTime(o.getLastInlineTime());
			setLastLoginTime(o.getLastLoginTime());
			setLoginSessionID(o.getLoginSessionID());
			setStatus(o.getStatus());
			setAddress(o.getAddress());
			setAreaCode(o.getAreaCode());
			setOpenTime(o.getOpenTime());
			setCloseTime(o.getCloseTime());
			setContacter(o.getContacter());
			setDayRunTime(o.getDayRunTime());
			setGuid(o.getGuid());
			setManufNo(o.getManufNo());
			setStartTime(o.getStartTime());
			setWorkKey(o.getWorkKey());
			extField0 = o.extField0;
			extField1 = o.extField1;
			extField2 = o.extField2;
			extField3 = o.extField3;
			extField4 = o.extField4;
			settlementType = o.settlementType;
			setIp(o.getIp());// add by jurstone 20120611
		}
	}

	public int getTermTypeId() {
		return termTypeId;
	}

	public void setTermTypeId(int termTypeId) {
		this.termTypeId = termTypeId;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public int getOrgId() {
		return orgId;
	}

	public void setOrgId(int orgId) {
		this.orgId = orgId;
	}

	public Integer getBankTermId() {
		return bankTermId;
	}

	public void setBankTermId(Integer bankTermId) {
		if (bankTermId != null && bankTermId < 0)
			this.bankTermId = null;
		else
			this.bankTermId = bankTermId;
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

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
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

	public String getContacter() {
		return contacter;
	}

	public void setContacter(String contacter) {
		this.contacter = contacter;
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

	private long lastTermTypeUpdateTime = 0;

	public CachedTermType getTermType() {
		if (termType == null
				|| isRelatedObjectNeedUpdate(lastTermTypeUpdateTime)) {
			lastTermTypeUpdateTime = System.currentTimeMillis();
			termType = CacheHelper.termTypeMap.get(termTypeId);
		}
		return termType;
	}

	private long lastAppUpdateTime = 0;

	public CachedApp getApp() {
		if (app == null || isRelatedObjectNeedUpdate(lastAppUpdateTime)) {
			lastAppUpdateTime = System.currentTimeMillis();
			app = CacheHelper.appMap.get(appId);
		}
		return app;
	}

	public CachedOrg getOrg() throws NamingException {
		if (org == null) {
			org = CacheHelper.orgMap.get(orgId);
		}
		return org;
	}

	private long lastBankTermUpdateTime = 0;

	public CachedBankTerm getBankTerm() {
		if (bankTermId != null) {
			if (bankTerm == null
					|| isRelatedObjectNeedUpdate(lastBankTermUpdateTime)) {
				lastBankTermUpdateTime = System.currentTimeMillis();
				bankTerm = CacheHelper.bankTermMap.get(bankTermId);
			}
		}
		return bankTerm;
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

	public long getLastInlineTime() {
		return lastInlineTime;
	}

	public void setLastInlineTime(long lastInlineTime) {
		this.lastInlineTime = lastInlineTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public String getWorkKey() {
		return workKey;
	}

	public void setWorkKey(String workKey) {
		this.workKey = workKey;
	}

	public String getLoginSessionID() {
		return loginSessionID;
	}

	public void setLoginSessionID(String loginSessionID) {
		this.loginSessionID = loginSessionID;
	}

	public long getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new CachedTerm();
	}

	@Override
	public String getText() {
		return termDesp;
	}

	@Override
	public void setText(String text) {
		termDesp = text;
	}

	public int getOnlineStatus(long curTime) {
		if (getLastInlineTime() == 0)
			return -1;
		return DateTime.minutesBetween(curTime, getLastInlineTime()) < 10 ? 1
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
		return getOnlineStatus(System.currentTimeMillis());
	}

	public String getExtField0() {
		return extField0;
	}

	public void setExtField0(String extField0) {
		this.extField0 = extField0;
	}

	public String getExtField1() {
		return extField1;
	}

	public void setExtField1(String extField1) {
		this.extField1 = extField1;
	}

	public String getExtField2() {
		return extField2;
	}

	public void setExtField2(String extField2) {
		this.extField2 = extField2;
	}

	public String getExtField3() {
		return extField3;
	}

	public void setExtField3(String extField3) {
		this.extField3 = extField3;
	}

	public String getExtField4() {
		return extField4;
	}

	public void setExtField4(String extField4) {
		this.extField4 = extField4;
	}

	public SettlementType getSettlementType() {
		return settlementType;
	}

	public void setSettlementType(SettlementType settlementType) {
		this.settlementType = settlementType;
	}

	public CopyOnWriteArrayList<Integer> getParentOrgIds() {
		return parentOrgIds;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
}

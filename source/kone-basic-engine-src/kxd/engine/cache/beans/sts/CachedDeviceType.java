package kxd.engine.cache.beans.sts;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import kxd.engine.cache.beans.CachedIntegerObject;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.util.emun.AlarmLevel;
import kxd.remote.scs.util.emun.FaultPromptOption;
import kxd.util.IdableObject;
import kxd.util.KeyValue;
import kxd.util.stream.Stream;

/**
 * 缓存终端数据
 * 
 * @author zhaom
 * 
 */
public class CachedDeviceType extends CachedIntegerObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.devicetype";
	String deviceTypeCode;
	/**
	 * 硬件模块类型描述
	 */
	String deviceTypeDesp;
	/**
	 * 故障提示操作
	 */
	FaultPromptOption faultPromptOption;
	/**
	 * 告警通知操作 -1 ： 不通知,0 ： 通知维护人员,1..N ： 依次通知到终端所辖区的第几级主管(含维护人员)
	 */
	short alarmNotifyOption;
	/**
	 * 故障通知操作 -1 ： 不通知,0 ： 通知维护人员,1..N ： 依次通知到终端所辖区的第几级主管(含维护人员)
	 */
	short faultNotifyOption;
	/**
	 * 告警时自动产生工单
	 */
	boolean alarmSendForm;
	/**
	 * 故障时自动产生工单
	 */
	boolean faultSendForm;
	int deviceTypeDriverId;
	/**
	 * 模块驱动
	 */
	CachedDeviceTypeDriver deviceTypeDriver;
	Hashtable<Integer, CachedAlarmCode> alarmCodes = new Hashtable<Integer, CachedAlarmCode>();

	public CachedDeviceType() {
		super();
	}

	public CachedDeviceType(int id) {
		super(id);
	}

	public CachedDeviceType(Integer id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public String getDeviceTypeCode() {
		return deviceTypeCode;
	}

	public void setDeviceTypeCode(String deviceTypeCode) {
		this.deviceTypeCode = deviceTypeCode;
	}

	public String getDeviceTypeDesp() {
		return deviceTypeDesp;
	}

	public void setDeviceTypeDesp(String deviceTypeDesp) {
		this.deviceTypeDesp = deviceTypeDesp;
	}

	public FaultPromptOption getFaultPromptOption() {
		return faultPromptOption;
	}

	public void setFaultPromptOption(FaultPromptOption faultPromptOption) {
		this.faultPromptOption = faultPromptOption;
	}

	public short getAlarmNotifyOption() {
		return alarmNotifyOption;
	}

	public void setAlarmNotifyOption(short alarmNotifyOption) {
		this.alarmNotifyOption = alarmNotifyOption;
	}

	public short getFaultNotifyOption() {
		return faultNotifyOption;
	}

	public void setFaultNotifyOption(short faultNotifyOption) {
		this.faultNotifyOption = faultNotifyOption;
	}

	public boolean isAlarmSendForm() {
		return alarmSendForm;
	}

	public void setAlarmSendForm(boolean alarmSendForm) {
		this.alarmSendForm = alarmSendForm;
	}

	public boolean isFaultSendForm() {
		return faultSendForm;
	}

	public void setFaultSendForm(boolean faultSendForm) {
		this.faultSendForm = faultSendForm;
	}

	private long lastDeviceTypeDriverUpdateTime = 0;

	public CachedDeviceTypeDriver getDeviceTypeDriver() {
		if (deviceTypeDriver == null
				|| isRelatedObjectNeedUpdate(lastDeviceTypeDriverUpdateTime)) {
			lastDeviceTypeDriverUpdateTime = System.currentTimeMillis();
			deviceTypeDriver = CacheHelper.deviceTypeDriverMap
					.get(deviceTypeDriverId);
		}
		return deviceTypeDriver;
	}

	public int getDeviceTypeDriverId() {
		return deviceTypeDriverId;
	}

	public void setDeviceTypeDriverId(int deviceTypeDriverId) {
		this.deviceTypeDriverId = deviceTypeDriverId;
	}

	public Hashtable<Integer, CachedAlarmCode> getAlarmCodes() {
		return alarmCodes;
	}

	public void setAlarmCodes(Hashtable<Integer, CachedAlarmCode> alarmCodes) {
		this.alarmCodes = alarmCodes;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setDeviceTypeCode(stream.readPacketByteString(3000));
		setDeviceTypeDesp(stream.readPacketByteString(3000));
		setFaultPromptOption(FaultPromptOption.valueOf(stream.readByte(3000)));
		setAlarmNotifyOption(stream.readShort(false, 3000));
		setFaultNotifyOption(stream.readShort(false, 3000));
		setAlarmSendForm(stream.readBoolean(3000));
		setFaultSendForm(stream.readBoolean(3000));
		setDeviceTypeDriverId(stream.readInt(false, 3000));
		alarmCodes.clear();
		int c = stream.readShort(false, 3000);
		for (int i = 0; i < c; i++) {
			CachedAlarmCode code = new CachedAlarmCode();
			code.readData(stream);
			alarmCodes.put(code.getId(), code);
		}
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writePacketByteString(getDeviceTypeCode(), 3000);
		stream.writePacketByteString(getDeviceTypeDesp(), 3000);
		stream.writeByte((byte) getFaultPromptOption().getValue(), 3000);
		stream.writeShort(getAlarmNotifyOption(), false, 3000);
		stream.writeShort(getFaultNotifyOption(), false, 3000);
		stream.writeBoolean(isAlarmSendForm(), 3000);
		stream.writeBoolean(isFaultSendForm(), 3000);
		stream.writeInt(getDeviceTypeDriverId(), false, 3000);
		stream.writeShort((short) alarmCodes.size(), false, 3000);
		Enumeration<CachedAlarmCode> en = alarmCodes.elements();
		while (en.hasMoreElements()) {
			en.nextElement().writeData(stream);
		}
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedDeviceType) {
			CachedDeviceType o = (CachedDeviceType) src;
			setDeviceTypeCode(o.getDeviceTypeCode());
			setDeviceTypeDesp(o.getDeviceTypeDesp());
			setFaultPromptOption(o.getFaultPromptOption());
			setAlarmNotifyOption(o.getAlarmNotifyOption());
			setFaultNotifyOption(o.getFaultNotifyOption());
			setAlarmSendForm(o.isAlarmSendForm());
			setFaultSendForm(o.isFaultSendForm());
			setDeviceTypeDriverId(o.getDeviceTypeDriverId());
			setAlarmCodes(o.alarmCodes);
		}
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new CachedDeviceType();
	}

	@Override
	public String getText() {
		return deviceTypeDesp;
	}

	@Override
	public void setText(String text) {
		deviceTypeDesp = text;
	}

	public KeyValue<CachedAlarmCode, AlarmLevel> getAlarmLevel(int status) {
		CachedAlarmCode code = getAlarmCodes().get(status);
		AlarmLevel level;
		if (code != null) {
			level = code.getAlarmCategory().getAlarmLevel();
		} else {
			if (status == 0 || status > 1000) {
				level = AlarmLevel.NORMAL;
			} else if (status < 0) {
				level = AlarmLevel.FAULT;
			} else {
				level = AlarmLevel.ALARM;
			}
		}
		return new KeyValue<CachedAlarmCode, AlarmLevel>(code, level);
	}
}

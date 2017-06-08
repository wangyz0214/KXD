package kxd.remote.scs.beans.device;

import kxd.remote.scs.beans.BaseDeviceType;
import kxd.remote.scs.util.emun.FaultPromptOption;
import kxd.util.IdableObject;
import kxd.util.Uploadable;
import org.apache.log4j.Logger;

public class EditedDeviceType extends BaseDeviceType implements Uploadable {
	private static final long serialVersionUID = 1L;
	private FaultPromptOption faultPromptOption;
	private short alarmNotifyOption;
	private short faultNotifyOption;
	private boolean alarmSendForm;
	private boolean faultSendForm;

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
		logger.debug( prefix + "faultPromptOption: "
				+ faultPromptOption + ";");
		logger.debug( prefix + "alarmNotifyOption: "
				+ alarmNotifyOption + ";");
		logger.debug( prefix + "faultNotifyOption: "
				+ faultNotifyOption + ";");
		logger.debug( prefix + "alarmSendForm: " + alarmSendForm + ";");
		logger.debug( prefix + "falutSendForm: " + faultSendForm + ";");
	}

	public EditedDeviceType() {
		super();
	}

	public EditedDeviceType(Integer id, String deviceTypeDesp) {
		super(id, deviceTypeDesp);
	}

	public EditedDeviceType(Integer id) {
		super(id);
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new EditedDeviceType();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof EditedDeviceType))
			return;
		EditedDeviceType d = (EditedDeviceType) src;
		faultPromptOption = d.faultPromptOption;
		alarmNotifyOption = d.alarmNotifyOption;
		faultNotifyOption = d.faultNotifyOption;
		alarmSendForm = d.alarmSendForm;
		faultSendForm = d.faultSendForm;
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

	@Override
	public String getFileName() {
		if (getDriver() == null)
			return null;
		else
			return getDriver().getDriverFile();
	}
}

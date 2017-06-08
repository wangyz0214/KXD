package kxd.remote.scs.beans.right;

import kxd.remote.scs.util.emun.UserGroup;
import kxd.util.ListItem;
import kxd.util.IdableObject;
import org.apache.log4j.Logger;

public class EditedFunction extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private String funcDesp;
	private String funcIcon;
	private String funcUrl;
	private Short funcDepth;
	private UserGroup userGroup;
	private boolean customEnabled;
	private boolean selected;

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug( prefix + "funcDesp: " + funcDesp + ";");
		logger.debug( prefix + "funcIcon: " + funcIcon + ";");
		logger.debug( prefix + "funcUrl: " + funcUrl + ";");
		logger.debug( prefix + "funcDepth: " + funcDepth + ";");
		logger.debug( prefix + "userGroup: " + userGroup + ";");
		logger.debug( prefix + "customEnabled: " + customEnabled + ";");
		logger.debug( prefix + "selected: " + selected + ";");
	}

	public EditedFunction() {
		super();
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof EditedFunction))
			return;
		EditedFunction d = (EditedFunction) src;
		funcDesp = d.funcDesp;
		funcIcon = d.funcIcon;
		funcUrl = d.funcUrl;
		funcDepth = d.funcDepth;
		userGroup = d.userGroup;
		customEnabled = d.customEnabled;
		selected = d.selected;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new EditedFunction();
	}

	public EditedFunction(Integer funcId, String funcDesp) {
		super(funcId);
		this.funcDesp = funcDesp;
	}

	public Integer getFuncId() {
		return getId();
	}

	public void setFuncId(Integer funcId) {
		this.setId(funcId);
	}

	public String getFuncDesp() {
		return funcDesp;
	}

	public void setFuncDesp(String funcDesp) {
		this.funcDesp = funcDesp;
	}

	public String getFuncIcon() {
		return funcIcon;
	}

	public void setFuncIcon(String funcIcon) {
		this.funcIcon = funcIcon;
	}

	public String getFuncUrl() {
		return funcUrl;
	}

	public void setFuncUrl(String funcUrl) {
		this.funcUrl = funcUrl;
	}

	public Short getFuncDepth() {
		return funcDepth;
	}

	public void setFuncDepth(Short funcDepth) {
		this.funcDepth = funcDepth;
	}

	public UserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	public boolean isCustomEnabled() {
		return customEnabled;
	}

	public void setCustomEnabled(boolean customEnabled) {
		this.customEnabled = customEnabled;
	}

	@Override
	protected String toDisplayLabel() {
		return funcDesp;
	}

	@Override
	public String toString() {
		return funcDesp + "(" + getId() + ")";
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String getIdString() {
		if (getId() == null)
			return null;
		else
			return getId().toString();
	}

	@Override
	public void setIdString(String id) {
		if (id == null || id.trim().isEmpty())
			setId(null);
		else
			setId(Integer.parseInt(id));
	}

	@Override
	public String getText() {
		return null;
	}

	@Override
	public void setText(String text) {
	}
}

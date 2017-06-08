package kxd.remote.scs.beans;

import kxd.remote.scs.util.emun.FileCachedType;
import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BaseFileCategory extends ListItem<Short> {
	private static final long serialVersionUID = 1L;
	private String fileCategoryDesp;
	private FileCachedType cachedType;
	private short fileHost;

	@Override
	public IdableObject<Short> createObject() {
		return new BaseFileCategory();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseFileCategory))
			return;
		BaseFileCategory d = (BaseFileCategory) src;
		fileCategoryDesp = d.fileCategoryDesp;
		cachedType = d.cachedType;
		fileHost = d.fileHost;
	}

	public BaseFileCategory() {
		super();
	}

	public BaseFileCategory(Short id) {
		super(id);
	}

	public Short getFileCategoryId() {
		return getId();
	}

	public void setFileCategoryId(Short id) {
		setId(id);
	}

	public String getFileCategoryDesp() {
		return fileCategoryDesp;
	}

	public void setFileCategoryDesp(String alarmCategoryDesp) {
		this.fileCategoryDesp = alarmCategoryDesp;
	}

	@Override
	protected String toDisplayLabel() {
		return fileCategoryDesp;
	}

	@Override
	public String toString() {
		return fileCategoryDesp + "(" + getId() + ")";
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
			setId(Short.parseShort(id));
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "desp: " + getFileCategoryDesp() + ";");
	}

	@Override
	public String getText() {
		return fileCategoryDesp;
	}

	@Override
	public void setText(String text) {
		fileCategoryDesp = text;
	}

	public FileCachedType getCachedType() {
		return cachedType;
	}

	public void setCachedType(FileCachedType cachedType) {
		this.cachedType = cachedType;
	}

	public short getFileHost() {
		return fileHost;
	}

	public void setFileHost(short fileHost) {
		this.fileHost = fileHost;
	}

}

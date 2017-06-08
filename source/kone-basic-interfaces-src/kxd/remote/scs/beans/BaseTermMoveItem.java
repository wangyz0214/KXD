package kxd.remote.scs.beans;

import java.util.Date;

import kxd.util.DataUnit;
import kxd.util.DebugableObject;
import org.apache.log4j.Logger;

public class BaseTermMoveItem extends DebugableObject {
	private static final long serialVersionUID = 1L;
	BaseOrg org;
	Date moveTime;

	@Override
	protected void debugContent(Logger logger, String prefix) {
		logger.debug( prefix + "moveTime: "
				+ DataUnit.formatDateTime(moveTime, "yyyy-MM-dd HH:mm:ss")
				+ ";");
		logger.debug( prefix + "org: ");
		org.debug(logger, prefix + "  ");
	}

	public BaseTermMoveItem(BaseOrg org, Date moveTime) {
		super();
		this.org = org;
		this.moveTime = moveTime;
	}

	public void copyData(Object src) {
		if (!(src instanceof BaseTermMoveItem))
			return;
		BaseTermMoveItem d = (BaseTermMoveItem) src;
		moveTime = d.moveTime;
		org = d.org;
	}

	public BaseTermMoveItem createObject() {
		return new BaseTermMoveItem();
	}

	public BaseTermMoveItem() {
		super();
	}

	public BaseOrg getOrg() {
		return org;
	}

	public void setOrg(BaseOrg org) {
		this.org = org;
	}

	public Date getMoveTime() {
		return moveTime;
	}

	public void setMoveTime(Date moveTime) {
		this.moveTime = moveTime;
	}

}

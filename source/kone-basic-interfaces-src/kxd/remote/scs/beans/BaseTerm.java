package kxd.remote.scs.beans;

import kxd.util.ListItem;
import kxd.util.IdableObject;
import org.apache.log4j.Logger;

public class BaseTerm extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private String termCode;
	private String termDesp;

	@Override
	public String getText() {
		return termDesp;
	}

	@Override
	public void setText(String text) {
		termDesp = text;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "code: " + termCode + ";");
		logger.debug(prefix + "desp: " + termDesp + ";");
	}

	public BaseTerm() {
		super();
	}

	public BaseTerm(Integer id) {
		super(id);
	}

	public BaseTerm(Integer id, String termCode, String termDesp) {
		super(id);
		this.termCode = termCode;
		this.termDesp = termDesp;
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseTerm))
			return;
		BaseTerm d = (BaseTerm) src;
		termCode = d.termCode;
		termDesp = d.termDesp;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseTerm();
	}

	public Integer getTermId() {
		return getId();
	}

	public void setTermId(Integer id) {
		setId(id);
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

	@Override
	protected String toDisplayLabel() {
		return termDesp;
	}

	@Override
	public String toString() {
		return termDesp + "(" + getTermCode() + ")";
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
}

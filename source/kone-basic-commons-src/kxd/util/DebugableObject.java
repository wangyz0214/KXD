package kxd.util;

import java.io.Serializable;

import org.apache.log4j.Logger;

public abstract class DebugableObject implements Serializable, Copyable,
		Debugable {
	private static final long serialVersionUID = 1L;

	public DebugableObject() {
		super();
	}

	public abstract DebugableObject createObject();

	public void copy(Object src) {
		copyData(src);
	}

	public DebugableObject copy() {
		DebugableObject obj = createObject();
		obj.copy(this);
		return obj;
	}

	protected void debugStart(Logger logger, String prefix) {
		logger.debug(prefix + this.getClass().getSimpleName() + "{");
	}

	abstract protected void debugContent(Logger logger, String prefix);

	protected void debugEnd(Logger logger, String prefix) {
		logger.debug(prefix + "}");
	}

	public void debug(Logger logger, String prefix) {
		if (!logger.isDebugEnabled())
			return;
		debugStart(logger, prefix);
		debugContent(logger, prefix + "  ");
		debugEnd(logger, prefix);
	}
}

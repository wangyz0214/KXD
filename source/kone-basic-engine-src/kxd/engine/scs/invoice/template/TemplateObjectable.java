package kxd.engine.scs.invoice.template;

import java.io.Serializable;

import kxd.util.Streamable;

public interface TemplateObjectable extends Serializable, Streamable {
	static final public int OBJ_FIELD = 0;
	static final public int OBJ_ROW = 1;
	static final public int OBJ_COLUMN = 2;
	static final public int OBJ_COLUMNS = 3;
	static final public int OBJ_ADROW = 4;
	static final public int OBJ_HEADER = 5;
	static final public int OBJ_FOOTER = 6;
	static final public int OBJ_DETAIL = 7;
	static final public int OBJ_TEMPLATE = 100;

	public int getDataType();
}

package kxd.engine.scs.invoice.template;


public class Columns extends TemplateObjectList<Column> {
	private static final long serialVersionUID = 1;

	@Override
	public Column newItemInstance(int type) {
		return new Column();
	}

	@Override
	public int getDataType() {
		return OBJ_COLUMNS;
	}
}

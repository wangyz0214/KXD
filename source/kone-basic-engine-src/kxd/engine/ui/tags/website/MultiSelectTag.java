package kxd.engine.ui.tags.website;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.util.ListItem;

public class MultiSelectTag extends DropdownTag {
	private static final long serialVersionUID = 1L;
	private Object items;
	private String allDesp;
	private Integer paramCategoryId;
	private boolean multiSelect;
	private Integer columns;

	@Override
	public void release() {
		items = null;
		allDesp = null;
		columns = null;
		super.release();
	}

	@Override
	protected String getDropdownClass() {
		return "ComboButton";
	}

	@Override
	protected void writeButton(JspWriter writer) throws JspTagException,
			IOException {
		super.writeButton(writer);
		writer.write(",'multiselect':" + isMultiSelect());
		if (getColumns() != null)
			writer.write(",'columns':" + this.getColumns());
	}

	@Override
	protected void writePopupWindow(JspWriter writer) throws JspTagException,
			IOException {
		super.writePopupWindow(writer);
		writer.write(",'items':[");
		Object[] array = null;
		Object items = getItems();
		if (items != null) {
			if (items instanceof Object[])
				array = (Object[]) items;
			else if (items instanceof Collection<?>)
				array = ((Collection<?>) items).toArray();
		}
		boolean isFirst = true;
		if (getAllDesp() != null) {
			isFirst = false;
			writeText(writer, "{value:'',text:'" + getAllDesp() + "'}");
		}
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				Object o = array[i];
				if (o instanceof ListItem<?>) {
					if (!isFirst)
						writeText(writer, ",");
					isFirst = false;
					ListItem<?> e = (ListItem<?>) o;
					writeText(writer, "{value:'" + e.getIdString() + "',text:'"
							+ e.getText() + "'");
					if (e.isChecked())
						writeText(writer, ",checked:" + e.isChecked());
					writer.write("}");
				} else
					throw new IOException(o.getClass().getSimpleName()
							+ ":参数必须是ListItem<E>");
			}
		} else if (paramCategoryId != null) {
			Map<String, String> map = AdminSessionObject
					.getParamConfig(paramCategoryId);
			if (map != null) {
				List<String> names = new ArrayList<String>();
				Iterator<String> it = map.keySet().iterator();
				while (it.hasNext())
					names.add(it.next());
				Collections.sort(names);
				for (int i = 0; i < names.size(); i++) {
					String k = names.get(i);
					String v = map.get(k);
					if (!isFirst)
						writeText(writer, ",");
					isFirst = false;
					writeText(writer, "{value:'" + k + "',text:'" + v + "'");
					writer.write("}");
				}
			}
		} else if (items != null && items instanceof String) {
			writeText(writer, items);
		}
		writer.write("]");
	}

	@Override
	public String getReadonly() {
		return "readonly";
	}

	public Object getItems() {
		return items;
	}

	public void setItems(Object items) {
		this.items = items;
	}

	public String getAllDesp() {
		return allDesp;
	}

	public void setAllDesp(String allDesp) {
		this.allDesp = allDesp;
	}

	public Integer getParamCategoryId() {
		return paramCategoryId;
	}

	public void setParamCategoryId(Integer paramCategoryId) {
		this.paramCategoryId = paramCategoryId;
	}

	public boolean isMultiSelect() {
		return multiSelect;
	}

	public void setMultiSelect(boolean multiSelect) {
		this.multiSelect = multiSelect;
	}

	public Integer getColumns() {
		return columns;
	}

	public void setColumns(Integer columns) {
		this.columns = columns;
	}
}

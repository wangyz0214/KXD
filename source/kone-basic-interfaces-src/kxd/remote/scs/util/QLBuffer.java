package kxd.remote.scs.util;

import java.util.ArrayList;
import java.util.Iterator;

public class QLBuffer {
	private ArrayList<String> tables = new ArrayList<String>();
	private ArrayList<String> wheres = new ArrayList<String>();

	public void addTable(String name) {
		if (name != null) {
			name = name.trim();
			if (name.length() > 0)
				tables.add(name);
		}
	}

	public void addWhere(String str) {
		if (str != null) {
			str = str.trim();
			if (str.length() > 0)
				wheres.add(str);
		}
	}

	public String toQLString(String firstQL, String lastQL) {
		StringBuffer buffer = new StringBuffer();
		Iterator<String> it = tables.iterator();
		if (it.hasNext()) {
			buffer.append(it.next());
			while (it.hasNext()) {
				buffer.append("," + it.next());
			}
		}
		it = wheres.iterator();
		if (it.hasNext()) {
			buffer.append(" where " + it.next());
			while (it.hasNext()) {
				buffer.append(" and " + it.next());
			}
		}
		return firstQL + " from " + buffer.toString() + lastQL;
	}

	public boolean hasWhere() {
		return wheres.size() > 0;
	}
}

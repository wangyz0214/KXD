package kxd.engine.scs.invoice.template;

import java.io.IOException;
import java.util.ArrayList;

import kxd.util.stream.Stream;

abstract public class TemplateObjectList<E extends TemplateObjectable>
		implements TemplateObjectable {
	private static final long serialVersionUID = 1L;
	/**
	 * 列表
	 */
	private ArrayList<E> items = new ArrayList<E>();

	public ArrayList<E> getItems() {
		return items;
	}

	abstract public E newItemInstance(int type);

	@Override
	public void readData(Stream stream) throws IOException {
		int c = stream.readShort(false, 3000);
		for (int i = 0; i < c; i++) {
			E field = newItemInstance(stream.readByte(3000));
			field.readData(stream);
			items.add(field);
		}
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		stream.writeShort((short) items.size(), false, 3000);
		for (int i = 0; i < items.size(); i++) {
			E e = items.get(i);
			stream.writeByte((byte) e.getDataType(), 3000);
			e.writeData(stream);
		}
	}
}

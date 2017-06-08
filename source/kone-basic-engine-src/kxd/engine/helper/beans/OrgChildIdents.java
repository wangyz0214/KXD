package kxd.engine.helper.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kxd.engine.cache.beans.CachedIntegerObject;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

public class OrgChildIdents extends CachedIntegerObject {
	private static final long serialVersionUID = 1L;
	private List<IdentOrg> items = new ArrayList<IdentOrg>();

	public List<IdentOrg> getItems() {
		return items;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		// setId(stream.readInt(false, 3000));
		int c = stream.readInt(false, 3000);
		for (int i = 0; i < c; i++) {
			IdentOrg o = new IdentOrg();
			o.readData(stream);
			items.add(o);
		}
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		// stream.writeInt(getId(), false, 3000);
		stream.writeInt(items.size(), false, 3000);
		for (int i = 0; i < items.size(); i++)
			items.get(i).writeData(stream);
	}

	@Override
	public String getText() {
		return null;
	}

	@Override
	public void setText(String text) {
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new OrgChildIdents();
	}
}

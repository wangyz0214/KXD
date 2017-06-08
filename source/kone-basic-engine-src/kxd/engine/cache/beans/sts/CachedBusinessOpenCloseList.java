package kxd.engine.cache.beans.sts;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import kxd.engine.cache.beans.CachedIntegerObject;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

import org.apache.log4j.Logger;

public class CachedBusinessOpenCloseList extends CachedIntegerObject {
	private static final long serialVersionUID = 1L;
	private CopyOnWriteArrayList<CachedBusinessOpenClose> configList = new CopyOnWriteArrayList<CachedBusinessOpenClose>();

	@Override
	public IdableObject<Integer> createObject() {
		return new CachedBusinessOpenCloseList();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof CachedBusinessOpenCloseList))
			return;
		CachedBusinessOpenCloseList d = (CachedBusinessOpenCloseList) src;
		configList.addAll(d.configList);
	}

	public CachedBusinessOpenCloseList() {
		super();
	}

	public CachedBusinessOpenCloseList(Integer id) {
		super(id);
	}

	@Override
	protected String toDisplayLabel() {
		return this + "";
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
	}

	@Override
	public String getText() {
		return null;
	}

	@Override
	public void setText(String text) {
	}

	@Override
	protected void doReadData(Stream stream) throws IOException {
		int c = stream.readShort(false, 3000);
		configList.clear();
		for (int i = 0; i < c; i++) {
			CachedBusinessOpenClose o = new CachedBusinessOpenClose();
			o.readData(stream);
			configList.add(o);
		}
	}

	@Override
	protected void doWriteData(Stream stream) throws IOException {
		stream.writeShort((short) configList.size(), false, 3000);
		for (CachedBusinessOpenClose o : configList) {
			o.writeData(stream);
		}
	}

	public List<CachedBusinessOpenClose> getConfigList() {
		return configList;
	}

}

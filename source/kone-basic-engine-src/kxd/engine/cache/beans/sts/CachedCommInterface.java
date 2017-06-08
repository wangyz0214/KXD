package kxd.engine.cache.beans.sts;

import java.io.IOException;

import kxd.engine.cache.beans.CachedShortObject;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 缓存接口信息数据
 * 
 * @author zhaom
 * 
 */
public class CachedCommInterface extends CachedShortObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.comminterface";
	private String desp;
	private int type;

	public CachedCommInterface() {
		super();
	}

	public CachedCommInterface(short id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public CachedCommInterface(short id) {
		super(id);
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setDesp(stream.readPacketByteString(3000));
		setType(stream.readInt(false, 3000));
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writePacketByteString(desp, 3000);
		stream.writeInt(type, false, 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedCommInterface) {
			CachedCommInterface o = (CachedCommInterface) src;
			desp = o.desp;
			type = o.type;
		}
	}

	@Override
	public IdableObject<Short> createObject() {
		return new CachedCommInterface();
	}

	@Override
	public String getText() {
		return desp;
	}

	@Override
	public void setText(String text) {
		desp = text;
	}

	public String getDesp() {
		return desp;
	}

	public void setDesp(String desp) {
		this.desp = desp;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}

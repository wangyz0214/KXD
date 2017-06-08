package kxd.engine.cache.beans.sts;

import java.io.IOException;

import kxd.engine.cache.beans.CachedIntegerObject;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 缓存终端数据
 * 
 * @author zhaom
 * 
 */
public class CachedManuf extends CachedIntegerObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.manuf";
	private String manufCode;
	private String manufName;
	private int serialNumber;
	private short manufType;

	public CachedManuf() {
		super();
	}

	public CachedManuf(int id) {
		super(id);
	}

	public CachedManuf(Integer id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public String getManufCode() {
		return manufCode;
	}

	public void setManufCode(String manufCode) {
		this.manufCode = manufCode;
	}

	public String getManufName() {
		return manufName;
	}

	public void setManufName(String manufName) {
		this.manufName = manufName;
	}

	public int getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public short getManufType() {
		return manufType;
	}

	public void setManufType(short manufType) {
		this.manufType = manufType;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setManufCode(stream.readPacketByteString(3000));
		setManufName(stream.readPacketByteString(3000));
		setSerialNumber(stream.readInt(false, 3000));
		setManufType(stream.readShort(false, 3000));
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writePacketByteString(getManufCode(), 3000);
		stream.writePacketByteString(getManufName(), 3000);
		stream.writeInt(getSerialNumber(), false, 3000);
		stream.writeShort(getManufType(), false, 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedManuf) {
			CachedManuf o = (CachedManuf) src;
			setManufCode(o.getManufCode());
			setManufName(o.getManufName());
			setSerialNumber(o.getSerialNumber());
			setManufType(getManufType());
		}
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new CachedManuf();
	}

	@Override
	public String getText() {
		return manufName;
	}

	@Override
	public void setText(String text) {
		manufName = text;
	}

}

package kxd.engine.cache.beans.sts;

import java.io.IOException;

import kxd.engine.cache.beans.CachedShortObject;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 
 * 
 * @author zhaom
 * 
 */
public class CachedPrintType extends CachedShortObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.printtype";
	/**
	 * 打印类型描述
	 */
	private String printTypeDesp;

	public CachedPrintType() {
		super();
	}

	public CachedPrintType(Short id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public CachedPrintType(short id) {
		super(id);
	}

	public String getPrintTypeDesp() {
		return printTypeDesp;
	}

	public void setPrintTypeDesp(String printTypeDesp) {
		this.printTypeDesp = printTypeDesp;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setPrintTypeDesp(stream.readPacketByteString(3000));
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writePacketByteString(getPrintTypeDesp(), 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedPrintType) {
			CachedPrintType o = (CachedPrintType) src;
			setPrintTypeDesp(o.printTypeDesp);
		}
	}

	@Override
	public IdableObject<Short> createObject() {
		return new CachedPrintType();
	}

	@Override
	public String getText() {
		return printTypeDesp;
	}

	@Override
	public void setText(String text) {
		printTypeDesp = text;
	}

}

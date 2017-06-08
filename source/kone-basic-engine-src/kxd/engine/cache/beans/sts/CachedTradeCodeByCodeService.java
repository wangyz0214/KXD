package kxd.engine.cache.beans.sts;

import java.io.IOException;

import kxd.engine.cache.beans.CachedStringObject;
import kxd.engine.helper.CacheHelper;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 以tradeCode+"."+service为键缓存交易代码ID，用于快速通过交易服务和交易代码查询交易
 * 
 * @author zhaom
 * 
 */
public class CachedTradeCodeByCodeService extends CachedStringObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.tradecodeid";
	private int tradeCodeId;
	private CachedTradeCode tradeCode;

	public CachedTradeCodeByCodeService() {
		super();
	}

	public CachedTradeCodeByCodeService(String id) {
		super(id);
	}

	public CachedTradeCodeByCodeService(String id, int tradeCodeId) {
		super(id);
		this.tradeCodeId = tradeCodeId;
	}

	public CachedTradeCodeByCodeService(String id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public int getTradeCodeId() {
		return tradeCodeId;
	}

	public void setTradeCodeId(int tradeCodeId) {
		this.tradeCodeId = tradeCodeId;
	}

	public CachedTradeCode getTradeCode() {
		if (tradeCode == null) {
			tradeCode = CacheHelper.tradeCodeMap.get(tradeCodeId);
		}
		return tradeCode;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setTradeCodeId(stream.readInt(false, 3000));
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writeInt(getTradeCodeId(), false, 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedTradeCodeByCodeService) {
			CachedTradeCodeByCodeService o = (CachedTradeCodeByCodeService) src;
			setTradeCodeId(o.tradeCodeId);
		}
	}

	@Override
	public IdableObject<String> createObject() {
		return new CachedTradeCodeByCodeService();
	}

	@Override
	public String getText() {
		return getId();
	}

	@Override
	public void setText(String text) {
		setId(text);
	}

}

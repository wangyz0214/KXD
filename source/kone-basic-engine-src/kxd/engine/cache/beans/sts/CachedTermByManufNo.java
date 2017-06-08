package kxd.engine.cache.beans.sts;

import java.io.IOException;

import kxd.engine.cache.beans.CachedStringObject;
import kxd.engine.helper.CacheHelper;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 以出厂编码为键缓存终端
 * 
 * @author zhaom
 * 
 */
public class CachedTermByManufNo extends CachedStringObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.termmanufno";
	private int termId;
	private CachedTermConfig term;

	public CachedTermByManufNo() {
		super();
	}

	public CachedTermByManufNo(String id) {
		super(id);
	}

	public CachedTermByManufNo(String id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public CachedTermByManufNo(String id, int termId) {
		super(id);
		this.termId = termId;
	}

	public int getTermId() {
		return termId;
	}

	public void setTermId(int termId) {
		this.termId = termId;
	}

	public CachedTermConfig getTerm() {
		if (term == null) {
			term = CacheHelper.termMap.get(termId);
		}
		return term;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setTermId(stream.readInt(false, 3000));
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writeInt(getTermId(), false, 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedTermByManufNo) {
			CachedTermByManufNo o = (CachedTermByManufNo) src;
			setTermId(o.termId);
		}
	}

	@Override
	public IdableObject<String> createObject() {
		return new CachedTermByManufNo();
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

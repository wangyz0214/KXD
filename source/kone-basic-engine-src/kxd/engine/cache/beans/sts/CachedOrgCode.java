package kxd.engine.cache.beans.sts;

import java.io.IOException;

import kxd.engine.cache.beans.CachedStringObject;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

public class CachedOrgCode extends CachedStringObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String KEY_PREFIX = "$cache.cachedorgcode";
	private Integer orgId;

	public CachedOrgCode() {
		super();
	}

	public CachedOrgCode(String id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public CachedOrgCode(String id) {
		super(id);
	}

	@Override
	protected void doReadData(Stream stream) throws IOException {
		orgId = stream.readInt(false, 3000);
	}

	@Override
	protected void doWriteData(Stream stream) throws IOException {
		stream.writeInt(orgId, false, 3000);
	}

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	@Override
	public IdableObject<String> createObject() {
		return new CachedOrgCode();
	}

	@Override
	public String getText() {
		return null;
	}

	@Override
	public void setText(String text) {

	}

}

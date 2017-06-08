package kxd.remote.scs.beans;

import kxd.util.IdableObject;

public class BaseTermAd extends BaseAd {
	private static final long serialVersionUID = 1L;
	private BaseTerm term;

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseTermAd();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseTermAd))
			return;
		BaseTermAd d = (BaseTermAd) src;
		term = d.term;
	}

	public BaseTermAd() {
		super();
	}

	public BaseTermAd(Integer id) {
		super(id);
	}

	public BaseTerm getTerm() {
		return term;
	}

	public void setTerm(BaseTerm term) {
		this.term = term;
	}

}

package kxd.remote.scs.beans;

import kxd.util.IdableObject;

public class BaseOrgAd extends BaseAd {
	private static final long serialVersionUID = 1L;
	private BaseOrg org;

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseOrgAd();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseOrgAd))
			return;
		BaseOrgAd d = (BaseOrgAd) src;
		org = d.org;
	}

	public BaseOrgAd() {
		super();
	}

	public BaseOrgAd(Integer id) {
		super(id);
	}

	public BaseOrg getOrg() {
		return org;
	}

	public void setOrg(BaseOrg org) {
		this.org = org;
	}

}

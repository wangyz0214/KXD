package kxd.engine.scs.admin.actions.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import kxd.engine.cache.beans.sts.CachedManuf;
import kxd.engine.helper.CacheHelper;
import kxd.engine.scs.admin.AdminSessionObject;
import kxd.net.HttpRequest;
import kxd.remote.scs.beans.right.LoginUser;

abstract public class BaseManufReportAction extends BaseReportAction {
	Collection<CachedManuf> manufList;
	Integer manufid;

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (orgid == null) {
			LoginUser user = ((AdminSessionObject) session).getLoginUser();
			if (user.getUserGroup().isSystemManager()) {
				manufList = new ArrayList<CachedManuf>();
				Enumeration<Integer> en = CacheHelper.manufMap.keys();
				while (en.hasMoreElements()) {
					int k = en.nextElement();
					manufList.add(CacheHelper.manufMap.get(k));
				}
				CachedManuf nm = new CachedManuf();
				nm.setManufName("全部厂商");
				((List<CachedManuf>) manufList).add(0, nm);
			} else if (user.getManuf().getId() != null) {
				manufList = new ArrayList<CachedManuf>();
				CachedManuf manuf = new CachedManuf();
				manuf.setId(user.getManuf().getId());
				manuf.setManufName(user.getManuf().getManufName());
				manufList.add(manuf);
			}
		} else
			manufid = request.getParameterIntDef("manufid", null);
	}

	public Collection<CachedManuf> getManufList() {
		return manufList;
	}

	public void setManufList(Collection<CachedManuf> manufList) {
		this.manufList = manufList;
	}

	public Integer getManufid() {
		return manufid;
	}

	public void setManufid(Integer manufid) {
		this.manufid = manufid;
	}

}

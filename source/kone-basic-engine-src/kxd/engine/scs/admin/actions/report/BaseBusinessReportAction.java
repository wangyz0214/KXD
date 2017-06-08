package kxd.engine.scs.admin.actions.report;

import java.util.Collection;
import java.util.List;

import kxd.engine.cache.beans.sts.CachedBusiness;
import kxd.engine.cache.beans.sts.CachedBusinessCategory;
import kxd.engine.helper.CacheHelper;
import kxd.net.HttpRequest;
import kxd.util.SimpleTreeNode;
import kxd.util.StringUnit;
import kxd.util.TreeNode;

abstract public class BaseBusinessReportAction extends BaseReportAction {
	List<CachedBusiness> businessList;
	TreeNode<Integer> businessTree;
	String businessid;
	List<Integer> businessIdList;

	/**
	 * 重载过滤需要选择的业务
	 */
	protected List<CachedBusiness> buildBusinessMap() {
		List<CachedBusiness> ret = (List<CachedBusiness>) CacheHelper.businessMap
				.values();
		return ret;
	}

	protected List<CachedBusinessCategory> buildBusinessCategoryMap() {
		return (List<CachedBusinessCategory>) CacheHelper.businessCategoryMap
				.values();
	}

	/**
	 * 重载创建自定义的树对象
	 */
	protected SimpleTreeNode<Integer, String> createTreeRoot() {
		SimpleTreeNode<Integer, String> root = new SimpleTreeNode<Integer, String>();
		root.setData("全部业务");
		root.setExpanded(true);
		return root;
	}

	/**
	 * 重载构建自定义的树数据
	 */
	protected TreeNode<Integer> buildBusinessTree() {
		List<CachedBusinessCategory> businessCategoryList = buildBusinessCategoryMap();
		TreeNode<Integer> businessTree = new SimpleTreeNode<Integer, String>();
		SimpleTreeNode<Integer, String> root = createTreeRoot();
		businessTree.add(root);
		for (CachedBusinessCategory o : businessCategoryList) {
			if (businessList.size() == 0)
				break;
			SimpleTreeNode<Integer, String> p = new SimpleTreeNode<Integer, String>(
					null, o.getBusinessCategoryDesp());
			p.setExpanded(true);
			for (int i = 0; i < businessList.size(); i++) {
				CachedBusiness b = businessList.get(i);
				if (b.getBusinessCategoryId() == o.getId()) {
					SimpleTreeNode<Integer, String> cp = new SimpleTreeNode<Integer, String>(
							b.getId(), b.getBusinessDesp());
					p.add(cp);
					businessList.remove(i);
					i--;
				}
			}
			if (p.getChildren().size() > 0)
				root.add(p);
		}
		return businessTree;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (orgid == null) {
			businessList = buildBusinessMap();
			businessTree = buildBusinessTree();
		} else {
			businessid = request.getParameter("businessid");
			businessIdList = StringUnit.splitToInt1(businessid, ",");
			businessList = (List<CachedBusiness>) CacheHelper.businessMap
					.values(businessIdList);
		}
	}

	public Collection<CachedBusiness> getBusinessList() {
		return businessList;
	}

	public String getBusinessid() {
		return businessid;
	}

	public void setBusinessid(String businessid) {
		this.businessid = businessid;
	}

	public List<Integer> getBusinessIdList() {
		return businessIdList;
	}

	public void setBusinessIdList(List<Integer> businessIdList) {
		this.businessIdList = businessIdList;
	}

	public TreeNode<Integer> getBusinessTree() {
		return businessTree;
	}

}

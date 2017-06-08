package kxd.engine.scs.admin.actions.adinfo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.fileservice.FileProcessor;
import kxd.engine.fileservice.FileProperty;
import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.AdminTradeExecutor;
import kxd.engine.scs.admin.IntegerItem;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseAdCategory;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.BaseOrgAd;
import kxd.remote.scs.interfaces.OrgAdBeanRemote;
import kxd.remote.scs.util.emun.AdPriority;
import kxd.remote.scs.util.emun.AdStoreType;
import kxd.util.DateTime;

public class EditOrgAdAction extends EditAction {
	private BaseOrgAd orgAd;
	boolean added;
	private String startDate, endDate, md5;
	private long fileSize;
	static List<IntegerItem> storeTypeItems = new CopyOnWriteArrayList<IntegerItem>();
	static List<IntegerItem> priorityItems = new CopyOnWriteArrayList<IntegerItem>();
	static {
		for (AdPriority o : AdPriority.values()) {
			priorityItems.add(new IntegerItem(o.getValue(), o.toString()));
		}
		for (AdStoreType o : AdStoreType.values()) {
			storeTypeItems.add(new IntegerItem(o.getValue(), o.toString()));
		}
	}

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditOrgAd(request);
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		orgAd = getOrgAd();
		orgAd.setId(request.getParameterIntDef("id", null));
		BaseOrg b = new BaseOrg(request.getParameterIntDef("orgid", null));
		orgAd.setOrg(b);
		b.setOrgName(request.getParameterDef("orgdesp", null));
		if (!isFormSubmit) {
			if (orgAd.getId() != null) {
				LoopNamingContext context = new LoopNamingContext("db");
				try {
					OrgAdBeanRemote bean = context.lookup(
							Lookuper.JNDI_TYPE_EJB, "kxd-ejb-orgAdBean",
							OrgAdBeanRemote.class);
					orgAd = bean.find(orgAd.getId());
					startDate = new DateTime(orgAd.getStartDate())
							.format("yyyy-MM-dd");
					endDate = new DateTime(orgAd.getEndDate())
							.format("yyyy-MM-dd");
					if (!orgAd.isUploadComplete()) {
						FileProcessor fp = new FileProcessor(
								AdminTradeExecutor.fileUserId,
								AdminTradeExecutor.filePwd, (short) 4);
						FileProperty ffp = fp.getFileProperty(
								orgAd.getFileName(), true);
						md5 = ffp.getMd5();
						fileSize = ffp.getSize();
						String fileName = orgAd.getFileName();
						fileName = fileName
								.substring(fileName.lastIndexOf("/") + 1);
						orgAd.setFileName(fileName);
					}
				} finally {
					if (context != null)
						context.close();
				}
			} else {
				startDate = new DateTime().format("yyyy-MM-dd");
				endDate = new DateTime().format("yyyy-MM-dd");
			}
		}
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	private void addOrEditOrgAd(HttpRequest request) throws Throwable {
		orgAd.setAdDesp(request.getParameter("addesp"));
		orgAd.setAdCategory(new BaseAdCategory(request
				.getParameterShort("category")));
		orgAd.getAdCategory().setAdCategoryDesp(
				request.getParameter("category_desp"));
		startDate = request.getParameter("startdate");
		endDate = request.getParameter("enddate");
		orgAd.setStartDate(new DateTime(startDate, "yyyy-MM-dd").getTime());
		orgAd.setEndDate(new DateTime(endDate + " 23:59:59",
				"yyyy-MM-dd HH:mm:ss").getTime());
		orgAd.setDuration(request.getParameterInt("duration"));
		orgAd.setPriority(AdPriority.valueOfIntString(request
				.getParameter("priority")));
		orgAd.setStoreType(AdStoreType.valueOfIntString(request
				.getParameter("storetype")));
		if (orgAd.getStoreType().equals(AdStoreType.LOCAL)) {
			orgAd.setFileName(request.getParameter("localfile"));
			orgAd.setUploadComplete(true);
		}
		added = orgAd.getId() == null;

		LoopNamingContext context = new LoopNamingContext("db");
		try {
			OrgAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-orgAdBean", OrgAdBeanRemote.class);
			if (added) {
				bean.add(((AdminSessionObject) session).getLoginUser()
						.getUserId(), orgAd);
			} else
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), orgAd);

		} finally {
			if (context != null)
				context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + orgAd.getIdString() + "',desp:'"
				+ orgAd.getAdDesp() + "',columns:[";
		script += "'<a href=\"#\" onclick=\"ajax_table.showEditDialog(arguments[0]);return false;\">"
				+ "编辑</a> <a href=\"#\" onclick=\"jax_table.deleteSelected(arguments[0]);return false;\">"
				+ "删除</a> <a href=\"#\" onclick=\"onlineReq(arguments[0]);return false;\">申请上线</a>',";
		script += "'" + orgAd.getAdDesp() + "',";
		script += "'" + orgAd.getAdCategory().getAdCategoryDesp() + "',";
		if (orgAd.getFileName() == null)
			script += "null,";
		else
			script += "'" + orgAd.getFileName() + "',";
		script += "'" + orgAd.getOrg().getOrgName() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public BaseOrgAd getOrgAd() {
		if (orgAd == null) {
			orgAd = new BaseOrgAd();
		}
		return orgAd;
	}

	public String getFileName() {
		if (orgAd == null) {
			return "";
		}
		String ret = orgAd.getFileName();
		int index = ret.lastIndexOf(".");
		int index1 = ret.lastIndexOf("/");
		ret = ret.substring(index1, index);
		return ret;
	}

	public void setOrgAd(BaseOrgAd orgAd) {
		this.orgAd = orgAd;
	}

	@Override
	public int getEditRight() {
		return UserRight.ORGAD_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.ORGAD;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public List<IntegerItem> getStoreTypeItems() {
		return storeTypeItems;
	}

	public List<IntegerItem> getPriorityItems() {
		return priorityItems;
	}

}

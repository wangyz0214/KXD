package kxd.engine.scs.admin.actions.adinfo;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.BasePrintAdCategory;
import kxd.remote.scs.beans.adinfo.EditedPrintAd;
import kxd.remote.scs.interfaces.PrintAdBeanRemote;
import kxd.util.DateTime;

public class EditPrintAdAction extends EditAction {
	private EditedPrintAd printAd;
	boolean added;
	private String startDate, endDate;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditPrintAd(request);
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		printAd = getPrintAd();
		printAd.setId(request.getParameterIntDef("id", null));
		BaseOrg b = new BaseOrg(request.getParameterIntDef("orgid", null));
		printAd.setOrg(b);
		b.setOrgName(request.getParameterDef("orgdesp", null));
		if (!isFormSubmit) {
			if (printAd.getId() != null) {
				LoopNamingContext context = new LoopNamingContext("db");
				try {
					PrintAdBeanRemote bean = context.lookup(
							Lookuper.JNDI_TYPE_EJB, "kxd-ejb-printAdBean",
							PrintAdBeanRemote.class);
					printAd = (EditedPrintAd) bean.find(printAd.getId());
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

	private void addOrEditPrintAd(HttpRequest request) throws Throwable {
		printAd.setAdCategory(new BasePrintAdCategory(request
				.getParameterShort("category")));
		printAd.getAdCategory().setPrintAdCategoryDesp(
				request.getParameter("category_desp"));
		printAd.setContent(request.getParameter("content"));
		added = printAd.getId() == null;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PrintAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-printAdBean", PrintAdBeanRemote.class);
			if (added) {
				printAd.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getId(), printAd));
			} else
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), getPrintAd());

		} finally {
			if (context != null)
				context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + printAd.getIdString() + "',desp:'"
				+ printAd.getAdCategory().getPrintAdCategoryDesp()
				+ "',columns:[";
		script += "'<a href=\"#\" onclick=\"ajax_table.showEditDialog(arguments[0]);return false;\">"
				+ "编辑</a> <a href=\"#\" onclick=\"ajax_table.deleteSelected(arguments[0]);return false;\">"
				+ "删除</a> <a href=\"#\" onclick=\"onlineReq(arguments[0]);return false;\">申请上线</a>',";
		script += "'" + printAd.getAdCategory().getPrintAdCategoryDesp() + "',";
		script += "'" + printAd.getOrg().getOrgName() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public EditedPrintAd getPrintAd() {
		if (printAd == null) {
			printAd = new EditedPrintAd();
		}
		return printAd;
	}

	public void setPrintAd(EditedPrintAd printAd) {
		this.printAd = printAd;
	}

	@Override
	public int getEditRight() {
		return UserRight.PRINTAD_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.PRINTAD;
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
}

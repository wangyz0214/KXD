package kxd.engine.scs.admin.actions.adinfo;

import java.util.List;

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
import kxd.remote.scs.beans.BaseTerm;
import kxd.remote.scs.beans.BaseTermAd;
import kxd.remote.scs.interfaces.TermAdBeanRemote;
import kxd.remote.scs.util.emun.AdPriority;
import kxd.remote.scs.util.emun.AdStoreType;
import kxd.util.DateTime;

public class EditTermAdAction extends EditAction {
	private BaseTermAd termAd;
	boolean added;
	private String startDate, endDate, md5;
	private long fileSize;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditTermAd(request);
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		termAd = getTermAd();
		termAd.setId(request.getParameterIntDef("id", null));
		BaseTerm b = new BaseTerm(request.getParameterIntDef("termid", null));
		termAd.setTerm(b);
		b.setTermDesp(request.getParameterDef("termdesp", null));
		if (!isFormSubmit) {
			if (termAd.getId() != null) {
				LoopNamingContext context = new LoopNamingContext("db");
				try {
					TermAdBeanRemote bean = context.lookup(
							Lookuper.JNDI_TYPE_EJB, "kxd-ejb-termAdBean",
							TermAdBeanRemote.class);
					termAd = bean.find(termAd.getId());
					startDate = new DateTime(termAd.getStartDate())
							.format("yyyy-MM-dd");
					endDate = new DateTime(termAd.getEndDate())
							.format("yyyy-MM-dd");
					if (!termAd.isUploadComplete()) {
						FileProcessor fp = new FileProcessor(
								AdminTradeExecutor.fileUserId,
								AdminTradeExecutor.filePwd, (short) 5);
						FileProperty ffp = fp.getFileProperty(
								termAd.getFileName(), true);
						md5 = ffp.getMd5();
						fileSize = ffp.getSize();
						String fileName = termAd.getFileName();
						fileName = fileName
								.substring(fileName.lastIndexOf("/") + 1);
						termAd.setFileName(fileName);
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

	private void addOrEditTermAd(HttpRequest request) throws Throwable {
		termAd.setAdDesp(request.getParameter("addesp"));
		termAd.setAdCategory(new BaseAdCategory(request
				.getParameterShort("category")));
		termAd.getAdCategory().setAdCategoryDesp(
				request.getParameter("category_desp"));
		startDate = request.getParameter("startdate");
		endDate = request.getParameter("enddate");
		termAd.setStartDate(new DateTime(startDate, "yyyy-MM-dd").getTime());
		termAd.setEndDate(new DateTime(endDate + " 23:59:59",
				"yyyy-MM-dd HH:mm:ss").getTime());
		termAd.setDuration(request.getParameterInt("duration"));
		termAd.setPriority(AdPriority.valueOfIntString(request
				.getParameter("priority")));
		termAd.setStoreType(AdStoreType.valueOfIntString(request
				.getParameter("storetype")));
		if (termAd.getStoreType().equals(AdStoreType.LOCAL)) {
			termAd.setFileName(request.getParameter("localfile"));
			termAd.setUploadComplete(true);
		}
		added = termAd.getId() == null;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termAdBean", TermAdBeanRemote.class);
			if (added) {
				bean.add(((AdminSessionObject) session).getLoginUser()
						.getUserId(), termAd);
			} else
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), getTermAd());

		} finally {
			if (context != null)
				context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + termAd.getIdString() + "',desp:'"
				+ termAd.getFileName() + "',columns:[";
		script += "'" + termAd.getIdString() + "',";
		if (termAd.getFileName() == null)
			script += "null,";
		else
			script += "'" + termAd.getFileName() + "',";
		script += "'" + termAd.getTerm().getId() + "',";
		script += "'" + termAd.getTerm().getTermDesp() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public BaseTermAd getTermAd() {
		if (termAd == null) {
			termAd = new BaseTermAd();
		}
		return termAd;
	}

	public void setTermAd(BaseTermAd termAd) {
		this.termAd = termAd;
	}

	@Override
	public int getEditRight() {
		return UserRight.TERMAD_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.TERMAD;
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

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public List<IntegerItem> getStoreTypeItems() {
		return EditOrgAdAction.storeTypeItems;
	}

	public List<IntegerItem> getPriorityItems() {
		return EditOrgAdAction.priorityItems;
	}

}

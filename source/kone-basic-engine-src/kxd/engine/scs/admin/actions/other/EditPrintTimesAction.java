package kxd.engine.scs.admin.actions.other;

import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.cache.beans.sts.CachedPrintType;
import kxd.engine.helper.CacheHelper;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.interfaces.PrintTypeBeanRemote;

public class EditPrintTimesAction extends EditAction {
	private String userno;
	private int month;
	private Collection<CachedPrintType> printTypes;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addPrintTimes(request.getParameterInt("printtype"));
			return "success";
		} else {
			printTypes = CacheHelper.printTypeMap.values();
			return null;
		}
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			month = request.getParameterInt("month");
			userno = request.getParameter("userno");
		}
	}

	private void addPrintTimes(int printType) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PrintTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-printTypeBean", PrintTypeBeanRemote.class);
			bean.addUserPrintTimes(userno, month, printType, 1);
		} finally {
			context.close();
		}
	}

	@Override
	public int getEditRight() {
		return UserRight.ADDACCOUNTPRINTTIMES;
	}

	@Override
	public int getQueryRight() {
		return UserRight.ADDACCOUNTPRINTTIMES;
	}

	public Collection<CachedPrintType> getPrintTypes() {
		return printTypes;
	}

	public void setPrintTypes(Collection<CachedPrintType> printTypes) {
		this.printTypes = printTypes;
	}
}

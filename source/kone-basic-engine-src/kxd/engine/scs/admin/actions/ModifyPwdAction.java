package kxd.engine.scs.admin.actions;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.engine.ui.core.FacesError;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.interfaces.UserBeanRemote;
import kxd.util.DataSecurity;

public class ModifyPwdAction extends EditAction {
	private String oldpwd;
	private String password;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		return modifyPwd();
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		oldpwd = request.getParameter("oldpwd");
		password = request.getParameter("password");
	}

	public String modifyPwd() throws Throwable {
		if (oldpwd == null || oldpwd.trim().length() == 0) {
			throw new FacesError("error", "未提供[旧密码]");
		}
		if (password == null || password.trim().length() == 0) {
			throw new FacesError("error", "未提供[新密码]");
		}
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			UserBeanRemote userBean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-userBean", UserBeanRemote.class);
			String password = DataSecurity.md5(getPassword().getBytes());
			String oldpwd = DataSecurity.md5(getOldpwd().getBytes());
			userBean.modifyPwd(((AdminSessionObject) session).getLoginUser()
					.getUserId(), oldpwd, password);
			return "success";
		} finally {
			context.close();
		}
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getOldpwd() {
		return oldpwd;
	}

	public void setOldpwd(String oldpwd) {
		this.oldpwd = oldpwd;
	}

	@Override
	public int getEditRight() {
		return UserRight.MODIFYPWD;
	}

	@Override
	public int getQueryRight() {
		return UserRight.MY;
	}
}

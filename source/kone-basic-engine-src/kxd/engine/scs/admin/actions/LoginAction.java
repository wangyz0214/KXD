package kxd.engine.scs.admin.actions;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.ui.core.BaseAction;
import kxd.engine.ui.core.FacesError;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.right.LoginUser;
import kxd.remote.scs.interfaces.UserBeanRemote;
import kxd.util.DataSecurity;

public class LoginAction extends BaseAction {
	private String userName;
	private String password;
	private String verifyCode;
	static public volatile boolean notCheckVerifyCode;

	@Override
	protected boolean needLogin() {
		return false;
	}

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		return login();
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		userName = request.getParameter("userName");
		password = request.getParameter("password");
		verifyCode = request.getParameter("verifyCode");
	}

	public String login() throws Throwable {
		if (userName == null || userName.trim().length() == 0) {
			throw new FacesError("error", "请输入[用户编码]");
		}
		if (password == null || password.trim().length() == 0) {
			throw new FacesError("error", "请输入[登录密码]");
		}
		if (!notCheckVerifyCode) {
			String verifyCode = (String) ((AdminSessionObject) session)
					.removeAttribute("verifyCode");
			if (verifyCode == null
					|| !verifyCode.equalsIgnoreCase(this.getVerifyCode())) {
				throw new FacesError("error", "验证码不正确或已无效");
			}
		}
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			UserBeanRemote userBean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-userBean", UserBeanRemote.class);
			String password = DataSecurity.md5(getPassword().getBytes());
			LoginUser user = userBean.login(userName, password);
			((AdminSessionObject) session).setLoginUser(user);
			return "success";
		} finally {
			context.close();
		}
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	@Override
	public int getEditRight() {
		return 0;
	}

	@Override
	public int getQueryRight() {
		return 0;
	}

	@Override
	protected void processRight(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
	}

}

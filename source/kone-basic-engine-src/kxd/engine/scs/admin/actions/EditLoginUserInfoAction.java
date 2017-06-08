package kxd.engine.scs.admin.actions;

import java.io.IOException;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.engine.ui.core.FacesError;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.right.QueryedUser;
import kxd.remote.scs.interfaces.UserBeanRemote;
import kxd.remote.scs.util.AppException;
import kxd.util.KoneException;
import kxd.util.StringUnit;

public class EditLoginUserInfoAction extends EditAction {
	private String username;
	private String mobile;
	private String telphone;
	private String email;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit)
			return modifyUserInfo();
		else {
			try {
				QueryedUser user = getUserInfo();
				username = user.getUserName();
				mobile = user.getMobile();
				telphone = user.getTelphone();
				email = user.getEmail();
			} catch (NamingException e) {
				throw new KoneException(StringUnit.getExceptionMessage(e));
			}
			return null;
		}
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			username = request.getParameter("username");
			mobile = request.getParameter("mobile");
			telphone = request.getParameter("telphone");
			email = request.getParameter("email");
		}
	}

	public QueryedUser getUserInfo() throws NamingException, IOException,
			FacesError {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			UserBeanRemote userBean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-userBean", UserBeanRemote.class);
			return userBean.find(((AdminSessionObject) session).getLoginUser()
					.getId());
		} catch (AppException e) {
			throw new FacesError("error", StringUnit.getExceptionMessage(e));
		} catch (Throwable e) {
			throw new FacesError("error", StringUnit.getExceptionMessage(e));
		} finally {
			context.close();
		}
	}

	public String modifyUserInfo() throws NamingException, IOException,
			FacesError {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			UserBeanRemote userBean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-userBean", UserBeanRemote.class);
			QueryedUser user = new QueryedUser();
			user.setId(((AdminSessionObject) session).getLoginUser()
					.getUserId());
			user.setEmail(email);
			user.setTelphone(telphone);
			user.setMobile(mobile);
			user.setUserName(username);
			userBean.editInfo(user);
			return "success";
		} finally {
			context.close();
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public int getEditRight() {
		return UserRight.PROFILE;
	}

	@Override
	public int getQueryRight() {
		return UserRight.MY;
	}
}

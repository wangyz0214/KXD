package kxd.engine.scs.admin.actions.right;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.json.JSONException;
import kxd.json.JSONObject;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.right.EditedOrg;
import kxd.remote.scs.interfaces.OrgBeanRemote;
import kxd.remote.scs.util.AppException;

public class EditOrgAction extends EditAction {
	private EditedOrg org;
	boolean added;
	private String parentFullPath;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditOrg();
			return "success";
		} else
			return null;
	}

	/**
	 * 处理自动生成机构编码，继承类可以按自己的规则实现
	 * 
	 * @param isFormSubmit
	 * @param request
	 */
	protected void autoGenOrgCode(boolean isFormSubmit, HttpRequest request) {
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);

		autoGenOrgCode(isFormSubmit, request);
		setParentFullPath(request.getParameter("parentfullpath"));
		if (isFormSubmit) {
			org = getOrg();
			org.setId(request.getParameterIntDef("id", null));
			added = org.getId() == null;
			org.setOrgName(request.getParameter("desp"));
			org.setOrgFullName(request.getParameter("fullname"));
			if (org.getOrgCode() == null)
				org.setOrgCode(request.getParameter("code"));
			org.setEmail(request.getParameterDef("email", null));
			org.setAddress(request.getParameterDef("address", null));
			org.setContacter(request.getParameterDef("contacter", null));
			org.setTelphone(request.getParameterDef("telphone", null));
			org.setExtField0(request.getParameterDef("extfield0", null));
			org.setExtField1(request.getParameterDef("extfield1", null));
			org.setExtField2(request.getParameterDef("extfield2", null));
			org.setExtField3(request.getParameterDef("extfield3", null));
			org.setExtField4(request.getParameterDef("extfield4", null));
			org.setSerialNumber(request.getParameterIntDef("serialnumber", 0));
			org.setOrgType(request.getParameterInt("orgtype"));
			org.setStandardAreaCode(request.getParameterDef("standardareacode",
					null));
			org.setParentOrg(new BaseOrg(request.getParameterIntDef("parentid",
					null)));
			org.setSimpleName(request.getParameterDef("simplename", null));
			if (org.getOrgName().contains("\"")
					|| org.getOrgName().contains("'"))
				throw new AppException("机构名称中不能包含”或'字符");
		} else {
			getOrg(request.getParameterIntDef("id", null));
			if (org == null)
				getOrg().setParentOrg(
						new BaseOrg(request.getParameterInt("parentid")));
		}
	}

	private void getOrg(Integer id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			OrgBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-orgBean", OrgBeanRemote.class);
			org = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditOrg() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			OrgBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-orgBean", OrgBeanRemote.class);
			if (added) {
				org.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), org));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), org);
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() throws JSONException {
		if (added) {
			JSONObject o = new JSONObject();
			o.put("nodeid", org.getId());
			o.put("text", org.getOrgName());
			o.put("expanded", true);
			o.put("disabled", false);
			o.put("icon", "org.gif");
			String script = "var o=top.org.find("
					+ org.getParentOrg().getIdString() + ",true);";
			script += "if(!o.needload) o.add(" + o.toString() + ");";
			return script;
		} else {
			String script = "var node=top.org.find(" + org.getIdString()
					+ ",true);";
			script += "node.ps['text']='" + org.getOrgName() + "';";
			script += "node.textLink.set('text','" + org.getOrgName() + "');";
			return script;
		}
	}

	public EditedOrg getOrg() {
		if (org == null) {
			org = new EditedOrg();
		}
		return org;
	}

	public void setOrg(EditedOrg org) {
		this.org = org;
	}

	@Override
	public int getEditRight() {
		return UserRight.ORG_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.ORG;
	}

	public String getParentOrgDesp() throws UnsupportedEncodingException {
		if (org != null && org.getParentOrg() != null) {
			return URLEncoder.encode(org.getParentOrg().getOrgName(), "utf-8");
		} else
			return "";
	}

	public String getParentFullPath() {
		return parentFullPath;
	}

	public void setParentFullPath(String parentFullPath) {
		this.parentFullPath = parentFullPath;
	}
}

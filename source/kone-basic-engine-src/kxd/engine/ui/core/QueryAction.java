package kxd.engine.ui.core;

import javax.servlet.http.HttpServletResponse;

import kxd.net.HttpRequest;

abstract public class QueryAction extends BaseAction {
	protected void processRight(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (!isQueryEnabled())
			throw new FacesError("error", "访问受限，您的权限不足!");
	}

	String tableOptionHtml;

	protected String doGetTableOptionHtml() {
		return "<a href='#' onclick='ajax_table.showEditDialog(arguments[0]);return false;'>编辑</a> "
				+ "<a href='#' onclick='ajax_table.deleteSelected(arguments[0]);return false;'>删除</a>";
	}

	public String getTableOptionHtml() {
		if (!this.isEditEnabled())
			return "";
		if (tableOptionHtml == null) {
			tableOptionHtml = doGetTableOptionHtml();
		}
		return tableOptionHtml;
	}
}

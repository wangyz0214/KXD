package kxd.engine.scs.admin.actions;

import kxd.engine.ui.core.QueryAction;
import kxd.net.HttpRequest;
import kxd.util.Pageable;

abstract public class BaseDatatableAction extends QueryAction implements
		Pageable {
	private int pageRecordCount;
	private int page;
	private int recordCount;
	private String keyword;
	private Object dataList;

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		setPageRecordCount(request.getParameterIntDef("pagerecordcount", 50));
		setPage(request.getParameterIntDef("page", 0));
		setRecordCount(request.getParameterIntDef("recordcount", -1));
		setKeyword(request.getParameterDef("keyword", null));
	}

	public int getPageRecordCount() {
		return pageRecordCount;
	}

	public void setPageRecordCount(int pageRecordCount) {
		if (pageRecordCount <= 0)
			pageRecordCount = 20;
		this.pageRecordCount = pageRecordCount;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	public Object getDataList() {
		return dataList;
	}

	public void setDataList(Object dataList) {
		this.dataList = dataList;
	}

	public boolean isFirstQuery() {
		return recordCount < 0;
	}

	public int getStartRecord() {
		return getPage() * getPageRecordCount();
	}

	@Override
	public int getPages() {
		int pages = getRecordCount() / getPageRecordCount();
		if (getRecordCount() % getPageRecordCount() != 0)
			pages++;
		return pages;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

}

package kxd.remote.scs.beans;

import java.io.Serializable;

;

public class UserAttribute implements Serializable {
	private static final long serialVersionUID = 311134371262420508L;
	private Integer mainMenuId, subMenuId;
	private String currentPage;
	private String loginRedirect;

	public Integer getMainMenuId() {
		return mainMenuId;
	}

	public void setMainMenuId(Integer mainMenuId) {
		if (this.mainMenuId == null) {
			if (mainMenuId == null)
				return;
		} else if (this.mainMenuId.equals(mainMenuId))
			return;
		this.mainMenuId = mainMenuId;
	}

	public Integer getSubMenuId() {
		return subMenuId;
	}

	public void setSubMenuId(Integer subMenuId) {
		if (this.subMenuId == null) {
			if (subMenuId == null)
				return;
		} else if (this.subMenuId.equals(subMenuId))
			return;
		this.subMenuId = subMenuId;
	}

	public String getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(String currentPage) {
		if (this.currentPage == null) {
			if (currentPage == null)
				return;
		} else if (this.currentPage.equals(currentPage))
			return;
		this.currentPage = currentPage;
	}

	public String getLoginRedirect() {
		return loginRedirect;
	}

	public void setLoginRedirect(String loginRedirect) {
		if (this.loginRedirect == null) {
			if (loginRedirect == null)
				return;
		} else if (this.loginRedirect.equals(loginRedirect))
			return;
		this.loginRedirect = loginRedirect;
	}
}

package kxd.engine.scs.trade;

public class ServiceBean {
	String name;
	String jndiName;
	int[] orgIds, excludeOrgIds;
	int[] appIds, excludeAppIds;

	public ServiceBean(String name, String jndiName) {
		super();
		this.name = name;
		this.jndiName = jndiName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJndiName() {
		return jndiName;
	}

	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	public int[] getOrgIds() {
		return orgIds;
	}

	public void setOrgIds(int[] orgIds) {
		this.orgIds = orgIds;
	}

	public int[] getAppIds() {
		return appIds;
	}

	public void setAppIds(int[] appIds) {
		this.appIds = appIds;
	}

	public int[] getExcludeOrgIds() {
		return excludeOrgIds;
	}

	public void setExcludeOrgIds(int[] excludeOrgIds) {
		this.excludeOrgIds = excludeOrgIds;
	}

	public int[] getExcludeAppIds() {
		return excludeAppIds;
	}

	public void setExcludeAppIds(int[] excludeAppIds) {
		this.excludeAppIds = excludeAppIds;
	}

}

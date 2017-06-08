/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 用户权限分组 0 - 超级管理员； 1 - 系统管理员； 2 - 普通管理员； 3 - 普通操作员； 4 - 自定义操作员(角色分配权限)；
 * 
 * @author 赵明
 */
public enum UserGroup {

	SUPER {

		@Override
		public String toString() {
			return "超级管理员";
		}
	},
	SYSTEM {

		@Override
		public String toString() {
			return "系统管理员";
		}
	},
	MANAGER {

		@Override
		public String toString() {
			return "机构管理员";
		}
	},
	OPERATOR {

		@Override
		public String toString() {
			return "操作员";
		}
	},
	CUSTOMER {

		@Override
		public String toString() {
			return "角色用户";
		}
	};
	static public final UserGroup HIGHEST = SUPER;
	static public final UserGroup LOWEREST = CUSTOMER;

	public boolean groupIn(UserGroup high, UserGroup lower) {
		return compareTo(high) >= 0 && compareTo(lower) <= 0;
	}

	public boolean isSuperManager() {
		return equals(SUPER);
	}

	public boolean isSystemManager() {
		return compareTo(SYSTEM) <= 0;
	}

	public boolean isManager() {
		return compareTo(MANAGER) <= 0;
	}

	public boolean isOperator() {
		return equals(OPERATOR);
	}

	public boolean isCustomer() {
		return equals(CUSTOMER);
	}

	static public UserGroup valueOf(int v) {
		switch (v) {
		case 0:
			return SUPER;
		case 1:
			return SYSTEM;
		case 2:
			return MANAGER;
		case 3:
			return OPERATOR;
		default:
			return CUSTOMER;
		}
	}

	public int getValue() {
		return this.ordinal();
	}

	public String getString() {
		switch (this) {
		case SUPER:
			return SUPER.toString();
		case SYSTEM:
			return SYSTEM.toString();
		case MANAGER:
			return MANAGER.toString();
		case OPERATOR:
			return OPERATOR.toString();
		case CUSTOMER:
			return CUSTOMER.toString();
		}
		return super.toString();
	}

	static public UserGroup valueOfIntString(Object str) {
		if (str == null)
			return CUSTOMER;
		return valueOf(Integer.valueOf(str.toString()));
	}

}

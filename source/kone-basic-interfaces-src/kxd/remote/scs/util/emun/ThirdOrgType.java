/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 终端状态 0 - 普通机构,1 - 支付机构
 * 
 * @author 赵明
 */
public enum ThirdOrgType {

	GENERAL {

		@Override
		public String toString() {
			return "普通机构";
		}
	},
	PAY {

		@Override
		public String toString() {
			return "支付机构";
		}
	};
	static public ThirdOrgType valueOf(int v) {
		switch (v) {
		case 0:
			return GENERAL;
		default:
			return PAY;
		}
	}

	public int getValue() {
		return this.ordinal();
	}

	static public ThirdOrgType valueOfIntString(Object str) {
		if (str == null)
			return GENERAL;
		return valueOf(Integer.valueOf(str.toString()));
	}

}

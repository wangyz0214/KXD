/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 广告优先级控制
 * 
 * @author 赵明
 */
public enum AdPriority {

	LOWER_ORG {

		@Override
		public String toString() {
			return "下级机构优先";
		}
	},
	HIGHER_ORG {

		@Override
		public String toString() {
			return "上级机构优先";
		}
	},
	INSRETED {

		@Override
		public String toString() {
			return "插播";
		}
	};

	static public AdPriority valueOf(int v) {
		switch (v) {
		case 0:
			return LOWER_ORG;
		case 1:
			return HIGHER_ORG;
		default:
			return INSRETED;
		}
	}

	static public AdPriority valueOfIntString(Object str) {
		if (str == null)
			return LOWER_ORG;
		return valueOf(Integer.valueOf(str.toString()));
	}

	public int getValue() {
		return this.ordinal();
	}
}

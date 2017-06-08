/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 终端权限范围 0 - 区域，1 - 终端
 * 
 * @author 赵明
 */
public enum ManageScope {

	AREA {

		@Override
		public String toString() {
			return "区域";
		}
	},
	TERM {

		@Override
		public String toString() {
			return "终端";
		}
	};
	static public ManageScope valueOf(int v) {
		switch (v) {
		case 0:
			return AREA;
		default:
			return TERM;
		}
	}

	public int getValue() {
		return this.ordinal();
	}

	public String getString() {
		switch (this) {
		case AREA:
			return AREA.toString();
		case TERM:
			return TERM.toString();
		}
		return super.toString();
	}

	static public ManageScope valueOfIntString(Object str) {
		if (str == null)
			return AREA;
		return valueOf(Integer.valueOf(str.toString()));
	}

}

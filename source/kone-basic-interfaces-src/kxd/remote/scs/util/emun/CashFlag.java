/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 终端状态 0 - 无现金模块,1 - 有现金模块
 * 
 * @author 赵明
 */
public enum CashFlag {

	NOCASH {

		@Override
		public String toString() {
			return "无现金模块";
		}
	},
	HASCASH {

		@Override
		public String toString() {
			return "有现金模块";
		}
	};
	static public CashFlag valueOf(int v) {
		switch (v) {
		case 1:
			return HASCASH;
		default:
			return NOCASH;
		}
	}

	public int getValue() {
		return this.ordinal();
	}

	static public CashFlag valueOfIntString(Object str) {
		if (str == null)
			return NOCASH;
		return valueOf(Integer.valueOf(str.toString()));
	}

}

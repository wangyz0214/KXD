/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 业务开停模式
 * 
 * @author 赵明
 */
public enum BusinessOpenCloseMode {

	OPEN {

		@Override
		public String toString() {
			return "开放";
		}
	},
	STOP {

		@Override
		public String toString() {
			return "停止";
		}
	},
	FORCE_OPEN {

		@Override
		public String toString() {
			return "强制开放";
		}
	},
	FORCE_STOP {

		@Override
		public String toString() {
			return "强制停止";
		}
	};

	static public BusinessOpenCloseMode valueOf(int v) {
		switch (v) {
		case 0:
			return OPEN;
		case 1:
			return STOP;
		case 2:
			return FORCE_OPEN;
		default:
			return FORCE_STOP;
		}
	}

	static public BusinessOpenCloseMode valueOfIntString(Object str) {
		if (str == null)
			return OPEN;
		return valueOf(Integer.valueOf(str.toString()));
	}

	public int getValue() {
		return this.ordinal();
	}
}

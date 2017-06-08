/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 终端状态 0 - 未安装，1 - 未启用，2 - 正常，3 - 停用
 * 
 * @author 赵明
 */
public enum TermStatus {

	NOTINSTALL {

		@Override
		public String toString() {
			return "未安装";
		}
	},
	NOTACTIVE {

		@Override
		public String toString() {
			return "未启用";
		}
	},
	NORMAL {

		@Override
		public String toString() {
			return "已上线";
		}
	},
	DISUSE {

		@Override
		public String toString() {
			return "停用";
		}
	};
	static public TermStatus valueOf(int v) {
		switch (v) {
		case 0:
			return NOTINSTALL;
		case 1:
			return NOTACTIVE;
		case 2:
			return NORMAL;
		default:
			return DISUSE;
		}
	}

	public int getValue() {
		return this.ordinal();
	}

	static public TermStatus valueOfIntString(Object str) {
		if (str == null)
			return NOTINSTALL;
		return valueOf(Integer.valueOf(str.toString()));
	}

}

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
public enum TermRunStatus {

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
			return "正常";
		}
	},
	DISUSE {

		@Override
		public String toString() {
			return "停用";
		}
	},
	OFFLINE {

		@Override
		public String toString() {
			return "离线";
		}
	},
	ALARM {

		@Override
		public String toString() {
			return "告警";
		}
	},
	FAULT {

		@Override
		public String toString() {
			return "故障";
		}
	};
	static public TermRunStatus valueOf(int v) {
		switch (v) {
		case 0:
			return NOTINSTALL;
		case 1:
			return NOTACTIVE;
		case 2:
			return NORMAL;
		case 4:
			return OFFLINE;
		case 5:
			return ALARM;
		case 6:
			return FAULT;
		default:
			return DISUSE;
		}
	}

	public int getValue() {
		return this.ordinal();
	}

	static public TermRunStatus valueOfIntString(Object str) {
		if (str == null)
			return NOTINSTALL;
		return valueOf(Integer.valueOf(str.toString()));
	}

}

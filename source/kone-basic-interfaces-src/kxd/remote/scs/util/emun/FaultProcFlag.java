/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 终端状态 0 - 未处理，1 - 处理中，2 - 部件更新，3 - 已恢复，4 - 自动恢复，5 - 故障已升级
 * 
 * @author 赵明
 */
public enum FaultProcFlag {

	UNPROCESS {

		@Override
		public String toString() {
			return "未处理";
		}
	},
	PROCESSING {

		@Override
		public String toString() {
			return "处理中";
		}
	},
	PARTS_UPDATE {

		@Override
		public String toString() {
			return "部件更新";
		}
	},
	RESTORED {

		@Override
		public String toString() {
			return "已恢复";
		}
	},
	AUTO_RESTORED {

		@Override
		public String toString() {
			return "自动恢复";
		}
	},
	FAULT_UPGRADED {

		@Override
		public String toString() {
			return "故障已升级";
		}
	};
	static public FaultProcFlag valueOf(int v) {
		switch (v) {
		case 0:
			return UNPROCESS;
		case 1:
			return PROCESSING;
		case 2:
			return PARTS_UPDATE;
		case 3:
			return RESTORED;
		case 4:
			return AUTO_RESTORED;
		default:
			return FAULT_UPGRADED;
		}
	}

	public int getValue() {
		return this.ordinal();
	}

	static public FaultProcFlag valueOfIntString(Object str) {
		if (str == null)
			return UNPROCESS;
		return valueOf(Integer.valueOf(str.toString()));
	}

}

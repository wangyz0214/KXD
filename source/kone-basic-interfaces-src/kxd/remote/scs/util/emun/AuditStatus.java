/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 审核状态
 * 
 * @author 赵明
 */
public enum AuditStatus {

	OFFLINE {

		@Override
		public String toString() {
			return "已下线";
		}
	},
	ONLINE_REQ {

		@Override
		public String toString() {
			return "上线申请中";
		}
	},
	ONLINE {

		@Override
		public String toString() {
			return "已上线";
		}
	},
	OFFLINE_REQ {

		@Override
		public String toString() {
			return "下线申请中";
		}
	};
	static public AuditStatus valueOf(int v) {
		switch (v) {
		case 0:
			return OFFLINE;
		case 1:
			return ONLINE_REQ;
		case 2:
			return ONLINE;
		default:
			return OFFLINE_REQ;
		}
	}

	public int getValue() {
		return this.ordinal();
	}

	static public AuditStatus valueOfIntString(Object str) {
		if (str == null)
			return OFFLINE;
		return valueOf(Integer.valueOf(str.toString()));
	}

}

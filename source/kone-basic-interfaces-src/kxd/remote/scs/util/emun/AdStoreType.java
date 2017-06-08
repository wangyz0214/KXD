/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 广告存储类型
 * 
 * @author 赵明
 */
public enum AdStoreType {

	SERVER {

		@Override
		public String toString() {
			return "服务器存储";
		}
	},
	LOCAL {

		@Override
		public String toString() {
			return "本地存储";
		}
	};

	static public AdStoreType valueOf(int v) {
		switch (v) {
		case 0:
			return SERVER;
		case 1:
			return LOCAL;
		default:
			return LOCAL;
		}
	}

	static public AdStoreType valueOfIntString(Object str) {
		if (str == null)
			return SERVER;
		return valueOf(Integer.valueOf(str.toString()));
	}

	public int getValue() {
		return this.ordinal();
	}
}

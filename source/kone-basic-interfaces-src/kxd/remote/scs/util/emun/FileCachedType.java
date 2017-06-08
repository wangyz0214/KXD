/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 缓存
 * 
 * @author 赵明
 */
public enum FileCachedType {

	DEFAULT {

		@Override
		public String toString() {
			return "默认";
		}
	},
	LOCAL {

		@Override
		public String toString() {
			return "本地缓存";
		}
	};

	static public FileCachedType valueOf(int v) {
		switch (v) {
		case 0:
			return DEFAULT;
		default:
			return LOCAL;
		}
	}

	public int getValue() {
		return this.ordinal();
	}

	static public FileCachedType valueOfIntString(Object str) {
		if (str == null)
			return DEFAULT;
		return valueOf(Integer.valueOf(str.toString()));
	}

}

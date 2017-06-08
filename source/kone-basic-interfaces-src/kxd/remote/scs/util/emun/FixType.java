/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 终端安装类型 0 - 大堂式（立式）,1 - 穿墙式, 2 - 壁挂式
 * 
 * @author 赵明
 */
public enum FixType {

	HALL_STAND {

		@Override
		public String toString() {
			return "大堂式";
		}
	},
	THROUGH_WALL {

		@Override
		public String toString() {
			return "穿墙式";
		}
	},
	WALL_MOUNTED {

		@Override
		public String toString() {
			return "壁挂式";
		}
	};
	static public FixType valueOf(int v) {
		switch (v) {
		case 0:
			return HALL_STAND;
		case 1:
			return THROUGH_WALL;
		default:
			return WALL_MOUNTED;
		}
	}

	public int getValue() {
		return this.ordinal();
	}

	static public FixType valueOfIntString(Object str) {
		if (str == null)
			return HALL_STAND;
		return valueOf(Integer.valueOf(str.toString()));
	}

}

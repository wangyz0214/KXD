/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.remote.scs.util.emun;

/**
 * 文件访问权限 0，表示
 * 任何用户都可以访问该属主的文件。为1，表示只有提供用户和密码的用户才可以访问;为2，表示只有提供用户和密码，并且该用户的属主ID与当前的属主一致
 * ，才可能访问。
 * 
 * @author 赵明
 */
public enum FileVisitRight {

	ALL {

		@Override
		public String toString() {
			return "任何用户均可访问";
		}
	},
	USER {

		@Override
		public String toString() {
			return "任何定义的用户";
		}
	},
	OWNERUSER {

		@Override
		public String toString() {
			return "属于属主的用户";
		}
	};

	static public FileVisitRight valueOf(int v) {
		switch (v) {
		case 0:
			return ALL;
		case 1:
			return USER;
		default:
			return OWNERUSER;
		}
	}

	public int getValue() {
		return this.ordinal();
	}

	static public FileVisitRight valueOfIntString(Object str) {
		if (str == null)
			return ALL;
		return valueOf(Integer.valueOf(str.toString()));
	}

}

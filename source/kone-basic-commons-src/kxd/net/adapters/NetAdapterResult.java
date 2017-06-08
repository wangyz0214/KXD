package kxd.net.adapters;

/**
 * 通信结果
 * 
 * @author 赵明
 * @since 4.1
 */
public enum NetAdapterResult {

	SUCCESS {

		@Override
		public String toString() {
			return "通信成功";
		}
	},
	NOTARRIVALED {

		@Override
		public String toString() {
			return "通信未送达";
		}
	},
	FAILURE {

		@Override
		public String toString() {
			return "通信失败";
		}
	},
	TIMEOUT {

		@Override
		public String toString() {
			return "通信超时";
		}
	};
	static public NetAdapterResult valueOf(int v) {
		switch (v) {
		case 0:
			return SUCCESS;
		case 1:
			return NOTARRIVALED;
		case 2:
			return FAILURE;
		default:
			return TIMEOUT;
		}
	}

	public int getValue() {
		return this.ordinal();
	}
}

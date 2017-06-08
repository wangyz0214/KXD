package kxd.util;

public class KoneException extends RuntimeException {
	private static final long serialVersionUID = 8324817294616028113L;
	Object data; // 异常关联的数据

	public KoneException() {
	}

	public KoneException(String msg) {
		super(msg);
	}

	public KoneException(Throwable cause) {
		super(cause);
	}

	public KoneException(String msg, Object data) {
		super(msg);
		this.data = data;
	}

	public KoneException(Throwable cause, Object data) {
		super(cause);
		this.data = data;
	}

	public KoneException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public KoneException(String msg, Throwable cause, Object data) {
		super(msg, cause);
		this.data = data;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}

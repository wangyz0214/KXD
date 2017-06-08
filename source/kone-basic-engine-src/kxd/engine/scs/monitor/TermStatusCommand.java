package kxd.engine.scs.monitor;

import java.io.Serializable;
import java.util.Date;

public class TermStatusCommand implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int command;
	Object data;
	Date time;

	public TermStatusCommand(int command, Object data) {
		super();
		this.command = command;
		this.data = data;
		this.time = new Date();
	}

	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

}

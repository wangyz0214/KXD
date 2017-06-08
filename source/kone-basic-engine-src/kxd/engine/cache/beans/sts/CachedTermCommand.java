package kxd.engine.cache.beans.sts;

import java.io.IOException;
import java.util.Date;

import kxd.engine.cache.beans.CachedIntegerObject;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 缓存终端数据
 * 
 * @author zhaom
 * 
 */
public class CachedTermCommand extends CachedIntegerObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.termcommand";
	private String param;
	private String param1;
	private Date commandTime;
	private int command;

	public CachedTermCommand() {
		super();
	}

	public CachedTermCommand(Integer id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public CachedTermCommand(int id) {
		super(id);
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public Date getCommandTime() {
		return commandTime;
	}

	public void setCommandTime(Date commandTime) {
		this.commandTime = commandTime;
	}

	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setCommand(stream.readInt(false, 3000));
		setCommandTime(stream.readLongDateTime(3000));
		setParam(stream.readPacketByteString(3000));
		setParam1(stream.readPacketByteString(3000));
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writeInt(getCommand(), false, 3000);
		stream.writeLongDateTime(commandTime, 3000);
		stream.writePacketByteString(getParam(), 3000);
		stream.writePacketByteString(getParam1(), 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedTermCommand) {
			CachedTermCommand o = (CachedTermCommand) src;
			setCommand(o.command);
			setCommandTime(o.commandTime);
			setParam(o.param);
			setParam1(o.param1);
		}
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new CachedTermCommand();
	}

	@Override
	public String getText() {
		return param;
	}

	@Override
	public void setText(String text) {
		param = text;
	}
}

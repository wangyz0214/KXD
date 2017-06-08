package kxd.remote.scs.transaction;

import java.io.Serializable;

public class TransactionResponse implements Serializable {

	private static final long serialVersionUID = 1711185823846598350L;

	public Result result;

	public byte[] data;
}

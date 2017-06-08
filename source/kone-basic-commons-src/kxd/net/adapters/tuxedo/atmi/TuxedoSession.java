package kxd.net.adapters.tuxedo.atmi;

public class TuxedoSession {
	int handle = 0;
	Tuxedo tuxedo;

	TuxedoSession(Tuxedo tuxedo, String service, Buffer input, int flags)
			throws TuxedoException {
		if (input == null)
			handle = TuxedoAtmi.tpconnect(service.getBytes(), 0, 0, flags);
		else
			handle = TuxedoAtmi.tpconnect(service.getBytes(), input.getHandle(),
					input.getLength(), flags);
		if (handle == -1) {
			tuxedo.procError();
			throw new TuxedoException(0, TuxedoAtmi.getErrorString());
		}
		this.tuxedo = tuxedo;
	}

	public void dispose() {
		if (handle != 0) {
			TuxedoAtmi.tpdiscon(handle);
			handle = 0;
		}
	}

	/**
	 * 发送数据
	 * 
	 * @param sendBuf
	 * @param flags
	 * @return 发送返回的事件 [TPEV_DISCONIMM,TPEV_SVCERR,TPEV_SVCFAIL]
	 * @throws TuxedoException
	 */
	public int send(Buffer sendBuf, int flags) throws TuxedoException {
		byte[] event = new byte[1];
		if (TuxedoAtmi.tpsend(handle, sendBuf.getHandle(), sendBuf.getLength(),
				flags, event) == -1) {
			tuxedo.procError();
			throw new TuxedoException(0, TuxedoAtmi.getErrorString());
		}
		return event[0];
	}

	/**
	 * 接收数据
	 * 
	 * @param recvBuf
	 * @param flags
	 * @return 接收返回的事件
	 *         [TPEV_DISCONIMM,TPEV_SENDONLY,TPEV_SVCERR,TPEV_SVCFAIL,TPEV_SVCSUCC
	 *         ]
	 * @throws TuxedoException
	 */
	public int recv(Buffer recvBuf, int flags) throws TuxedoException {
		byte[] event = new byte[1];
		if (TuxedoAtmi.tprecv(handle, recvBuf.getHandle(), flags, event) != -1) {
			tuxedo.procError();
			throw new TuxedoException(0, TuxedoAtmi.getErrorString());
		}
		return event[0];
	}
}

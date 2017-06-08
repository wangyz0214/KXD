package kxd.net.adapters.tuxedo.atmi;

public class TuxedoAsynCall {
	int handle = 0;
	Tuxedo tuxedo;

	TuxedoAsynCall(Tuxedo tuxedo, String service, Buffer input, int flags)
			throws TuxedoException {
		handle = TuxedoAtmi.tpacall(service.getBytes(), input.getHandle(),
				input.getLength(), flags);
		if (handle == -1) {
			tuxedo.procError();
			throw new TuxedoException(0, TuxedoAtmi.getErrorString());
		}
		this.tuxedo = tuxedo;
	}

	public void cancel() {
		if (handle > 0) {
			TuxedoAtmi.tpcancel(handle);
			handle = 0;
		}
	}

	public void getReply(Buffer recvBuf, int flags) throws TuxedoException {
		if (TuxedoAtmi.tpgetrply(handle, recvBuf.handle, flags) == -1) {
			tuxedo.procError();
			throw new TuxedoException(0, TuxedoAtmi.getErrorString());
		}
	}

	public int getHandle() {
		return handle;
	}
}

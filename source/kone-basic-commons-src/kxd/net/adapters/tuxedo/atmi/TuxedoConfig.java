package kxd.net.adapters.tuxedo.atmi;

import java.io.Serializable;

public class TuxedoConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	String wsnAddr;
	int flags;
	String usrname;
	String cltname;
	String passwd;
	String grpname;
	byte[] data;

	public TuxedoConfig() {
		super();
	}

	public TuxedoConfig(String wsnAddr, boolean Multi) throws TuxedoException {
		this(wsnAddr, Multi ? TuxedoAtmi.TPMULTICONTEXTS : 0, null, null, null, null,
				null);
	}

	public TuxedoConfig(String wsnAddr, int flags, String usrname,
			String cltname, String passwd, String grpname, byte[] data) {
		super();
		this.wsnAddr = wsnAddr;
		this.flags = flags;
		this.usrname = usrname;
		this.cltname = cltname;
		this.passwd = passwd;
		this.grpname = grpname;
		this.data = data;
	}

	public String getWsnAddr() {
		return wsnAddr;
	}

	public void setWsnAddr(String wsnAddr) {
		this.wsnAddr = wsnAddr;
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public String getUsrname() {
		return usrname;
	}

	public void setUsrname(String usrname) {
		this.usrname = usrname;
	}

	public String getCltname() {
		return cltname;
	}

	public void setCltname(String cltname) {
		this.cltname = cltname;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getGrpname() {
		return grpname;
	}

	public void setGrpname(String grpname) {
		this.grpname = grpname;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

}

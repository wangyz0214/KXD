package kxd.engine.helper.beans;

import java.io.IOException;
import java.io.Serializable;

import kxd.util.Streamable;
import kxd.util.stream.Stream;

/**
 * 带唯一标识符的机构
 * 
 * @author zhaom
 * 
 */
public class IdentOrg implements Serializable, Streamable {
	private static final long serialVersionUID = 1L;
	private String orgDesp;
	private int ident;
	private int orgId;

	public int getIdent() {
		return ident;
	}

	public void setIdent(int ident) {
		this.ident = ident;
	}

	public String getOrgDesp() {
		return orgDesp;
	}

	public void setOrgDesp(String orgDesp) {
		this.orgDesp = orgDesp;
	}

	@Override
	public String toString() {
		return orgDesp + "(" + orgId + ")";
	}

	public int getOrgId() {
		return orgId;
	}

	public void setOrgId(int orgId) {
		this.orgId = orgId;
	}

	@Override
	public void readData(Stream stream) throws IOException {
		orgDesp = stream.readPacketByteString(3000);
		orgId = stream.readInt(false, 3000);
		ident = stream.readInt(false, 3000);
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		stream.writePacketByteString(orgDesp, 3000);
		stream.writeInt(orgId, false, 3000);
		stream.writeInt(ident, false, 3000);
	}

}

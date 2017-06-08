package kxd.engine.helper.beans;

import java.io.IOException;

import kxd.engine.cache.beans.CachedIntegerObject;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

public class IdentOrgScope extends CachedIntegerObject {
	private static final long serialVersionUID = 1L;
	private int startIdent;
	private int endIdent;
	private int depth;
	private String orgDesp;

	public int getStartIdent() {
		return startIdent;
	}

	public void setStartIdent(int startIdent) {
		this.startIdent = startIdent;
	}

	public int getEndIdent() {
		return endIdent;
	}

	public void setEndIdent(int endIdent) {
		this.endIdent = endIdent;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public String getOrgDesp() {
		return orgDesp;
	}

	public void setOrgDesp(String orgDesp) {
		this.orgDesp = orgDesp;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		// setId(stream.readInt(false, 3000));
		orgDesp = stream.readPacketByteString(3000);
		startIdent = stream.readInt(false, 3000);
		endIdent = stream.readInt(false, 3000);
		depth = stream.readInt(false, 3000);
		orgDesp = stream.readPacketByteString(3000);
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		// stream.writeInt(getId(), false, 3000);
		stream.writePacketByteString(orgDesp, 3000);
		stream.writeInt(startIdent, false, 3000);
		stream.writeInt(endIdent, false, 3000);
		stream.writeInt(depth, false, 3000);
		stream.writePacketByteString(orgDesp, 3000);
	}

	@Override
	public String getText() {
		return null;
	}

	@Override
	public void setText(String text) {
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new IdentOrgScope();
	}
}

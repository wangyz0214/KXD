package kxd.engine.scs.report;

import java.io.IOException;
import java.io.Serializable;

import kxd.util.Streamable;
import kxd.util.stream.Stream;

/**
 * 终端名称项，包含：省，地市，营业厅，终端四项数据
 * 
 * @author zhaom
 * 
 */
public class TermNameItem implements Serializable, Streamable {
	private static final long serialVersionUID = 1L;
	private String provinceName, cityName, hallName, termCode;
	private int orgId;

	public int getOrgId() {
		return orgId;
	}

	public void setOrgId(int orgId) {
		this.orgId = orgId;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getHallName() {
		return hallName;
	}

	public void setHallName(String hallName) {
		this.hallName = hallName;
	}

	public String getTermCode() {
		return termCode;
	}

	public void setTermCode(String termCode) {
		this.termCode = termCode;
	}

	@Override
	public void readData(Stream stream) throws IOException {
		provinceName = stream.readPacketByteString(3000);
		cityName = stream.readPacketByteString(3000);
		hallName = stream.readPacketByteString(3000);
		termCode = stream.readPacketByteString(3000);
		orgId = stream.readInt(false, 3000);
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		stream.writePacketByteString(provinceName, 3000);
		stream.writePacketByteString(cityName, 3000);
		stream.writePacketByteString(hallName, 3000);
		stream.writePacketByteString(termCode, 3000);
		stream.writeInt(orgId, false, 3000);
	}

}

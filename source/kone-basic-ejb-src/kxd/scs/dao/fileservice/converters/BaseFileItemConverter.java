package kxd.scs.dao.fileservice.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseFileItem;

public class BaseFileItemConverter extends BaseDaoConverter<BaseFileItem>
		implements SqlConverter<BaseFileItem> {

	public BaseFileItemConverter() {

	}

	public BaseFileItem doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseFileItem r = new BaseFileItem();
		r.setId((String) o[0]);
		r.setFileCategoryId(Short.valueOf(o[1].toString()));
		r.setFileHostId(Short.valueOf(o[2].toString()));
		r.setFileOwnerId(Short.valueOf(o[3].toString()));
		r.setSavePath(o[4] == null ? "" : (String) o[4]);
		r.setOriginalFileName(o[5] == null ? "" : (String) o[5]);
		r.setMd5(o[6] == null ? "" : (String) o[6]);
		return r;
	}

	private final static String DELETE_SQL = "delete from filelist where fileid=?1";

	public SqlParams getDeleteSql(BaseFileItem o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into filelist (fileid,"
			+ "categoryid,hostid,fileownerid,savepath,originalfilename,md5) values(?1,?2,?3,?4,?5,?6,?7)";

	public SqlParams getInsertSql(BaseFileItem o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getFileCategoryId(),
				o.getFileHostId(), o.getFileOwnerId(), o.getSavePath(),
				o.getOriginalFileName(), o.getMd5());
	}

	private final static String SEQUENCE_STRING = "";

	public String getSequenceString(BaseFileItem o) {
		return SEQUENCE_STRING;
	}

	public SqlParams getUpdateSql(BaseFileItem o) {
		throw new UnsupportedOperationException();
	}

}

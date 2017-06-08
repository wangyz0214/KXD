package kxd.engine.cache.beans.sts;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kxd.engine.cache.beans.CachedIntegerObject;
import kxd.remote.scs.util.emun.BusinessOpenCloseMode;
import kxd.util.DateTime;
import kxd.util.IdableObject;
import kxd.util.StringUnit;
import kxd.util.stream.Stream;

import org.apache.log4j.Logger;

public class CachedBusinessOpenClose extends CachedIntegerObject {
	public static class Item implements Serializable {
		private static final long serialVersionUID = 1L;
		public Date startDate, endDate;
		public int[][] openTimes;
		public BusinessOpenCloseMode openMode;
		public int[] businessCategoryIds, businessIds, payways, payitems;
		public String reason;
	}

	private static final long serialVersionUID = 1L;
	private List<Item> configList = new ArrayList<Item>();

	@Override
	public IdableObject<Integer> createObject() {
		return new CachedBusinessOpenClose();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof CachedBusinessOpenClose))
			return;
		CachedBusinessOpenClose d = (CachedBusinessOpenClose) src;
		configList.addAll(d.configList);
	}

	public CachedBusinessOpenClose() {
		super();
	}

	public CachedBusinessOpenClose(Integer id, boolean isTermConfig) {
		super(id);
	}

	@Override
	protected String toDisplayLabel() {
		return "";
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
	}

	@Override
	public String getText() {
		return null;
	}

	@Override
	public void setText(String text) {
	}

	public void addItem(String businessCategoryId, String businessIds,
			String openTimes, Date startDate, Date endDate, int openType,
			String reason, String payways, String payitems) {
		Item o = new Item();
		configList.add(o);
		o.businessCategoryIds = StringUnit.splitToInt(businessCategoryId, ",");
		o.businessIds = StringUnit.splitToInt(businessIds, ",");
		o.payways = StringUnit.splitToInt(payways, ",");
		o.payitems = StringUnit.splitToInt(payitems, ",");
		String[] t = StringUnit.split(openTimes, ";");
		o.openTimes = new int[t.length][];
		for (int j = 0; j < t.length; j++) {
			String str[] = StringUnit.split(t[j], "-");
			o.openTimes[j] = new int[] {
					Integer.valueOf(str[0].replace(":", "")),
					Integer.valueOf(str[1].replace(":", "")) };
		}
		o.startDate = startDate;
		DateTime d = new DateTime(endDate);
		d.set(23, 59, 59, 0);
		o.endDate = d.getTime();
		o.reason = reason;
		o.openMode = BusinessOpenCloseMode.valueOf(openType);
	}

	@Override
	protected void doReadData(Stream stream) throws IOException {
		int c = stream.readShort(false, 3000);
		configList.clear();
		for (int i = 0; i < c; i++) {
			Item o = new Item();
			configList.add(o);
			o.businessCategoryIds = StringUnit.splitToInt(
					stream.readPacketShortString(false, 3000), ",");
			o.businessIds = StringUnit.splitToInt(
					stream.readPacketShortString(false, 3000), ",");
			o.payways = StringUnit.splitToInt(
					stream.readPacketShortString(false, 3000), ",");
			o.payitems = StringUnit.splitToInt(
					stream.readPacketShortString(false, 3000), ",");
			String[] t = StringUnit.split(
					stream.readPacketShortString(false, 3000), ";");
			o.openTimes = new int[t.length][];
			for (int j = 0; j < t.length; j++) {
				o.openTimes[j] = StringUnit.splitToInt(t[j], ",");
			}
			o.startDate = stream.readLongDateTime(3000);
			o.endDate = stream.readLongDateTime(3000);
			o.reason = stream.readPacketByteString(3000);
			o.openMode = BusinessOpenCloseMode.valueOf(stream.readInt(false,
					3000));
		}
	}

	@Override
	protected void doWriteData(Stream stream) throws IOException {
		stream.writeShort((short) configList.size(), false, 3000);
		for (Item o : configList) {
			stream.writePacketShortString(
					StringUnit.arrayToString(o.businessCategoryIds), false,
					3000);
			stream.writePacketShortString(
					StringUnit.arrayToString(o.businessIds), false, 3000);
			stream.writePacketShortString(StringUnit.arrayToString(o.payways),
					false, 3000);
			stream.writePacketShortString(StringUnit.arrayToString(o.payitems),
					false, 3000);
			StringBuffer sb = new StringBuffer();
			int i = 0;
			for (int[] a : o.openTimes) {
				if (i > 0 && a.length > 0)
					sb.append(";");
				int j = 0;
				for (int a1 : a) {
					if (j > 0)
						sb.append(",");
					sb.append(a1);
					j++;
				}
				i++;
			}
			stream.writePacketShortString(sb.toString(), false, 3000);
			stream.writeLongDateTime(o.startDate, 3000);
			stream.writeLongDateTime(o.endDate, 3000);
			stream.writePacketByteString(o.reason, 3000);
			stream.writeInt(o.openMode.getValue(), false, 3000);
		}
	}

	public List<Item> getConfigList() {
		return configList;
	}

}

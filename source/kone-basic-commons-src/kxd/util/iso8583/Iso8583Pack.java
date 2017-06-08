package kxd.util.iso8583;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import kxd.util.DataUnit;

//import kxd.util.logging.Logger;

public abstract class Iso8583Pack {

	static public Logger logger = Logger.getLogger(Iso8583Pack.class);

	// 下面定义在本类中支持的ISO 8583域的数据格式
	public static final int SRC = 0; // 原始值，不处理长度

	public static final int STR = 1; // 字串，不足长度后补空

	public static final int AV2 = 2; // 2位变长，2位十进制字串长度+数据

	public static final int AV3 = 3; // 3位变长，3位十进制字串长度+数据

	public static final int BCD = 4; // BCD码

	public static final int BV2 = 5; // 2位变长，2位十进制字串长度+BCD数据

	public static final int BV3 = 6; // 3位变长，3位十进制字串长度+BCD数据

	public static final int BAV2 = 7; // 2位变长，2位BCD长度+数据

	public static final int BAV3 = 8; // 3位变长，3位BCD长度+数据

	public static final int BBV2 = 9; // 2位变长，2位BCD长度+BCD数据

	public static final int BBV3 = 10; // 3位变长，3位BCD长度+BCD数据

	public static final int BIN = 11; // 二进制数据

	public static final int BINA2 = 12; // 2位十进制字串长度+BIN数据

	public static final int BINA3 = 13; // 3位十进制字串长度+BIN数据

	public static final int BINB2 = 14; // 2位BCD长度+BIN数据

	public static final int BINB3 = 15; // 3位十进制字串BCD长度+BIN数据

	public static final int AV4 = 16;

	public static final int BAV4 = 17;

	public static final int HEX = 18; // 16进制字符

	/**
	 * 位图域字节数
	 */
	protected int bitmapSize;

	/**
	 * Iso8583 Field数组，大小为bitmapSize * 8
	 */
	protected Iso8583Field[] fields = null;

	/**
	 * 位图域，大小为bitmapSize
	 */
	protected byte[] mapField = null;

	/**
	 * Iso8583 的消息ID，4字节
	 */
	public int messageId = 0;

	public int messageidType = BCD;

	/**
	 * 读数据超时，默认为10秒
	 */
	public int readdata_timeout = 10;

	protected abstract void init() throws EIso8583Error;

	/**
	 * 创建一个Iso8583 包对象，此函数用于解包时，所有参数均从输入流中解出
	 */
	public Iso8583Pack() throws EIso8583Error {

		init();
	}

	/**
	 * 从流中读取数据
	 * 
	 * @param stream
	 *            InputStream 输入流
	 * @param b
	 *            byte[] 接收字串缓冲区
	 * @param offset
	 *            int 偏移量
	 * @param len
	 *            int 接收字串长度
	 * @throws IOException
	 *             超时或读数据出错抛出
	 */
	public void readstream(InputStream stream, byte[] b, int offset, int len) throws EIso8583Error {

		long firstTime = System.currentTimeMillis();
		int timeout = readdata_timeout * 1000;
		int endindex = offset + len;
		try {
			while (true) {
				long times = System.currentTimeMillis() - firstTime;
				if (times > timeout) { throw new EIso8583Error("接收数据超时"); }
				synchronized (stream) {
					offset += stream.read(b, offset, len);
				}
				if (offset >= endindex)
					break;
				try {
					Thread.sleep(10);
				} catch (Exception e) {
				}
			}
		} catch (EIso8583Error e) {
			throw e;
		} catch (Exception e) {
			throw new EIso8583Error(e.toString());
		}
	}

	/**
	 * 从流中读取数据
	 * 
	 * @param stream
	 *            InputStream 输入流
	 * @param b
	 *            byte[] 接收字串缓冲区
	 * @throws IOException
	 *             超时或读数据出错抛出
	 */
	public void readstream(InputStream stream, byte[] b) throws EIso8583Error {

		readstream(stream, b, 0, b.length);
	}

	/**
	 * 初始化Iso8583包
	 * 
	 * @param bitmapSize
	 * @param messageId
	 * @throws EIso8583Error
	 */
	protected void init(int bitmapSize) throws EIso8583Error {

		if (!(bitmapSize == 8 || bitmapSize == 16))
			throw new EIso8583Error("bitmapSize必须为8或16");

		this.bitmapSize = bitmapSize;
		mapField = new byte[bitmapSize];

		for (int i = 0; i < bitmapSize; i++)
			mapField[i] = 0;

		if (bitmapSize == 16) // 128位时，位图域0的最高位为1
			mapField[0] |= 0x80;

		// mapField[0] |= 0x40; // 填充位图域标记
		// 初始化位图域，第0域无用，从1开始计算
		fields = new Iso8583Field[bitmapSize * 8 + 1];

		for (int i = 1; i < fields.length; i++) {
			fields[i] = new Iso8583Field();
			fields[i].field = i;
		}
	}

	/**
	 * 设置messageID
	 * 
	 * @param messageId
	 * @param messageidType
	 */
	public void setMessageID(int messageId, int messageidType) {

		this.messageId = messageId;
		this.messageidType = messageidType;
	}

	public boolean hasField(int field) {

		if (field < 0 || field > bitmapSize * 8)
			return false;
		int i = (field - 1) / 8;
		int j = field - i * 8 - 1;
		return (byte) (mapField[i] & (0x01 << (8 - j - 1))) != 0;
	}

	/**
	 * 取字段值
	 * 
	 * @param field
	 * @return
	 */
	public byte[] getFieldValue(int field) {

		if (field < 0 || field > getFields().length)
			return null;
		Iso8583Field f = getFields()[field];
		if (f.value == null || f.value.length <= f.length) {
			return f.value;
		}
		else {
			// 去掉填充字符
			byte[] dest = new byte[f.length];
			int pos = f.alignleft ? 0 : (f.value.length - f.length);
			System.arraycopy(f.value, pos, dest, 0, f.length);
			return dest;
		}
	}

	/**
	 * 取字段值
	 * 
	 * @param field
	 * @return
	 */
	public String getFieldStringValue(int field, String def) {

		if (field < 0 || field > getFields().length)
			return def;

		Iso8583Field f = getFields()[field];

		if (f.value == null)
			return def;
		else if (f.value.length <= f.length) {
			return new String(f.value);
		}
		else {
			// 去掉填充字符
			byte[] dest = new byte[f.length];
			int pos = f.alignleft ? 0 : (f.value.length - f.length);
			System.arraycopy(f.value, pos, dest, 0, f.length);
			return new String(dest);
		}
	}

	public Iso8583Field[] getFields() {

		return fields;
	}

	/**
	 * 填充一个位图域，如果位图域中field指定的索引位为0，则置为1
	 * 
	 * @param field
	 *            - Iso8583 域索引
	 */
	public void fillinMapField(int field) {

		int byteIndex = (field - 1) / 8;
		int addValue = 0x01 << ((8 - field % 8) % 8);
		mapField[byteIndex] |= addValue;
	}

	/**
	 * 定义域,如果为<br>
	 * ascii码数据，默认左对齐，后补空格<br>
	 * BCD码数据默认右对齐，补'0'/其他默认左对齐，补0
	 * 
	 * @param field
	 * @param encodetype
	 * @param decodetype
	 */
	public void setFieldType(int field, int type, int len) {

		switch (type) {
		case SRC:
		case STR:
		case AV2:
		case AV3:
		case BAV2:
		case BAV3:
			setFieldType(field, type, len, (byte) ' ', true);
			break;
		case BCD:
		case BV2:
		case BV3:
		case BBV2:
		case BBV3:
			setFieldType(field, type, len, (byte) '0', false);
			break;
		default:
			setFieldType(field, type, len, (byte) 0, true);
		}
	}

	public void setFieldType(int field, int type, int len, byte fillchar, boolean alignleft) {

		fields[field].encodeIndex = type;
		fields[field].decodeIndex = type;
		fields[field].alignleft = alignleft;
		fields[field].fillchar = fillchar;
		fields[field].length = len;

		// System.out.println("setFieldType"+field);
	}

	/**
	 * 
	 * @param field
	 * @param src
	 */
	public void setFieldValue(int field, String src) {

		setFieldValue(field, src.getBytes(), 0, src.length());
	}

	public void setFieldValue(int field, int src) {

		String srcstr = Integer.toString(src);
		setFieldValue(field, srcstr);
	}

	public void setFieldValue(int field, byte[] src) {

		// System.out.println("setFieldValue("+field+",("+new String(src)+")");
		setFieldValue(field, src, 0, src.length);
	}

	public void setFieldValue(int field, byte[] src, int start, int length) {

		if (field > 0 && field <= fields.length) {
			int len = length;
			Iso8583Field f = fields[field];
			if (length <= 0 || len > src.length)
				len = src.length;
			else if (len > f.length)
				len = f.length;
			// 定长
			if (f.encodeIndex == SRC || f.encodeIndex == STR || f.encodeIndex == BCD || f.encodeIndex == BIN) {
				f.value = new byte[f.length];
				if (f.alignleft) {
					System.arraycopy(src, 0, f.value, start, len);
					for (int i = len; i < f.length; i++)
						f.value[i] = f.fillchar;
				}
				else {
					for (int i = 0; i < f.length - len; i++)
						f.value[i] = f.fillchar;
					System.arraycopy(src, start, f.value, f.length - len, len);
				}
				// System.out.println(new String(f.value));
			}
			else {
				f.value = new byte[len];
				System.arraycopy(src, 0, f.value, start, len);
			}
			// 当设置了一个Iso8583 域的同时，填充该域的位图域为1
			fillinMapField(field);

		}
	}

	public void encode(OutputStream stream) throws EIso8583Error {

		try {
			if (messageId == 0)
				throw new EIso8583Error("请先设置message id");
			String msgidStr = Integer.toString(messageId);
			while (msgidStr.length() < 4) {
				msgidStr = "0" + msgidStr;
			}
			if (messageidType == BCD) {
				// stream.write(DataUnit.decToBcd(msgidStr,4));
				stream.write(asciiToBCD(msgidStr));

			}
			else if (messageidType == HEX) {
				stream.write(Integer.toHexString(messageId).getBytes());
			}
			else if (messageidType == BIN) {
				stream.write(DataUnit.shortToBytes((short) messageId));
			}
			else if (messageidType == -1) { // TEST
				stream.write(asciiToBCD("00000100"));
			}

			else {
				stream.write(msgidStr.getBytes());
			}
			// setFieldsPackMethod(fields);
			stream.write(mapField);
			for (int i = 0; i < bitmapSize; i++) {
				for (int j = 0; j < 8; j++) {
					if ((byte) (mapField[i] & (0x01 << (8 - j - 1))) != 0) { // 该位域已填充
						int field = i * 8 + j + 1;
						if (field > 1) { // 位图域不再打包

							encodeField(fields[field], stream);
						}
					}
				}
			}
		} catch (IOException e) {
			throw new EIso8583Error("写数据失败");
		} catch (Exception e) {
			throw new EIso8583Error("未知错误" + e.toString());
		}
	}

	// ------------------------------------------------------------------------------
	// 方法：decode
	// 作用：从参数输入流中解包至8583包体
	// 参数：stream －－ InputStream
	// 返回：无
	// 异常处理：无
	// 调用说明：
	// ------------------------------------------------------------------------------

	public void decode(InputStream stream) throws EIso8583Error {

		try {

			if (messageidType == BCD) {
				byte[] tmp = new byte[2];
				readstream(stream, tmp);
				messageId = (((tmp[0] >> 4) & 0x0f) * 1000) + ((tmp[0] & 0x0f) * 100)
						+ (((tmp[1] >> 4) & 0x0f) * 10) + (tmp[1] & 0x0f);
				// System.arraycopy(messageId,0,DataFormat.bcdToAscii(tmp),0,2);
			}
			else if (messageidType == HEX) {
				byte[] tmp = new byte[2];
				readstream(stream, tmp);
				messageId = Integer.parseInt(new String(tmp), 16);
			}
			else if (messageidType == BIN) {
				byte[] tmp = new byte[2];
				readstream(stream, tmp);
				messageId = DataUnit.bytesToShort(tmp, 2);
			}
			else {
				byte[] tmp = new byte[4];
				readstream(stream, tmp);
				messageId = Integer.parseInt(new String(tmp), 10);
			}

			byte[] bit = new byte[1];
			readstream(stream, bit, 0, 1);
			if (bitmapSize != (((bit[0] & 0x80) == 0x80) ? 16 : 8)) { throw new EIso8583Error("8583位图错误"); }

			mapField[0] = bit[0];
			readstream(stream, mapField, 1, bitmapSize - 1);
			// fields = new Iso8583Field[bitmapSize*8+1];
			/*
			 * for (int i=1;i<fields.length;i++){ fields[i] = new Iso8583Field(); fields[i].field = i; }
			 */
			// 设置域的打、解包方法
			// setFieldsPackMethod(fields);
			// fields[1].value = mapField;//填充第一域（位图域）
			for (int i = 0; i < bitmapSize; i++) {
				for (int j = 0; j < 8; j++) {
					if ((byte) (mapField[i] & (0x01 << (8 - j - 1))) != 0) { // 该位域已填充
						int field = i * 8 + j + 1;
						if (field > 1) { // 位图域不再解包
							// System.out.println("decodeField."+field);
							// System.out.println("length="+fields[field].length);
							decodeField(fields[field], stream);
						}
					}
				}
			}
		} catch (IOException e) {
			throw new EIso8583Error("写数据失败", e);
		} catch (Exception e) {
			throw new EIso8583Error(e.toString(), e);
		}
	}

	public void decode(InputStream stream, OutputStream log_stream) throws EIso8583Error {

		try {

			if (messageidType == BCD) {
				byte[] tmp = new byte[2];
				readstream(stream, tmp);
				log_stream.write(tmp);
				messageId = (((tmp[0] >> 4) & 0x0f) * 1000) + ((tmp[0] & 0x0f) * 100)
						+ (((tmp[1] >> 4) & 0x0f) * 10) + (tmp[1] & 0x0f);
				// System.arraycopy(messageId,0,DataFormat.bcdToAscii(tmp),0,2);
			}
			else if (messageidType == HEX) {
				byte[] tmp = new byte[2];
				readstream(stream, tmp);
				log_stream.write(tmp);
				messageId = Integer.parseInt(new String(tmp), 16);
			}
			else if (messageidType == BIN) {
				byte[] tmp = new byte[2];
				readstream(stream, tmp);
				log_stream.write(tmp);
				messageId = DataUnit.bytesToShort(tmp, 2);
			}
			else {
				byte[] tmp = new byte[4];
				readstream(stream, tmp);
				log_stream.write(tmp);
				messageId = Integer.parseInt(new String(tmp), 10);
			}
			// System.out.println("messageId="+messageId);
			byte[] bit = new byte[1];
			readstream(stream, bit, 0, 1);
			log_stream.write(bit);
			if ((bit[0] & 0x80) == 0x80)
				bitmapSize = 16;
			else
				bitmapSize = 8;
			init(bitmapSize);
			// System.out.println("bitmapSize="+bitmapSize);
			mapField[0] = bit[0];
			readstream(stream, mapField, 1, bitmapSize - 1);
			log_stream.write(mapField);
			// fields = new Iso8583Field[bitmapSize*8+1];
			/*
			 * for (int i=1;i<fields.length;i++){ fields[i] = new Iso8583Field(); fields[i].field = i; }
			 */
			// 设置域的打、解包方法
			// setFieldsPackMethod(fields);
			// fields[1].value = mapField;//填充第一域（位图域）
			for (int i = 0; i < bitmapSize; i++) {
				for (int j = 0; j < 8; j++) {
					if ((byte) (mapField[i] & (0x01 << (8 - j - 1))) != 0) { // 该位域已填充
						int field = i * 8 + j + 1;
						if (field > 1) { // 位图域不再解包
							// System.out.println("decode fields["+field+"]");
							decodeField(fields[field], stream, log_stream);
							// System.out.println("fields["+field+"]="+com.kxd.common.Function.byte2hex(fields[field].value,fields[field].length));
						}
					}
				}
			}
		} catch (IOException e) {
			throw new EIso8583Error("写数据失败", e);
		} catch (Exception e) {
			throw new EIso8583Error(e.toString(), e);
		}
	}

	protected void encodeField(Iso8583Field field, OutputStream stream) throws EIso8583Error, IOException {

		byte[] tmp, tmp1;
		switch (field.encodeIndex) {
		case SRC: // 值不变
			stream.write(field.value);
			break;
		case STR: //
		case BIN:
			stream.write(fixedBytes(field.value, (byte) field.fillchar, field.length, field.alignleft));
			break;
		case AV2: // 2位变长，2位十进制字串长度+数据
		case BINA2:
			stream.write(fillString(Integer.toString(field.value.length), (byte) '0', 2, true).getBytes());
			stream.write(field.value);
			break;
		case AV3: // 3位变长，3位十进制字串长度+数据
		case BINA3:
			stream.write(fillString(Integer.toString(field.value.length), (byte) '0', 3, true).getBytes());
			stream.write(field.value);
			break;
		case BCD: // BCD码，主要用于整数转换，长度不足前补\x0
			tmp = asciiToBCD(field.value, 0, field.value.length, field.fillchar, field.alignleft);
			stream.write(tmp);
			break;
		case BV2: // 2位变长，2位十进制字串长度+BCD数据
			tmp = asciiToBCD(field.value);
			stream.write(fillString(Integer.toString(tmp.length), (byte) '0', 2, true).getBytes());
			stream.write(tmp);
			break;
		case BV3: // 3位变长，3位十进制字串长度+BCD数据
			tmp = asciiToBCD(field.value);
			stream.write(fillString(Integer.toString(tmp.length), (byte) '0', 3, true).getBytes());
			stream.write(tmp);
			break;
		case BAV2: // 2位变长，2位BCD长度+数据
		case BINB2:
			tmp1 = asciiToBCD(Integer.toString(field.value.length).getBytes()); // 长度转换成BCD

			// if(tmp1.length == 1) stream.write("\0".getBytes()); // 前补0
			stream.write(tmp1); // 写BCD长度
			stream.write(field.value);
			break;
		case BAV3: // 3位变长，3位BCD长度+数据
		case BINB3:
			tmp1 = asciiToBCD(Integer.toString(field.value.length).getBytes()); // 长度转换成BCD
			if (tmp1.length == 1)
				stream.write("\0".getBytes()); // 前补0
			else if (tmp1.length == 0)
				stream.write("\0\0".getBytes()); // 前补0
			stream.write(tmp1); // 写BCD长度
			stream.write(field.value);
			break;
		case BAV4:
			tmp1 = asciiToBCD(Integer.toString(field.value.length).getBytes()); // 长度转换成BCD
			if (tmp1.length == 1)
				stream.write("\0".getBytes()); // 前补0
			else if (tmp1.length == 0)
				stream.write("\0\0".getBytes()); // 前补0
			stream.write(tmp1); // 写BCD长度
			stream.write(field.value);
			break;

		case BBV2: // 2位变长，2位BCD长度+BCD数据
			tmp = asciiToBCD(new String(field.value).getBytes(), 0, field.value.length, field.fillchar,
					field.alignleft); // 数据转换成BCD
			tmp1 = asciiToBCD(Integer.toString(field.value.length).getBytes()); // 长度转换成BCD

			stream.write(tmp1); // 写BCD长度
			stream.write(tmp); // 写BCD数据
			break;
		case BBV3: // 3位变长，3位BCD长度+BCD数据
			// 数据转换成BCD
			tmp = asciiToBCD(field.value, 0, field.value.length, field.fillchar, field.alignleft);
			tmp1 = asciiToBCD(Integer.toString(field.value.length).getBytes()); // 长度转换成BCD
			if (tmp1.length == 1)
				stream.write("\0".getBytes()); // 前补0
			else if (tmp1.length == 0)
				stream.write("\0\0".getBytes()); // 前补0
			stream.write(tmp1); // 写BCD长度
			stream.write(tmp); // 写BCD数据
			break;

		default:
			throw new EIso8583Error("不支持的数据格式：" + Integer.toString(field.encodeIndex));
		}
	}

	protected void decodeField(Iso8583Field field, InputStream stream) throws EIso8583Error, IOException {

		byte[] tmp;
		int itmp;
		switch (field.decodeIndex) {
		case SRC: // 值不变
		case STR: // 后补空
		case BIN:
			field.value = new byte[field.length];
			readstream(stream, field.value);

			break;
		case AV2: // 2位变长，2位十进制字串长度+数据
		case BINA2:
			tmp = new byte[2];
			readstream(stream, tmp);
			itmp = Integer.parseInt(new String(tmp).trim());
			field.value = new byte[itmp];
			readstream(stream, field.value);
			break;
		case AV3: // 3位变长，3位十进制字串长度+数据
		case BINA3:
			tmp = new byte[3];
			readstream(stream, tmp);
			itmp = Integer.parseInt(new String(tmp).trim());
			field.value = new byte[itmp];
			readstream(stream, field.value);
			break;
		case BCD: // BCD码，
			field.value = new byte[field.length / 2 + field.length % 2];
			readstream(stream, field.value);
			field.value = bcdToAscii(field.value);
			break;
		case BV2: // 2位变长，2位十进制字串长度+BCD数据
			tmp = new byte[2];
			readstream(stream, tmp);
			itmp = Integer.parseInt(new String(tmp));
			field.value = new byte[itmp];
			readstream(stream, field.value);
			field.value = bcdToAscii(field.value);
			break;
		case BV3: // 3位变长，3位十进制字串长度+BCD数据
			tmp = new byte[3];
			readstream(stream, tmp);
			itmp = Integer.parseInt(new String(tmp));
			field.value = new byte[itmp];
			readstream(stream, field.value);
			field.value = bcdToAscii(field.value);
			break;
		case BAV2: // 2位变长，2位BCD长度+数据
		case BINB2:
			tmp = new byte[1];
			readstream(stream, tmp);
			itmp = Integer.parseInt(new String(bcdToAscii(tmp)));
			field.value = new byte[itmp];
			readstream(stream, field.value);
			break;
		case BAV3: // 3位变长，3位BCD长度+数据
		case BINB3:
			tmp = new byte[2];
			readstream(stream, tmp);
			itmp = Integer.parseInt(new String(bcdToAscii(tmp)));
			field.value = new byte[itmp];
			readstream(stream, field.value);
			break;
		case BAV4:
			tmp = new byte[2];
			readstream(stream, tmp);
			itmp = Integer.parseInt(new String(bcdToAscii(tmp)));
			field.value = new byte[itmp];
			readstream(stream, field.value);
			break;

		case BBV2: // 2位变长，2位BCD长度+BCD数据
			tmp = new byte[1];
			readstream(stream, tmp);
			itmp = Integer.parseInt(new String(bcdToAscii(tmp)));
			field.value = new byte[itmp / 2 + itmp % 2];
			readstream(stream, field.value);
			field.value = bcdToAscii(field.value);
			if (itmp % 2 != 0) {
				if (field.alignleft)
					field.value = (new String(field.value)).substring(0, itmp).getBytes();
			}
			break;
		case BBV3: // 3位变长，3位BCD长度+BCD数据
			tmp = new byte[2];
			readstream(stream, tmp);
			itmp = Integer.parseInt(new String(bcdToAscii(tmp)));
			field.value = new byte[itmp / 2 + itmp % 2];
			readstream(stream, field.value);
			field.value = bcdToAscii(field.value);
			if (itmp % 2 != 0) {
				if (field.alignleft)
					field.value = (new String(field.value)).substring(0, itmp).getBytes();
			}

			break;
		default:
			throw new EIso8583Error("不支持的数据格式：" + Integer.toString(field.decodeIndex));
		}
	}

	protected void decodeField(Iso8583Field field, InputStream stream, OutputStream log_stream)
			throws EIso8583Error, IOException {

		// System.out.println("Decode Field No."+field.decodeIndex);
		byte[] tmp;
		int itmp;
		switch (field.decodeIndex) {
		case SRC: // 值不变
		case STR: // 后补空
		case BIN:
			field.value = new byte[field.length];
			readstream(stream, field.value);
			log_stream.write(field.value);
			break;
		case AV2: // 2位变长，2位十进制字串长度+数据
		case BINA2:
			tmp = new byte[2];
			readstream(stream, tmp);
			log_stream.write(tmp);
			itmp = Integer.parseInt(new String(tmp).trim());
			field.value = new byte[itmp];
			readstream(stream, field.value);
			log_stream.write(field.value);
			break;
		case AV3: // 3位变长，3位十进制字串长度+数据
		case BINA3:
			tmp = new byte[3];
			readstream(stream, tmp);
			log_stream.write(tmp);
			itmp = Integer.parseInt(new String(tmp).trim());
			field.value = new byte[itmp];
			readstream(stream, field.value);
			log_stream.write(field.value);
			break;
		case BCD: // BCD码，
			field.value = new byte[field.length / 2 + field.length % 2];
			readstream(stream, field.value);
			log_stream.write(field.value);
			field.value = DataUnit.bcdToDec(field.value, 0, field.value.length).getBytes();
			break;
		case BV2: // 2位变长，2位十进制字串长度+BCD数据
			tmp = new byte[2];
			readstream(stream, tmp);
			log_stream.write(tmp);
			itmp = Integer.parseInt(new String(tmp));
			field.value = new byte[itmp];
			readstream(stream, field.value);
			log_stream.write(field.value);
			field.value = DataUnit.bcdToDec(field.value, 0, field.value.length).getBytes();
			break;
		case BV3: // 3位变长，3位十进制字串长度+BCD数据
			tmp = new byte[3];
			readstream(stream, tmp);
			log_stream.write(tmp);
			itmp = Integer.parseInt(new String(tmp));
			field.value = new byte[itmp];
			readstream(stream, field.value);
			log_stream.write(field.value);
			field.value = DataUnit.bcdToDec(field.value, 0, field.value.length).getBytes();
			break;
		case BAV2: // 2位变长，2位BCD长度+数据
		case BINB2:
			tmp = new byte[1];
			readstream(stream, tmp);
			log_stream.write(tmp);
			itmp = Integer.parseInt(new String(DataUnit.bcdToDec(tmp, 0, 1)));
			field.value = new byte[itmp];
			readstream(stream, field.value);
			log_stream.write(field.value);
			break;
		case BAV3: // 3位变长，3位BCD长度+数据
		case BINB3:
			tmp = new byte[2];
			readstream(stream, tmp);
			log_stream.write(tmp);
			itmp = Integer.parseInt(new String(DataUnit.bcdToDec(tmp, 0, 2)));
			field.value = new byte[itmp];
			readstream(stream, field.value);
			log_stream.write(field.value);
			break;
		case BAV4:
			tmp = new byte[2];
			readstream(stream, tmp);
			log_stream.write(tmp);
			itmp = Integer.parseInt(new String(DataUnit.bcdToDec(tmp, 0, 2)));
			field.value = new byte[itmp];
			readstream(stream, field.value);
			log_stream.write(field.value);
			break;

		case BBV2: // 2位变长，2位BCD长度+BCD数据
			tmp = new byte[1];
			readstream(stream, tmp);
			log_stream.write(tmp);
			itmp = Integer.parseInt(new String(DataUnit.bcdToDec(tmp, 0, 1)));
			field.value = new byte[itmp / 2 + itmp % 2];
			readstream(stream, field.value);
			log_stream.write(field.value);
			field.value = DataUnit.bcdToDec(field.value, 0, field.value.length).getBytes();
			break;
		case BBV3: // 3位变长，3位BCD长度+BCD数据
			tmp = new byte[2];
			readstream(stream, tmp);
			log_stream.write(tmp);
			itmp = Integer.parseInt(new String(DataUnit.bcdToDec(tmp, 0, 2)));
			field.value = new byte[itmp / 2 + itmp % 2];
			readstream(stream, field.value);
			log_stream.write(field.value);
			field.value = DataUnit.bcdToDec(field.value, 0, field.value.length).getBytes();
			break;
		default:
			throw new EIso8583Error("不支持的数据格式：" + Integer.toString(field.decodeIndex));
		}
	}

	/**
	 * 填充字串
	 * 
	 * @param src
	 *            要填充的字串
	 * @param ch
	 *            填充的字符
	 * @param len
	 *            填充后的字串长度
	 * @param left
	 *            true - 向左填充,false - 向右填充
	 * @return 转换后的字串
	 * @throws
	 *******************************************************************/
	public byte[] fixedBytes(byte[] src, byte ch, int len, boolean left) {

		byte[] ret = new byte[len];
		if (src == null) {
			for (int i = 0; i < len; i++) {
				ret[i] = ch;
			}
		}
		else if (src.length < len) {
			byte[] b = new byte[len - src.length];
			for (int i = 0; i < b.length; i++) {
				b[i] = ch;
			}
			if (left) {
				System.arraycopy(b, 0, ret, 0, b.length);
				System.arraycopy(src, 0, ret, b.length, src.length);
			}
			else {
				System.arraycopy(src, 0, ret, 0, src.length);
				System.arraycopy(b, 0, ret, src.length, b.length);
			}
		}
		else if (left)
			System.arraycopy(src, src.length - len, ret, 0, len);
		else
			System.arraycopy(src, 0, ret, 0, len);
		return ret;
	}

	public String fillString(String src, byte ch, int len, boolean left) {

		int length = src.getBytes().length;
		if (length < len) {
			byte[] b = new byte[len - length];
			for (int i = 0; i < b.length; i++) {
				b[i] = ch;
			}
			if (left)
				return new String(b) + src;
			else
				return src + new String(b);
		}
		else
			return src;
	}

	/**
	 * ascii转换bcd码
	 * 
	 * @param src
	 * @return
	 */
	static public byte[] asciiToBCD(String src) {

		return asciiToBCD(src, src.length());
	}

	static public byte[] asciiToBCD(String src, int len) {

		while (src.length() < len) {
			src = "0" + src;
		}
		return asciiToBCD(src.getBytes());
	}

	static public byte[] asciiToBCD(byte[] src) {

		return asciiToBCD(src, 0, src.length, (byte) 0, false);
	}

	static public byte[] asciiToBCD(byte[] src, int start, int end, byte fillchar, boolean left) {

		int len = end - start;
		byte[] srctmp = null;
		// 前补fillchar或后补fillchar
		if (len % 2 != 0) {
			srctmp = new byte[len + 1];
			if (left) { // 后补
				System.arraycopy(src, start, srctmp, 0, len);
				srctmp[len] = fillchar;
			}
			else { // 前补
				srctmp[0] = fillchar;
				System.arraycopy(src, start, srctmp, 1, len);
			}
		}
		else {
			srctmp = new byte[len];
			System.arraycopy(src, start, srctmp, 0, len);
		}

		// 计算bcd
		byte dest[] = new byte[srctmp.length / 2];
		for (int i = 0; i < srctmp.length; i += 2) {
			dest[i / 2] = (byte) (((srctmp[i] & 0x0f) << 4) + (srctmp[i + 1] & 0x0f));
		}

		return dest;

	}

	/**
	 * bcd转ascii
	 * 
	 * @param src
	 * @param start
	 * @param end
	 * @return
	 */
	static public byte[] bcdToAscii(byte[] src) {

		return bcdToAscii(src, 0, src.length);
	}

	static public byte[] bcdToAscii(byte[] src, int start, int end) {

		if (start < 0)
			start = 0;
		if (end == 0)
			end = src.length;
		byte[] dest = new byte[(end - start) * 2];

		for (int i = start; i < end; i++) {
			dest[2 * (i - start)] = (byte) (((src[i] >> 4) & 0x0f) + 0x30);
			dest[2 * (i - start) + 1] = (byte) ((src[i] & 0x0f) + 0x30);
		}
		return dest;
	}

}

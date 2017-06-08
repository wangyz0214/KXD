/*
 * DataUtil.java
 * 
 * Created on 2007-11-17, 14:27:06
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author Administrator
 */
public class DataUnit {
	static public void memset(byte[] src, byte ch, int offset, int len) {
		if (len <= 0 || src == null)
			return;
		if (offset < 0)
			offset = 0;
		if (offset + len > src.length)
			return;
		for (int i = offset; i < len; i++)
			src[i] = ch;
	}

	/**
	 * 内存比较
	 * 
	 * @param src
	 * @param srcpos
	 * @param dest
	 * @param destpos
	 * @param len
	 * @return
	 */
	public static int memcmp(byte[] src, int srcpos, byte[] dest, int destpos,
			int len) {
		for (int i = 0; i < len; i++) {
			int si = srcpos + i;
			int di = destpos + i;
			if (si >= src.length) {
				if (di >= dest.length)
					return 0;
				else
					return -1;
			} else if (di >= dest.length)
				return 1;
			int ret = src[si] - dest[di];
			if (ret != 0)
				return ret;
		}
		return 0;
	}

	/**
	 * 将字节转换为无符号数
	 * 
	 * @param b
	 *            byte 字节值
	 * @return short 无符号数
	 */
	static public short byteToUnsigned(byte b) {
		short ret = b;
		if (b < 0)
			ret += 256;
		return ret;
	}

	/**
	 * 二进制字节数组转浮点数,一个浮点数占4个字节,应该保证b.length>=offset+4
	 * 
	 * @param b
	 *            byte[] 要转换二进制字节数组
	 * @param offset
	 *            int 偏移量,即从字节数组的第几个几字开始转换
	 * @return float 转换后的浮点数
	 */
	static public float bytesToFloat(byte[] b, int offset) {
		return Float.intBitsToFloat(bytesToInt(b, offset));
	}

	/**
	 * 浮点数转换为二进制字节数组
	 * 
	 * @param f
	 *            float 要转换浮点数
	 * @return byte[] 转换后的二进制字节数组
	 */
	static public byte[] floatToBytes(float f) {
		return intToBytes(Float.floatToIntBits(f));
	}

	/**
	 * 二进制字节数组转双精度浮点数,一个双精度浮点数占8个字节,应该保证b.length>=offset+8
	 * 
	 * @param b
	 *            byte[] 要转换二进制字节数组
	 * @param offset
	 *            int 偏移量,即从字节数组的第几个几字开始转换
	 * @return float 转换后的双精度浮点数
	 */
	static public double bytesToDouble(byte[] b, int offset) {
		return Double.longBitsToDouble(bytesToLong(b, offset));
	}

	/**
	 * 双精浮点数转换为二进制字节数组
	 * 
	 * @param f
	 *            float 要转换双精浮点数
	 * @return byte[] 转换后的二进制字节数组
	 */
	static public byte[] doubleToBytes(double f) {
		return longToBytes(Double.doubleToLongBits(f));
	}

	/**
	 * 二进制字节数组转长整数,一个长整数占8个字节,由于一个整数可以只占1~8个字节,本函数可以从 要转换的字节数组,提取1~8个字节来转换.
	 * 
	 * @param b
	 *            byte[] 要转换二进制字节数组
	 * @param offset
	 *            int 偏移量,即从字节数组的第几个几字开始转换
	 * @param len
	 *            长度.将多少个字节转换成长整数
	 * @return long 转换后长整数
	 */
	static public long bytesToLong(byte[] b, int offset, int len) {
		long tmp = 0;
		long ret = 0;
		for (int i = 0; i < len; i++) {
			tmp = (b[offset + i] & 0xff);
			tmp = tmp << (i * 8);
			ret |= tmp;
		}
		return ret;
	}

	/**
	 * 二进制字节数组转长整数,一个长整数占8个字节,要确保b.length>=offset+8.
	 * 
	 * @param b
	 *            byte[] 要转换二进制字节数组
	 * @param offset
	 *            int 偏移量,即从字节数组的第几个几字开始转换
	 * @return long 转换后长整数
	 */
	static public long bytesToLong(byte[] b, int offset) {
		return bytesToLong(b, offset, 8);
	}

	/**
	 * 长整数转换成二进制字节数组
	 * 
	 * @param long v 要转换的长整数
	 * @return byte[] 转换后的二进制字节数组
	 */
	static public byte[] longToBytes(long v) {
		byte[] ret = new byte[8];
		for (int i = 0; i < 8; i++) {
			ret[i] = (byte) ((v >> (i * 8)) & 0xff);
		}
		return ret;
	}

	/**
	 * 长整数转换成二进制字节数组,只转换低len位字节
	 * 
	 * @param long v 要转换的长整数
	 * @param len
	 *            转换后的字节长度
	 * @return byte[] 转换后的二进制字节数组
	 */
	static public byte[] longToBytes(long v, int len) {
		byte[] ret = new byte[len];
		for (int i = 0; i < len; i++) {
			ret[i] = (byte) ((v >> (i * 8)) & 0xff);
		}
		return ret;
	}

	/**
	 * 二进制字节数组转换成整数,应确保b.length>=offset+4
	 * 
	 * @param b
	 *            byte[] 字节数组
	 * @param offset
	 *            int 转换偏移量,基于字节数组b
	 * @return long 转换后的整数
	 */
	static public int bytesToInt(byte[] b, int offset) {
		return (int) bytesToLong(b, offset, 4);
	}

	/**
	 * 整数转换成二进制字节数组
	 * 
	 * @param int v 要转换的整数
	 * @return byte[] 转换后的二进制字节数组
	 */
	static public byte[] intToBytes(int v) {
		byte[] ret = new byte[4];
		for (int i = 0; i < 4; i++) {
			ret[i] = (byte) ((v >> (i * 8)) & 0xff);
		}
		return ret;
	}

	/**
	 * 二进制字节数组转换成短整数
	 * 
	 * @param b
	 *            byte[] 二进制字节数组
	 * @param offset
	 *            int 转换偏移量
	 * @return long 转换后的短整数
	 */
	static public short bytesToShort(byte[] b, int offset) {
		return (short) bytesToLong(b, offset, 2);
	}

	/**
	 * 短整数转换成二进制字节数组
	 * 
	 * @param short v 要转换的短整数
	 * @return byte[] 转换后的二进制字节数组
	 */
	static public byte[] shortToBytes(short v) {
		byte[] ret = new byte[2];
		for (int i = 0; i < 2; i++) {
			ret[i] = (byte) ((v >> (i * 8)) & 0xff);
		}
		return ret;
	}

	/**
	 * 字节反相输出,即高位字节和低位字节互换
	 * 
	 * @param b
	 *            byte[] 字节数组
	 * @param offset
	 *            偏移量
	 * @param len
	 *            转换长度
	 * @return byte[] 反相输出后的字节数组
	 */
	static public byte[] bytesReverse(byte[] b, int offset, int len) {
		byte[] ret = new byte[len];
		for (int i = 0; i < len; i++)
			ret[i] = b[len + offset - i - 1];
		return ret;
	}

	/**
	 * 将主机字节的整数转换为网络字节的整数
	 * 
	 * @param b
	 *            int 主机字节的整数
	 * @return int 网络字节的整数
	 */
	static int htonl(int b) {
		return htonl(intToBytes(b), 0);
	}

	/**
	 * 将主机字节的二进制字节数组转换为网络字节的整数,应确保b.length>=offset+4
	 * 
	 * @param b
	 *            int 主机字节数组
	 * @param offset
	 *            int 转换字节数组的偏移量
	 * @return int 网络字节的整数
	 */
	static int htonl(byte[] b, int offset) {
		return bytesToInt(bytesReverse(b, offset, 4), 0);
	}

	/**
	 * 将主机字节的整数转换为网络字节的短整数
	 * 
	 * @param b
	 *            int 主机字节的整数
	 * @return int 网络字节的整数
	 */
	static short htons(short b) {
		return htons(shortToBytes(b), 0);
	}

	/**
	 * 将主机字节的二进制字节数组转换为网络字节的短整数,应确保b.length>=offset+2
	 * 
	 * @param b
	 *            int 主机字节数组
	 * @param offset
	 *            int 转换字节数组的偏移量
	 * @return int 网络字节的整数
	 */
	static short htons(byte[] b, int offset) {
		return bytesToShort(bytesReverse(b, offset, 2), 0);
	}

	/**
	 * 将网络字节的整数转换为主机字节的整数
	 * 
	 * @param b
	 *            int 网络字节的整数
	 * @return int 主机字节的整数
	 */
	static int ntohl(int b) {
		return ntohl(intToBytes(b), 0);
	}

	/**
	 * 将网络字节的二进制字节数组转换为主机字节的整数,应确保b.length>=offset+4
	 * 
	 * @param b
	 *            int 网络字节数组
	 * @param offset
	 *            int 转换字节数组的偏移量
	 * @return int 主机字节的整数
	 */
	static int ntohl(byte[] b, int offset) {
		return bytesToInt(bytesReverse(b, offset, 4), 0);
	}

	/**
	 * 将网络字节的整数转换为主机字节的短整数
	 * 
	 * @param b
	 *            int 网络字节的整数
	 * @return int 主机字节的整数
	 */
	static short ntohs(short b) {
		return ntohs(shortToBytes(b), 0);
	}

	/**
	 * 将网络字节的二进制字节数组转换为主机字节的短整数,应确保b.length>=offset+2
	 * 
	 * @param b
	 *            int 网络字节数组
	 * @param offset
	 *            int 转换字节数组的偏移量
	 * @return int 主机网络字节的整数
	 */
	static short ntohs(byte[] b, int offset) {
		return bytesToShort(bytesReverse(b, offset, 2), 0);
	}

	/**
	 * 字节转换为16进制字符串
	 * 
	 * @param ch
	 *            byte 要转换的字节
	 * @return String 16进制字符串
	 */
	static public String intToHex(int number) {
		return Integer.toHexString(number);
	}

	/**
	 * 字节转换为16进制字符串
	 * 
	 * @param ch
	 *            byte 要转换的字节
	 * @return String 16进制字符串
	 */
	static public String byteToHex(byte ch) {
		String str[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
				"B", "C", "D", "E", "F" };
		return str[ch >> 4 & 0xF] + str[ch & 0xF];
	}

	/**
	 * 二进制字节数组转换为16进制字符串
	 * 
	 * @param ch
	 *            byte[] 二进制字节数组
	 * @return String 16进制字串
	 */
	static public String bytesToHex(byte[] ch) {
		String ret = "";
		for (int i = 0; i < ch.length; i++)
			ret += byteToHex(ch[i]);
		return ret;
	}

	/**
	 * 二进制字节数组转换为16进制字符串
	 * 
	 * @param ch
	 *            byte[] 二进制字节数组
	 * @return String 16进制字串
	 */
	static public String bytesToHex(byte[] ch, int offset, int length) {
		String ret = "";
		for (int i = 0; i < length; i++)
			ret += byteToHex(ch[i + offset]);
		return ret;
	}

	/**
	 * 16进制字串转换为字节
	 * 
	 * @param str
	 *            String 16进制字串
	 * @return byte 转换后的字节
	 */
	static public byte hexToByte(String str) {
		if (str.length() < 2)
			return 0;
		str = str.toUpperCase();
		byte[] bs = str.getBytes();
		byte l = 0, h = 0;
		l = bs[1];
		h = bs[0];
		if (l >= '0' && l <= '9')
			l -= '0';
		else if (l >= 'A' && l <= 'F') {
			l -= 'A';
			l += 10;
		} else
			l = 0;
		if (h >= '0' && h <= '9')
			h -= '0';
		else if (h >= 'A' && h <= 'F') {
			h -= 'A';
			h += 10;
		} else
			h = 0;
		h = (byte) (h << 4);
		return (byte) (h | l);
	}

	/**
	 * 16进制字节符串转换为二进制字节数组
	 * 
	 * @param str
	 *            String 16进制字串
	 * @return byte[] 二进制字节数组
	 */
	static public byte[] hexToBytes(String str) {
		int len = str.length() / 2;
		str = str.toUpperCase();
		byte[] bs = str.getBytes();
		byte[] ret = new byte[len];
		for (int i = 0; i < len * 2; i += 2) {
			byte l = 0, h = 0;
			l = bs[i + 1];
			h = bs[i];
			if (l >= '0' && l <= '9')
				l -= '0';
			else if (l >= 'A' && l <= 'F') {
				l -= 'A';
				l += 10;
			} else
				l = 0;
			if (h >= '0' && h <= '9')
				h -= '0';
			else if (h >= 'A' && h <= 'F') {
				h -= 'A';
				h += 10;
			} else
				h = 0;
			h = (byte) (h << 4);
			ret[i / 2] = (byte) (h | l);
		}
		return ret;
	}

	/**
	 * bcd码转换为10进制字符串
	 * 
	 * @param bcd
	 *            byte[] bcd字节数组
	 * @param len
	 *            int 要转换的长度
	 * @return String 转换后的字符串
	 */
	static public String bcdToDec(byte[] bcd, int offset, int len) {
		if (len <= 0 || len > bcd.length)
			len = bcd.length;
		String ret = "";
		for (int i = 0; i < len; i++) {
			String str = byteToHex(bcd[offset + i]);
			ret += str;
		}
		return ret;
	}

	/**
	 * 10进制字串转换为bcd码
	 * 
	 * @param s
	 *            String 10进制字串
	 * @param len
	 *            int 转换长度
	 * @return byte[] 转换后的字节数组
	 * @throws IOException
	 *             转换失败后抛出
	 */
	static public byte[] decToBcd(String s, int len) throws IOException {
		if ((s.length() % 2) != 0)
			s = "0" + s;
		byte[] bs = s.getBytes();
		if (len <= 0)
			len = bs.length;
		if (len > bs.length)
			len = bs.length;
		len = len / 2;
		byte[] ret = new byte[len];
		for (int i = 0; i < len; i++) {
			byte c = (byte) (bs[i * 2] - '0');
			byte c1 = (byte) (bs[i * 2 + 1] - '0');
			if (c < 0 || c > 9 || c1 > 9 || c1 < 0)
				throw new IOException("不是有效的数字!");
			ret[i] = (byte) (c << 4 | c1);
		}
		return ret;
	}

	/**
	 * 元转换为分
	 * 
	 * @param src
	 *            String 原始字串
	 * @return int 转换后整数,以分为单位
	 */
	static public int yuanToFen(String src) {
		src = src.trim();
		if (src.length() == 0)
			return 0;
		int f = 1;
		if (src.getBytes()[0] == '-') {
			f = -1;
			src = src.substring(1);
		}
		int r = 0;
		if (src.length() > 0) {
			int pos = src.indexOf(".");
			if (pos < 0) {
				r = Integer.parseInt(src);
			} else {
				String s = src.substring(0, pos);
				src = src.substring(pos + 1);
				int len = src.length();
				if (len > 2)
					src = src.substring(0, 2);
				else if (len == 0)
					src = "00";
				else if (len == 1)
					src = src + "0";
				r = Integer.parseInt(s + src);
			}
		}
		return r * f;
	}

	/**
	 * 分转换为元
	 * 
	 * @param src
	 *            int 分
	 * @return String 元
	 */
	static public String fenToYuan(int src) {
		String f = "";
		if (src < 0) {
			f = "-";
			src = -src;
		}
		if (src < 10)
			return f + "0.0" + Integer.toString(src);
		else if (src < 100)
			return f + "0." + Integer.toString(src);
		else {
			int i = src / 100;
			int s = src % 100;
			if (s < 10)
				return f + Integer.toString(i) + ".0" + Integer.toString(s);
			else
				return f + Integer.toString(i) + "." + Integer.toString(s);
		}
	}

	/**
	 * 分转换为元
	 * 
	 * @param src
	 *            int 分
	 * @return String 元
	 */
	static public String fenToYuan(long src) {
		String f = "";
		if (src < 0) {
			f = "-";
			src = -src;
		}
		if (src < 10)
			return f + "0.0" + Long.toString(src);
		else if (src < 100)
			return f + "0." + Long.toString(src);
		else {
			long i = src / 100;
			long s = src % 100;
			if (s < 10)
				return f + Long.toString(i) + ".0" + Long.toString(s);
			else
				return f + Long.toString(i) + "." + Long.toString(s);
		}
	}

	/**
	 * 分转换为元
	 * 
	 * @param srcStr
	 *            String 分
	 * @return String 元
	 */
	static public String fenToYuan(String srcStr) {
		long src = 0;
		srcStr = srcStr.trim();
		if (srcStr.length() > 0)
			src = Long.parseLong(srcStr);
		String f = "";
		if (src < 0) {
			f = "-";
			src = -src;
		}
		if (src < 10)
			return f + "0.0" +  Long.toString(src);
		else if (src < 100)
			return f + "0." +  Long.toString(src);
		else {
			long i = src / 100;
			long s = src % 100;
			if (s < 10)
				return f + Long.toString(i) + ".0" + Long.toString(s);
			else
				return f + Long.toString(i) + "." + Long.toString(s);
		}
	}

	static public String formatNumber(String s, int srcDots, int destDots) {
		if (srcDots < 0)
			srcDots = 0;
		if (destDots < 0)
			destDots = 0;
		s = s.trim();
		String ne = s.startsWith("-") ? "-" : "";
		if (ne.length() > 0)
			s = s.substring(1);
		int index = s.indexOf(".");
		String is = s, fs = "";
		if (index > -1) {
			is = s.substring(0, index);
			fs = s.substring(index + 1);
		}
		int len = srcDots - fs.length();
		if (len > 0) {
			for (int i = 0; i < len; i++) {
				fs += "0";
			}
		} else
			fs = fs.substring(0, srcDots);
		is += fs;
		len = destDots - is.length() + 1;
		for (int i = 0; i < len; i++)
			is = "0" + is;
		if (destDots > 0) {
			len = destDots - is.length() + 1;
			len = is.length() - destDots;
			is = is.substring(0, len) + "." + is.substring(len);
		}
		len = is.length();
		for (index = 0; index < len - 1; index++) {
			char ch = is.charAt(index);
			if (ch != '0' || (ch == '0' && is.charAt(index + 1) != '0'))
				break;
		}
		if (index > 0)
			is = is.substring(index);
		try {
			if (Double.parseDouble(is) == 0)
				ne = "";
		} catch (Throwable e) {
		}
		return ne + is;
	}

	/**
	 * 格式化人民币数据
	 * 
	 * @param s
	 *            String 原始数据
	 * @param srcIsFen
	 *            boolean 原始数据格式.true - 分;false - 元
	 * @return String 转换后的字串,以元为单位
	 */
	static public String formatMoney(String s, boolean srcIsFen) {
		s = s.trim();
		if (s.length() == 0)
			return "0.00";
		String f = "";
		if (s.getBytes()[0] == '-') {
			f = "-";
			s = s.substring(1);
		}
		if (srcIsFen) {
			while (s.length() < 3)
				s = "0" + s;
			return f + s.substring(0, s.length() - 2) + "."
					+ s.substring(s.length() - 2);
		} else {
			int pos = s.indexOf(".");
			if (pos < 0) {
				return f + s + ".00";
			}
			String s1 = s.substring(pos + 1, s.length());
			while (s1.length() < 2)
				s1 += "0";
			if (s1.length() > 2)
				s1 = s1.substring(0, 2);
			s = s.substring(0, pos);
			if (s.length() == 0)
				s = "0";
			return f + s + "." + s1;
		}
	}

	/**
	 * 格式当前时间
	 * 
	 * @param format
	 *            String 时间格式
	 * @return String 时间字串
	 */
	public static String formatCurrentDateTime(String format) {
		SimpleDateFormat f = new SimpleDateFormat(format);
		return f.format(new Date());
	}

	/**
	 * 格式化时间
	 * 
	 * @param time
	 *            long 时间
	 * @param format
	 *            String 时间格式
	 * @return String 时间字串
	 */
	public static String formatDateTime(long time, String format) {
		SimpleDateFormat f = new SimpleDateFormat(format);
		return f.format(new Date(time));
	}

	/**
	 * 格式化时间
	 * 
	 * @param time
	 *            Date 时间
	 * @param format
	 *            String 时间格式
	 * @return String 时间字串
	 */
	public static String formatDateTime(Date time, String format) {
		SimpleDateFormat f = new SimpleDateFormat(format);
		return f.format(time);
	}

	/**
	 * 提取时间
	 * 
	 * @param time
	 *            String 时间字串
	 * @param format
	 *            String 时间格式
	 * @return Date 时间字串
	 * @throws ParseException
	 */
	public static Date parseDateTime(String time, String format)
			throws ParseException {
		SimpleDateFormat f = new SimpleDateFormat(format);
		return f.parse(time);
	}

	public static Date parseDateTimeDef(String time, String format, Date def) {
		try {
			SimpleDateFormat f = new SimpleDateFormat(format);
			return f.parse(time);
		} catch (Throwable e) {
			return def;
		}
	}

	public static String trimRight(String str) {
		int len = str.length();
		for (int i = len - 1; i >= 0; i--) {
			if (str.charAt(i) > ' ')
				return str.substring(0, i + 1);
		}
		return "";
	}

	public static String trimLeft(String str) {
		int len = str.length();
		for (int i = 0; i < len; i++) {
			if (str.charAt(i) > ' ')
				return str.substring(i);
		}
		return "";
	}

	static public boolean isVisibleChar(byte c) {
		switch (c) {
		case '.':
		case ',':
		case '>':
		case '<':
		case '?':
		case '/':
		case '\'':
		case ';':
		case ':':
		case '\"':
		case '\\':
		case '|':
		case ']':
		case '[':
		case '}':
		case '{':
		case '-':
		case '_':
		case '+':
		case '=':
		case ')':
		case '(':
		case '*':
		case '&':
		case '^':
		case '%':
		case '$':
		case '#':
		case '@':
		case '!':
		case '`':
		case '~':
		case ' ':
			return true;
		default:
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
					|| (c >= '0' && c <= '9'))
				return true;
			else if (c < 0)
				return true;
			else
				return false;
		}
	}

	public static String[] formatHex(byte[] data) {
		if (data == null || data.length == 0)
			return new String[0];
		int len = data.length;
		int rows = len / 16 + (len % 16 > 0 ? 1 : 0);
		String[] ret = new String[rows];
		for (int i = 0; i < rows; i++) {
			String str = "";
			byte bb[] = null;
			if (len >= (i + 1) * 16)
				bb = new byte[16];
			else
				bb = new byte[len - i * 16];
			for (int j = 0; j < bb.length; j++) {
				byte b = data[i * 16 + j];
				str += byteToHex(b) + " ";
				if (j == i * 16 + 8)
					str += " ";
				if (!isVisibleChar(b))
					bb[j] = '.';
				else
					bb[j] = b;
			}
			String a = Integer.toHexString(i * 16);
			while (a.length() < 8)
				a = "0" + a;
			while (str.length() < 49)
				str += " ";
			ret[i] = a + "h: " + str + "; " + new String(bb);
		}
		return ret;
	}

	public static Object checkNull(Object src, Object def) {
		if (src == null)
			return def;
		else
			return src;
	}

	/**
	 * 返回字符串src的前len个字串（以字节为单位计数）
	 * 
	 * @param src
	 * @param len
	 * @return
	 */
	public static String checkBytesLength(String src, int len) {
		if (src == null)
			return "";
		if (src.getBytes().length < len)
			return src;
		int l = 0;
		for (int i = 0; i < src.length(); i++) {
			char c = src.charAt(i);
			if (c > 255)
				l += 2;
			else
				l += 1;
			if (l == len)
				return src.substring(0, i + 1);
			else if (l > len)
				return src.substring(0, i);
		}
		return src;
	}

	public static void main(String[] args) {
		System.out.println(checkBytesLength("工要的a工要", 6));
	}
}

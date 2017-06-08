package kxd.util;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class StringUnit {
	static public void main(String args[]) {
		System.out.println(formatMemory(123455));
		System.out.println(formatMemory(1234550000));
		System.out.println(upperRMB(110234L));
		System.out.println(lowerRMB(110234L));
	}

	public static String formatMemory(long size) {
		double m = (double) size / (1024 * 1024);
		if (m < 1024)
			return String.format("%.3f", m) + "m";
		else
			return String.format("%.3f", m / 1024) + "g";
	}

	public static String parseFileUrlPath(String fileUrl) {
		if (fileUrl != null && !fileUrl.isEmpty()) {
			if (fileUrl.toLowerCase().startsWith("file:")) {
				fileUrl = fileUrl.substring(5);
				fileUrl = fileUrl.replace("//", "/");
				if (fileUrl.indexOf(":") > -1 && fileUrl.startsWith("/"))
					fileUrl = fileUrl.substring(1);
			}
		}
		return fileUrl;
	}

	/**
	 * 获取包含环境变量的字符串，${变量名}
	 * 
	 * @param src
	 * @return
	 */
	public static String getAttributeText(String src) {
		char[] b = src.toCharArray();
		StringBuffer buf = new StringBuffer("");
		int markIndex = -1;
		int lastIndex = 0;
		for (int i = 0; i < b.length; i++) {
			char c = b[i];
			if (markIndex > -1) {
				if (c == '}') {
					buf.append(b, lastIndex, markIndex - lastIndex - 2);
					String attr = new String(b, markIndex, i - markIndex);
					buf.append(System.getProperty(attr, ""));
					lastIndex = i + 1;
					markIndex = -1;
				}
			} else if (c == '$') {
				if (b[i + 1] == '{') {
					i++;
					markIndex = i + 1;
				}
			}
		}
		if (lastIndex < b.length) {
			buf.append(b, lastIndex, b.length - lastIndex);
		}
		return buf.toString();
	}

	public static String format(Locale locale, String format, Object... params) {
		Formatter formatter = new Formatter();
		return formatter.format(locale, format, params).toString();
	}

	public static String[] split(String v, String s) {
		if (v == null)
			return new String[0];
		ArrayList<String> ls = new ArrayList<String>();
		split(v, s, ls);
		String[] r = new String[ls.size()];
		for (int i = 0; i < r.length; i++)
			r[i] = ls.get(i);
		ls = null;
		return r;
	}

	public static List<Integer> splitToInt1(String v, String s) {
		ArrayList<Integer> r = new ArrayList<Integer>();
		if (v == null)
			return r;
		ArrayList<String> ls = new ArrayList<String>();
		split(v, s, ls);
		for (int i = 0; i < ls.size(); i++) {
			String str = ls.get(i);
			if (str != null) {
				str = str.trim();
				if (!str.isEmpty())
					r.add(Integer.valueOf(str));
			}
		}
		ls = null;
		return r;
	}

	public static List<Long> splitToLong1(String v, String s) {
		ArrayList<Long> r = new ArrayList<Long>();
		if (v == null)
			return r;
		ArrayList<String> ls = new ArrayList<String>();
		split(v, s, ls);
		for (int i = 0; i < ls.size(); i++) {
			String str = ls.get(i);
			if (str != null) {
				str = str.trim();
				if (!str.isEmpty())
					r.add(Long.valueOf(str));
			}
		}
		ls = null;
		return r;
	}

	public static Integer[] splitToInt2(String v, String s) {
		List<Integer> r = splitToInt1(v, s);
		Integer ri[] = new Integer[r.size()];
		for (int i = 0; i < ri.length; i++)
			ri[i] = r.get(i);
		return ri;
	}

	public static Long[] splitToLong2(String v, String s) {
		List<Long> r = splitToLong1(v, s);
		Long ri[] = new Long[r.size()];
		for (int i = 0; i < ri.length; i++)
			ri[i] = r.get(i);
		return ri;
	}

	public static Short[] splitToShort2(String v, String s) {
		List<Short> r = splitToShort1(v, s);
		Short ri[] = new Short[r.size()];
		for (int i = 0; i < ri.length; i++)
			ri[i] = r.get(i);
		return ri;
	}

	public static List<Short> splitToShort1(String v, String s) {
		ArrayList<Short> r = new ArrayList<Short>();
		if (v == null)
			return r;
		ArrayList<String> ls = new ArrayList<String>();
		split(v, s, ls);
		for (int i = 0; i < ls.size(); i++) {
			String str = ls.get(i);
			if (str != null) {
				str = str.trim();
				if (!str.isEmpty())
					r.add(Short.valueOf(str));
			}
		}
		ls = null;
		return r;
	}

	public static int[] splitToInt(String v, String s) {
		if (v == null)
			return null;
		ArrayList<String> ls = new ArrayList<String>();
		split(v, s, ls);
		ArrayList<Integer> r = new ArrayList<Integer>();
		for (int i = 0; i < ls.size(); i++) {
			String str = ls.get(i);
			if (str != null) {
				str = str.trim();
				if (!str.isEmpty())
					r.add(Integer.valueOf(str));
			}
		}
		ls = null;
		int ri[] = new int[r.size()];
		for (int i = 0; i < ri.length; i++)
			ri[i] = r.get(i);
		return ri;
	}

	public static String listToString(List<?> a) {
		StringBuffer sb = new StringBuffer();
		if (a != null) {
			int i = 0;
			for (Object o : a) {
				if (i > 0)
					sb.append(",");
				sb.append(o.toString());
				i++;
			}
		}
		return sb.toString();
	}

	public static String arrayToString(Object[] a) {
		StringBuffer sb = new StringBuffer();
		if (a != null) {
			int i = 0;
			for (Object o : a) {
				if (i > 0)
					sb.append(",");
				sb.append(o.toString());
				i++;
			}
		}
		return sb.toString();
	}

	public static String arrayToString(int[] a) {
		StringBuffer sb = new StringBuffer();
		if (a != null) {
			int i = 0;
			for (int o : a) {
				if (i > 0)
					sb.append(",");
				sb.append(Integer.toString(o));
				i++;
			}
		}
		return sb.toString();
	}

	public static String arrayToString(short[] a) {
		StringBuffer sb = new StringBuffer();
		if (a != null) {
			int i = 0;
			for (int o : a) {
				if (i > 0)
					sb.append(",");
				sb.append(Integer.toString(o));
				i++;
			}
		}
		return sb.toString();
	}

	public static short[] splitToShort(String v, String s) {
		if (v == null)
			return null;
		ArrayList<String> ls = new ArrayList<String>();
		split(v, s, ls);
		ArrayList<Short> r = new ArrayList<Short>();
		for (int i = 0; i < ls.size(); i++) {
			String str = ls.get(i);
			if (str != null) {
				str = str.trim();
				if (!str.isEmpty())
					r.add(Short.valueOf(str));
			}
		}
		ls = null;
		short ri[] = new short[r.size()];
		for (int i = 0; i < ri.length; i++)
			ri[i] = r.get(i);
		return ri;
	}

	public static void split(String v, String s, List<String> ls) {
		int index = v.indexOf(s);
		int size = s.length();
		if (index >= 0) {
			while (index >= 0) {
				ls.add(v.substring(0, index));
				v = v.substring(index + size);
				index = v.indexOf(s);
			}
			ls.add(v);
		} else if (v.length() > 0)
			ls.add(v);
	}

	static public String trimString(String str) {
		if (str == null)
			return null;
		else
			return str.trim();
	}

	static public String getExceptionMessage(Throwable e) {
		if (e == null)
			return "null pointer";
		else {
			if (!(e instanceof KoneException))
				while (e.getCause() != null && !e.getCause().equals(e)) {
					e = e.getCause();
					if (e instanceof KoneException)
						break;
				}
			String ret = e.getLocalizedMessage();
			if (ret == null)
				return "null pointer";
			else
				return ret;
		}
	}

	public static long[] splitToLong(String v, String s) {
		ArrayList<String> ls = new ArrayList<String>();
		split(v, s, ls);
		ArrayList<Long> r = new ArrayList<Long>();
		for (int i = 0; i < ls.size(); i++) {
			String str = ls.get(i);
			if (str != null) {
				str = str.trim();
				if (!str.isEmpty())
					r.add(Long.valueOf(str));
			}
		}
		ls = null;
		long ri[] = new long[r.size()];
		for (int i = 0; i < ri.length; i++)
			ri[i] = r.get(i);
		return ri;
	}

	public static String getStringCheckNull(Object o) {
		if (o == null)
			return "";
		else
			return o.toString();
	}

	/**
	 * 首字大写
	 * 
	 * @param o
	 * @return
	 */
	public static String firstWordCap(String o) {
		if (o != null && o.length() > 0)
			return o.substring(0, 1).toUpperCase() + o.substring(1);
		else
			return o;
	}

	public static String alignString(String src, String align, byte fillChar,
			int length) {
		int len = 0;
		for (int i = 0; i < src.length(); i++) {
			if (src.charAt(i) > 255)
				len += 2;
			else
				len++;
		}
		int leftlen = length - len;
		if (leftlen > 0) {
			if (align.equalsIgnoreCase("left"))
				return src + fillChar(fillChar, leftlen);
			else if (align.equalsIgnoreCase("right"))
				return fillChar(fillChar, leftlen) + src;
			else
				return fillChar(fillChar, leftlen / 2) + src
						+ fillChar(fillChar, leftlen - leftlen / 2);
		} else
			return src;
	}

	public static void wordBreak(String src, String align, byte fillchar,
			int width, int maxLines, List<String> ls) {
		if (src == null)
			src = "";
		String f = Character.toString((char) fillchar), lstr = "", rstr = "";
		int pos = 0, clen = 0, tl;
		for (int i = 0; i < src.length(); i++) {
			if (src.charAt(i) > 255)
				tl = 2;
			else
				tl = 1;
			if (clen + tl > width) {
				if (clen < width)
					ls.add(src.substring(pos, i) + f);
				else
					ls.add(src.substring(pos, i));
				if (maxLines > 0 && ls.size() >= maxLines)
					return;
				pos = i;
				clen = 0;
			}
			clen += tl;
		}
		clen = width - clen;
		if (clen > 0) {
			if (align.equalsIgnoreCase("left")) {
				rstr = fillChar(fillchar, clen);
			} else if (align.equalsIgnoreCase("right")) {
				lstr = fillChar(fillchar, clen);
			} else {
				int l = clen / 2;
				if (l > 0)
					lstr = fillChar(fillchar, l);
				rstr = fillChar(fillchar, clen - l);
			}
		}
		ls.add(lstr + src.substring(pos, src.length()) + rstr);
	}

	public static String fillChar(byte ch, int len) {
		byte b[] = new byte[len];
		DataUnit.memset(b, ch, 0, len);
		return new String(b);
	}

	/**
	 * 人民币大写金额转换
	 * 
	 * @param money
	 *            以分为单位的金额数字
	 * @return 转换后的大写人民币
	 */
	public static String upperRMB(long money) {
		long fen = money % 100; // 分
		long jiao = fen / 10; // 角
		fen %= 10;
		money /= 100;
		if (money > 1000000000000L)
			throw new InvalidParameterException("参数不能大于1万亿");
		char[] ch = { '零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖' };
		String[] radices = { "", "拾", "佰", "仟" };
		String[] bigRadices = { "", "万", "亿" };
		int zeroCount = 0;
		String ret = "";
		char a[] = Long.toString(money).toCharArray();
		for (int i = 0; i < a.length; i++) {
			int p = a.length - i - 1;
			char d = a[i];
			int quotient = p / 4;
			int modulus = p % 4;
			if (d == '0') {
				zeroCount++;
			} else {
				if (zeroCount > 0) {
					ret += ch[0];
				}
				zeroCount = 0;
				ret += ch[d - '0'] + radices[modulus];
			}
			if (modulus == 0 && zeroCount < 4) {
				ret += bigRadices[quotient];
			}
		}
		if (ret.length() > 0)
			ret += "元";
		if (jiao == 0 && fen == 0) {
			if (ret.length() == 0)
				ret = "零元整";
			else
				ret += "整";
		} else {
			if (jiao > 0) {
				ret += ch[(int) jiao] + "角";
			}
			if (fen > 0) {
				if (ret.length() > 0 && jiao == 0)
					ret += "零";
				ret += ch[(int) fen] + "分";
			}
		}
		return ret;
	}

	public static String lowerRMB(long number) {
		String str = Long.toString(number);
		if (str.length() == 1) {
			return "0.0" + str;
		} else if (str.length() == 2) {
			return "0." + str;
		} else {
			String f = str.substring(str.length() - 2, str.length());
			char[] a = str.substring(0, str.length() - 2).toCharArray();
			String ret = "";
			for (int i = 0; i < a.length; i++) {
				if ((a.length - i) % 3 == 0 && i > 0)
					ret += ",";
				ret += a[i];
			}
			return ret + "." + f;
		}
	}
}

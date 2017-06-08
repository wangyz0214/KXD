package kxd.util.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import kxd.util.DataUnit;
import kxd.util.DateTime;

abstract public class AbstractStream {
	/**
	 * 写一次数据
	 * 
	 * @param offset
	 *            偏移量
	 * @param length
	 *            写数据的长度
	 * @param data
	 *            数据缓冲区
	 * @param timeout
	 *            超时时间。以秒为单位
	 * @return int 本次真正写入的数据长度
	 * @throws SerialError
	 */
	abstract public int writeOne(int offset, int length, byte[] data,
			int timeout) throws IOException;

	/**
	 * 读一次数据
	 * 
	 * @param offset
	 *            偏移量
	 * @param length
	 *            读数据的长度
	 * @param data
	 *            数据缓冲区
	 * @param timeout
	 *            超时时间。以秒为单位
	 * @return int 真正读出的数据长度
	 * @throws SerialError
	 */
	abstract public int readOne(int offset, int maxcount, byte[] data,
			int timeout) throws IOException;

	/**
	 * 关闭数据
	 * 
	 */
	abstract public void close() throws IOException;

	/**
	 * 获取数据大小，某些继承类无用，可直接抛出异常
	 * 
	 */
	abstract public int getSize() throws IOException;

	protected int doRead(int offset, int maxcount, byte[] data, int timeout)
			throws IOException {
		return this.readOne(offset, maxcount, data, timeout);
	}

	protected int doWrite(int offset, int maxcount, byte[] data, int timeout)
			throws IOException {
		return writeOne(offset, maxcount, data, timeout);
	}

	/**
	 * 写固定长度的数据
	 * 
	 * @param offset
	 *            int 偏移量,从data的第0个字节开始计数
	 * @param length
	 *            int 写数据的长度
	 * @param data
	 *            byte[] 需要写的数据缓冲区
	 * @param timeout
	 *            int 超时时间,毫秒为单位
	 * @throws DriverError
	 *             写数据失败和超时时抛出
	 */
	public void write(int offset, int length, byte[] data, int timeout)
			throws IOException {
		long prev = System.currentTimeMillis();
		int leftLen = length;
		while (leftLen > 0 && (System.currentTimeMillis() - prev) < timeout) {
			int len = doWrite(offset + length - leftLen, leftLen, data, timeout
					- (int) (System.currentTimeMillis() - prev));
			if (len > 0) {
				prev = System.currentTimeMillis() - timeout + 1000; // 多给1秒钟
				leftLen -= len;
				if (leftLen <= 0)
					break;
			}
		}
		if (leftLen > 0) {
			throw new IOException("写数据超时");
		}
	}

	/**
	 * 向流写固定长度的数据
	 * 
	 * @param data
	 *            byte[] 需要写的数据缓冲区
	 * @param timeout
	 *            int 超时时间,毫秒为单位
	 * @throws IOException
	 *             写数据失败和超时时抛出
	 */
	public void write(byte[] data, int timeout) throws IOException {
		if (data == null || data.length == 0)
			return;
		write(0, data.length, data, timeout);
	}

	/**
	 * 向流写字符串
	 * 
	 * @param data
	 *            String 需要写的数据
	 * @param timeout
	 *            int 超时时间,毫秒为单位
	 * @throws IOException
	 *             写数据失败和超时时抛出
	 */
	public void writeString(String data, int timeout) throws IOException {
		if (data == null)
			return;
		byte[] b = data.getBytes();
		write(0, b.length, b, timeout);
	}

	/**
	 * 写一行数据
	 * 
	 * @param data
	 *            byte[] 要写的数据
	 * @param eof
	 *            String 换行符
	 * @param timeout
	 *            int 超时时间,单位为毫秒
	 * @throws IOException
	 *             写数据失败或超时时抛出
	 */
	public void writeln(byte[] data, String eof, int timeout)
			throws IOException {
		if (data != null)
			write(data, timeout);
		write(eof.getBytes(), timeout);
	}

	/**
	 * 写一行数据,即写入data和回车换行符
	 * 
	 * @param data
	 *            byte[] 要写的数据
	 * @param timeout
	 *            int 超时时间,单位为毫秒
	 * @throws IOException
	 *             写数据失败或超时时抛出
	 */
	public void writeln(byte[] data, int timeout) throws IOException {
		if (data != null)
			write(data, timeout);
		write("\r\n".getBytes(), timeout);
	}

	/**
	 * 写一行数据,即写入data和回车换行符
	 * 
	 * @param data
	 *            byte[] 要写的数据
	 * @param timeout
	 *            int 超时时间,单位为毫秒
	 * @throws IOException
	 *             写数据失败或超时时抛出
	 */
	public void writeln(String data, int timeout) throws IOException {
		if (data != null)
			writeString(data, timeout);
		writeString("\r\n", timeout);
	}

	/**
	 * 向流写入一整数
	 * 
	 * @param src
	 *            int 要写入的整数
	 * @param convert
	 *            是否高低位转换
	 * @param timeout
	 *            int 超时时间,单位为毫秒
	 * @throws IOException
	 *             写数据失败或超时时抛出
	 */
	public void writeInt(int src, boolean convert, int timeout)
			throws IOException {
		if (convert)
			write(DataUnit.bytesReverse(DataUnit.intToBytes(src), 0, 4),
					timeout);
		else
			write(DataUnit.intToBytes(src), timeout);
	}

	/**
	 * 向流写入一短整数
	 * 
	 * @param src
	 *            short 要写入的短整数
	 * @param convert
	 *            是否高低位转换
	 * @param timeout
	 *            int 超时时间,单位为毫秒
	 * @throws IOException
	 *             写数据失败或超时时抛出
	 */
	public void writeShort(short src, boolean convert, int timeout)
			throws IOException {
		if (convert)
			write(DataUnit.bytesReverse(DataUnit.shortToBytes(src), 0, 2),
					timeout);
		else
			write(DataUnit.shortToBytes(src), timeout);
	}

	/**
	 * 向流写入一长整数
	 * 
	 * @param src
	 *            long 要写入的长整数
	 * @param timeout
	 *            int 超时时间,单位为毫秒
	 * @throws IOException
	 *             写数据失败或超时时抛出
	 */
	public void writeLong(long src, int timeout) throws IOException {
		write(DataUnit.longToBytes(src), timeout);
	}

	/**
	 * 向流写入一浮点数
	 * 
	 * @param src
	 *            float 要写入的浮点数
	 * @param timeout
	 *            int 超时时间,单位为毫秒
	 * @throws IOException
	 *             写数据失败或超时时抛出
	 */
	public void writeFloat(float src, int timeout) throws IOException {
		write(DataUnit.floatToBytes(src), timeout);
	}

	/**
	 * 向流写入一双精度浮点数
	 * 
	 * @param src
	 *            double 要写入的双精度浮点数
	 * @param timeout
	 *            int 超时时间,单位为毫秒
	 * @throws IOException
	 *             写数据失败或超时时抛出
	 */
	public void writeDouble(double src, int timeout) throws IOException {
		write(DataUnit.doubleToBytes(src), timeout);
	}

	/**
	 * 向流写入一字节
	 * 
	 * @param src
	 *            byte 要写入的字节
	 * @param timeout
	 *            int 超时时间,单位为毫秒
	 * @throws IOException
	 *             写数据失败或超时时抛出
	 */
	public void writeByte(byte src, int timeout) throws IOException {
		byte[] b = new byte[1];
		b[0] = src;
		write(b, timeout);
	}

	/**
	 * 写流写入一用包长打好包的数据,包长为短整数,2字节
	 * 
	 * @param b
	 *            byte[] 要写的数据
	 * @param convert
	 *            boolean 包头是否完成高低位转换
	 * @param timeout
	 *            int 超时时间,单位为毫秒
	 * @throws IOException
	 *             写数据失败或超时时抛出
	 */
	public void writePacketShort(byte[] b, int offset, int len,
			boolean convert, int timeout) throws IOException {
		if (b == null)
			b = new byte[0];
		if (b.length > 65535)
			throw new IOException("要写的数据太长");
		writeShort((short) b.length, convert, timeout);
		write(offset, len, b, timeout);
	}

	public void writePacketShort(byte[] b, int offset, int len, int timeout)
			throws IOException {
		writePacketShort(b, offset, len, false, timeout);
	}

	public void writePacketShort(byte[] b, boolean convert, int timeout)
			throws IOException {
		if (b == null)
			b = new byte[0];
		if (b.length > 65535)
			throw new IOException("要写的数据太长");
		writeShort((short) b.length, convert, timeout);
		write(b, timeout);
	}

	public void writePacketShort(byte[] b, int timeout) throws IOException {
		writePacketShort(b, false, timeout);
	}

	/**
	 * 写流写入一用包长打好包的数据,包长为整数,4字节
	 * 
	 * @param b
	 *            byte[] 要写的数据
	 * @param convert
	 *            boolean 包头是否完成高低位转换
	 * @param timeout
	 *            int 超时时间,单位为毫秒
	 * @throws IOException
	 *             写数据失败或超时时抛出
	 */
	public void writePacketInt(byte[] b, boolean convert, int timeout)
			throws IOException {
		if (b == null)
			b = new byte[0];
		writeInt(b.length, convert, timeout);
		write(b, timeout);
	}

	public void writePacketInt(byte[] b, int timeout) throws IOException {
		writePacketInt(b, false, timeout);
	}

	public void writePacketInt(byte[] b, int offset, int len, boolean convert,
			int timeout) throws IOException {
		if (b == null)
			b = new byte[0];
		writeInt(b.length, convert, timeout);
		write(offset, len, b, timeout);
	}

	public void writePacketInt(byte[] b, int offset, int len, int timeout)
			throws IOException {
		writePacketInt(b, offset, len, false, timeout);
	}

	/**
	 * 写流写入一用包长打好包的数据,包长1字节
	 * 
	 * @param b
	 *            byte[] 要写的数据
	 * @param timeout
	 *            int 超时时间,单位为毫秒
	 * @throws IOException
	 *             写数据失败或超时时抛出
	 */
	public void writePacketByte(byte[] b, int timeout) throws IOException {
		if (b == null)
			b = new byte[0];
		if (b.length > 255)
			throw new IOException("要写的数据太长");
		writeByte((byte) b.length, timeout);
		write(b, timeout);
	}

	public void writePacketByte(byte[] b, int offset, int len, int timeout)
			throws IOException {
		if (b == null)
			b = new byte[0];
		if (b.length > 255)
			throw new IOException("要写的数据太长");
		writeByte((byte) b.length, timeout);
		write(offset, len, b, timeout);
	}

	/**
	 * 写流写入一用包长打好包的数据,包长为短整数,2字节
	 * 
	 * @param b
	 *            byte[] 要写的数据
	 * @param convert
	 *            boolean 包头是否完成高低位转换
	 * @param timeout
	 *            int 超时时间,单位为毫秒
	 * @throws IOException
	 *             写数据失败或超时时抛出
	 */
	public void writePacketShortString(String b, boolean convert, int timeout)
			throws IOException {
		if (b == null)
			b = "";
		writePacketShort(b.getBytes(), convert, timeout);
	}

	/**
	 * 写流写入一用包长打好包的数据,包长为整数,4字节
	 * 
	 * @param b
	 *            byte[] 要写的数据
	 * @param convert
	 *            boolean 包头是否完成高低位转换
	 * @param timeout
	 *            int 超时时间,单位为毫秒
	 * @throws IOException
	 *             写数据失败或超时时抛出
	 */
	public void writePacketIntString(String b, boolean convert, int timeout)
			throws IOException {
		if (b == null)
			b = "";
		writePacketInt(b.getBytes(), convert, timeout);
	}

	/**
	 * 写流写入一用包长打好包的数据,包长1字节
	 * 
	 * @param b
	 *            byte[] 要写的数据
	 * @param timeout
	 *            int 超时时间,单位为毫秒
	 * @throws IOException
	 *             写数据失败或超时时抛出
	 */
	public void writePacketByteString(String b, int timeout) throws IOException {
		if (b == null)
			b = "";
		writePacketByte(b.getBytes(), timeout);
	}

	/**
	 * 向流读指定长度的数据
	 * 
	 * @param offset
	 *            int 偏移量,从data的第0个字节开始计数
	 * @param length
	 *            int 读取数据的最大长度
	 * @param data
	 *            byte[] 需要读的数据缓冲区
	 * @param timeout
	 *            int 超时时间,毫秒为单位
	 * @return int 成功读数据的长度.
	 * @throws IOException
	 *             读数据失败和超时时抛出
	 */
	public void read(int offset, int length, byte[] data, int timeout)
			throws IOException {
		long prev = System.currentTimeMillis();
		int leftLen = length;
		while (leftLen > 0 && (System.currentTimeMillis() - prev) < timeout) {
			int len = doRead(offset + length - leftLen, leftLen, data, timeout
					- (int) (System.currentTimeMillis() - prev));
			if (len > 0) {
				prev = System.currentTimeMillis() - timeout + 1000; // 多给1秒钟
				leftLen -= len;
				if (leftLen <= 0)
					break;
			}
		}
		if (leftLen > 0) {
			throw new IOException("读数据超时");
		}
	}

	/**
	 * 读取指定长度的数据
	 * 
	 * @param length
	 *            int 需要读取的长度
	 * @param timeout
	 *            int 超时时间,毫秒为单位
	 * @return byte[] 返回读取的数据
	 * @throws IOException
	 *             读数据失败和超时时抛出
	 */
	public byte[] read(int length, int timeout) throws IOException {
		byte[] ret = new byte[length];
		read(0, length, ret, timeout);
		return ret;
	}

	/**
	 * 从流读取一整数
	 * 
	 * @param convert
	 *            boolean 是否完成高低位转换
	 * @param timeout
	 *            int 超时时间,毫秒为单位
	 * @return int 读取的整数
	 * @throws IOException
	 *             读数据失败和超时时抛出
	 */
	public int readInt(boolean convert, int timeout) throws IOException {
		if (convert)
			return DataUnit.bytesToInt(DataUnit.bytesReverse(read(4, timeout),
					0, 4), 0);
		else
			return DataUnit.bytesToInt(read(4, timeout), 0);
	}

	/**
	 * 从流读取一短整数
	 * 
	 * @param convert
	 *            boolean 是否完成高低位转换
	 * @param timeout
	 *            int 超时时间,毫秒为单位
	 * @return int 读取的短整数
	 * @throws IOException
	 *             读数据失败和超时时抛出
	 */
	public short readShort(boolean convert, int timeout) throws IOException {
		if (convert)
			return DataUnit.bytesToShort(DataUnit.bytesReverse(
					read(2, timeout), 0, 2), 0);
		else
			return DataUnit.bytesToShort(read(2, timeout), 0);
	}

	/**
	 * 从流读取一长整数
	 * 
	 * @param timeout
	 *            int 超时时间,毫秒为单位
	 * @return long 读取的长整数
	 * @throws IOException
	 *             读数据失败和超时时抛出
	 */
	public long readLong(int timeout) throws IOException {
		return DataUnit.bytesToLong(read(8, timeout), 0);
	}

	/**
	 * 从流读取一浮点数
	 * 
	 * @param timeout
	 *            int 超时时间,毫秒为单位
	 * @return long 读取的浮点数
	 * @throws IOException
	 *             读数据失败和超时时抛出
	 */
	public float readFloat(int timeout) throws IOException {
		return DataUnit.bytesToFloat(read(4, timeout), 0);
	}

	/**
	 * 从流读取一双精度浮点数
	 * 
	 * @param timeout
	 *            int 超时时间,毫秒为单位
	 * @return long 读取的双精度浮点数
	 * @throws IOException
	 *             读数据失败和超时时抛出
	 */
	public double readDouble(int timeout) throws IOException {
		return DataUnit.bytesToDouble(read(8, timeout), 0);
	}

	/**
	 * 读取一字符
	 * 
	 * @param timeout
	 *            int 超时时间,毫秒为单位
	 * @return long 读取的字符
	 * @throws IOException
	 *             读数据失败和超时时抛出
	 */
	public byte readByte(int timeout) throws IOException {
		return read(1, timeout)[0];
	}

	/**
	 * 读入一字符串
	 * 
	 * @param src
	 *            String 要写入的字串
	 * @param timeout
	 *            int 超时时间,单位为毫秒
	 * @throws IOException
	 *             写数据失败或超时时抛出
	 */
	public String readString(int len, int timeout) throws IOException {
		return new String(read(len, timeout));
	}

	public void skipln(int timeout) throws IOException {
		byte[] b = new byte[1];
		byte r = 0, n = 0;
		while (true) {
			read(0, 1, b, timeout);
			r = n;
			n = b[0];
			if (r == '\r' && n == '\n')
				return;
		}
	}

	/**
	 * 读取一行数据
	 * 
	 * @param eof
	 *            byte[] 行结束符
	 * @param return_include_eof
	 *            boolean 返回数据中是否包含行结束符
	 * @param timeout
	 *            int 超时时间,毫秒为单位
	 * @return byte[] 读取的数据,含结束符
	 * @throws IOException
	 *             读数据失败和超时时抛出
	 */
	public byte[] readln(byte[] eof, boolean return_include_eof, int timeout)
			throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			stream.write(read(eof.length, timeout));
			long prev = System.currentTimeMillis();
			while ((System.currentTimeMillis() - prev) < timeout) {
				byte[] b = stream.toByteArray();
				if (DataUnit.memcmp(b, b.length - eof.length, eof, 0,
						eof.length) == 0) {
					int len = b.length;
					if (!return_include_eof)
						len -= eof.length;
					byte[] ret = new byte[len];
					System.arraycopy(b, 0, ret, 0, len);
					return ret;
				}
				stream.write(read(1, timeout));
			}
		} catch (IOException e) {
			throw new IOException("系统故障");
		}
		throw new IOException("读数据超时");
	}

	/**
	 * 读取一行数据,以回车换行符为结束符
	 * 
	 * @param timeout
	 *            int 超时时间,毫秒为单位
	 * @param return_include_eof
	 *            boolean 返回数据中是否包含行结束符
	 * @return byte[] 读取的数据,含结束符
	 * @throws IOException
	 *             读数据失败和超时时抛出
	 */
	public byte[] readln(boolean return_include_eof, int timeout)
			throws IOException {
		return readln("\r\n".getBytes(), return_include_eof, timeout);
	}

	/**
	 * 读取一行数据
	 * 
	 * @param eof
	 *            byte 行结束符
	 * @param return_include_eof
	 *            boolean 返回数据中是否包含行结束符
	 * @param timeout
	 *            int 超时时间,毫秒为单位
	 * @return byte[] 读取的数据,含结束符
	 * @throws IOException
	 *             读数据失败和超时时抛出
	 */
	public byte[] readln(byte eof, boolean return_include_eof, int timeout)
			throws IOException {
		return readln(new byte[] { eof }, return_include_eof, timeout);
	}

	/**
	 * 读取一行数据.行结束符可以eofs中定义的任何一个
	 * 
	 * @param eofs
	 *            byte[][] 行结束符列表
	 * @param out_eof_index
	 *            int[1] 传出当前的行结束符索引
	 * @param return_include_eof
	 *            boolean 返回数据中是否包含行结束符
	 * @param timeout
	 *            int 超时时间,毫秒为单位
	 * @return byte[] 读到的数据
	 * @throws IOException
	 *             读数据失败和超时时抛出
	 */
	public byte[] readln(byte[][] eofs, int[] out_eof_index,
			boolean return_include_eof, int timeout) throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			stream.write(read(1, timeout));
			long prev = System.currentTimeMillis();
			while ((System.currentTimeMillis() - prev) < timeout) {
				byte[] b = stream.toByteArray();
				for (int i = 0; i < eofs.length; i++) {
					byte[] eof = eofs[i];
					if (b.length >= eof.length
							&& DataUnit.memcmp(b, b.length - eof.length, eof,
									0, eof.length) == 0) {
						int len = b.length;
						if (!return_include_eof)
							len -= eof.length;
						byte[] ret = new byte[len];
						System.arraycopy(b, 0, ret, 0, len);
						out_eof_index[0] = i;
						return ret;
					}
				}
				stream.write(read(1, timeout));
			}
		} catch (IOException e) {
			throw new IOException("系统故障");
		}
		throw new IOException("读数据超时");
	}

	/**
	 * 从串中读取一段打包数据,包长为短整数,2字节
	 * 
	 * @param convert
	 *            boolean 转换包长域
	 * @param timeout
	 *            int 超时时间,毫秒为单位
	 * @return byte[] 读取的数据
	 * @throws IOException
	 *             读数据失败和超时时抛出
	 */
	public byte[] readPacketShort(boolean convert, int timeout)
			throws IOException {
		int len = readShort(convert, timeout);
		if (len < 0 || len > 100000)
			throw new IOException("数据格式错误");
		return read(len, timeout);
	}

	/**
	 * 从串中读取一段打包数据,包长为整数,4字节
	 * 
	 * @param convert
	 *            boolean 转换包长域
	 * @param timeout
	 *            int 超时时间,毫秒为单位
	 * @return byte[] 读取的数据
	 * @throws IOException
	 *             读数据失败和超时时抛出
	 */
	public byte[] readPacketInt(boolean convert, int timeout)
			throws IOException {
		int len = readInt(convert, timeout);
		if (len < 0 || len > 10000000)
			throw new IOException("数据格式错误");
		return read(len, timeout);
	}

	/**
	 * 从串中读取一段打包数据,包长为1字节
	 * 
	 * @param timeout
	 *            int 超时时间,毫秒为单位
	 * @return byte[] 读取的数据
	 * @throws IOException
	 *             读数据失败和超时时抛出
	 */
	public byte[] readPacketByte(int timeout) throws IOException {
		int len = DataUnit.byteToUnsigned(readByte(timeout));
		if (len < 0 || len > 255)
			throw new IOException("数据格式错误");
		return read(len, timeout);
	}

	/**
	 * 从串中读取一段打包数据,包长为短整数,2字节
	 * 
	 * @param convert
	 *            boolean 转换包长域
	 * @param timeout
	 *            int 超时时间,毫秒为单位
	 * @return byte[] 读取的数据
	 * @throws IOException
	 *             读数据失败和超时时抛出
	 */
	public String readPacketShortString(boolean convert, int timeout)
			throws IOException {
		return new String(readPacketShort(convert, timeout));
	}

	/**
	 * 从串中读取一段打包数据,包长为整数,4字节
	 * 
	 * @param convert
	 *            boolean 转换包长域
	 * @param timeout
	 *            int 超时时间,毫秒为单位
	 * @return byte[] 读取的数据
	 * @throws IOException
	 *             读数据失败和超时时抛出
	 */
	public String readPacketIntString(boolean convert, int timeout)
			throws IOException {
		return new String(readPacketInt(convert, timeout));
	}

	/**
	 * 从串中读取一段打包数据,包长为1字节
	 * 
	 * @param timeout
	 *            int 超时时间,毫秒为单位
	 * @return byte[] 读取的数据
	 * @throws IOException
	 *             读数据失败和超时时抛出
	 */
	public String readPacketByteString(int timeout) throws IOException {
		return new String(readPacketByte(timeout));
	}

	/**
	 * 从流中读取布尔值
	 */
	public boolean readBoolean(int timeout) throws IOException {
		return readByte(3000) != 0;
	}

	/**
	 * 向流中写入布尔值
	 * 
	 */
	public void writeBoolean(boolean value, int timeout) throws IOException {
		writeByte((byte) (value ? 1 : 0), timeout);
	}

	/**
	 * 从流中读取日期时间，格式是长整型号
	 * 
	 * @param timeout
	 * @return null 负数时返回null<br>
	 *         正常时返回日期对象
	 * @throws IOException
	 * @throws java.text.ParseException
	 */
	public Date readLongDateTime(int timeout) throws IOException {
		long date = readLong(timeout);
		if (date < 0)
			return null;
		else
			return new Date(date);
	}

	/**
	 * 向流中写入长整型的日期
	 * 
	 * @param date
	 *            为null时，写入-1，否则，写入长整型日期
	 * @param timeout
	 * @throws IOException
	 * @throws java.text.ParseException
	 */
	public void writeLongDateTime(Date date, int timeout) throws IOException {
		if (date == null)
			writeLong(-1, timeout);
		else
			writeLong(date.getTime(), timeout);
	}

	public Date readDateTime(int timeout) throws IOException,
			java.text.ParseException {
		String str = readString(23, timeout);
		return new DateTime(str, "yyyy-MM-dd hh:mm:ss,SSS").getTime();

	}

	public Date readTime(int timeout) throws IOException,
			java.text.ParseException {
		String str = readString(12, timeout);
		return new DateTime(str, "hh:mm:ss,SSS").getTime();

	}

	public Date readDate(int timeout) throws IOException,
			java.text.ParseException {
		String str = readString(10, timeout);
		return new DateTime(str, "yyyy-MM-dd").getTime();
	}

	public void writeDateTime(Date date, int timeout) throws IOException {
		writeString(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS")
				.format(date), timeout);
	}

	public void writeDate(Date date, int timeout) throws IOException {
		writeString(new SimpleDateFormat("yyyy-MM-dd").format(date), timeout);
	}

	public void writeTime(Date date, int timeout) throws IOException {
		writeString(new SimpleDateFormat("HH:mm:ss").format(date), timeout);
	}

	public void readByteList(Collection<Byte> ls, int timeout)
			throws IOException {
		int c = readInt(false, timeout);
		for (int i = 0; i < c; i++)
			ls.add(readByte(timeout));
	}

	public void writeByteList(Collection<Byte> list, int timeout)
			throws IOException {
		int c = list.size();
		writeInt(c, false, timeout);
		Iterator<Byte> it = list.iterator();
		while (it.hasNext())
			writeByte(it.next(), timeout);
	}

	public void readShortList(Collection<Short> ls, int timeout)
			throws IOException {
		int c = readInt(false, timeout);
		for (int i = 0; i < c; i++)
			ls.add(readShort(false, timeout));
	}

	public void writeShortList(Collection<Short> list, int timeout)
			throws IOException {
		int c = list.size();
		writeInt(c, false, timeout);
		Iterator<Short> it = list.iterator();
		while (it.hasNext())
			writeShort(it.next(), false, timeout);
	}

	public void readIntList(Collection<Integer> ls, int timeout)
			throws IOException {
		int c = readInt(false, timeout);
		for (int i = 0; i < c; i++)
			ls.add(readInt(false, timeout));
	}

	public void writeIntList(Collection<Integer> list, int timeout)
			throws IOException {
		int c = list.size();
		writeInt(c, false, timeout);
		Iterator<Integer> it = list.iterator();
		while (it.hasNext())
			writeInt(it.next(), false, timeout);
	}

	public void readLongList(Collection<Long> ls, int timeout)
			throws IOException {
		int c = readInt(false, timeout);
		for (int i = 0; i < c; i++)
			ls.add(readLong(timeout));
	}

	public void writeLongList(Collection<Long> list, int timeout)
			throws IOException {
		int c = list.size();
		writeInt(c, false, timeout);
		Iterator<Long> it = list.iterator();
		while (it.hasNext())
			writeLong(it.next(), timeout);
	}
}

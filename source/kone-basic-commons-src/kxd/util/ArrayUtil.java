package kxd.util;

public class ArrayUtil {
	public static <E> boolean contains(E[] a, E[] v) {
		for (E o : v) {
			if (!contains(a, o))
				return false;
		}
		return true;
	}

	public static <E> boolean contains(E[] a, E v) {
		for (E o : a) {
			if (o.equals(v))
				return true;
		}
		return false;
	}

	public static boolean contains(int[] a, int v) {
		for (int o : a) {
			if (o == v)
				return true;
		}
		return false;
	}

	public static boolean contains(int[] a, int[] v) {
		for (int o : v) {
			if (!contains(a, o))
				return false;
		}
		return true;
	}

	public static boolean contains(byte[] a, byte v) {
		for (byte o : a) {
			if (o == v)
				return true;
		}
		return false;
	}

	public static boolean contains(byte[] a, byte[] v) {
		for (byte o : v) {
			if (!contains(a, o))
				return false;
		}
		return true;
	}

	public static boolean contains(short[] a, short v) {
		for (short o : a) {
			if (o == v)
				return true;
		}
		return false;
	}

	public static boolean contains(short[] a, short[] v) {
		for (short o : v) {
			if (!contains(a, o))
				return false;
		}
		return true;
	}

	public static boolean contains(long[] a, long v) {
		for (long o : a) {
			if (o == v)
				return true;
		}
		return false;
	}

	public static boolean contains(long[] a, long[] v) {
		for (long o : v) {
			if (!contains(a, o))
				return false;
		}
		return true;
	}

	public static boolean contains(float[] a, float v) {
		for (float o : a) {
			if (o == v)
				return true;
		}
		return false;
	}

	public static boolean contains(float[] a, float[] v) {
		for (float o : v) {
			if (!contains(a, o))
				return false;
		}
		return true;
	}

	public static boolean contains(double[] a, double v) {
		for (double o : a) {
			if (o == v)
				return true;
		}
		return false;
	}

	public static boolean contains(double[] a, double[] v) {
		for (double o : v) {
			if (!contains(a, o))
				return false;
		}
		return true;
	}

}

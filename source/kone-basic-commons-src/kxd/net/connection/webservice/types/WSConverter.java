package kxd.net.connection.webservice.types;

import java.io.IOException;

public class WSConverter {
	static public <T> String toString(T value) {
		if (value == null)
			return "";
		String r = value.toString();
		if (r == null)
			return "";
		else
			return r;
	}

	@SuppressWarnings("unchecked")
	static public <T> T fromString(String value, Class<T> clazz)
			throws IOException {
		String name = clazz.getSimpleName();
		if (name.equals("Integer"))
			return (T) Integer.valueOf(value);
		else if (name.equals("Float"))
			return (T) Float.valueOf(value);
		else if (name.equals("Double"))
			return (T) Double.valueOf(value);
		else if (name.equals("Short"))
			return (T) Short.valueOf(value);
		else if (name.equals("Boolean"))
			return (T) Boolean.valueOf(value);
		else if (name.equals("Byte"))
			return (T) Byte.valueOf(value);
		else if (name.equals("Long"))
			return (T) Long.valueOf(value);
		else if (name.equals("String"))
			return (T) value;
		else if (name.equals("Float"))
			return (T) Float.valueOf(value);
		else {
			throw new IOException("未知数据类型");
		}
	}

	static public String getStandardTypeName(String wsdlType)
			throws IOException {
		wsdlType = wsdlType.toLowerCase();
		if (wsdlType.equals("string"))
			return "String";
		else if (wsdlType.equals("int") || wsdlType.equals("integer"))
			return "Integer";
		else if (wsdlType.equals("float"))
			return "Float";
		else if (wsdlType.equals("boolean") || wsdlType.equals("bool"))
			return "Boolean";
		else if (wsdlType.equals("long"))
			return "Long";
		else if (wsdlType.equals("double"))
			return "Double";
		else if (wsdlType.equals("byte"))
			return "Byte";
		else if (wsdlType.equals("float"))
			return "Float";
		else if (wsdlType.equals("double"))
			return "Double";
		else
			throw new IOException("错误的类型[" + wsdlType + "]");
	}
}

package kxd.util.iso8583;

public class Iso8583Field {
	
	public int field = 0; 
	
	public int length = 0;
	
	public int encodeIndex = 0;
	
	public int decodeIndex = 0;
	
	public byte[] value = null;
	
	public byte fillchar = 0;
	
	public boolean alignleft = true;

	public Iso8583Field() {
		
	}
}
package com.github.pierrebeucher.cctalk4j.core;

import java.nio.ByteBuffer;

public class Utils {
	
	//private static Logger logger = LoggerFactory.getLogger(Utils.class);

	/**
	 * Array used by {@link #bytesToHex(byte[])} to convert byte value into their hex equivalent
	 */
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	
	private Utils(){}
	
	/**
	 * <p>Calculate the CRC 16 CCIT using the given byte array.</p>
	 * <p>Code inspired from http://introcs.cs.princeton.edu/java/61data/CRC16CCITT.java.html</p>
	 * @param bytes
	 * @return
	 */
	public static short checksumCRC16CCIT(byte[] bytes){
		short crc = 0x000;          // initial value
        int polynomial = 0x1021;   // 0001 0000 0010 0001  (0, 5, 12) 

        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b   >> (7-i) & 1) == 1);
                boolean c15 = ((crc >> 15    & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
            }
        }

        crc &= 0xffff;
        return crc;
	}
	
	/**
	 * <p>Convert the given byte into its unsigned integer equivalent.
	 * Given a byte ranging from 0 to 127, return the same value as int
	 * (e.g. <code>byteToUnsignedInt(42) == (int) 42 </code>).
	 * Given a byte ranging from -128 to -1, return the the byte
	 * value plus 256 (e.g. <code>byteToUnsignedInt(-1) == (int) 255 </code></p>
	 * <p>This function uses the the bitwise and operator to convert
	 * the signed int to an unsigned such as <code>b && 0xFF</code>.
	 * Considering <code>byte b = 127</code> and <code>byte b = -1</code>,
	 * their 8 bits binary representation being respectively <code><u>0</u>111 1111</code>
	 * and <code><u>1</u>111 111</code> (first bit is sign bit). By converting
	 * them using the Java & operator, we obtain the int 32 bit binary equivalent:
	 * <code><u>0</u>000 0000 0000 0000 0000 0000 0111 1111</code> and 
	 * <code><u>0</u>000 0000 0000 0000 0000 0000 1111 1111</code> representing
	 * 127 and 255 as java integer. 
	 * @param b
	 * @return
	 */
	public static int byteToUnsignedInt(byte b){
		return b & 0xFF;
	}
	
	/**
	 * <p>Convert an unsigned int into its byte equivalent.
	 * The given int must be in range [0-255].<p>
	 * <p>This function uses a <code>ByteBuffer</code> to extract
	 * the Least Significant Byte of the given integer, thus
	 * simply doing the extraction. For example,
	 * considering <code>int i = 255</code> (0000 0000 0000 0000 0000 0000 1111 1111 in binary),
	 * we have <code>unsignedIntToByte(i) == (byte) -1</code> (1111 1111 in binary) </p> 
	 * @param i an integer in range [0-255]
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static byte unsignedIntToByte(int i) throws IllegalArgumentException{
		if(i < 0 || i > 255){
			throw new IllegalArgumentException("Integer must be in range [0-255]");
		}
		
		return ByteBuffer.allocate(4).putInt(i).get(3);
	}
	
	/**
	 * Convert an unsigned int array into its byte array equivalent,
	 * bitwise
	 * using {@link #unsignedIntToByte(int)}.
	 * @param intArray int array to convert
	 * @return byte array equivalent
	 */
	public static byte[] unsignedIntsToBytes(int[] intArray){
		byte[] bytes = new byte[intArray.length];
		for(int j=0; j < intArray.length; j++){
			bytes[j] = Utils.unsignedIntToByte(intArray[j]);
		}
		return bytes;
	}
	
	/**
	 * Convert the given byte array into its hexadecimal representation
	 * @param bytes
	 * @return
	 */
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public static String byteToHex(byte b) {
	    return Utils.bytesToHex(new byte[]{ b });
	}
	
	public static String shortToHex(short n){
		return Integer.toHexString(n & 0xffff);
	}
	
	/**
	 * Concat two byte arrays into one
	 * @param a byte array to concat (beginning)
	 * @param b byte array to concat (end)
	 * @return concatened byte array
	 */
	public static byte[] concat(byte[] a, byte[] b) {
	   byte[] result = new byte[a.length+b.length];
	   System.arraycopy(a, 0, result, 0, a.length);
	   System.arraycopy(b, 0, result, a.length, b.length);
	   return result;
	}

}

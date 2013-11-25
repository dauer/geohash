package geohash;

import java.math.BigDecimal;

public class GeoHash {
	
	private static final byte[] characters = {
	  // 0   1   2   3   4   5   6   7
		'0','1','2','3','4','5','6','7',
	  // 8   9  10  11  12  13  14  15
		'8','9','b','c','d','e','f','g',
	  //16  17  18  19  20  21  22  23
		'h','j','k','m','n','p','q','r',
	  //24  25  26  27  28  29  30  31
		's','t','u','v','w','x','y','z'};
	static final byte[] map = new byte['z'+1];
	static {
		for (byte i = 0; i < characters.length; i++)
			map[characters[i]] = i;
	}
	/** number of bits per character */
	static final int CHARACTER_BITS = 5;
	
	public static final double[] decode(final String hash) {
		return decode(hash.getBytes());
	}
	public static final double[] decode(final byte[] hash) {
		double[] latLon = new double[2];
		int lat = 0, lon = 0;//this gives us a bit length of 32 for each coordinate - ought to be sufficient
		boolean evenbit = true;
		
		//split hash into binary latitude and longitude parts
		final byte[] h = convertHashToBinary(hash);
		for (byte b : h) {
			//unrolled loop over each bit
			if (evenbit) {
				lon = extractEvenBits(lon, b);
				lat = extractUnevenBits(lat, b);
			} else {
				lat = extractEvenBits(lat, b);
				lon = extractUnevenBits(lon, b);
			}
			evenbit = !evenbit;
		}
		latLon[0] = decode(lat, -90.0, 90, calculateLatitudeBits(h.length));
		latLon[1] = decode(lon, -180.0, 180, calculateLongitudeBits(h.length));
		
		return latLon;
	}
	
	static final byte[] convertHashToBinary(byte[] bytes) {
		final byte[] hash = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++)
			hash[i] = (byte)(0x1F & map[bytes[i]]);
		
		/*long l = (byte)(0x1F & map[bytes[0]]);
		for (int i = 1; i < bytes.length; i++) {
			l <<= 5;
			l |= (byte)(0x1F & map[bytes[i]]);
		}*/
		return hash;
	}
	
	static final int calculateLatitudeBits(final int quintets) {
		return calculateBits(quintets, 2);
	}
	static final int calculateLongitudeBits(final int quintets) {
		return calculateBits(quintets, 3);
	}
	/**
	 * A mathematical way of calculating the number of bits from input length
	 * (length / 2 * 5) + (length % 2 != 0 ? 3 : 0)
	 */
	private static final int calculateBits(final int quintets, final int unevenExtra) {
		return (((quintets >> 1) * CHARACTER_BITS) + ((quintets & 0x1) * unevenExtra));
	}
	
	static final double decode(final long coord, double min, double max, final int digits) {
		double val = 0.0;
		int mask = 1 << digits;
		while ((mask >>= 1) >= 1) {//while bits are left to be explored
			if ((mask & coord) > 0) {//bit == 1
				min = val;
				val = (val + max) / 2;
			} else {//bit == 0
				max = val;
				val = (val + min) / 2;
			}
		}
		//some rounding might be needed
		BigDecimal v = new BigDecimal(val);
		return v.setScale(digits / 5, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	static final int extractEvenBits(int value, final byte b) {
		value <<= 3;
		value |= ((b & 0x10) >> 2); value |= ((b & 0x04) >> 1); value |= (b & 0x01);
		return value;
	}

	static final int extractUnevenBits(int value, final byte b) {
		value <<= 2;
		value |= ((b & 0x08) >> 2); value |= ((b & 0x02) >> 1);
		return value;
	}

}

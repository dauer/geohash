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
	
	public static final double[] decode(String hash) {
		double[] latLon = new double[2];
		int lat = 0, lon = 0;//this gives us a bit length of 32 for each coordinate - ought to be sufficient
		int latDigits = 0, lonDigits = 0;
		boolean evenbit = true;
		
		//split hash into binary latitude and longitude parts
		final byte[] h = convertHashToBinary(hash.getBytes());
		for (byte b : h) {
			//outrolled loop over each bit
			if (evenbit) {
				lon <<= 3; lon |= ((b & 0x10) >> 2); lon |= ((b & 0x04) >> 1); lon |= (b & 0x01);
				lat <<= 2; lat |= ((b & 0x08) >> 2); lat |= ((b & 0x02) >> 1);
				lonDigits += 3; latDigits += 2;
			} else {
				lat <<= 3; lat |= ((b & 0x10) >> 2); lat |= ((b & 0x04) >> 1); lat |= (b & 0x01);
				lon <<= 2; lon |= ((b & 0x08) >> 2); lon |= ((b & 0x02) >> 1);
				latDigits += 3; lonDigits += 2; //TODO there must some mathematical way to calculate this on the length of input hash
			}
			evenbit = !evenbit;
		}
		latLon[0] = decode(lat, -90.0, 90, latDigits);
		latLon[1] = decode(lon, -180.0, 180, lonDigits);
		
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
	
	static final byte extractEvenBits(byte b) {
		byte e = 0;
		e |= ((b & 0x10) >> 2); e |= ((b & 0x04) >> 1); e |= (b & 0x01);
		return e;
	}

	static final byte extractUnevenBits(byte b) {
		byte u = 0;
		u |= ((b & 0x08) >> 2); u |= ((b & 0x02) >> 1);
		return u;
	}

}

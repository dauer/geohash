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
	private static final int BITS_PER_CHARACTER = 5;
	private	static final int MAX_BITS = 60;
	private static final int MAX_HASH_LENGTH = MAX_BITS / BITS_PER_CHARACTER;//maximum precision
	private static final double[] LATITUDE_RANGE = {-90.0, 90.0};
	private static final double[] LONGITUDE_RANGE = {-180.0, 180.0};
	
	public final double lat, lon;
	public final int precision;
	private GeoHash(final double lat, final double lon, final int precision) {
		this.lat = lat;
		this.lon = lon;
		this.precision = precision;
	}
	
	public static final GeoHash decode(final String hash) {
		return decode(hash.getBytes());
	}
	public static final GeoHash decode(final byte[] hash) {
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
		
		return new GeoHash(
				decode(lat, -90.0, 90, calculateLatitudeBits(h.length)), 	//lattitude
				decode(lon, -180.0, 180, calculateLongitudeBits(h.length)),	//longitude
				hash.length);
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
		return (((quintets >> 1) * BITS_PER_CHARACTER) + ((quintets & 0x1) * unevenExtra));
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
	
	static final double decode(final long coord, double min, double max, final int number_of_bits) {
		double val = 0.0;
		int mask = 1 << number_of_bits;
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
		return v.setScale(number_of_bits / 5, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * 
	 * @param lat
	 * @param lon
	 * @param precision Geohash length [1-12]
	 * @return
	 */
	public static final String encode(double lat, double lon, int precision) {
		//latInfo = [0]lat,[1]min,[2]max,[3]mid
		final double[] latInfo = {lat, LATITUDE_RANGE[0], LATITUDE_RANGE[1], 0.0};//TODO this could be made into an object data holder - and possibly even without loss of speed
		final double[] lonInfo = {lon, LONGITUDE_RANGE[0], LONGITUDE_RANGE[1], 0.0};
		long mask = 0x1l << Math.min(precision * BITS_PER_CHARACTER, MAX_BITS);//precision cannot be more than 60 bits (the nearest multiple of 5 under 64 (the bits of a long));
		long val = 0;
		boolean even = true;
		while ((mask >>= 1) > 0) {
			if (even) {
				//longitude
				val = encode(val, lonInfo);
			} else {
				//latitude
				val = encode(val, latInfo);
			}
			even = !even;
		}
		
		return new String(translate(val, precision));
	}
	public static final String encode(GeoHash coord) {
		return encode(coord.lat, coord.lon, coord.precision);
	}
	
	static final long encode(long value, final double[] info) {
		info[3] = (info[1] + info[2]) / 2;
		if (info[0] >= info[3]) {
			value <<= 1; value |= 0x1;//add one
			info[1] = info[3];
		} else {
			value <<= 1;//add zero
			info[2] = info[3];
		}
		return value;
	}
	
	static final byte[] translate(long value, int length_of_hash) {
		final byte[] h = new byte[ length_of_hash ];
		while (length_of_hash > 0) {
			h[--length_of_hash] = characters[(byte)(value & 0x1F)];
			value >>= BITS_PER_CHARACTER;
		}
		return h;
	}
	
	@Override
	public String toString() {
		return String.format("%f %f %d", this.lat, this.lon, 110111);//this.lat + " " + this.lon;
	}
	
}

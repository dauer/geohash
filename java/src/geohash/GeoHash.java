package geohash;

import java.math.BigDecimal;

public class GeoHash {
	
	protected static final byte[] characters = {
		// 0   1   2   3   4   5   6   7
		  '0','1','2','3','4','5','6','7',
		// 8   9  10  11  12  13  14  15
		  '8','9','b','c','d','e','f','g',
		//16  17  18  19  20  21  22  23
		  'h','j','k','m','n','p','q','r',
		//24  25  26  27  28  29  30  31
		  's','t','u','v','w','x','y','z'};
	protected static final byte[] map = new byte['z'+1];
	static {
		for (byte i = 0; i < characters.length; i++)
			map[characters[i]] = i;
	}
	/** number of bits per character */
	protected static final int BITS_PER_CHARACTER = 5;
	protected	static final int MAX_BITS = 60;
	protected static final int MAX_HASH_LENGTH = MAX_BITS / BITS_PER_CHARACTER;//maximum precision
	protected static final double[] LATITUDE_RANGE = {-90.0, 90.0};
	protected static final double[] LONGITUDE_RANGE = {-180.0, 180.0};
	
	protected Coordinate lat, lon;
	protected int precision;
	protected long bitValue;
	protected byte[] binaryValue;
	protected byte[] hash;
	private GeoHash(final double lat, final double lon, final int precision) {
		this.lat = new Coordinate(lat, LATITUDE_RANGE);
		this.lon = new Coordinate(lon, LONGITUDE_RANGE);
		this.precision = precision;
	}
	private GeoHash(final long bitValue, final byte[] binaryHash) {
		this.bitValue = bitValue;
		this.binaryValue = binaryHash;
		this.precision = binaryHash.length;
		this.lat = new Coordinate(0, LATITUDE_RANGE);
		this.lon = new Coordinate(0, LONGITUDE_RANGE);
	}
	protected GeoHash() {
		
	}
	public final double latitude() {
		return lat.coord;
	}
	public final double longitude() {
		return lon.coord;
	}
	public final int precision() {
		return precision;
	}
	public final long bitValue() {
		return bitValue;
	}
	public final String hash() {
		return new String(hash);
	}
	
	public static final GeoHash decode(final String hash) {
		return decode(hash.getBytes());
	}
	public static final GeoHash decode(final byte[] hash) {
		boolean evenbit = true;
		
		//split hash into binary latitude and longitude parts
		GeoHash geohash = convertHashToBinary(hash);
		for (byte b : geohash.binaryValue) {
			//unrolled loop over each bit
			if (evenbit) {
				extractEvenBits(geohash.lon, b);
				extractUnevenBits(geohash.lat, b);
			} else {
				extractEvenBits(geohash.lat, b);
				extractUnevenBits(geohash.lon, b);
			}
			evenbit = !evenbit;
		}
		
		geohash.hash = hash;
		decode(geohash.lat, calculateLatitudeBits(hash.length));	//latitude
		decode(geohash.lon, calculateLongitudeBits(hash.length));	//longitude
		return geohash;
	}
	
	protected static final GeoHash convertHashToBinary(byte[] bytes) {
		long l = (byte)(0x1F & map[bytes[0]]);
		final byte[] hash = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			hash[i] = (byte)(0x1F & map[bytes[i]]);
			l <<= 5;
			l |= (byte)(0x1F & map[bytes[i]]);
		} 
		
		return new GeoHash(l, hash);
	}
	
	protected static final int calculateLatitudeBits(final int quintets) {
		return calculateBits(quintets, 2);
	}
	protected static final int calculateLongitudeBits(final int quintets) {
		return calculateBits(quintets, 3);
	}
	/**
	 * A mathematical way of calculating the number of bits from input length
	 * (length / 2 * 5) + (length % 2 != 0 ? 3 : 0)
	 */
	private static final int calculateBits(final int quintets, final int unevenExtra) {
		return (((quintets >> 1) * BITS_PER_CHARACTER) + ((quintets & 0x1) * unevenExtra));
	}
	
	protected static final void extractEvenBits(final Coordinate coord, final byte b) {
		long value = coord.bitValue;
		value <<= 3;
		value |= ((b & 0x10) >> 2); value |= ((b & 0x04) >> 1); value |= (b & 0x01);
		coord.bitValue = value;
	}

	protected static final void extractUnevenBits(final Coordinate coord, final byte b) {
		long value = coord.bitValue;
		value <<= 2;
		value |= ((b & 0x08) >> 2); value |= ((b & 0x02) >> 1);
		coord.bitValue = value;
	}
	
	protected static final void decode(final Coordinate coord, final int number_of_bits) {
		double val = 0.0;
		int mask = 1 << number_of_bits;
		while ((mask >>= 1) >= 1) {//while bits are left to be explored
			if ((mask & coord.bitValue) > 0) {//bit == 1
				coord.min = val;
				val = (val + coord.max) / 2;
			} else {//bit == 0
				coord.max = val;
				val = (val + coord.min) / 2;
			}
		}
		//some rounding might be needed
		BigDecimal v = new BigDecimal(val);
//		return v.setScale(number_of_bits / 5, BigDecimal.ROUND_HALF_UP).doubleValue();
		coord.coord = v.setScale(number_of_bits / 5, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * 
	 * @param lat
	 * @param lon
	 * @param precision Geohash length [1-12]
	 * @return
	 */
	public static final GeoHash encode(double lat, double lon, int precision) {
		if (precision < 1) precision = 1;
		final GeoHash geohash = new GeoHash(lat, lon, precision);
		long mask = 0x1l << Math.min(precision * BITS_PER_CHARACTER, MAX_BITS);//precision cannot be more than 60 bits (the nearest multiple of 5 under 64 (the bits of a long));
		geohash.bitValue = 0;
		boolean even = true;
		while ((mask >>= 1) > 0) {
			if (even) {
				//longitude
				encode(geohash, geohash.lon);
			} else {
				//latitude
				encode(geohash, geohash.lat);
			}
			even = !even;
		}
		
		geohash.hash = translate(geohash.bitValue, precision);
		return geohash;//new String(translate(geohash.bitValue, precision));
	}
	public static final GeoHash encode(GeoHash geohash) {
		return encode(geohash.lat.coord, geohash.lon.coord, geohash.precision);
	}
	
	protected static final void encode(GeoHash geohash, final Coordinate info) {
		info.mid = (info.min + info.max) / 2;
		long value = geohash.bitValue;
		if (info.coord >= info.mid) {
			value <<= 1; value |= 0x1;//add one
			info.min = info.mid;
		} else {
			value <<= 1;//add zero
			info.max = info.mid;
		}
		geohash.bitValue = value;
	}
	
	protected static final byte[] translate(long value, int length_of_hash) {
		final byte[] h = new byte[ length_of_hash ];
		while (length_of_hash > 0) {
			h[--length_of_hash] = characters[(byte)(value & 0x1F)];
			value >>= BITS_PER_CHARACTER;
		}
		return h;
	}
	
	@Override
	public String toString() {
		return String.format("%f %f %d", this.lat.coord, this.lon.coord, this.bitValue);
	}
	
	
	//factory methods
	protected static final Coordinate createLatCoordinate() {
		return new Coordinate(0, LATITUDE_RANGE);
	}
	protected static final Coordinate createLatCoordinate(long bitValue) {
		Coordinate coord = createLatCoordinate();
		coord.bitValue = bitValue;
		return coord;
	}
	protected static final Coordinate createLonCoordinate() {
		return new Coordinate(0, LONGITUDE_RANGE);
	}
	protected static final Coordinate createLonCoordinate(long bitValue) {
		Coordinate coord = createLonCoordinate();
		coord.bitValue = bitValue;
		return coord;
	}
	
	//Helper data class
	protected static final class Coordinate {
		public double coord, min, max, mid;
		public long bitValue;
		public Coordinate(final double c, final double[] range) {
			this.coord = c;
			this.min = range[0];
			this.max = range[1];
			this.mid = 0.0;
			this.bitValue = 0;
		}
	}
	
}

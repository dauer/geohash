package geohash;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;

public class GeoHashTest {
	
	/*Geohash hash;
	@BeforeClass
	public static void start() {
		hash = new Geohash();
	}*/

	/*@Test
	public void mapLengthBase32() {
		assertEquals(32, Geohash.map.length);
	}*/
	
	@Test
	public void mapping() {
		//decoding the hash ezs42
		assertEquals(13, GeoHash.map['e']);
		assertEquals(31, GeoHash.map['z']);
		assertEquals(24, GeoHash.map['s']);
		assertEquals(4, GeoHash.map['4']);
		assertEquals(2, GeoHash.map['2']);
	}
	
	@Test
	public void convertHash() {
		//decoding the hash ezs42 should translate into 01101 11111 11000 00100 00010 = 0DFE082
		assertEquals(0x0DFE082, GeoHash.decode("ezs42").bitValue);//GeoHash.translateHashToBinary("ezs42".getBytes()));
		assertEquals(0x3B439CA1, GeoHash.decode("xn7751").bitValue);//GeoHash.translateHashToBinary("xn7751".getBytes()));
	}
	
	@Test
	public void decodeLatitude() {
		GeoHash.Coordinate coord = new GeoHash.Coordinate(0.0, GeoHash.LATITUDE_RANGE, 12);
		//0xBC9 = 101111001001
		assertEquals(42.6,GeoHash.decodeCoordinate(0xBC9, coord).coord, 0.0000005);
	}
	@Test
	public void decodeLongitude() {
		GeoHash.Coordinate coord = new GeoHash.Coordinate(0.0, GeoHash.LONGITUDE_RANGE, 13);
		//0111110000000 = 0x0F80
		assertEquals(-5.6, GeoHash.decodeCoordinate(0x0F80, coord).coord, 0.000005);
	}
	
	@Test
	public void extractEvenBits() {
		//'Even' starts from the left of the binary representation with 0 as startindex
		//00010110 = 0x16
		//110 = 0x06
		assertEquals(0x06, GeoHash.extractEvenBits(0,(byte)0x16));
		//00001111 = 0xF
		//011 = 3
		assertEquals(0x03, GeoHash.extractEvenBits(0,(byte)0xF));
	}
	@Test
	public void extractUnevenBits() {
		//00010110 = 0x16
		//01 = 0x01
		assertEquals(0x01, GeoHash.extractUnevenBits(0,(byte)0x16));
		//00001111 = 0xF
		//11 = 3
		assertEquals(0x03, GeoHash.extractUnevenBits(0,(byte)0xF));
	}
	
	@Test
	public void numberOfBitsPerCoordinate() {
		assertEquals(30, GeoHash.calculateLatitudeBits(12));
		assertEquals(30, GeoHash.calculateLongitudeBits(12));
	}
	
	@Test
	public void decode() {
		assertCoordinates(GeoHash.decode("ezs42"), 42.6, -5.6);
		assertCoordinates(GeoHash.decode("u3butn2urfrd"), 55.669840, 12.525787);
		assertCoordinates(GeoHash.decode("k3vngx2pbh0r"), -33.928548, 18.435059);
		assertCoordinates(GeoHash.decode("f244kwx00425"), 45.431226, -75.730133);
		assertCoordinates(GeoHash.decode("xn77513czbxu"), 35.690764, 139.704895);
		assertCoordinates(GeoHash.decode("xn7751"), 35.692, 139.708);
		assertCoordinates(GeoHash.decode("u4pruydqqvj"), 57.64911, 10.40744);
		assertCoordinates(GeoHash.decode("u4pbbs8wmv7k"), 56.407657, 10.921348);
		assertCoordinates(GeoHash.decode("u1zpxuch99e5"),56.188983, 10.185768);
		
		GeoHash coord = GeoHash.decode("u1zpxuch99e5");
		System.out.println(coord);
	}
	
	@Test
	public void encodeLatitude() {
//		System.out.println(GeoHash.encode(42.6, -90.0, 90.0, 12));
//		double[] info = {42.6, -90.0, 90.0, 0};
		GeoHash.Coordinate coord = new GeoHash.Coordinate(42.6, GeoHash.LATITUDE_RANGE, 12);
		long value = 0;
		for(int i = 0; i < 12; i++)
			value = GeoHash.encode(value, coord);
		assertEquals(0xBC9,value);
	}
	
	@Test
	public void encodeLongitude() {
//		assertEquals(0x0F80, GeoHash.encode(-5.6, -180.0, 180.0, 13));
//		double[] info = {-5.6, -180.0, 180.0, 0};
		GeoHash.Coordinate coord = new GeoHash.Coordinate(-5.6, GeoHash.LONGITUDE_RANGE, 13);
		long value = 0;
		for(int i = 0; i < 13; i++)
			value = GeoHash.encode(value, coord);
		assertEquals(0x0F80,value);
	}
	
	@Test
	public void translate() {
		//ezs42 should translate into 01101 11111 11000 00100 00010 = 0DFE082
		assertArrayEquals(/*new byte[]{0x0D,0x1F,0x18,0x04,0x02}*/new byte[]{'e','z','s','4','2'}, GeoHash.translateBinaryToHash(0x0DFE082, 5));
	}
	
	@Test
	public void encode() {
		assertHash("ezs42");
		assertHash("xn7751");
		assertHash("u3butn2urfrd");
		assertHash("k3vngx2pbh0r");
		assertHash("f244kwx00425");
		assertHash("xn77513czbxu");
		assertHash("u4pruydqqvj");
		assertHash("u4pbbs8wmv7k");
		assertEquals("u1zpxuch99e5", GeoHash.encode(56.188983, 10.185768, 12).toHashString());
	}
	
	@Test
	public void correctStringRepresentation() {
		assertEquals( "01010", GeoHash.binaryRepresentation(10, 1));
		assertEquals( "0000110111", GeoHash.binaryRepresentation(55, 2));
		assertEquals("110100000111111101011110111010010111000001001010010110100101", GeoHash.binaryRepresentation(938989452414723493L, 12));
	}
	
	@Test
	public void timing() {
		int size = 20000;
		double[][] locations = new double[size][];
		for (int i = 0; i < size; i++)
			locations[i] = generateLocation();
		
		long time = System.currentTimeMillis();
		for (double[] loc : locations) {
			GeoHash encoded = GeoHash.encode(loc[0], loc[1], (int)loc[2]);
			GeoHash decoded = GeoHash.decode(encoded.hash);
			assertTrue(encoded.equals(decoded));
			//assert loc == decode
		}
		System.out.println("Time in milliseconds: " + (System.currentTimeMillis() - time));
	}
	
	private void assertCoordinates(GeoHash coord, double lat, double lon) {
		assertEquals(lat, coord.lat, 0.0000005);
		assertEquals(lon, coord.lon, 0.0000005);
	}
	
	private void assertHash(String hash) {
		GeoHash decode = GeoHash.decode(hash);
		assertEquals(hash, GeoHash.encode(decode.lat, decode.lon, decode.precision).toHashString());
	}
	
	private double[] generateLocation() {
		//generate random locations
		//precision [5-12]
		Random r = new Random(System.currentTimeMillis());
		int precision = 5 + r.nextInt(7);
		double lat = r.nextInt(180) - 90 + r.nextDouble();
		double lon = r.nextInt(360) - 180 + r.nextDouble();
//		System.out.println(lat + " " + lon + " " + precision);
		return new double[]{lat, lon, precision};
	}
	
}

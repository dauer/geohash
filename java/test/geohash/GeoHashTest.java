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
		assertArrayEquals(new byte[]{0x0D,0x1F,0x18,0x04,0x02}, GeoHash.convertHashToBinary("ezs42".getBytes()).binaryValue);
		assertArrayEquals(/*0x3B439CA1*/new byte[]{0x1D, 0x14, 0x07, 0x07, 0x05, 0x01}, GeoHash.convertHashToBinary("xn7751".getBytes()).binaryValue);
	}
	
	@Test
	public void decodeLatitude() {
		//0xBC9 = 101111001001
		//assertEquals(42.6,GeoHash.decode(0xBC9, -90.0, 90.0, 12), 0.0000005);
		GeoHash.Coordinate coord = GeoHash.createLatCoordinate(0xBC9);
		GeoHash.decode(coord, 12);
		assertEquals(42.6, coord.coord, 0.0000005);
	}
	@Test
	public void decodeLongitude() {
		//0111110000000 = 0x0F80
		GeoHash.Coordinate coord = GeoHash.createLonCoordinate(0xF80);
		GeoHash.decode(coord, 13);
		assertEquals(-5.6, coord.coord, 0.000005);
	}
	
	@Test
	public void extractEvenBits() {
		//'Even' starts from the left of the binary representation with 0 as startindex
		//00010110 = 0x16
		//110 = 0x06
		GeoHash.Coordinate coord = GeoHash.createLonCoordinate();
		GeoHash.extractEvenBits(coord,(byte)0x16);
		assertEquals(0x06, coord.bitValue);
		//00001111 = 0xF
		//011 = 3
		coord = GeoHash.createLonCoordinate();
		GeoHash.extractEvenBits(coord,(byte)0xF);
		assertEquals(0x03, coord.bitValue);
	}
	@Test
	public void extractUnevenBits() {
		//00010110 = 0x16
		//01 = 0x01
		GeoHash.Coordinate coord = GeoHash.createLatCoordinate();
		GeoHash.extractUnevenBits(coord,(byte)0x16);
		assertEquals(0x01, coord.bitValue);
		//00001111 = 0xF
		//11 = 3
		coord = GeoHash.createLatCoordinate();
		GeoHash.extractUnevenBits(coord,(byte)0xF);
		assertEquals(0x03, coord.bitValue);
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
		GeoHash geohash = new GeoHash();
		GeoHash.Coordinate info = new GeoHash.Coordinate(42.6, GeoHash.LATITUDE_RANGE);
		for(int i = 0; i < 12; i++)
			GeoHash.encode(geohash, info);
		assertEquals(0xBC9,geohash.bitValue);
	}
	
	@Test
	public void encodeLongitude() {
//		assertEquals(0x0F80, GeoHash.encode(-5.6, -180.0, 180.0, 13));
//		double[] info = {-5.6, -180.0, 180.0, 0};
		GeoHash geohash = new GeoHash();
		GeoHash.Coordinate info = new GeoHash.Coordinate(-5.6, GeoHash.LONGITUDE_RANGE);
		for(int i = 0; i < 13; i++)
			GeoHash.encode(geohash, info);
		assertEquals(0x0F80,geohash.bitValue);
	}
	
	@Test
	public void translate() {
		//ezs42 should translate into 01101 11111 11000 00100 00010 = 0DFE082
		assertArrayEquals(/*new byte[]{0x0D,0x1F,0x18,0x04,0x02}*/new byte[]{'e','z','s','4','2'}, GeoHash.translate(0x0DFE082, 5));
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
		assertEquals("u1zpxuch99e5",GeoHash.encode(56.188983, 10.185768, 12).hash());
	}
	
	@Test
	public void timing() {
		int size = 20000;
		double[][] locations = new double[size][];
		for (int i = 0; i < size; i++)
			locations[i] = generateLocation();
		
		long time = System.currentTimeMillis();
		for (double[] loc : locations) {
			GeoHash geohash = GeoHash.encode(loc[0], loc[1], (int)loc[2]);
			GeoHash decode = GeoHash.decode(geohash.hash);
			//assert loc == decode
		}
		System.out.println("Time in milliseconds: " + (System.currentTimeMillis() - time));
	}
	
	private void assertCoordinates(GeoHash geohash, double lat, double lon) {
		assertEquals(lat, geohash.latitude(), 0.0000005);
		assertEquals(lon, geohash.longitude(), 0.0000005);
	}
	
	private void assertHash(String hash) {
		GeoHash decode = GeoHash.decode(hash);
		assertEquals(hash,GeoHash.encode(decode).hash());
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

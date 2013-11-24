package geohash;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class GeohashTest {
	
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
		assertArrayEquals(new byte[]{0x0D,0x1F,0x18,0x04,0x02}, GeoHash.convertHashToBinary("ezs42".getBytes()));
		assertArrayEquals(/*0x3B439CA1*/new byte[]{0x1D, 0x14, 0x07, 0x07, 0x05, 0x01}, GeoHash.convertHashToBinary("xn7751".getBytes()));
	}
	
	@Test
	public void decodeLatitude() {
		//0xBC9 = 101111001001
		assertEquals(42.6,GeoHash.decode(0xBC9, -90.0, 90.0, 12), 0.0000005);
	}
	@Test
	public void decodeLongitude() {
		//0111110000000 = 0x0F80
		assertEquals(-5.6, GeoHash.decode(0x0F80, -180.0, 180.0, 13), 0.000005);
	}
	
	@Test
	public void extractEvenBits() {
		//'Even' starts from the left of the binary representation with 0 as startindex
		//00010110 = 0x16
		//110 = 0x06
		assertEquals(0x06, GeoHash.extractEvenBits((byte)0x16));
	}
	@Test
	public void extractUnevenBits() {
		//00010110 = 0x16
		//01 = 0x01
		assertEquals(0x01, GeoHash.extractUnevenBits((byte)0x16));
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
		
		
		double[] coord = GeoHash.decode("u4pruydqqvj");
		System.out.println(coord[0] + "  " + coord[1]);
	}
	
	private void assertCoordinates(double[] coord, double lat, double lon) {
		assertEquals(lat, coord[0], 0.0000005);
		assertEquals(lon, coord[1], 0.0000005);
	}
	
}

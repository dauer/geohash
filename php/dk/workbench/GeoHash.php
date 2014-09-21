<?php
/**
 * dk\workbench namespace containing GeoHash class
 */

namespace dk\workbench\geohash;

require_once('Coordinate.php');
require_once('GeoHash/StringUtils.php');

/**
 * A class for decoding and encoding GeoHashes
 */
class GeoHash {


    /** @const int BITS_PER_CHARACTER Number of bits per character, 32 characters is 5 bits 11111 = 32 */
    const BITS_PER_CHARACTER = 5;

    /** @var string Contains valid GeoHash characters */
    private static $map = '0123456789bcdefghjkmnpqrstuvwcyz';

    /**
     * Decodes a GeoHash hash into a Coordinate set
     *
     * @param string $hash representing the geohash to decode
     * @return Coordinate set representing the specified hash
     */
    public static function decode($hash) {
        // Convert hash into 'binary' string
        $bin = self::_binary($hash);
        // Split 'binary' string into latitude and longitude parts
        $coords = split($bin);
        return new Coordinate(self::_decode($coords['even'], -90.0, 90.0),
                              self::_decode($coords['odd'], -180.0, 180.0));
    }

    /**
     * Encodes a coordinate (latitude, longitude) into a GeoHash string
     *
     * @param double $latitude representing the latitude part of the coordinate set
     * @param double $longitude representing the longitude part of the coordinate set
     * @return string containing the GeoHash for the specified coordinates
     */
    public static function encode($latitude, $longitude) {
        // Find precision (number of decimals)
        $digits = self::_decimal($latitude, $longitude);
        // Translate coordinates to binary strings
        $latBinStr = self::_encode($latitude, -90.0, 90.0, $digits);
        $lonBinStr = self::_encode($longitude, -180.0, 180.0, $digits);
        // Merge the two binary strings
        $binStr = merge($latBinStr, $lonBinStr);
        // Calculate and return Geohash for 'binary' string
        return self::_translate($binStr);
    }

    /**
     * Converts a binary string into a double
     *
     * @param string $coord Containing binary string representation of coordinate
     * @param double $min Minimum range for coordinate
     * @param double $max Maximum range for coordinate
     * @return double representing coordinate
     */
    private static function _decode($coord, $min, $max) {
        $mid = 0.0;
        $val = 0.0;
        for($i = 0; $i < strlen($coord); $i++) {
            if(substr($coord, $i, 1) == '1') {
                $min = $mid;
                $val = ($mid + $max) / 2;
                $mid = $val;
            } else {
                $max = $mid;
                $val = ($mid + $min) / 2;
                $mid = $val;
            }
        }
        // We want number of decimals according to hash length
        $val = (double)sprintf("%." . (strlen($coord) / self::BITS_PER_CHARACTER) . "f", $val);
        return $val;
    }

    /**
     * Converts a hash to a binary string representation
     *
     * @param string $hash Hash string to be converted to binary string
     * @return string Containing binary string
     */
    private function _binary($hash) {
        $bin = '';
        for($i = 0; $i < strlen($hash); $i++) {
            $pos = strpos(self::$map, $hash[$i]);
            if($pos !== false) {
                $bin .= sprintf("%05s", decbin($pos));
            }
        }
        return $bin;
    }

    /**
     * Find max number of decimals in latitude and longitude doubles
     *
     * @param double $lat
     * @param double $long
     * @return int length of longest decimal list ex.: 10.246834 = 6
     *                                                   |______|
     */
    private static function _decimal($lat, $long) {
        $d1 = strlen(substr($lat, strpos($lat, '.') + 1));
        $d2 = strlen(substr($long, strpos($long, '.') + 1));
        if($d1 > $d2) {
            return $d1;
        }
        return $d2;
    }

    /**
     * Encode a part of a coordinate into a binary string representation
     *
     * @param double $coord Coordinate part to encode into binary string representation
     * @param double $min Minimum range for coordinate
     * @param double $max Maximum range for coordinate
     * @param int $precision How many digits do we want calculated
     * @return string containing the encoded GeoHash
     */
    private static function _encode($coord, $min, $max, $precision) {
        $mid = 0.0;
        $x   = 0.0;
        $y   = 0.0;
        $p   = ($precision * self::BITS_PER_CHARACTER);
        $result = '';
        for($i = 0; $i < $p; $i++) {
            if($coord <= $max && $coord >= $mid) {
                $result .= '1';
                $x = $mid;
                $y = $max;
            } else {
                $result .= '0';
                $x = $min;
                $y = $mid;
            }
            $min = $x;
            $mid = $x + (($y - $x) / 2);
            $max = $y;
        }
        return $result;
    }

    /**
     * Translate binary string into GeoHash string
     *
     * @param string $binstr Binary string to be converted into GeoHash string
     * @return string Containing translated GeoHash
     */
    private static function _translate($binstr) {
        $hash = '';
        for($i = 0; $i < strlen($binstr); $i += self::BITS_PER_CHARACTER) {
            $pos = bindec(substr($binstr, $i, self::BITS_PER_CHARACTER));
            $hash .= substr(self::$map, $pos, 1);
        }
        return $hash;
    }

}

?>

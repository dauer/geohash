<?php

namespace dk\workbench;

require_once('Coordinate.php');

/**
 * A class for decoding and encoding GeoHashes
 */
class GeoHash {

    private static $map = '0123456789bcdefghjkmnpqrstuvwcyz';

    /**
     * Decodes a GeoHash hash into a Coordinate set
     *
     * @param String representing the geohash to decode
     * @return Coordinate set representing the specified hash
     */
    public static function decode($hash) {
        // Convert hash into 'binary' string
        $bin = self::_binary($hash);
        // Split 'binary' string into latitude and longitude parts
        $coords = self::_split($bin);
        return new Coordinate(self::_decode($coords['lat'], 90.0, 90.0),
                              self::_decode($coords['long'], 180.0, 180.0));
    }

    /**
     * Encodes a coordinate (latitude, longitude) into a GeoHash string
     *
     * @param double representing the latitude part of the coordinate set
     * @param double representing the longitude part of the coordinate set
     * @return string containing the GeoHash for the specified coordinates
     */
    public static function encode($lat, $long) {
        // Find precision (number of decimals)
        $digits = self::_decimal($lat, $long);
        // Translate coordinates to binary strings
        $latBinStr = self::_encode($lat, -90.0, 90.0, $digits);
        $lonBinStr = self::_encode($long, -180.0, 180.0, $digits);
        // Merge the two binary strings
        $binStr = self::_merge($latBinStr, $lonBinStr);
        // Calculate and return Geohash for 'binary' string
        return self::_translate($binStr);
    }

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
        $val = (double)sprintf("%." . (strlen($coord) / 5) . "f", $val);
        return $val;
    }

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

    private static function _split($bin) {
        $lat = '';
        $lon = '';
        for($i = 0; $i < strlen($bin); $i++) {
            $c = substr($bin, $i, 1);
            if($i % 2 == 0) {
                $lon .= $c;
            } else {
                $lat .= $c;
            }
        }
        return array('lat' => $lat, 'long' => $lon);
    }

    private static function _decimal($lat, $long) {
        $d1 = strlen(substr($lat, strpos($lat, '.') + 1));
        $d2 = strlen(substr($long, strpos($long, '.') + 1));
        if($d1 > $d2) {
            return $d1;
        }
        return $d2;
    }

    private static function _encode($coord, $min, $max, $precision) {
        $mid = 0.0;
        $x   = 0.0;
        $y   = 0.0;
        $p   = ($precision * 5);
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

   private static function _merge($latbin, $longbin) {
        $res = '';
        for($i = 0; $i < strlen($latbin); $i++) {
            $res .= substr($longbin, $i, 1) . substr($latbin, $i, 1);
        }
        return $res;
    }

    private static function _translate($binstr) {
        $hash = '';
        for($i = 0; $i < strlen($binstr); $i += 5) {
            $pos = bindec(substr($binstr, $i, 5));
            $hash .= substr(self::$map, $pos, 1);
        }
        return $hash;
    }

}

?>

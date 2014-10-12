<?php

require_once('dk/workbench/GeoHash.php');

use \dk\workbench\geohash\GeoHash as GeoHash;

$hash = GeoHash::encode(45.081046, 38.564758);
echo $hash . "\n";
// ub52mwcbpupf

$coordinates = GeoHash::decode('ub52mwcbpupf');
echo 'Latitude: ' . $coordinates->latitude . "\n";
// Latitude: 45.081046

echo 'Longitude: ' . $coordinates->longitude . "\n";
// Longitude: 38.564758

echo 'Coordinate: ' . $coordinates . "\n";
// Coordinate: 45.081046, 38.564758

?>

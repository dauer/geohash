<?php

require_once('dk/workbench/GeoHash.php');

use \dk\workbench\geohash\GeoHash as GeoHash;

$hash = GeoHash::encode(45.081046, 38.564758);
echo $hash . "\n";

$coordinates = GeoHash::decode('ub52mwcbpupf');
echo 'Latitude: ' . $coordinates->latitude . "\n";
echo 'Longitude: ' . $coordinates->longitude . "\n";
echo 'Coordinate: ' . $coordinates . "\n";

?>

<?php

namespace dk\workbench;

/**
 * A simple class for representing a coordinate (latitude, longitude)
 */

class Coordinate {
    private $latitude = 0.0;
    private $longitude = 0.0;

    public function __construct($lat, $long) {
        $this->latitude = $lat;
        $this->longitude = $long;
    }

    public function __get($attr) {
        switch($attr) {
            case 'latitude':
                return $this->latitude;
                break;
            case 'longitude':
                return $this->longitude;
                break;
            default:
                trigger_error("Invalid attribute: " . $attr, E_USER_NOTICE);
        }
        return null;
    }

    public function __toString() {
        return $this->latitude . ", " . $this->longitude;
    }
}

?>

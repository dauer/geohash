<?php
/**
 * dk\workbench namespace containing Coordinate class
 */

namespace dk\workbench\geohash;

/**
 * A simple class for representing a coordinate point (latitude, longitude)
 */

class Coordinate {
    /** @var double Latitude part of coordinate point */
    private $latitude = 0.0;
    /** @var double Longitude part of coordinate point */
    private $longitude = 0.0;

    /**
     * Class constructor
     *
     * @param double $latitude part of coordinate point
     * @param double $longitude part of the coordinate point
     */
    public function __construct($latitude, $longitude) {
        $this->latitude = $latitude;
        $this->longitude = $longitude;
    }

    /**
     * Implement get methods for class attributes
     *
     * @param string $attr Name of attribute to get
     * @return double Attribute value
     */
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

    /**
     * Implements the toString method
     *
     * @return string Representation of the coordinate
     */
    public function __toString() {
        return $this->latitude . ", " . $this->longitude;
    }
}

?>

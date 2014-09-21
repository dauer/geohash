<?php

namespace dk\workbench\geohash;

/**
 * Split a string into two strings, odd positioned characters in one and even
 * positioned in the other.
 * Example: "ABABABABAB" => array('even' => 'AAAAA', 'odd' => 'BBBBB')
 *
 * @param string $str String to split up
 * @return array containing characters from odd and even positions of the specified string
 */
function split($str) {
    $odd = '';
    $even = '';
    for($i = 0; $i < strlen($str); $i++) {
        $c = substr($str, $i, 1);
        if($i % 2 == 0) {
            $odd .= $c;
        } else {
            $even .= $c;
        }
    }
    return array('even' => $even, 'odd' => $odd);
}

/**
 * Merge two strings into one
 * Example: merge("AAAAA", "BBBBB") => "ABABABABAB"
 *
 * @param string $str1 First string to merge
 * @param string $str2 Second string to merge
 * @return string The merged string
 */
function merge($str1, $str2) {
    $res = '';
    for($i = 0; $i < strlen($str1); $i++) {
        $res .= substr($str2, $i, 1) . substr($str1, $i, 1);
    }
    return $res;
}

?>

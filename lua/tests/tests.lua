package.path = package.path .. ";../lib/geohash.lua"

require "luaunit"

Testing = {}

function Testing:testDecode()
    local GeoHash = require "geohash.lua"
    -- Copenhagen, Denmark
    local x1, y1 = GeoHash.decode('u3butn2urfrd')
    assertEquals(x1, 55.669840)
    assertEquals(y1, 12.525787)
    -- Cape Town, South Africa
    local x2, y2 = GeoHash.decode('k3vngx2pbh0r')
    assertEquals(x2, -33.928548)
    assertEquals(y2, 18.435059)
    -- Ottawa, Canada
    local x3, y3 = GeoHash.decode('f244kwx00425')
    assertEquals(x3, 45.431226)
    assertEquals(y3, -75.730133)
    -- Tokyo, Japan
    local x4, y4 = GeoHash.decode('xn77513czbxu')
    assertEquals(x4, 35.690764)
    assertEquals(y4, 139.704895)
    -- Tokyo, Japan less coordinates
    local x5, y5 = GeoHash.decode('xn7751')
    assertEquals(x5, 35.692)
    assertEquals(y5, 139.708)
end

function Testing:testEncode()
    local GeoHash = require "geohash.lua"
    -- Copenhagen, Denmark
    local h1 = GeoHash.encode(55.669840, 12.525787)
    assertEquals(h1, 'u3butn2urfrd')
    -- Cape Town, South Africa
    local h2 = GeoHash.encode(-33.928548, 18.435059)
    assertEquals(h2, 'k3vngx2pbh0r')
    -- Ottawa, Canada
    local h3 = GeoHash.encode(45.431226, -75.730133)
    assertEquals(h3, 'f244kwx00425')
    -- Tokyo, Japan
    local h4 = GeoHash.encode(35.690764, 139.704895)
    assertEquals(h4, 'xn77513czbxu')
    -- Tokyo, Japan less coordinates
    local h5 = GeoHash.encode(35.690, 139.704)
    assertEquals(h5, 'xn7751')
end

function Testing:testPrecision()
    local GeoHash = require "geohash.lua"
    -- Copenhagen, Denmark
    local h1 = GeoHash.encode(55.669840, 12.525787)
    assertEquals(h1, 'u3butn2urfrd')
    -- less precision
    GeoHash.precision(3)
    local h2 = GeoHash.encode(55.669840, 12.525787)
    assertEquals(h2, 'u3butn')
    -- Test value is set for module in generale
    local h3 = GeoHash.encode(55.669840, 12.525787)
    assertEquals(h3, 'u3butn')
end

function Testing:testNoDecimals()
    local GeoHash = require "geohash.lua"
    -- Near Vordingborg, Denmark
    local h1 = GeoHash.encode(55, 12)
    assertEquals(h1, 'u3b8')
end

function Testing:testEquator()
    local GeoHash = require "geohash.lua"
    -- Equator
    local h1 = GeoHash.encode(0, 0)
    assertEquals(h1, 's000')
end

function Testing:testInvalidHash()
    local GeoHash = require "geohash.lua"
    -- Attempt to decode invalid hash (invalid characters: 'a', 'l')
    local x, y = GeoHash.decode("ezas4l2")
    assertEquals(x, nil)
    assertEquals(y, nil)
end

LuaUnit:run()

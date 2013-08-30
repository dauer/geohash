GeoHash = require("lib/geohash")

print(GeoHash.decode("ub52mwcbpupf"))
-- 45.081046   38.564758

print(GeoHash.encode(45.081046, 38.564758))
-- ub52mwcbpupf

GeoHash.precision(4)

print(GeoHash.decode("ub52mwcbpupf"))
-- 45.081046   38.564758

print(GeoHash.encode(45.081046, 38.564758))
-- ub52mwcb

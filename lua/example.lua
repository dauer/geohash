require("geohash")

-- Default precision is 5
GeoHash.precision(8)
print(GeoHash.decode("ub52mwcbpupf"))
print(GeoHash.encode(45.081046, 38.564758))

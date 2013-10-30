package = "Geohash"
version = "0.9-1"

source = {
   url = "https://github.com/dauer/geohash/raw/master/lua/release/geohash-0.9.tgz",
   md5 = "282df78fa1f0c85ca2129854a81ba5f9"
}

description = {
   summary = "Lua library for encoding and decoding GeoHashes.",
   detailed = [[
      A library written in Lua containing methods for encoding and decoding of GeoHashes.
      For more info: http://geohash.org/
   ]],
   homepage = "https://github.com/dauer/geohash",
   license = "MIT"
}

dependencies = {
   "lua >= 5.1"
}

build = {
   type = "builtin",
   modules = {
      ["geohash"] = "geohash.lua"
   }
}

# GeoHash #

A library written in Lua contaning methods for encoding and decoding of GeoHashes.

This project is very early in development, expect errors etc.

For more info:

http://geohash.org/

http://geohash.org/site/tips.html

http://en.wikipedia.org/wiki/Geohash

## Version ##

The current Geohash version is 0.9.

Q: Why 0.9?

A: I think it is stable and ready for release, but since I am the only user, I'll wait to someone else has testet it before releasing 1.0...

## Requirements ##

* Lua (http://www.lua.org)
* Luaunit (http://phil.freehackers.org/programs/luaunit/) only required if you want to run the unittests in the ./lua/tests folder
* Luarocks (http://luarocks.org/) for easy installation

## Installation ##

I have added a Luarock Rockspec file to the project for easy installation:

    luarocks install geohash-0.9-1.rockspec

And removal:

    luarocks remove geohash

## Documentation ##

Have a look at the 'lua/example.lua' file to see how to use the library.

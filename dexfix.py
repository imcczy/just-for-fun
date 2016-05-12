#!/usr/bin/env python
#! -*- coding: utf-8 -*-
import mmap
import zlib
import hashlib
import struct
import sys

def readdex(dexfile):
	with open(dexfile,'rb') as dex:
	    dex = mmap.mmap(dex.fileno(),0,access = mmap.ACCESS_COPY)
	return dex

def checksum(dex): 
	sum = zlib.adler32(dex[12:len(dex)])
	intsum = bin(sum & int("1"*32, 2))
	hexsum = struct.pack("<I",int(intsum,2))
	return hexsum


def signature(dex):
	return hashlib.sha1(dex[32:len(dex)]).digest()
	

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print "USAGE: dexfix.py </path/to/dex>"
        sys.exit()
    dex = readdex(sys.argv[1])
    dexsum = checksum(dex)
    dexsig = signature(dex)
    if dexsig != dex[12:32]:
    	print "fixing"
    	dex.seek(12,0)
    	dex.write(dexsig)
    else:
    	print "chcksum pass"
    if dexsum != dex[8:12]:
    	print "fixing"
    	dex.seek(8,0)
    	dex.write(dexsum)
    else:
    	print "signature pass"
    with open("out.dex","wb") as out:
    	out.write(dex)
    dex.close()


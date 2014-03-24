// Copyright (C) 2011 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package jpe.header;

import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.util.Date;

import jsaf.intf.io.IRandomAccess;
import jsaf.io.LittleEndian;
import jsaf.io.Streams;

/**
 * See http://msdn.microsoft.com/en-us/library/ms680313%28v=vs.85%29.aspx
 *
 * @author David A. Solin
 * @version %I% %G%
 */
public class ImageFileHeader {
    public static final int BUFFER_SIZE = 20;

    short machine;
    short numberOfSections;
    long  timeDateStamp;
    int   pointerToSymbolTable;
    int   numberOfSymbols;
    short sizeOfOptionalHeader;
    short characteristics;

    private byte[] buff;

    public ImageFileHeader(InputStream in) throws IOException {
	buff = new byte[BUFFER_SIZE];
	Streams.readFully(in, buff);
	loadFromBuffer();
    }

    public ImageFileHeader(IRandomAccess ra) throws IOException {
	buff = new byte[BUFFER_SIZE];
	ra.readFully(buff);
	loadFromBuffer();
    }

    public void debugPrint(PrintStream out) {
	out.println("RAW Buffer for IMAGE_FILE_HEADER:");
	Streams.hexDump(buff, out);
	out.println("IMAGE_FILE_HEADER:");
	out.println("  machine: " + LittleEndian.toHexString(machine));
	out.println("  numberOfSections: " + LittleEndian.toHexString(numberOfSections));
	out.println("  timeDateStame: " + new Date(timeDateStamp).toString());
	out.println("  pointerToSymbolTable: " + LittleEndian.toHexString(pointerToSymbolTable));
	out.println("  numberOfSymbols: " + LittleEndian.toHexString(numberOfSymbols));
	out.println("  sizeOfOptionalHeader: " + LittleEndian.toHexString(sizeOfOptionalHeader));
	out.println("  characteristics: " + LittleEndian.toHexString(characteristics));
    }

    // Private

    private void loadFromBuffer() {
	if (buff.length != BUFFER_SIZE) {
	    throw new IllegalArgumentException("Illegal Image File Header buffer size: " + buff.length);
	}

	machine				= LittleEndian.getUShort(buff, 0);
	numberOfSections		= LittleEndian.getUShort(buff, 2);
	timeDateStamp			= (long)LittleEndian.getUInt(buff, 4) * 1000; // val is in secs since 1970
	pointerToSymbolTable		= LittleEndian.getUInt(buff, 8);
	numberOfSymbols			= LittleEndian.getUInt(buff, 12);
	sizeOfOptionalHeader		= LittleEndian.getUShort(buff, 16);
	characteristics			= LittleEndian.getUShort(buff, 18);
    }
}

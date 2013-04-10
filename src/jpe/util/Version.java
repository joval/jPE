// Copyright (C) 2011 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package jpe.util;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * A representation of a Windows "version", which is 4 ints.
 *
 * @author David A. Solin
 * @version %I% %G%
 */
public class Version implements Comparable<Version> {
    private int[] parts;

    /**
     * Construct a 4-part version from a pair of integers.
     */
    public Version (int major, int minor) {
	this((0xFFFF0000 & major) >> 16, 0x0000FFFF & major, (0xFFFF0000 & minor) >> 16, 0x0000FFFF & minor);
    }

    public Version (int major_hi, int major_lo, int minor_hi, int minor_lo) {
	parts = new int[4];
	parts[0] = major_hi & 0xFFFF;
	parts[1] = major_lo & 0xFFFF;
	parts[2] = minor_hi & 0xFFFF;
	parts[3] = minor_lo & 0xFFFF;
    }

    public String toString() {
	StringBuffer sb = new StringBuffer();
	for (int i=0; i < parts.length; i++) {
	    if (i > 0) {
		sb.append('.');
	    }
	    sb.append(Integer.toString(parts[i]));
	}
	return sb.toString();
    }

    // Implement Comparable

    public int compareTo(Version other) {
	int num = Math.min(parts.length, other.parts.length);
	for (int i=0; i < num; i++) {
	    if (parts[i] > other.parts[i]) {
		return 1;
	    } else if (parts[i] < other.parts[i]) {
		return -1;
	    }
	}
	if (parts.length > other.parts.length) {
	    for (int i=num; i < parts.length; i++) {
		if (parts[i] > 0) {
		    return 1;
		}
	    }
	} else if (parts.length < other.parts.length) {
	    for (int i=num; i < other.parts.length; i++) {
		if (other.parts[i] > 0) {
		    return -1;
		}
	    }
	}
	return 0;
    }
}

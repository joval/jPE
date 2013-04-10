// Copyright (C) 2011 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package jpe.intf;

/**
 * Characteristice specific to DLLs, for ImageFileHeader.
 *
 * @author David A. Solin
 * @version %I% %G%
 */
public interface DLLCharacteristics {
    int IMAGE_DLLCHARACTERISTICS_DYNAMIC_BASE		= 0x0040;
    int IMAGE_DLLCHARACTERISTICS_FORCE_INTEGRITY 	= 0x0080;
    int IMAGE_DLLCHARACTERISTICS_NX_COMPAT 		= 0x0100;
    int IMAGE_DLLCHARACTERISTICS_NO_ISOLATION 		= 0x0200;
    int IMAGE_DLLCHARACTERISTICS_NO_SEH 		= 0x0400;
    int IMAGE_DLLCHARACTERISTICS_NO_BIND 		= 0x0800;
    int IMAGE_DLLCHARACTERISTICS_WDM_DRIVER 		= 0x2000;
    int IMAGE_DLLCHARACTERISTICS_TERMINAL_SERVER_AWARE 	= 0x8000;
}

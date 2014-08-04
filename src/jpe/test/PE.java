// Copyright (C) 2011 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package jpe.test;

import java.io.IOException;
import java.util.Hashtable;

import jsaf.JSAFSystem;
import jsaf.intf.io.IFile;
import jsaf.intf.io.IFilesystem;
import jsaf.intf.system.IComputerSystem;
import jsaf.intf.system.ISession;
import jsaf.provider.SessionFactory;

import jpe.header.Header;
import jpe.header.ImageOptionalHeader;
import jpe.header.ImageOptionalHeader32;
import jpe.header.ImageOptionalHeader64;
import jpe.resource.version.VsFixedFileInfo;
import jpe.resource.version.VsVersionInfo;
import jpe.util.LanguageConstants;

/**
 * Test class and usage example.
 *
 * @author David A. Solin
 * @version %I% %G%
 */
public class PE {
    public static void main(String[] argv) {
	try {
	    ISession session = (ISession)SessionFactory.newInstance(JSAFSystem.getDataDirectory()).createSession();
	    session.connect();
	    PE pe = new PE(session);
	    pe.test(argv[0]);
	    session.dispose();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    IComputerSystem session;

    public PE(ISession session) {
	this.session = (IComputerSystem)session;
    }

    public void test(String path) {
	IFilesystem fs = session.getFilesystem();
	try {
	    IFile f = fs.getFile(path);
	    System.out.println("Scanning file... " + f.getPath());
	    readPEHeader(f);

//	    Header header = new Header(f);
//	    header.debugPrint(System.out);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Read the Portable Execution format header information and extract data from it.
     */
    public void readPEHeader(IFile file) throws Exception {
	Header header = new Header(file);
	ImageOptionalHeader ioh = header.getNTHeader().getImageOptionalHeader();
	if (ioh instanceof ImageOptionalHeader32) {
	    System.out.println("   Instruction Set: 32-bit");
	} else if (ioh instanceof ImageOptionalHeader64) {
	    System.out.println("   Instruction Set: 64-bit");
	} else {
	    System.out.println("   Instruction Set: Unknown class " + ioh.getClass().getName());
	}

	//
	// Get the MS Checksum from the NT headers
	//
	System.out.println("       MS Checksum: " + ioh.getChecksum());

	//
	// Get the version information string table, in the default language
	//
	String key = VsVersionInfo.LANGID_KEY;
	VsVersionInfo versionInfo = header.getVersionInfo();
	VsFixedFileInfo value = null;
	if (versionInfo != null) {
	    value = versionInfo.getValue();
	    key = versionInfo.getDefaultTranslation();
	}
	Hashtable<String, String> stringTable = versionInfo.getStringTable(key);

	//
	// Get the file version from the VsFixedFileInfo structure
	//
	if (value == null) {
	    System.out.println("           Version: (none)");
	} else {
	    System.out.println("           Version: " + versionInfo.getValue().getFileVersion().toString());
	}

	//
	// Get the language from the key
	//
	String locale = LanguageConstants.getLocaleString(key);
	if (locale == null) {
	    System.out.println("          Language: (unknown key \"" + key + "\")");
	} else {
	    System.out.println("          Language: " + LanguageConstants.getLocaleString(key));
	}

	//
	// Get remaining file information from the StringTable
	//
	if (stringTable.containsKey("CompanyName")) {
	    System.out.println("      Company Name: " + stringTable.get("CompanyName"));
	}
	if (stringTable.containsKey("InternalName")) {
	    System.out.println("     Internal Name: " + stringTable.get("InternalName"));
	}
	if (stringTable.containsKey("ProductName")) {
	    System.out.println("      Product Name: " + stringTable.get("ProductName"));
	}
	if (stringTable.containsKey("OriginalFilename")) {
	    System.out.println(" Original Filename: " + stringTable.get("OriginalFilename"));
	}
	if (stringTable.containsKey("ProductVersion")) {
	    System.out.println("   Product Version: " + stringTable.get("ProductVersion"));
	}
	if (stringTable.containsKey("FileVersion")) {
	    String version = stringTable.get("FileVersion");
	    System.out.println("      File Version: " + version);
	    try {
		System.out.println(" Development Class: " + getDevelopmentClass(version));
	    } catch (IllegalArgumentException e) {
	    }
	}
    }

    /**
     * Retrieve the development class from the FileVersion string, if possible.
     */
    private String getDevelopmentClass(String fileVersion) throws IllegalArgumentException {
	int begin=0, end=0, trunc=0;
	begin = fileVersion.indexOf("(");
	if (begin != -1) {
	    end = fileVersion.indexOf(")");
	    if (end > begin) {
		String vs = fileVersion.substring(begin+1, end);
		trunc = vs.length() - 12; // strip off .XXXXXX-YYY
		if (trunc > 0) {
		    return vs.substring(0, trunc);
		}
	    }
	}
	throw new IllegalArgumentException(fileVersion);
    }
}

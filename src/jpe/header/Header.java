// Copyright (C) 2011 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package jpe.header;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.List;
import java.util.NoSuchElementException;

import jsaf.intf.io.IFile;
import jsaf.intf.io.IRandomAccess;

import jpe.intf.tree.INode;
import jpe.resource.ImageResourceDataEntry;
import jpe.resource.ImageResourceDirectory;
import jpe.resource.ImageResourceDirectoryEntry;
import jpe.resource.Types;
import jpe.resource.version.StringFileInfo;
import jpe.resource.version.StringStructure;
import jpe.resource.version.StringTable;
import jpe.resource.version.Var;
import jpe.resource.version.VarFileInfo;
import jpe.resource.version.VsFixedFileInfo;
import jpe.resource.version.VsVersionInfo;
import jpe.util.tree.TreeHash;

/**
 * Provides simple access to PE file header information.
 *
 * @author David A. Solin
 * @version %I% %G%
 */
public class Header {
    private TreeHash<Object> resources;
    private ImageDOSHeader dosHeader;
    private ImageNTHeaders ntHeader;
    private VsVersionInfo versionInfo;
    private VarFileInfo varFileInfo;

    public Header(IFile file) throws IllegalArgumentException, IOException {
	if (file.isFile()) {
	    if (file.length() == 0) {
		throw new IllegalArgumentException("Zero length: " + file.getPath());
	    }
	    IRandomAccess ra = null;
	    try {
		ra = file.getRandomAccess("r");
		dosHeader = new ImageDOSHeader(ra);
		ra.seek((long)dosHeader.getELFHeaderRVA());
		ntHeader = new ImageNTHeaders(ra);
		resources = new TreeHash<Object>("ImageResourceDirs", "/");
		long rba = ntHeader.getResourceBaseAddress(ImageDataDirectory.RESOURCE_TABLE);
		if (rba == 0) {
		    //
		    // This means that there is no resource section in the PE file
		    //
		} else {
		    ra.seek(rba);
		    ImageResourceDirectory root = new ImageResourceDirectory(ra);
		    long rva = ntHeader.getImageDirEntryRVA(ImageDataDirectory.RESOURCE_TABLE);
		    traverse("", root, ra, rba, rva);
		}
	    } finally {
		if (ra != null) {
		    try {
		        ra.close();
		    } catch (IOException e) {
		    }
		}
	    }
	} else {
	    throw new IllegalArgumentException("Not a file: " + file.getPath());
	}
    }

    public ImageDOSHeader getDOSHeader() {
	return dosHeader;
    }

    public ImageNTHeaders getNTHeader() {
	return ntHeader;
    }

    public VsVersionInfo getVersionInfo() {
	return versionInfo;
    }

    public Object getResource(String path) throws NoSuchElementException {
	return resources.getData(path);
    }

    public void debugPrint(PrintStream out) {
        dosHeader.debugPrint(out);
        ntHeader.debugPrint(out);
	for (INode node : resources.getRoot().getChildren()) {
	    debugPrint(0, node, out);
	}
    }

    // Private

    private void debugPrint(int level, INode node, PrintStream out) {
	StringBuffer sb = new StringBuffer();
	for (int i=0; i < level; i++) {
	    sb.append("  ");
	}
	String indent = sb.toString();

	switch(node.getType()) {
	  case TREE:
	  case BRANCH:
	    out.print(indent);
	    out.println("Dir: " + node.getName());
	    for (INode child : node.getChildren()) {
		debugPrint(level + 1, child, out);
	    }
	    break;

	  case LEAF:
	    out.print(indent);
	    out.println("Node: " + node.getName() + " {");
	    Object obj = resources.getData(node.getPath());
	    if (obj instanceof VsVersionInfo) {
		((VsVersionInfo)obj).debugPrint(out, level + 1);
	    } else if (obj instanceof ImageResourceDataEntry) {
		((ImageResourceDataEntry)obj).debugPrint(out, level + 1);
	    } else {
		out.print(indent);
		out.println("  Unknown Node Datatype: " + obj.getClass().getName());
	    }
	    out.print(indent);
	    out.println("}");
	}
    }

    /**
     * Recursively load resources.  Currently the only resource type that is fully implemented is VS_VERSIONINFO.
     */
    private void traverse(String path, ImageResourceDirectory dir, IRandomAccess ra, long rba, long rva) throws IOException {
        ImageResourceDirectoryEntry[] entries = dir.getChildEntries();
        for (int i=0; i < entries.length; i++) {
            ImageResourceDirectoryEntry entry = entries[i];
            String name = entry.getName(ra, rba);
            if (entry.isDir()) {
                int type = entry.getType();
                if (path.length() == 0 && type >= 0 && type < Types.NAMES.length) {
                    name = Types.NAMES[type];
                }
                ra.seek(rba + entry.getOffset());
                traverse(path + name + "/", new ImageResourceDirectory(ra), ra, rba, rva);
            } else {
                ImageResourceDataEntry de = entry.getDataEntry(ra, rba, rva);
		if (path.startsWith(Types.NAMES[Types.RT_VERSION])) {
		    ra.seek(de.getDataAddress());
		    versionInfo = new VsVersionInfo(ra);
		    resources.putData(path + name, versionInfo);
		} else {
		    resources.putData(path + name, de);
		}
            }
        }
    }
}

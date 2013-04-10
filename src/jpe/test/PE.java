// Copyright (C) 2011 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package jpe.test;

import java.io.IOException;

import jsaf.JSAFSystem;
import jsaf.intf.io.IFilesystem;
import jsaf.intf.system.ISession;
import jsaf.provider.SessionFactory;

import jpe.header.Header;

public class PE {
    public static void main(String[] argv) {
	try {
	    ISession session = (ISession)SessionFactory.newInstance(JSAFSystem.getDataDirectory()).createSession();
	    if (session.connect()) {
		PE pe = new PE(session);
		pe.test(argv[0]);
		session.dispose();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    ISession session;

    public PE(ISession session) {
	this.session = session;
    }

    public void test(String path) {
	System.out.println("Scanning " + path);
	IFilesystem fs = session.getFilesystem();
	try {
	    Header header = new Header(fs.getFile(path));
	    header.debugPrint(System.out);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}

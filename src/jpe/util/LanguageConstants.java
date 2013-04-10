// Copyright (C) 2011 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package jpe.util;

import java.io.IOException;
import java.util.Properties;

/**
 * See http://msdn.microsoft.com/en-us/library/dd318693%28v=vs.85%29.aspx
 *
 * @author David A. Solin
 * @version %I% %G%
 */
public class LanguageConstants {
    private static Properties locales;

    static {
	locales = new Properties();
	ClassLoader cl = LanguageConstants.class.getClassLoader();
	try {
	    locales.load(cl.getResourceAsStream("windows.locales.properties"));
	} catch (Throwable e) {
	    System.err.println("WARNING: missing resource windows.locales.properties");
	}
    }

    public static String getLocaleString(String hex) {
	return locales.getProperty(hex.substring(0, 4).toLowerCase());
    }
}

/* ========================================================================
 * JCommon : a free general purpose class library for the Java(tm) platform
 * ========================================================================
 *
 * (C) Copyright 2000-2014, by Object Refinery Limited and Contributors.
 * 
 * Project Info:  http://www.jfree.org/jcommon/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 * 
 * ----------------
 * IOUtilsTest.java
 * ----------------
 * (C)opyright 2003-2014, by Thomas Morgner and Contributors.
 *
 * Original Author:  Thomas Morgner;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: IOUtilsTest.java,v 1.5 2007/11/02 17:50:35 taqua Exp $
 *
 * Changes 
 * -------
 * 05-Jan-2004 : Initial version
 *  
 */

package org.jfree.io;

import java.io.IOException;
import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Creates a new test case.
 */
public class IOUtilsTest extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(IOUtilsTest.class);
    }

    /**
     * Constructs a new set of tests.
     */
    public IOUtilsTest() {
        super();
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public IOUtilsTest(final String name) {
        super(name);
    }

    /**
     * A test for the createRelativeURL() method.
     * 
     * @throws IOException if there is an I/O problem.
     */
    public void testCreateRelativeURL() throws IOException {
        final URL baseurl = new URL
            ("http://test.com:80/test/a/funny/directory/basefile.xml");

        final URL testInput1 = new URL ("http://test.com:80/test/a/funny/directory/datafile.jpg");
        String result = IOUtils.getInstance().createRelativeURL(testInput1, baseurl);
        assertEquals("datafile.jpg", result);
        assertEquals(testInput1, new URL (baseurl, result));

        final URL testInput2 = new URL ("http://test.com:80/test/adatafile.jpg");
        result = IOUtils.getInstance().createRelativeURL(testInput2, baseurl);
        assertEquals("../../../adatafile.jpg", result);
        assertEquals(testInput2, new URL (baseurl, result));

        final URL testInput3 = new URL ("http://test.com:80/test/adatafile.jpg?query=test");
        result = IOUtils.getInstance().createRelativeURL(testInput3, baseurl);
        assertEquals("../../../adatafile.jpg?query=test", result);
        assertEquals(testInput3, new URL (baseurl, result));
    }
    
}

/* ========================================================================
 * JCommon : a free general purpose class library for the Java(tm) platform
 * ========================================================================
 *
 * (C) Copyright 2000-2013, by Object Refinery Limited and Contributors.
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
 * -------------
 * Licences.java
 * -------------
 * (C) Copyright 2001-2013, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: Licences.java,v 1.4 2005/11/16 15:58:41 taqua Exp $
 *
 * Changes
 * -------
 * 26-Nov-2001 : Version 1 (DG);
 * 29-Jan-2002 : Appended the instructions to the end of the GNU LGPL and GPL 
 *               to comply with GNU requirements (DG);
 * 28-Feb-2002 : Moved to package com.jrefinery.ui.about (DG);
 * 08-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 17-Aug-2013 : Removed the text from GPL and LGPL static strings, which just
 *               waste memory, and deprecated to point people to getGPL() and
 *               getLGPL() (DG);
 *
 */

package org.jfree.ui.about;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Contains the full texts of the GNU General Public Licence and the GNU Lesser
 * General Public Licence.  These are used in the presentation of a standard 
 * 'About' frame.
 */
public class Licences {

    /** 
     * The GNU General Public Licence.
     * 
     * @deprecated Use {@link #getGPL()}. 
     */
    public static final String GPL = "GNU GENERAL PUBLIC LICENSE\n";

    /** 
     * The GNU Lesser General Public Licence. 
     * 
     * @deprecated Use {@link #getLGPL()}. 
     */
    public static final String LGPL = "GNU LESSER GENERAL PUBLIC LICENSE\n";

    /** The singleton instance of this class. */
    private static Licences singleton;

    /**
     * Returns a reference to the single instance of this class.  In fact,
     * there is little point to having an instance of this class, but we're
     * leaving it that way for backwards compatibility until the JCommon 
     * retirement.
     *
     * @return the instance reference.
     */
    public static Licences getInstance() {
        if (singleton == null) {
            singleton = new Licences();
        }
        return singleton;
    }

    /**
     * Returns the GPL (v2.1) text.
     *
     * @return the GPL licence text.
     */
    public String getGPL() {
        return readStringResource("gpl-2.0.txt");
    }

    /**
     * Returns the LGPL (v2.1) text.
     *
     * @return the LGPL licence text.
     */
    public String getLGPL() {
        return readStringResource("lgpl-2.1.txt");
    }
    
    private String readStringResource(String name) {
        StringBuilder sb = new StringBuilder();
        InputStreamReader streamReader = null;
        try {
            InputStream inputStream = getClass().getResourceAsStream(name);
            streamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader in = new BufferedReader(streamReader);
            for (String line; (line = in.readLine()) != null;) {
              sb.append(line).append("\n");
            }
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                streamReader.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return sb.toString();        
    }
 
}

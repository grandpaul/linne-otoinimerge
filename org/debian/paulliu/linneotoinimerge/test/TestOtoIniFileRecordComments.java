/*
    Copyright(C) 2010 Ying-Chun Liu(PaulLiu). All rights reserved.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

package org.debian.paulliu.linneotoinimerge.test;

import org.debian.paulliu.linneotoinimerge.OtoIniFileRecordComments;
import junit.framework.TestCase;
import junit.textui.TestRunner;
import junit.framework.TestSuite;
import java.io.*;

public class TestOtoIniFileRecordComments extends TestCase {

    private org.debian.paulliu.linneotoinimerge.OtoIniFileRecordComments otoFileRecordComments;

    @Override
    public void setUp() {
	otoFileRecordComments = new org.debian.paulliu.linneotoinimerge.OtoIniFileRecordComments();
    }

    @Override
    public void tearDown() {
	otoFileRecordComments = null;
    }

    public void testOtoIniFileRecordParseString() {
	String testData = ";test";
	OtoIniFileRecordComments comments = new org.debian.paulliu.linneotoinimerge.OtoIniFileRecordComments();

	assertEquals(comments.getComments().size(), 0);
	comments.addComments(testData);
	assertEquals(comments.getComments().size(), 1);
	comments.addComments(testData);
	assertEquals(comments.getComments().size(), 2);
    }
}

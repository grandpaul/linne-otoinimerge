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

import org.debian.paulliu.linneotoinimerge.OtoIniFileRecord;
import junit.framework.TestCase;
import junit.textui.TestRunner;
import junit.framework.TestSuite;
import java.io.*;

public class TestOtoIniFileRecord extends TestCase {

    private org.debian.paulliu.linneotoinimerge.OtoIniFileRecord otoFileRecord;

    @Override
    public void setUp() {
	otoFileRecord = new org.debian.paulliu.linneotoinimerge.OtoIniFileRecord();
    }

    @Override
    public void tearDown() {
	otoFileRecord = null;
    }

    public void testOtoIniFileRecordFields() {
	String filename="a.wav";
	String path=File.separator+"tmp";
	String alias="T";
	double offset=100.0;
	double consonant=200.0;
	double blank=-1000.0;
	double prevoice=50.0;
	double overlap=10.0;

	otoFileRecord.setFilename(filename);
	otoFileRecord.setAlias(alias);
	otoFileRecord.setPath(new File(path));
	otoFileRecord.setOffset(offset);
	otoFileRecord.setConsonant(consonant);
	otoFileRecord.setBlank(blank);
	otoFileRecord.setPrevoice(prevoice);
	otoFileRecord.setOverlap(overlap);

	assertEquals(filename, otoFileRecord.getFilename().getName());
	assertEquals(alias, otoFileRecord.getAlias());
	assertEquals("tmp", otoFileRecord.getPath().getName());
	assertEquals(offset, otoFileRecord.getOffset());
	assertEquals(consonant, otoFileRecord.getConsonant());
	assertEquals(blank, otoFileRecord.getBlank());
	assertEquals(prevoice, otoFileRecord.getPrevoice());
	assertEquals(overlap, otoFileRecord.getOverlap());
    }

    public void testOtoIniFileRecordParseString() {
	String testData = "test.wav=alias,100,200,-1000,50,10";
	OtoIniFileRecord r1 = OtoIniFileRecord.parseOtoIniFileRecord(testData);

	assertEquals("test.wav", r1.getFilename().getName());
	assertEquals("alias", r1.getAlias());
	assertEquals(100.0, r1.getOffset());
	assertEquals(200.0, r1.getConsonant());
	assertEquals(-1000.0, r1.getBlank());
	assertEquals(50.0, r1.getPrevoice());
	assertEquals(10.0, r1.getOverlap());
    }

    public void testOtoIniFileRecordToString() {
	OtoIniFileRecord r1 = new OtoIniFileRecord();
	r1.setFilename("test.wav");
	r1.setAlias("alias");
	r1.setPath(null);
	r1.setOffset(100.0);
	r1.setConsonant(200.0);
	r1.setBlank(-1000.0);
	r1.setPrevoice(50.0);
	r1.setOverlap(10.0);
	OtoIniFileRecord r2 = OtoIniFileRecord.parseOtoIniFileRecord(r1.toString());
	assert(r1.compareTo(r2)==0);
	assertEquals(r1.toString(), r2.toString());
    }
}

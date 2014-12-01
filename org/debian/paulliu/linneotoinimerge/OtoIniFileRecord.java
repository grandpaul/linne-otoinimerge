/*
    Copyright(C) 2013 Ying-Chun Liu(PaulLiu). All rights reserved.

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

package org.debian.paulliu.linneotoinimerge;

import java.io.*;

public class OtoIniFileRecord implements Comparable<OtoIniFileRecord> {
    private File filename;
    private String alias;
    private File path;
    private double offset;
    private double consonant;
    private double blank;
    private double prevoice;
    private double overlap;
    private OtoIniFileRecordComments comments;

    public OtoIniFileRecord() {
	path=null;
    }

    public void setFilename(String filename) {
	this.filename = new File(filename);
    }
    public File getFilename() {
	return filename;
    }

    public void setAlias(String alias) {
	this.alias = alias;
    }
    public String getAlias() {
	return alias;
    }

    public void setPath(File path) {
	this.path = path;
    }
    public File getPath() {
	return path;
    }

    public void setOffset(double offset) {
	this.offset = offset;
    }
    public double getOffset() {
	return offset;
    }

    public void setConsonant(double consonant) {
	this.consonant = consonant;
    }
    public double getConsonant() {
	return consonant;
    }

    public void setBlank(double blank) {
	this.blank = blank;
    }
    public double getBlank() {
	return blank;
    }

    public void setPrevoice(double prevoice) {
	this.prevoice = prevoice;
    }
    public double getPrevoice() {
	return prevoice;
    }

    public void setOverlap(double overlap) {
	this.overlap = overlap;
    }
    public double getOverlap() {
	return overlap;
    }

    public void setComments(OtoIniFileRecordComments comments) {
	this.comments = comments;
    }
    public OtoIniFileRecordComments getComments() {
	return comments;
    }

    public int compareTo(OtoIniFileRecord b) {
	int r = 0;
	r = alias.compareTo(b.alias);
	if (r!=0) {
	    return r;
	}
	r = filename.compareTo(b.filename);
	if (r!=0) {
	    return r;
	}
	if (offset < b.offset) {
	    return -1;
	} else if (offset > b.offset) {
	    return 1;
	}
	if (blank < b.blank) {
	    return -1;
	} else if (blank > b.blank) {
	    return 1;
	}
	if (consonant < b.consonant) {
	    return -1;
	} else if (consonant > b.consonant) {
	    return 1;
	}
	if (prevoice < b.prevoice) {
	    return -1;
	} else if (prevoice > b.prevoice) {
	    return 1;
	}
	if (overlap < b.overlap) {
	    return -1;
	} else if (overlap > b.overlap) {
	    return 1;
	}
	return 0;
    }

    public String toString() {
	StringBuffer retBuf = new StringBuffer();
	String ret=null;
	retBuf.append(String.format("%1$s=%2$s", filename.getName(), alias));
	double[] data = new double[5];
	data[0] = offset;
	data[1] = consonant;
	data[2] = blank;
	data[3] = prevoice;
	data[4] = overlap;

	for (int i=0; i<data.length; i++) {
	    retBuf.append(",");
	    String tmp1 = String.format("%1$.3f", data[i]);
	    if (tmp1.endsWith(".000")) {
		tmp1 = tmp1.substring(0, tmp1.length()-4);
	    }
	    retBuf.append(tmp1);
	}
	ret = retBuf.toString();
	return ret;
    }

    public static OtoIniFileRecord parseOtoIniFileRecord(String s) throws java.lang.IllegalArgumentException {
	if (s==null) {
	    throw (new java.lang.IllegalArgumentException("string is null"));
	}
	String[] s_a=null;
	s_a = s.split("[=]");
	if (s_a == null || s_a.length != 2) {
	    throw (new java.lang.IllegalArgumentException("= is not 1"));
	}
	String filename = s_a[0];
	String[] s_b = null;
	s_b = s_a[1].split("[,]");
	if (s_b == null || s_b.length != 6) {
	    throw (new java.lang.IllegalArgumentException(", is not 5"));
	}
	String alias = s_b[0];
	String offset_s = s_b[1];
	String consonant_s = s_b[2];
	String blank_s = s_b[3];
	String prevoice_s = s_b[4];
	String overlap_s = s_b[5];

	double offset=0.0;
	double consonant=0.0;
	double blank=0.0;
	double prevoice=0.0;
	double overlap=0.0;
	try {
	    offset = Double.parseDouble(offset_s);
	    consonant = Double.parseDouble(consonant_s);
	    blank = Double.parseDouble(blank_s);
	    prevoice = Double.parseDouble(prevoice_s);
	    overlap = Double.parseDouble(overlap_s);
	} catch (Exception e) {
	    throw (new java.lang.IllegalArgumentException("parse numbers error"));
	}
	OtoIniFileRecord rec = new OtoIniFileRecord();
	rec.setFilename(filename);
	rec.setAlias(alias);
	rec.setPath(null);
	rec.setOffset(offset);
	rec.setConsonant(consonant);
	rec.setBlank(blank);
	rec.setPrevoice(prevoice);
	rec.setOverlap(overlap);
	return rec;
    }

}

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
import java.util.*;

/**
 * Class to read an oto.ini
 *
 * The format of oto.ini:
 * Filename Alias Offset 子音部 Blank 先行發聲 Overlap
 * Unit: ms
 * Filename: the file name of wav file.
 * Alias: the alias used in editor.
 * Offset: 從 0 秒開始算. 數值 >=0 
 * 子音部: 從 offset 開始算, 數值 >=0
 * Blank: 從尾端開始倒算. 數值 >=0. 其中 Offset+Blank 不可能超過整個 wav 長度
 *        若數值 < 0. 則為從 Offset 起算
 * 先行發聲: 0 ~ wav 長度
 * Overlap: 任意值
 *
 */
public class OtoIniFileReader {
    public OtoIniFileReader() {
    }

    public OtoIniFileFormat getOtoIniFileFormat(File otoFile) {
	FileInputStream fin = null;

	try {
	    fin = new FileInputStream(otoFile);
	} catch (java.io.FileNotFoundException e) {
	    return (new OtoIniFileFormat("SJIS"));
	}

	boolean ret = false;
	try {
	    int c;
	    int c2;
	    c = fin.read();
	    if (c==0xff) {
		c2 = fin.read();
		if (c2==0xfe) {
		    ret=true;
		}
	    } else if (c==0xfe) {
		c2 = fin.read();
		if (c2==0xff) {
		    ret=true;
		}
	    }
	} catch (Exception e) {
	}

	try {
	    fin.close();
	} catch (Exception e) {
	}

	if (ret) {
	    return (new OtoIniFileFormat("UTF-16"));
	} else {
	    return (new OtoIniFileFormat("SJIS"));
	}

    }

    /**
     * get OtoIniFileRecords from file
     * @return an ArrayList of OtoFileRecord.
     */
    public ArrayList<OtoIniFileRecord> getOtoIniFileRecords(File otoFile) {
	ArrayList<OtoIniFileRecord> ret = new ArrayList<OtoIniFileRecord>();
	String encoding = null;

	OtoIniFileFormat format = getOtoIniFileFormat(otoFile);
	encoding = format.getEncodig();

	FileInputStream fin_is = null;

	try {
	    fin_is = new FileInputStream(otoFile);
	} catch (java.io.FileNotFoundException e) {
	    return ret;
	}

	BufferedReader fin = null;
	try {
	    InputStreamReader fin_sr=null;
	    fin_sr = new InputStreamReader(fin_is, encoding);
	    fin = new BufferedReader(fin_sr);
	} catch (Exception e) {
	    return ret;
	}

	while (true) {
	    String s=null;
	    try {
		s = fin.readLine();
	    } catch (Exception e) {
		break;
	    }
	    if (s==null) {
		break;
	    }
	    OtoIniFileRecord rec = null;
	    try {
		rec = OtoIniFileRecord.parseOtoIniFileRecord(s);
	    } catch (Exception e) {
		continue;
	    }
	    File path = null;
	    try {
		path = otoFile.getAbsoluteFile().getParentFile();
	    } catch (Exception e) {
		continue;
	    }
	    if (path == null) {
		continue;
	    }
	    rec.setPath(path);
	    ret.add(rec);
	}

	return ret;
    }

}

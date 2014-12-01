/*
    Copyright(C) 2014 Ying-Chun Liu(PaulLiu). All rights reserved.

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

public class OtoIniFileRecordComments implements Comparable<OtoIniFileRecordComments> {
    private ArrayList<String> comments;

    public OtoIniFileRecordComments() {
	comments = new ArrayList<String>();
    }

    public ArrayList<String> getComments() {
	return comments;
    }

    public void addComments(String s) {
	comments.add(s);
    }

    public int compareTo(OtoIniFileRecordComments b) {
	int r=0;
	if (comments.size() > b.getComments().size()) {
	    r=1;
	} else if (comments.size() < b.getComments().size()) {
	    r=-1;
	}
	if (r!=0) {
	    return r;
	}

	for (int i=0; i<comments.size(); i++) {
	    r = comments.get(i).compareTo(b.getComments().get(i));
	    if (r!=0) {
		return r;
	    }
	}
	return r;
    }
}

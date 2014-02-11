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

public class Main {

    private java.util.logging.Logger logger = null;
    private static String logger_name = "linne";
    private Stdin stdin = new Stdin();

    public enum DiffDataType {
	ADD, DELETE, UPDATE
    }

    public class DiffData {
	public DiffDataType type;
	public OtoIniFileRecord oldRecord;
	public OtoIniFileRecord newRecord;
    }

    public Main() {
	this.logger = java.util.logging.Logger.getLogger(Main.logger_name);
    }

    public void usage() {
    }

    public int begin(String[] argv) {
	if (argv.length <= 0) {
	    usage();
	    return 0;
	}
	if (argv[0].compareTo("diff")==0) {
	    diff(new File(argv[1]), new File(argv[2]));
	    return 0;
	} else if (argv[0].compareTo("merge")==0) {
	    int result;
	    result = merge(new File(argv[1]), new File(argv[2]), new File(argv[3]), new File(argv[4]));
	    return result;
	} else if (argv[0].compareTo("resolve")==0) {
	    int result;
	    result = resolve(new File(argv[1]), new File(argv[2]), new File(argv[3]), new File(argv[4]));
	    return result;
	}
	return 0;
    }

    Map<String, OtoIniFileRecord> getDataFromFile(File otoFile) {
	OtoIniFileReader otoReader = new OtoIniFileReader();
	ArrayList<OtoIniFileRecord> result1 = otoReader.getOtoIniFileRecords(otoFile);
	TreeMap<String, OtoIniFileRecord> map1 = new TreeMap<String, OtoIniFileRecord>();
	for (Iterator<OtoIniFileRecord> i = result1.iterator(); i.hasNext(); ) {
	    OtoIniFileRecord r = i.next();
	    map1.put(r.getAlias(), r);
	}
	return map1;
    }

    private ArrayList<DiffData> getDiff(Map<String, OtoIniFileRecord> oldData, Map<String, OtoIniFileRecord> newData) {
	ArrayList<DiffData> ret = new ArrayList<DiffData> ();
	Iterator<String> i1=oldData.keySet().iterator();
	Iterator<String> i2=newData.keySet().iterator();
	String key1 = null;
	String key2 = null;
	if (i1.hasNext()) {
	    key1 = i1.next();
	}
	if (i2.hasNext()) {
	    key2 = i2.next();
	}
	while (true) {
	    int d=0;
	    if (key1==null) {
		if (key2 == null) {
		    break;
		} else {
		    d=1;
		}
	    } else if (key2 == null) {
		d=-1;
	    } else {
		d = key1.compareTo(key2);
	    }
	    if (d==0) {
		/* check if it is update */
		OtoIniFileRecord r1 = oldData.get(key1);
		OtoIniFileRecord r2 = newData.get(key2);
		if (r1.compareTo(r2)==0) {
		} else {
		    DiffData d1 = new DiffData();
		    d1.type = Main.DiffDataType.UPDATE;
		    d1.oldRecord = r1;
		    d1.newRecord = r2;
		    ret.add(d1);
		}
		if (i1.hasNext()) {
		    key1=i1.next();
		} else {
		    key1=null;
		}
		if (i2.hasNext()) {
		    key2=i2.next();
		} else {
		    key2=null;
		}
	    } else if (d<0) {
		/* key1 is removed */
		OtoIniFileRecord r1 = oldData.get(key1);
		DiffData d1 = new DiffData();
		d1.type = Main.DiffDataType.DELETE;
		d1.oldRecord = r1;
		ret.add(d1);
		if (i1.hasNext()) {
		    key1=i1.next();
		} else {
		    key1=null;
		}
	    } else {
		/* key2 is newly added */
		OtoIniFileRecord r2 = newData.get(key2);
		DiffData d1 = new DiffData();
		d1.type = Main.DiffDataType.ADD;
		d1.newRecord = r2;
		ret.add(d1);
		if (i2.hasNext()) {
		    key2=i2.next();
		} else {
		    key2=null;
		}
	    }
	}
	return ret;
    }

    public void diff(File otoFile1, File otoFile2) {
	Map<String, OtoIniFileRecord> data1 = getDataFromFile(otoFile1);
	Map<String, OtoIniFileRecord> data2 = getDataFromFile(otoFile2);
	ArrayList<DiffData> diffs = getDiff(data1, data2);
	for (Iterator<DiffData> i = diffs.iterator(); i.hasNext(); ) {
	    DiffData d = i.next();
	    switch(d.type) {
	    case ADD:
		System.out.format("+%1$s%n",d.newRecord.toString());
		break;
	    case DELETE:
		System.out.format("-%1$s%n",d.oldRecord.toString());
		break;
	    case UPDATE:
		System.out.format("<%1$s%n",d.oldRecord.toString());
		System.out.format(">%1$s%n",d.newRecord.toString());
		break;
	    default:
		break;
	    }
	}
    }

    public int merge(File remote, File base, File local, File merged) {
	Map<String, OtoIniFileRecord> remoteData = getDataFromFile(remote);
	Map<String, OtoIniFileRecord> mergedData = getDataFromFile(remote);
	Map<String, OtoIniFileRecord> baseData = getDataFromFile(base);
	Map<String, OtoIniFileRecord> localData = getDataFromFile(local);

	ArrayList<DiffData> diffs = getDiff(baseData, localData);
	for (Iterator<DiffData> i = diffs.iterator(); i.hasNext(); ) {
	    DiffData d = i.next();
	    switch(d.type) {
	    case ADD:
		if (remoteData.containsKey(d.newRecord.getAlias())) {
		    OtoIniFileRecord old = remoteData.get(d.newRecord.getAlias());
		    if (old.compareTo(d.newRecord) != 0) {
			System.out.format("Failed to add \"%1$s\" because \"%2$s\" existed%n",d.newRecord.toString(), old.toString());
			return 1;
		    }
		} else {
		    mergedData.put(d.newRecord.getAlias(), d.newRecord);
		}
		break;
	    case DELETE:
		if (remoteData.containsKey(d.oldRecord.getAlias())) {
		    OtoIniFileRecord old = remoteData.get(d.oldRecord.getAlias());
		    if (old.compareTo(d.oldRecord) != 0) {
			System.out.format("Failed to delete \"%1$s\" because \"%2$s\" existed%n",d.oldRecord.toString(), old.toString());
			return 1;
		    } else {
			mergedData.remove(d.oldRecord.getAlias());
		    }
		} else {
		}
		break;
	    case UPDATE:
		if (remoteData.containsKey(d.oldRecord.getAlias())) {
		    OtoIniFileRecord old = remoteData.get(d.oldRecord.getAlias());
		    if (old.compareTo(d.newRecord) == 0) {
		    } else if (old.compareTo(d.oldRecord) != 0) {
			System.out.format("Failed to update \"%1$s\" because \"%2$s\" existed%n",d.oldRecord.toString(), old.toString());
			return 1;
		    } else {
			mergedData.put(d.oldRecord.getAlias(), d.newRecord);
		    }
		} else {
		    mergedData.put(d.newRecord.getAlias(), d.newRecord);
		}
		break;
	    default:
		break;
	    }
	}
	PrintStream fout = null;
	try {
	    fout = new PrintStream(merged);
	} catch (Exception e) {
	    System.out.println(e.toString());
	    return 1;
	}
	for (Iterator<String> i = mergedData.keySet().iterator(); i.hasNext(); ) {
	    String alias = i.next();
	    OtoIniFileRecord r = mergedData.get(alias);
	    fout.println(r.toString());
	}
	fout.close();
	return 0;
    }

    public int resolve(File remote, File base, File local, File merged) {
	Map<String, OtoIniFileRecord> remoteData = getDataFromFile(remote);
	Map<String, OtoIniFileRecord> mergedData = getDataFromFile(remote);
	Map<String, OtoIniFileRecord> baseData = getDataFromFile(base);
	Map<String, OtoIniFileRecord> localData = getDataFromFile(local);

	ArrayList<DiffData> diffs = getDiff(baseData, localData);
	for (Iterator<DiffData> i = diffs.iterator(); i.hasNext(); ) {
	    DiffData d = i.next();
	    switch(d.type) {
	    case ADD:
		if (remoteData.containsKey(d.newRecord.getAlias())) {
		    OtoIniFileRecord old = remoteData.get(d.newRecord.getAlias());
		    if (old.compareTo(d.newRecord) != 0) {
			System.out.format("Current: %1$s%n", old.toString());
			System.out.format("Add: %1$s%n", d.newRecord.toString());
			boolean loopFlag=true;
			while (loopFlag) {
			    System.out.print("Do you want to Keep or Update? (K/U)");
			    System.out.flush();
			    String r = stdin.gets();
			    if (r.compareToIgnoreCase("K")==0) {
				loopFlag=false;
			    } else if (r.compareToIgnoreCase("U")==0) {
				mergedData.put(d.newRecord.getAlias(), d.newRecord);
				loopFlag=false;
			    }
			}
		    }
		} else {
		    mergedData.put(d.newRecord.getAlias(), d.newRecord);
		}
		break;
	    case DELETE:
		if (remoteData.containsKey(d.oldRecord.getAlias())) {
		    OtoIniFileRecord old = remoteData.get(d.oldRecord.getAlias());
		    if (old.compareTo(d.oldRecord) != 0) {
			System.out.format("Current: %1$s%n", old.toString());
			System.out.format("Delete: %1$s%n", d.oldRecord.toString());
			boolean loopFlag=true;
			while (loopFlag) {
			    System.out.print("Do you want to Keep or Delete? (K/D)");
			    System.out.flush();
			    String r = stdin.gets();
			    if (r.compareToIgnoreCase("K")==0) {
				loopFlag=false;
			    } else if (r.compareToIgnoreCase("D")==0) {
				mergedData.remove(d.oldRecord.getAlias());
				loopFlag=false;
			    }
			}
		    } else {
			mergedData.remove(d.oldRecord.getAlias());
		    }
		} else {
		}
		break;
	    case UPDATE:
		if (remoteData.containsKey(d.oldRecord.getAlias())) {
		    OtoIniFileRecord old = remoteData.get(d.oldRecord.getAlias());
		    if (old.compareTo(d.newRecord) == 0) {
		    } else if (old.compareTo(d.oldRecord) != 0) {
			System.out.format("Current: %1$s%n", old.toString());
			System.out.format("Update from: %1$s%n", d.oldRecord.toString());
			System.out.format("Update to: %1$s%n", d.newRecord.toString());
			boolean loopFlag=true;
			while (loopFlag) {
			    System.out.print("Do you want to Keep current or keep From or Update? (K/F/U)");
			    System.out.flush();
			    String r = stdin.gets();
			    if (r.compareToIgnoreCase("K")==0) {
				loopFlag=false;
			    } else if (r.compareToIgnoreCase("F")==0) {
				mergedData.put(d.oldRecord.getAlias(), d.oldRecord);
				loopFlag=false;
			    } else if (r.compareToIgnoreCase("U")==0) {
				mergedData.put(d.newRecord.getAlias(), d.newRecord);
				loopFlag=false;
			    }
			}
		    } else {
			mergedData.put(d.oldRecord.getAlias(), d.newRecord);
		    }
		} else {
		    mergedData.put(d.newRecord.getAlias(), d.newRecord);
		}
		break;
	    default:
		break;
	    }
	}
	PrintStream fout = null;
	try {
	    fout = new PrintStream(merged);
	} catch (Exception e) {
	    System.out.println(e.toString());
	    return 1;
	}
	for (Iterator<String> i = mergedData.keySet().iterator(); i.hasNext(); ) {
	    String alias = i.next();
	    OtoIniFileRecord r = mergedData.get(alias);
	    fout.println(r.toString());
	}
	fout.close();
	return 0;
    }

    public static void main(String[] argv) {
	int ret=0;
	Main main = new Main();
	ret = main.begin(argv);
	System.exit(ret);
    }
}

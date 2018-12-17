/*
Copyright (C) 2006 Timo Tuunanen

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package lice.objects;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;

/**
 * Global storage for all dependency objects (source files, libraries, compiled objects)
 * When new package is analyzed all files will be stored into this structure
 * 
 * Contains lice.objects.TargetFile 's
 */
public class DependencyObjects {

	public static TreeMap<String, TargetFile> objects = new TreeMap<String, TargetFile>();
	
	public static void removeObject( String objectName ) {
		
    	TargetFile t = objects.get(objectName); 
    	if ( t != null ) {
    		Vector<TargetFile> parents = t.getParents();
    		for (TargetFile file : parents) {
				file.removeDependency(objectName);
			}
    	}
    	objects.remove(objectName); 
		
	}

	public static void removeObjectsWithPath( String path ) {

		Vector <TargetFile> toRemove = new Vector<TargetFile>();
		for (Iterator iter = DependencyObjects.objects.values().iterator(); iter.hasNext();) {
			TargetFile element = (TargetFile) iter.next();
			if ( element.getFilePath().equalsIgnoreCase(path)) {
	    		Vector<TargetFile> parents = element.getParents();
	    		for (TargetFile file : parents) {
					file.removeDependency(element.getFileName());
				}
	    		toRemove.add( element );
			}
		}
		
		for (TargetFile file : toRemove) {
	    	objects.remove(file.getFileName()); 
		}
		
	}

}

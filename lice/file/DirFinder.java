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
package lice.file;

import java.io.File;
import java.util.Vector;

/**
 * @author timtuun
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DirFinder {

	public DirFinder(String dirName) {
		directoryFilter = dirName;
	}
	
	public Vector findDirectories( String from ) {
		File dir = new File( from );
		
		File [] f = dir.listFiles();
		for (int i = 0; i < f.length; i++) {
			if ( f[i].isDirectory() ) {
//				System.out.println("Recursive dir: " + f[i].toString());
				if ( f[i].toString().endsWith(directoryFilter)) {
					foundDirs.add( f[i]);
				}
				findDirectories( f[i].toString() );
			}
		}
		return foundDirs;
	}
	
	private String directoryFilter = "";
	private Vector foundDirs = new Vector();
}

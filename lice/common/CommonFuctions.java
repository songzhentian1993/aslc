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
package lice.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;

import javax.print.attribute.standard.Finishings;

import lice.objects.DependencyObjects;
import lice.objects.TargetFile;

/**
 * @author timot
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CommonFuctions {

	public static int countAllSourceFiles( String path ) {
		int sourcefiles = 0;
		
		sourcefiles = findSourceFiles( path );
		
		return sourcefiles;
	}
	
	private static int findSourceFiles( String directoryName ) {
		int count = 0;
		File directory = new File(directoryName);
		File [] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			if ( files[i].isDirectory() ) {
				count += findSourceFiles( files[i].getPath() );
			} else {
				String fileName = files[i].getName();
				if ( CommonFuctions.isSourceFile(fileName) ) {
					count++;
				}
			}
		}
		return count;
	}
	
	
	
	public static Vector<TargetFile> getTopLevelObjects() {
		
		Vector<TargetFile> vec = new Vector<TargetFile>();
		// Show only files that have dependencies
		for (Iterator iter = DependencyObjects.objects.values().iterator(); iter.hasNext();) {
			TargetFile element = (TargetFile) iter.next();
			if ( !element.hasParents() ) {
				vec.add( element );
			}
			
/*			if( element.getFileType().equals( TargetFile.FILE_TYPE_LICENSE ) ) {
				vec.add( element );				
			} */
		}
		
		return vec;
	}

	public static Vector getTargetFileVector( ) {
		
		Vector vec = new Vector();
		for (Iterator iter = DependencyObjects.objects.values().iterator(); iter.hasNext();) {
			TargetFile element = (TargetFile) iter.next();
			vec.add( element );
		}
		
		return vec;
	}

	public static Vector getLicenses() {
		
		Vector vec = new Vector();
		// Show only files that have dependencies
		for (Iterator iter = DependencyObjects.objects.values().iterator(); iter.hasNext();) {
			TargetFile element = (TargetFile) iter.next();
			String licenseName = element.getLicense().getLicenseName();
			if ( !vec.contains( licenseName ) && !licenseName.equals("")) {
				vec.add( licenseName );				
			}
		}
		
		return vec;
	}

	public static boolean isBinaryFile ( String fileName ) throws Exception {
		boolean result = false;
		if ( fileName.endsWith(".o") || 
			fileName.endsWith(".so") || 
			fileName.endsWith(".lo") || 
			fileName.endsWith(".a") ) {

			return true;
		}

		FileReader in = null;
		File inputFile = new File(fileName);
		try {
			in = new FileReader(inputFile);
			// Make sure that the file is not binary
			FileInputStream fs = new FileInputStream( inputFile );
			long fileLength = inputFile.length();
			int readBytes = 0;
			if ( fileLength > 200 ) 
				readBytes = 200;
			else 
				readBytes = (int)fileLength;
				
			byte [] bytes = new byte [readBytes];
			fs.read( bytes, 0, readBytes );
			for (int i = 0; i < bytes.length; i++) {
				if ( bytes[i] == 0 ) {
					result = true;
				}
			}
			in.close();
		}
		catch (Exception e){
			System.err.println("Exception while trying to read a file: " + fileName);
			e.printStackTrace();
			if ( in != null ) {
				in.close();
			}
			throw e;
		}		
		
		return result;
	}

	public static boolean isFile( String fileName ) {
		
		boolean exists = (new File(fileName)).exists();
	    return exists;
	}

	private static void readSourceFileExtensionConfig() {
		FileReader fr = null;
		try {
			fr = new FileReader( "./config/config.txt" );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		BufferedReader buf = new BufferedReader( fr );
		String s = "";
		try {
			while ( (s = buf.readLine()) != null) {
				if ( s.startsWith("SOURCE_FILES=") ) {
					String suffixes = s.substring( s.indexOf('=') + 1, s.length() );
					sourceFileExtensions = suffixes.split( " " );
					break;
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			fr.close();
			buf.close();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
	}
	
	public static boolean isSourceFile( String fileName ) {
		
		boolean isSource = false;
		
		if ( sourceFileExtensions == null ) {
			readSourceFileExtensionConfig();
		} 
			
		for (int i = 0; i < sourceFileExtensions.length; i++) {
			if ( fileName.endsWith( sourceFileExtensions[i] ) ) {
				isSource = true;
				break;
			}
		}
		
		return isSource;
	}

	static String [] sourceFileExtensions = null;
}

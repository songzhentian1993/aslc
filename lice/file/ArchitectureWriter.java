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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Vector;

import lice.common.CommonFuctions;
import lice.objects.DependencyObjects;
import lice.objects.TargetFile;

public class ArchitectureWriter {


	public ArchitectureWriter( String path ) {
		this.path = path;
	}

	public void writeArchitecture( String fileName ) {

		try {
			Vector topLevelObjects = CommonFuctions.getTopLevelObjects();

			Writer out = openWriter( fileName );
			if ( out == null ) {
				System.out.println("Unable to open writer");
				return;
			}
	
			String packageName = path.substring( path.lastIndexOf("/")+1, path.length() );
			
//käännösvirhe??			out.append( "digraph temp { \n");
			// 	out.append( "\tsize=\"3,3\";\n");
//käännösvirhe?? out.append( "\tnode [color=lightblue2, style=filled];\n");
			
			for (Iterator iter = DependencyObjects.objects.values().iterator(); iter.hasNext();) {
				TargetFile element = (TargetFile) iter.next();
				if ( !element.getFileName().startsWith( path ) && 
						element.getFileType().equals( TargetFile.FILE_TYPE_OBJECT )) {
//käännösvirhe??					out.append( "\t\"" + packageName + "\" -> \"" + element.getDisplayFileName() + "\"\n");
					//System.out.println("External dependency: " + element.getFileName());
				}
			}

//käännösvirhe??			out.append("}");
			out.close();
		} catch ( IOException ex ) {
			ex.printStackTrace();
		}
	}

	private Writer openWriter( String fileName ) {
		File f = new File ( fileName );
		System.out.println( "Trying to open: " + f.getAbsolutePath() );
		FileOutputStream fs = null;
		try {
			fs = new FileOutputStream( f );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		try {
			Writer out = new OutputStreamWriter( fs, "UTF8" );
			return out;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	private String path = "";

}

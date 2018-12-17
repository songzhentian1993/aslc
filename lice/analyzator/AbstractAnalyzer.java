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
package lice.analyzator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import org.pf.file.FileFinder;

import lice.common.CommonFuctions;
import lice.common.StringFuncs;
import lice.licenses.LicenseChecker;
import lice.licenses.LicenseOfAFile;
import lice.objects.DependencyObjects;
import lice.objects.TargetFile;
import lice.ui.SwingWorker;

/**
 * Superclass for license analyzers
 * Implements methods for UI and addFileToHash that adds objects to
 * Dependency map (see lice.objects.DependencyObjects )
 */
public abstract class AbstractAnalyzer implements Analyzer {

	public void cancel( boolean c ) {
		if ( c ) worker.interrupt();
		cancel = c;
	}

	public boolean getCancel() {
		return cancel;
	}

	/**
	 * @return Returns the numberOfFiles.
	 */
	public int getNumberOfFiles() {
		return dependencyFiles.size();
	}

	/**
	 * @return Returns the progress.
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * @return Returns the done.
	 */
	public boolean isDone() {
		return done;
	}

	protected void addCopyingToMap() {
		copying = FileFinder.findFiles( path, "COPYING", true );
		
		for (int i = 0; i < copying.length; i++) {
			System.out.println("Adding " + copying[i].getPath());
			addFileToHash( copying[i].getPath() );
		}
		
	}
	
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	protected TargetFile addFileToHash( String fileName ) {
		
		TargetFile cf = null;
	
		fileName = StringFuncs.addPathToFileName( fileName, sourcePath );
		
		// Resolve possible symlinks
		File f = new File( fileName );
		try {
			fileName = f.getCanonicalPath();
		} catch (IOException e4) {
			// TODO Auto-generated catch block
			e4.printStackTrace();
			return null;
		} 
		
		if ( DependencyObjects.objects.containsKey(fileName) ) {
			cf = (TargetFile)DependencyObjects.objects.get( fileName );
			//System.out.println("Already in hash " + fileName );
			return cf;
		}
		
		boolean binary;
		try {
			binary = CommonFuctions.isBinaryFile(fileName);
		} catch (Exception e1) {
			return null;
		}
		FileReader in = null;
		File inputFile = new File(fileName);
		if ( inputFile.length() == 0 ) return null;
		try {
			in = new FileReader(inputFile);
			if ( in != null ) {
				in.close();
			} 
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
			return null;
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
			return null;
		}
		
		LicenseOfAFile license = new LicenseOfAFile(); 
			
		cf = new TargetFile( fileName, inputFile.length() );
		
		if ( binary ) {
			cf.setFileType( TargetFile.FILE_TYPE_OBJECT );
		} else {
				
			if ( StringFuncs.findMatchingString( "Makefile", fileName ) > 0 ) {
				cf.setFileType(TargetFile.FILE_TYPE_MAKEFILE );
			} else if ( CommonFuctions.isSourceFile( fileName ) ) {
				cf.setFileType(TargetFile.FILE_TYPE_SOURCE );
				license = LicenseChecker.solveLicense( cf, false );
				cf = RPCAnalyzer.findRPC(cf);
			} else if ( fileName.endsWith("COPYING") || fileName.endsWith("README") ) {
				cf.setFileType(TargetFile.FILE_TYPE_LICENSE );					
				license = LicenseChecker.solveLicense( cf, false );	
			} else {
				cf.setFileType(TargetFile.FILE_TYPE_UNKNOWN );					
			}
		}

		cf.setLicense( license );
		DependencyObjects.objects.put( fileName, cf );
		added ++;
		
		try {
			if ( in != null )
				in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cf;
	}
	
	protected int added = 0;

	protected File [] copying = null;
	protected String path = "";
	protected boolean myRecheck = false;
	protected int progress = 0;
	protected SwingWorker worker = null;
	protected boolean cancel = false;
	protected Vector dependencyFiles = new Vector();
	protected String sourcePath = "";
	protected boolean done = false;
}

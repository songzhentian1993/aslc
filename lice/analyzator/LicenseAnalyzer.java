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
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;

import lice.analyzator.DependencyAndLicenseAnalyzator.ReaderAndAnalyzator;
import lice.common.CommonFuctions;
import lice.licenses.LicenseChecker;
import lice.objects.DependencyObjects;
import lice.ui.SwingWorker;

import org.pf.file.FileFinder;

/**
 * @author timtuun
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LicenseAnalyzer extends AbstractAnalyzer {

	public LicenseAnalyzer ( String directoryName ) {
		DependencyObjects.objects = new TreeMap();
		cancel = false;
		dependencyFiles = new Vector();
		path = directoryName;
		LicenseChecker.clearLicenseStatistics();
		findSourceFiles( directoryName );
		System.out.println("Total number of source files " + dependencyFiles.size());
	}

	private void findSourceFiles( String directoryName ) {
		File directory = new File(directoryName);
		File [] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			if ( files[i].isDirectory() ) {
				findSourceFiles( files[i].getPath() );
			} else {
				String fileName = files[i].getName();
				if ( CommonFuctions.isSourceFile(fileName) ) {
					dependencyFiles.add( files[i] );
				}
			}
		}		
	}

	/* (non-Javadoc)
	 * @see lice.analyzator.Analyzer#go()
	 */
	public void go() {
		worker = new SwingWorker() {
            public Object construct() {
            	return new ReaderAndAnalyzator();
            }
        };
        worker.start();		
	}	
	
	class ReaderAndAnalyzator {

		public ReaderAndAnalyzator() {
			done = false;
//			copying = FileFinder.findFiles( path, "COPYING", true );
			addCopyingToMap();
			
			analyzeLicenses( path );
			System.out.println("Hash size " + DependencyObjects.objects.size());
			LicenseChecker.checkLicenseStatuses();
			System.out.println("Hash size " + DependencyObjects.objects.size());
			System.out.println("Added " + added );
			done = true;
		}


		private void analyzeLicenses ( String dir ) {
			for (Iterator iter = dependencyFiles.iterator(); iter.hasNext();) {
				File element = (File) iter.next();
				//System.out.println("Analyzing " + element.getPath() );
				sourcePath = element.getParent();
				sourcePath = sourcePath.replace(".deps", "");
				if ( !sourcePath.endsWith("/") ) {
					sourcePath = sourcePath + "/";
				}
				if ( cancel ) return;

				addFileToHash( element.getPath() );
				progress++;
			}
		}
		
	}
	
}

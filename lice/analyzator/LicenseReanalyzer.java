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
import java.util.Vector;

import lice.licenses.LicenseChecker;
import lice.licenses.LicenseTemplate;
import lice.objects.DependencyObjects;
import lice.objects.TargetFile;
import lice.ui.SwingWorker;

/**
 * @author timtuun
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LicenseReanalyzer implements Analyzer {

	public LicenseReanalyzer( LicenseTemplate l, boolean depAnalysis, boolean recheck  ) {
		cancel = false;
		myRecheck = recheck;
		dependecyAnalysis = depAnalysis;
		licenseTemplate = l;
		if ( recheck ) {
			LicenseChecker.clearLicenseStatistics();
		}

		numberOfTargetFiles = DependencyObjects.objects.keySet().size();

		System.out.println("Number of files: " + numberOfTargetFiles );
	}
	/**
	 * @return Returns the numberOfFiles.
	 */
	public int getNumberOfFiles() {
		return numberOfTargetFiles;
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
	public void cancel( boolean c ) {
		if ( c ) worker.interrupt();
		cancel = c;
	}

	public boolean getCancel() {
		return cancel;
	}
	
	public void go() {

		worker = new SwingWorker() {
            public Object construct() {
            	return new Reanalyzer();
            }
        };
        worker.start();
	}
	
	class Reanalyzer {

		public Reanalyzer() {			
		
			for (Iterator iter = DependencyObjects.objects.values().iterator(); iter.hasNext();) {
				TargetFile target = (TargetFile) iter.next();
				if ( target.getFileType() == TargetFile.FILE_TYPE_SOURCE ) {
					LicenseChecker.checkFileUsingLicense( 
							licenseTemplate, dependecyAnalysis, true, target );
				}
				progress++;
			}
			
			LicenseChecker.checkLicenseStatuses();

			done = true;
		}
		
	}

	
	private File [] copying = null;
	private Vector dependencyFiles = new Vector();
	private int numberOfTargetFiles = 0;
	private boolean done = false;
	private int progress = 0;
	private boolean cancel = false;
	private boolean dependecyAnalysis = false;
	private SwingWorker worker = null;
	private boolean myRecheck = false;
	private LicenseTemplate licenseTemplate = null;
	
}

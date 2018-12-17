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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import lice.common.CommonFuctions;
import lice.common.GlobalStatistics;
import lice.common.StringFuncs;
import lice.file.DirFinder;
import lice.licenses.LicenseChecker;
import lice.objects.Dependency;
import lice.objects.DependencyObjects;
import lice.objects.RemoteProcedureCallInfo;
import lice.objects.TargetFile;
import lice.objects.UnsolvedDependencies;
import lice.ui.SwingWorker;

import org.pf.file.FileFinder;

/**
 * @author timot
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DependencyAndLicenseAnalyzator extends AbstractAnalyzer {
	
	/**
	 * 
	 */
	public DependencyAndLicenseAnalyzator( String directoryName  ) {
		DependencyObjects.objects = new TreeMap();
		cancel = false;
		dependencyFiles = new Vector();
		path = directoryName;
		LicenseChecker.clearLicenseStatistics();
		findDependencyFiles( directoryName );
		System.out.println("Number of files: " + dependencyFiles.size());
	}

	private void findDependencyFiles( String directoryName ) {
		File [] fs = FileFinder.findFiles( path, "objects.dep", true );
		dependencyFiles.addAll( Arrays.asList( fs ) );
		System.out.println("Number of dependency files: " + fs.length );

		DirFinder finder = new DirFinder( ".deps" );
		Vector dirs = finder.findDirectories( directoryName );		
		for (int i = 0; i < dirs.size(); i++) {
			File f = (File)dirs.get(i);
			sourcePath = f.toString();

			sourcePath = sourcePath.replace(".deps", "");
			File [] files = f.listFiles();
			
			dependencyFiles.addAll( Arrays.asList( files ) );
		}

	}
	
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
			addCopyingToMap();
			
			analyzeDependenciesAndLicenses();
			LicenseChecker.checkLicenseStatuses();

			// REMOVE
			System.out.println("Found following RPC's");
			
			for (Iterator iterator = DependencyObjects.objects.keySet().iterator(); iterator.hasNext();) {
				String element = (String) iterator.next();
				TargetFile tf = DependencyObjects.objects.get(element);
				if ( !tf.getRpcInfo().getRpcStatus().equals(RemoteProcedureCallInfo.RPC_NONE )) {
					System.out.println("RPC: " + tf.getFileName() + " " + tf.getRpcInfo().getRpcStatus() );
					Vector<TargetFile> parents = findParents(tf);
				}
			}
			// END REMOVE
			
			done = true;
		}	
						
		private Vector<TargetFile> findParents( TargetFile object ) {

			Vector<TargetFile> found = new Vector<TargetFile>();

			Vector<TargetFile> parents = object.getParents();
			for (Iterator iter = parents.iterator(); iter.hasNext();) {
				TargetFile parent = (TargetFile) iter.next();
				findParent( parent, found );
			}

			return found;
		}

		private void findParent( TargetFile object, Vector<TargetFile> found ) {

			Vector<TargetFile> parents = object.getParents();
			if( parents.size() == 0 ) {
				if ( ! found.contains( object ) ) {
					found.add( object ); 
					System.out.println("Parent: " + object.getFileName());
				}
				return;
			}
			
			for (Iterator iter = parents.iterator(); iter.hasNext();) {
				TargetFile parent = (TargetFile) iter.next();
				findParent( parent, found );
			}
			
		}
		
		private void analyzeDependenciesAndLicenses( ) {
			
			//Read each DIF
			Iterator iter1 = dependencyFiles.iterator();
			if ( !iter1.hasNext() ) {
				JOptionPane.showMessageDialog(null, 
						"No dependency information files found. \n " +
						"The source package hasn't been appropriately compiled. \n" +
						"See the documentation for further details.",
						"Analysis Error",
						JOptionPane.WARNING_MESSAGE);
			}
			
			while (iter1.hasNext()) {

				File element = (File) iter1.next();
				sourcePath = element.getParent();
				sourcePath = sourcePath.replace(".deps", "");

				if ( !sourcePath.endsWith("/") ) {
					sourcePath = sourcePath + "/";
				}
				
				try {
					if ( cancel ) return;
					//System.out.println("Trying to read: " + element.toString() );
					// Read the file and analyze its license
					readDIFile( element.toString() );
					progress++;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("Error while trying to analyze file: " + element.toString());
					e.printStackTrace();
					progress++;
				}
	
			}

			// Clean dependency map
			cleanDependencyMap();
		}

		private void cleanDependencyMap() {
			TreeMap temp = new TreeMap();
			Vector<TargetFile> filesToBeRemoved = new Vector<TargetFile>();
			for (Iterator iter = DependencyObjects.objects.keySet().iterator(); iter.hasNext();) {
				String key = (String)iter.next();
				TargetFile targetFile = (TargetFile) DependencyObjects.objects.get(key);
				
				if ( targetFile.getFileName().endsWith(".lo" ) ) { 
					// this is needed when .o -files are put into .lo files by ar -tool 
					handleLibrary( targetFile, targetFile.getFileName() ); 
				}
	
				// Here we combine two objects into one 
				//( for example obj.o and .lib/obj.o can be the same file)
				if ( !temp.containsKey( targetFile.getDisplayFileName() ) ) {

					temp.put( targetFile.getDisplayFileName(), targetFile );
					
				} else { 
					TargetFile f = (TargetFile)temp.get( targetFile.getDisplayFileName() );
						
					if ( f.getFileSize() == targetFile.getFileSize() ) {
						
						TargetFile toBeRemoved = combineTwoFiles(f, targetFile);
						/*if ( toBeRemoved != null )
							System.out.println("To be rem: " + toBeRemoved.getFileName());*/
						filesToBeRemoved.add(toBeRemoved);
					}
				}
			}
			
			for (TargetFile file : filesToBeRemoved) {
				if (file != null)
					DependencyObjects.objects.remove(file.getFileName());
			}
			
		}
		
		private TargetFile combineTwoFiles( TargetFile f1, TargetFile targetFile ) {

			if( f1.getDependencies().size() == 0 && targetFile.getParents().size() == 0 ) {
				// copy dependencies from already existing file (the same file!)
				f1.setDependencies( targetFile.getDependencies() );
/*				for (Iterator iterator = f1.getParents().iterator(); iterator.hasNext();) {
					TargetFile e = (TargetFile) iterator.next();
				} */
				
				//targetFile.setDependencies( new Vector() );
				
				return targetFile;
				
			} else if ( f1.getParents().size() == 0 && targetFile.getDependencies().size() == 0 ) {
				targetFile.setDependencies( f1.getDependencies() );
				/*for (Iterator iterator = targetFile.getParents().iterator(); iterator.hasNext();) {
					TargetFile e = (TargetFile) iterator.next();
				}
				f1.setDependencies( new Vector() ); */
				return f1;
			}
			
			return null;
		}

		/**
		 * 
		 * @param filename
		 * @throws Exception
		 */
		private void readDIFile( String filename ) throws Exception {

			//System.out.println("Reading file: " + filename);
			
			String line;
			BufferedReader inputData = null;
			File inputFile = null;
			try {
				  inputFile = new File( filename );
				  FileReader in = new FileReader(inputFile);
				  inputData = new BufferedReader(in);
			}
			catch (Exception e){
				System.err.println("File input error");
				throw new Exception(e);
			}		
				  
			try {
				TargetFile [] targets = null;
				while ( (line = inputData.readLine()) != null) {

					line = line.trim();
					// find the location where the target object definition(s) end
					// and dependencies start				
					int location = line.indexOf(':');
				  	//int depStart = 0;
				  	
					if ( location > 0 ) {
						targets = addTargetsToMap( line );

						if ( targets == null || targets.length == 0 ) {
					  		//System.out.println("no add!");
					  		continue;
					  	}
						
						addDependenciesFromTheEndOfObjectLine( line, targets );
						continue;
				  	} 

					if ( targets == null || targets.length == 0 ) {
				  		//System.out.println("no add!");
				  		continue;
				  	}
				  	

				  	// Add dependencies to map also from possible consecutive lines
				  	addDependenciesToObjects( line, 0, targets);
				}
			}

			catch (Exception e){
				//System.err.println("String parse error");
				inputData.close();
				throw new Exception(e);
			}
			
			inputData.close();		
  				
		}

		
		private TargetFile[] addTargetsToMap( String line ) {
			TargetFile [] targets = null;
		  	int start = 0;
		  	int end = line.indexOf(':', 0); 
		  			  	
		  	String fileNameStr = line.substring(start, end );
 		
	  		// Check if there are more than one target object 
	  		int whitespace = fileNameStr.indexOf(' ');
	  		
	  		if ( whitespace != -1 ) {
	  			Vector<String> v = new Vector<String>();
	  			
	  			fileNameStr = fileNameStr.trim();

				String [] objs = fileNameStr.split(" ");

	  			boolean added = false;
	  			for (int i = 0; i < objs.length; i++) {
	  				String fileName = objs[i];
		  			File [] fs = FileFinder.findFiles( sourcePath, fileName, true );

		  			if ( fs.length == 1 ) {
		  				v.add( objs[i] );
		  				added = true;
		  			} else {
		  				UnsolvedDependencies.objects.put(fileName, fileName+ " in directory " + sourcePath);
		  			}
				}

	  			if ( !added ) {
	  				return targets;
	  			}
	  			
	  			targets = new TargetFile [v.size()];
	  			
	  			for (int i = 0; i < v.size(); i++) {
	  				//System.out.println("Adding :" + (String)v.get(i) );
	  				targets [i] = addFileToHash( (String)v.get(i) );
	  			}
	  			
	  		} else { // Only one target object. This is the case in 99% of the time
	  			//Find the object file
	  			File [] fs = null;
	  			
	  			if ( fileNameStr.contains("/") ) {
	  				//File name includes directory name
	  				if ( CommonFuctions.isFile(sourcePath + fileNameStr) ) {
	  					fs = new File[1];
	  					fs[0] = new File(sourcePath + fileNameStr);
	  				} else {
	  					fs = new File[0];
	  				}
	  					
	  			} else {
	  				
	  				
	  				fs = FileFinder.findFiles( sourcePath, fileNameStr, true );

	  			}

	  			if ( fs.length > 0 ) {
	  				targets = new TargetFile [fs.length];
	  				for (int i = 0; i < fs.length; i++) {
		  				try {
							targets [i] = addFileToHash( fs[i].getCanonicalPath() );
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
	  			} else {
	  				UnsolvedDependencies.objects.put(fileNameStr, fileNameStr + " in directory " + sourcePath);
	  			}
	  		}	

	  		return targets;
		}		

		private void addDependenciesToObjects ( String line, int depStart, TargetFile [] parents ) throws Exception {

			int end = 0;

			while (true) {
				end = line.indexOf(' ', depStart);
				if ( end == -1 ) { 
					if ( depStart < line.length() ) {
						end = line.length();
					} else {
						break;						
					}
				}
		  		String fileNameStr = line.substring( depStart, end );

		  		if ( fileNameStr.equalsIgnoreCase("") || fileNameStr.equalsIgnoreCase("\\") ) {
		  			break;
		  		}

		  		depStart = end + 1;

	  			TargetFile depFile = null;

	  			// Dependency files include the infromation about dynamic/static linking
	  			// if file is binary
	  			String dynamic = "";
	  			if ( fileNameStr.contains(":") ) {
	  				dynamic = fileNameStr.substring(0, 1);
	  				fileNameStr = fileNameStr.substring(2, fileNameStr.length() );
	  			}
	  				  			
  		  		depFile = addFileToHash( fileNameStr );
  		  		if ( depFile == null ) {
  		  			continue;
  		  		}

  		  		// All header files are considered dynamic linking
  		  		if ( fileNameStr.endsWith(".h")) {
  		  			dynamic = "1";
  		  		}
  		  		
  		  		Dependency dep = null;
  		  		if ( dynamic.equalsIgnoreCase("") ) {
  		  			dep = new Dependency( depFile, Dependency.DEP_TYPE_STATIC_LINK ); 
  		  		} else if ( dynamic.equalsIgnoreCase("0") ) {
  		  			dep = new Dependency( depFile, Dependency.DEP_TYPE_STATIC_LINK ); 
  		  		} else if ( dynamic.equalsIgnoreCase("1") ) {
  		  			dep = new Dependency( depFile, Dependency.DEP_TYPE_DYNAMIC_LINK ); 
  		  		}

  		  		
		  		for (int i = 0; i < parents.length; i++) {
		  			depFile.setParent( parents[i] );
		  			parents[i].addDependency( dep );
				}
		  		
			}

		}
		
		private void addDependenciesFromTheEndOfObjectLine( String line, TargetFile [] targets ) throws Exception {
			int depStart = line.indexOf(':') + 2;

			addDependenciesToObjects( line, depStart, targets );
			
			return;					
		}
		
		private void handleLibrary ( TargetFile loFile, String fileName ) {

			String fileContents = StringFuncs.readAll( loFile.getFileName() );
						
			Pattern pattern = Pattern.compile("^pic_object=.*$", Pattern.MULTILINE);
			Matcher matcher = pattern.matcher(fileContents);

			String sharedObjectName = "";
			if (matcher.find()) { 
				sharedObjectName = matcher.group();
				sharedObjectName = sharedObjectName.replace("pic_object=", "");
				sharedObjectName = sharedObjectName.replace("'", "");
				sharedObjectName = loFile.getFilePath() + "/" + sharedObjectName;
			} else {
				return;
			}

			
			TargetFile objectFile = null;

			Object o = DependencyObjects.objects.get( sharedObjectName );
			if ( o != null ) {
				objectFile = (TargetFile)o;
			} else {
				File inputFile = new File(sharedObjectName);
				objectFile = new TargetFile( sharedObjectName, inputFile.length() );
			}

			//Copy .lo -files possible dependencies to the .o or .so file
			Vector<Dependency> loDeps = loFile.getDependencies();
			objectFile.addDependencies(loDeps);
			for (Dependency dependency : loDeps) {
				// change the right parent
				dependency.getTargetFile().removeParent( loFile );
				dependency.getTargetFile().setParent( objectFile );
			}
			
			
			Vector<Dependency> dependencies = new Vector<Dependency>();
			Dependency dep = new Dependency( objectFile, Dependency.DEP_TYPE_STATIC_LINK );
			dependencies.add( dep );
			loFile.setDependencies( dependencies );
			Vector<TargetFile> parents = new Vector<TargetFile>();
			parents = objectFile.getParents();
			loFile.setParents( parents );
			Dependency dep2 = new Dependency( loFile, Dependency.DEP_TYPE_STATIC_LINK );
			for (Iterator iter = parents.iterator(); iter.hasNext();) {
				TargetFile element = (TargetFile) iter.next();
				element.addDependency( dep2 );
				element.removeDependency( objectFile );
			}

			objectFile.clearParents();
			objectFile.setParent( loFile );
		} 
	}

}

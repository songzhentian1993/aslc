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

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import lice.licenses.LicenseStatus;
import lice.licenses.LicenseOfAFile;

/**
 * Represents any object that is either source object or compiled object
 */
public class TargetFile {
	
	public static String FILE_TYPE_OBJECT = "Binary file";
	public static String FILE_TYPE_LICENSE = "License file";
	public static String FILE_TYPE_SOURCE = "Source file";
	public static String FILE_TYPE_MAKEFILE = "Makefile";
	public static String FILE_TYPE_CONFIG = "Configuration file";
	public static String FILE_TYPE_UNKNOWN = "Unknown";
	
	/**
	 * @param myFilename
	 */
	public TargetFile(String fileName, long size) {
		super();
		this.fileName = fileName;
		if ( fileName != null && fileName.length() > 0 ) {
			this.path = fileName.substring(0, fileName.lastIndexOf('/') );
			displayFileName = fileName.substring( fileName.lastIndexOf('/') +1, fileName.length());
		} else {
			this.path = "";
			displayFileName = "";
		}
		this.setFileSize(size);
	}
	
	/**
	 * @return Returns the myDependencies.
	 */
	public Vector<Dependency> getDependencies() {
		return dependencies;
	}

	/**
	 * @return Returns the myDependencies.
	 */
	public void removeDependency( String dependencyFileName ) {
		//System.out.println("Removing dep " + dependencyFileName + " from " + getFileName() );
		int index = 0;
		for (Iterator iter = dependencies.iterator(); iter.hasNext();) {
			Dependency element = (Dependency) iter.next();
			//System.out.println(element.getTargetFile().getFileName());
			
			if ( element.getTargetFile().getFileName().equalsIgnoreCase(dependencyFileName) ) {
				dependencies.remove(index);
				//System.out.println("remove ok");
				return;
			}
			index++;
		}
	}

	
	/**
	 * @param myDependencies The myDependencies to set.
	 */
	public void setDependencies(Vector<Dependency> myDependencies) {
		this.dependencies = myDependencies;
	}

	/**
	 * @param 
	 */
	public void addDependencies(Vector<Dependency> dependencies) {
		for (Dependency dependency : dependencies) {
			addDependency(dependency);
		}
	}

	/**
	 * @param isDependend The isDependend to set.
	 */
	public void addDependency(Dependency dep) {
		boolean contains = false;
		for (Iterator iter = dependencies.iterator(); iter.hasNext();) {
			Dependency element = (Dependency) iter.next();

			if ( element.getTargetFile().equals(dep.getTargetFile() ) ) {
				contains = true;
				return;
			}
		}
		
		if ( !contains ) {
			dependencies.add( dep );
		}
	}

	/**
	 * @param isDependend The isDependend to set.
	 */
	public void removeDependency(TargetFile dep) {

		for (Iterator iter = dependencies.iterator(); iter.hasNext();) {
			Dependency element = (Dependency) iter.next();
			if ( element.getTargetFile().equals( dep ) ) {
				dependencies.remove( element );
				break;
			}
		}
	}

	/**
	 * @return Returns the myFilename.
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @return Returns the myFilename.
	 */
	public String getFilePath() {
		return path;
	}

	/**
	 * @param myFilename The myFilename to set.
	 */
	public void setFileName(String myFilename) {
		this.fileName = myFilename;
		displayFileName = myFilename.substring( myFilename.lastIndexOf('/') +1, myFilename.length());
	}
	/**
	 * @return Returns the myLicense.
	 */
	public LicenseOfAFile getLicense() {
		return license;
	}
	/**
	 * @param myLicense The myLicense to set.
	 */
	public void setLicense(LicenseOfAFile myLicense) {
		if ( this.fileName.endsWith("COPYING") ) {
			System.out.println("Setting license " + myLicense.getLicenseName());
		}
		this.license = myLicense;
	}
	
	/**
	 * @param isDependend The isDependend to set.Dependency
	 */
	public void setParent(TargetFile parent) {
		if ( !parents.contains(parent) ) {
			parents.add( parent );
		}
	}

	/**
	 * @param isDependend The isDependend to set.Dependency
	 */
	public void removeParent(TargetFile parent) {
		if ( parents.contains(parent) ) {
			parents.remove( parent );
		}
	}

	public void clearParents() {
		parents = new Vector<TargetFile>();
	}

	/**
	 * @param isDependend The isDependend to set.
	 */
	public void setParents(Vector<TargetFile> parents) {
		for (Iterator iter = parents.iterator(); iter.hasNext();) {
			TargetFile element = (TargetFile) iter.next();
			setParent( element );
		}
		
	}

	/**
	 * @return Returns the myPossibleProblem.
	 */
	public Vector<TargetFile> getParents() {
		return parents;
	}

	/**
	 * @return Returns the myPossibleProblem.
	 */
	public boolean hasParents() {
		return parents.size() > 0 ? true : false;
	}

	/**
	 * @param myPossibleProblem The myPossibleProblem to set.
	 */
	public void setChildLicense(String licenseName) {
		
		if ( !licenseName.equalsIgnoreCase("") ) {
			if ( !childLicenses.contains( licenseName ) ) {
				childLicenses.add( licenseName );
			}
		}
	}

	/**
	 * @param myPossibleProblem The myPossibleProblem to set.
	 */
	public void overrideChildLicenses(String licenseName) {
		childLicenses.clear();
		if ( licenseName != null ) 
			childLicenses.add( licenseName );
	}

	/**
	 * @param licenseNames The licenseNames to set.
	 */
	public void setChildLicenses(Vector<String> licenseNames) {
		String s = "";
		for (int i = 0; i < licenseNames.size(); i++) {
			s = (String)licenseNames.get(i);
			if ( !childLicenses.contains( s ) )
				childLicenses.add( s );
		}
	}

	
	/**
	 * @param myPossibleProblem The myPossibleProblem to set.
	 */
	public Vector getChildLicenses() {
		return childLicenses;
	}

	/**
	 * @param myPossibleProblem The myPossibleProblem to set.
	 */
	public String getChildLicensesString() {
		String s = "";
		for (int i = 0; i < childLicenses.size(); i++) {
			if ( i == 0 ) {
				s = s + (String)childLicenses.get(i);
			} else {
				s = s + "; " + (String)childLicenses.get(i);
			}
		}
		return s;
	}

	/**
	 * @return Returns the isObjectFile.
	 */
	public String getFileType() {
		return fileType;
	}
	/**
	 * @param isObjectFile The isObjectFile to set.
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
	
	public String toString() {
		return displayFileName;
	}
	
	/**
	 * @return Returns the path.
	 */
	public String getDisplayFileName() {
		return displayFileName;
	}
	/**
	 * @param path The path to set.
	 */
	private void setDisplayFileName(String name) {
		this.displayFileName = name;
	}
		
	/**
	 * @return Returns the fileContents.
	 */
	public StyledDocument getFileContents() {
		
		if ( contents == null ) {
			contents = new DefaultStyledDocument();
			Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
			Style s = contents.addStyle("italic", def);
	        StyleConstants.setItalic(s, true);	
	        s = contents.addStyle("small", def);
	        StyleConstants.setFontSize(s, 12);
	        StyleConstants.setBackground(s, Color.YELLOW );
	    }

		if ( !fileType.equals( FILE_TYPE_SOURCE ) &&
				!fileType.equals( FILE_TYPE_LICENSE )) {
			try {
				contents.remove( 0 , contents.getLength() );
				contents.insertString( 0 , "Not a source file", null );
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return contents;
		}

		int location = 0;
		if ( contents.getLength() == 0 ) {
			BufferedReader inputData = null;
			try {
				File inputFile = new File( fileName );
				FileReader in = new FileReader(inputFile);
				inputData = new BufferedReader(in);
					
				String s;
				while ( (s = inputData.readLine()) != null) {
					/*System.out.println("small");
					contents.insertString(contents.getLength(), s + '\n',
								contents.getStyle("small") );*/						
					contents.insertString(contents.getLength(), s + '\n',
							contents.getStyle("italic") );
				}	
			}				
			catch (Exception e){
				//System.err.println("File input error");
				//throw new Exception(e);
				e.printStackTrace();
				try {
					contents.remove( 0 , contents.getLength() );
					contents.insertString( 0 , "Unable to read file: " + fileName, null );
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}		
		}		
		return contents;
	}

	/**
	 * @param fileSize The fileSize to set.
	 */
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * @return Returns the fileSize.
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * @param licenseStatus The licenseStatus to set.
	 */
	public void setLicenseStatus(String licenseStatus) {
		this.licenseStatus = licenseStatus;
	}

	/**
	 * @return Returns the licenseStatus.
	 */
	public String getLicenseStatus() {
		return licenseStatus;
	}

	public RemoteProcedureCallInfo getRpcInfo() {
		return rpcInfo;
	}

	public void setRpcInfo(RemoteProcedureCallInfo rpcInfo) {

		//System.out.println("Setting rpc info");
		if ( ! this.rpcInfo.getRpcStatus().equals(RemoteProcedureCallInfo.RPC_NONE) ) {
			System.out.println("Warning: Overriding RPC info." + fileName + " Curr: " + 
				this.rpcInfo.getRpcStatus() + " New: " + rpcInfo.getRpcStatus() );
		}
 		this.rpcInfo = rpcInfo;
	} 

	
	private String fileName = "";
	private String path = "";
	private String displayFileName;
	private Vector<Dependency> dependencies = new Vector<Dependency>();
	private Vector<TargetFile> parents = new Vector<TargetFile>();
	private LicenseOfAFile license = new LicenseOfAFile();
	private Vector<String> childLicenses = new Vector<String>();
	private String fileType = FILE_TYPE_UNKNOWN;
	private long fileSize = 0;
	private String licenseStatus = LicenseStatus.UNKNOWN;
	private RemoteProcedureCallInfo rpcInfo = new RemoteProcedureCallInfo();
	
	private DefaultStyledDocument contents = null;

}

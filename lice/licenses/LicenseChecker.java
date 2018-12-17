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
package lice.licenses;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JOptionPane;

import lice.common.CommonFuctions;
import lice.common.GlobalStatistics;
import lice.common.RuleEngine;
import lice.common.StringFuncs;
import lice.objects.Dependency;
import lice.objects.DependencyObjects;
import lice.objects.TargetFile;

/**
 * @author timot
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LicenseChecker //implements Comparator
{

	public static LicenseOfAFile solveLicense( TargetFile file, boolean reCheck ) {

		if( licenses.size() < 1 ) {
			readLicenseTemplates("./templates");
		}

		if( !reCheck ) {
			if ( file.getLicense().getLicenseType() != LicenseOfAFile.UNKNOWN ) {
				return file.getLicense();
			}
		}
	//	System.out.println("Checkin licesense for " + file.getFileName());

		LicenseOfAFile fileLicense = null; 
		if ( reCheck ) {
			fileLicense = file.getLicense();
		} else {
			fileLicense = new LicenseOfAFile();
		}

		String all;
		all = StringFuncs.readFileForLicenseAnalysis( file.getFileName(), true );
		all = all.toLowerCase();
		
//		System.out.println(all);
		
		String regexp = "";

		for (Iterator iter = licenses.iterator(); iter.hasNext();) {
			LicenseTemplate lic = (LicenseTemplate) iter.next();
			String licenseName = lic.getLicenseName();

//			Date time = new Date();
//			long start = time.getTime();

			regexp = lic.getLicenseTemplate();	
			regexp = regexp.toLowerCase();
						
			int numberOfMatches = StringFuncs.findMatchingString( regexp, all ); 
			// we found a single license
			// just set the license name and return

//			Date time2 = new Date();
//			long end = time2.getTime();
//			System.out.println("Time " + (end - start) );
			
			if ( numberOfMatches > 0 ) {
				fileLicense.setLicenseName( licenseName );
				optimizeAnalysis( lic );
				return fileLicense;
			} 
			
		}
		return fileLicense;
	}

	private static void optimizeAnalysis( LicenseTemplate lic ) {
		// Optimize the license order
		numberOfAnalysis++;
		lic.increaseNumberOfMatches();
		if ( numberOfAnalysis % 10 == 0 ) {
			Collections.sort( licenses );
		}
		// End Optimize the license order
	}
	
	public static void checkLicenseStatuses() {

		if ( DependencyObjects.objects == null ) return;
		
		// Clear old statuses
		for (Iterator iter = DependencyObjects.objects.values().iterator(); iter.hasNext();) {
			TargetFile element = (TargetFile) iter.next();
			if ( element.getFileType().equalsIgnoreCase( TargetFile.FILE_TYPE_LICENSE ) ) {
				continue;
			}
			
			element.setLicenseStatus( LicenseStatus.UNKNOWN );
						
			if ( !element.getFileType().equalsIgnoreCase( TargetFile.FILE_TYPE_SOURCE ) ) {
				element.setLicense( new LicenseOfAFile() );
			}
			element.overrideChildLicenses( null );
		}
		
		Vector topLevelObjects = CommonFuctions.getTopLevelObjects();
		
		for (int i = 0; i < topLevelObjects.size(); i++) {
			TargetFile file = (TargetFile)topLevelObjects.get(i);
			check( file );
		}
		
	}	
	
	private static void checkIncompatibilities( TargetFile parent, Dependency child ) {
				
		String childLicenseName = child.getTargetFile().getLicense().getLicenseName();
		String childLicenseType = child.getTargetFile().getLicense().getLicenseType();
		Vector derivedLicenses = child.getTargetFile().getChildLicenses();

		if ( !childLicenseType.equalsIgnoreCase( LicenseOfAFile.DERIVED ) ) {
			// set the license of the child as child license
			parent.setChildLicense( childLicenseName );
		}
		// set the childs child licenses as child licenses also
		parent.setChildLicenses( derivedLicenses );
		
		// If parent is a binary file
		if ( !parent.getFileType().equalsIgnoreCase( TargetFile.FILE_TYPE_SOURCE ) ) {
			LicenseOfAFile parentLicense = new LicenseOfAFile();
			parentLicense.setLicenseType( LicenseOfAFile.DERIVED );
			parent.setLicense( parentLicense );
			parent.setLicenseStatus( LicenseStatus.LICENCE_OK );
		}

		String parentLicenseName = parent.getLicense().getLicenseName();
		String parentLicenseType = parent.getLicense().getLicenseType();

		// If parent is a source file and it doesnt have a recognized license
		// Analysis is over.
		if ( parentLicenseType.equalsIgnoreCase( LicenseOfAFile.UNKNOWN ) ) {
			parent.setLicenseStatus( LicenseStatus.MISSING_LICENSE );
			return;
		}

		Map licenses = new TreeMap();
		// put the license of the parent file (or derived licenses) to a map
		if ( parentLicenseType.equalsIgnoreCase( LicenseOfAFile.DERIVED ) ) { // parent is a binary
			// If we have binary file, get the child licenses
			Vector childLicenseNames = parent.getChildLicenses();
			for (int i = 0; i < childLicenseNames.size(); i++) {
				String licenseName = (String)childLicenseNames.get( i );
				licenses.put( licenseName, new LicenseOfAFile( (String)childLicenseNames.get( i ) ) );
			}
		} else { // parent is a source file
			licenses.put( parent.getLicense().getLicenseName(), parent.getLicense() );
		}

		// File might have several licenses, so we need to collect rules for those
		// and check the child against each of these licenses
		Vector parentLicenses = new Vector();
		Collection values = licenses.values();
		for (Iterator iter = values.iterator(); iter.hasNext();) {
			LicenseOfAFile lic = (LicenseOfAFile) iter.next();
			LicenseRules licenseRule = RuleEngine.getRules( lic.getLicenseName() );
			if ( licenseRule != null ) {
				parentLicenses.add( licenseRule );
			}			
		}

		// allRules includes all license rules for licenses that are in parent
		for (int i = 0; i < parentLicenses.size(); i++) {

			LicenseRules rules = (LicenseRules)parentLicenses.get( i );
			// Get license rule for license that child has
			Rule [] childRules = null;

			if ( !childLicenseType.equalsIgnoreCase( LicenseOfAFile.DERIVED ) ) {
				childRules = new Rule[1];
				childRules [0] = rules.getRule( childLicenseName );
			} else {
				Vector childLicenseNames = parent.getChildLicenses();
				childRules = new Rule[childLicenseNames.size()];
				for (int j = 0; j < childRules.length; j++) {
					childRules [j] = rules.getRule( (String)childLicenseNames.get(j) );					
				}
			}
			
			for (int j = 0; j < childRules.length; j++) {
				
				if ( childRules[j].getRule( child ).equalsIgnoreCase( Rule.RESULT_OK ) ) {
					parent.setLicenseStatus( LicenseStatus.LICENCE_OK );
				} else if ( childRules[j].getRule( child ).equalsIgnoreCase( Rule.RESULT_NOK ) ) {
					parent.setLicenseStatus( LicenseStatus.INCOMPATIBLE_LICENSES );
					return;
				} else if ( childRules[j].getRule( child ).equalsIgnoreCase( Rule.RESULT_WARNING ) ) {
					parent.setLicenseStatus( LicenseStatus.WARNING );
					return;
				} else if ( childRules[j].getRule( child ).equalsIgnoreCase( Rule.RESULT_UNDEFINED ) ) {
					parent.setLicenseStatus( LicenseStatus.UNDEFINED_COMPATIBILITY );
					return;
				} 
			}
		}
	}

	// Jos on lenkkejä, niin tämä on ikikiersiö
	private static void check( TargetFile file ) {

		if ( !file.getLicenseStatus().equalsIgnoreCase( LicenseStatus.UNKNOWN ) ) {
			return; // this file has been checked already
		}
				
		Vector children = file.getDependencies();
		for (int i = 0; i < children.size(); i++) {
			Dependency dep = (Dependency)children.get(i);
			TargetFile child = dep.getTargetFile();
			check( child );
			// check child license status and calculate file license status
			String currentStatus = file.getLicenseStatus();

			// We cant change the status if it has already reached NOK state
			if ( currentStatus.equalsIgnoreCase( LicenseStatus.LICENCE_OK ) || 
					currentStatus.equalsIgnoreCase( LicenseStatus.UNKNOWN ) ) {

				if ( child.getLicenseStatus().equals( LicenseStatus.LICENCE_OK ) ) {
					checkIncompatibilities( file, dep );
				} else if ( child.getLicenseStatus().equals( LicenseStatus.INCOMPATIBLE_LICENSES ) ) {
					file.setLicenseStatus( LicenseStatus.INCOMPATIBLE_LICENSES );
				} else if ( child.getLicenseStatus().equals( LicenseStatus.MISSING_CHILD_LICENSES ) ) {
					file.setLicenseStatus( LicenseStatus.MISSING_CHILD_LICENSES );
				} else if ( child.getLicenseStatus().equals( LicenseStatus.UNDEFINED_COMPATIBILITY ) ) {
					file.setLicenseStatus( LicenseStatus.UNDEFINED_COMPATIBILITY );
				} else if ( child.getLicenseStatus().equals( LicenseStatus.MISSING_LICENSE ) ) {
					file.setLicenseStatus( LicenseStatus.MISSING_CHILD_LICENSES );
				} 
			}
			
		}
		
		// if file doesnt have any children, it is a source file and
		// it doesnt have a license
		if ( file.getFileType().equalsIgnoreCase( TargetFile.FILE_TYPE_SOURCE ) &&
			file.getLicense().getLicenseType().equals( LicenseOfAFile.UNKNOWN ) ) {
			file.setLicenseStatus( LicenseStatus.MISSING_LICENSE );
			return;
		}

		// All children have been checked and status is still unknown
		// so file is ok.
		if( file.getLicenseStatus().equals( LicenseStatus.UNKNOWN ) ) {
			file.setLicenseStatus( LicenseStatus.LICENCE_OK );
			return;
		}
	}
	
	public static void getLicenseSummary( StringBuffer str ) {
		str.append("####### LICENCE MATCH STATISTICS #######\n");
		for (Iterator iter = licenses.iterator(); iter.hasNext();) {
			LicenseTemplate lic = (LicenseTemplate) iter.next();
			if ( lic.getNumberOfMatches() > 0 )
				str.append(lic.getLicenseName() +  ": " + lic.getNumberOfMatches() + " times.\n");
		}					
		
		JOptionPane.showMessageDialog(null, 
				str.toString(),
				"License statistics",
				JOptionPane.INFORMATION_MESSAGE);

	}

	public static void clearLicenseStatistics() {
		for (Iterator iter = licenses.iterator(); iter.hasNext();) {
			LicenseTemplate lic = (LicenseTemplate) iter.next();
			lic.resetNumberOfMatches();
		}					

	}
	
	public static String [] getLicenseNames() {
		
		if( licenses.size() < 1 ) {
			readLicenseTemplates("./templates");
		}
		
		Vector names = new Vector();
		for (int i = 0; i < licenses.size(); i++) {
			String name = ((LicenseTemplate)licenses.get(i)).getLicenseName(); 
			if ( !names.contains( name ) ) {
				names.add( name );
			}
		}
		Collections.sort( names );
		String[] lmname = new String[names.size()];
		names.toArray(lmname);
		return lmname;
	}
	
	public static void checkFileUsingLicense( LicenseTemplate l, boolean depAnalysis, boolean recheck, TargetFile target ) {

		Vector oldLic = licenses;
		licenses = new Vector();
		licenses.add( l );
		LicenseOfAFile lic = null;
		if ( !recheck ) {
			LicenseOfAFile old = target.getLicense();
			if ( old.getLicenseType().equals( LicenseOfAFile.UNKNOWN ) ) {
				lic = solveLicense( target, true );
				target.setLicense( lic );						
			}
		}
		else {
			lic = solveLicense( target, true );
			target.setLicense( lic );
		} 

		licenses = oldLic;		
	}
	
	
	private static void readLicenseTemplates ( String directory ) {
		
		File dir = new File( directory );

		if ( dir.isDirectory() ) {
			File [] files = dir.listFiles();

			for (int i = 0; i < files.length; i++) {
				
				String license = StringFuncs.readAll( files[i].getPath() );
				String licenseName = license.substring( 0, license.indexOf("\n") );
				license = license.substring( license.indexOf('\n'), license.length() );
				license = license.substring( 0, license.lastIndexOf('\n') );
				license = license.replaceAll("\\s", "\\\\s*");
				license = license.replaceAll("/", "");
				
//				System.out.println("putting  " + licenseName + " " + strbuf.toString() );
				LicenseTemplate l = new LicenseTemplate( licenseName );
				l.setLicenseTemplate( license.toString() );
				licenses.add( l );
			}
			
		}
		
//		System.out.println("Leaving read licenses");
//		getLicenseSummary();
	}
	
	public static void applyLicenseToAllInPath( String path, LicenseOfAFile lic ) {
		for (Iterator iter = DependencyObjects.objects.values().iterator(); iter.hasNext();) {
			TargetFile element = (TargetFile) iter.next();
			
			if ( element.getFileName().startsWith( path ) ) {
				if ( element.getLicense().getLicenseType().equals( LicenseOfAFile.UNKNOWN ) ) {
					element.setLicense( lic );	
				}
			}
		}
		
		checkLicenseStatuses();
	}
	
	private static Vector licenses = new Vector();
	private static int numberOfAnalysis = 0;
	
}

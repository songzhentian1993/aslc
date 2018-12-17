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

/**
 * @author timot
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LicenseOfAFile {

	public static String UNKNOWN = "UNKNOWN";
	public static String DERIVED = "DERIVED";
	public static String MULTIPLE = "MULTIPLE_LICENSES";
	public static String SINGLE = "SINGLE";

	/**
	 * 
	 */
	public LicenseOfAFile() {
		licenseType = LicenseOfAFile.UNKNOWN;
	}

	/**
	 * 
	 */
	public LicenseOfAFile( String licenseName ) {
		myLicenseName = licenseName;
		licenseType = LicenseOfAFile.SINGLE;
	}

	public String getLicenseType() {
		return licenseType;
	}

	public void setLicenseType( String type ) {
		licenseType = type;
	}	
	
	/**
	 * @return Returns the myLicenseName.
	 */
	public String getLicenseName() {
/*		if ( myLicenseName.size() == 0 ) {
			return new String [0];
		}
		String[] lnames = new String[myLicenseName.size()];
		myLicenseName.toArray(lnames);
		return lnames; */
		return myLicenseName;
	}

	public void setLicenseName( String licenseName ) {
		myLicenseName = licenseName;
		licenseType = LicenseOfAFile.SINGLE;
	}	

/*	public void addLicenseName( String licenseName ) {
		myLicenseName.add( licenseName );		
	} */
	
//	private Vector myLicenseName = new Vector();
	private String myLicenseName = "";
	private String licenseType = LicenseOfAFile.UNKNOWN;
}

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
 * @author timtuun
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LicenseTemplate implements Comparable {

	public LicenseTemplate( String licenseName ) {
		this.licenseName = licenseName;		
	}
	

	/**
	 * @return Returns the licenseName.
	 */
	public String getLicenseName() {
		return licenseName;
	}


	/**
	 * @param licenseTemplate The licenseTemplate to set.
	 */
	public void setLicenseTemplate(String licenseTemplate) {
		this.licenseTemplate = licenseTemplate;
	}

	/**
	 * @return Returns the licenseTemplate.
	 */
	public String getLicenseTemplate() {
		return licenseTemplate;
	}

	public int getNumberOfMatches() {
		return numberOfMatches;
	}

	public void increaseNumberOfMatches() {
		this.numberOfMatches++;
	}

	public void resetNumberOfMatches() {
		this.numberOfMatches = 0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0) {
		LicenseTemplate lic = (LicenseTemplate)arg0;
		if ( numberOfMatches > lic.numberOfMatches ) {
			return -1;
		}
		if ( numberOfMatches < lic.numberOfMatches ) {
			return 1;
		}
		return 0;
	}
	
	
	private int numberOfMatches = 0;
	private String licenseName = "";
	private String licenseTemplate = "";
}

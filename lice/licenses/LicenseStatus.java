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
public class LicenseStatus {

	public static String LICENCE_OK = "Ok";
	public static String MISSING_CHILD_LICENSES = "Some children have unknown license";
	public static String MISSING_LICENSE = "Missing license";
	public static String INCOMPATIBLE_LICENSES = "Incompatible licenses";
	public static String WARNING = "Potential problems";
	public static String UNDEFINED_COMPATIBILITY = "Undefined compatibilities";
	public static String UNKNOWN = "Unknown";

		
	/**
	 * @return Returns the licenseStatus.
	 */
	public String getLicenseStatus() {
		return licenseStatus;
	}
	/**
	 * @param licenseStatus The licenseStatus to set.
	 */
	public void setLicenseStatus(String licenseStatus) {
		this.licenseStatus = licenseStatus;
	}

	private String licenseStatus = UNKNOWN;
}

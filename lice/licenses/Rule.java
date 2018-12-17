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

import lice.objects.Dependency;

public class Rule {

	public static String TYPE_STATIC = "Static linking";
	public static String TYPE_DYNAMIC = "Dynamic linking";
	public static String TYPE_RPC = "RPC";

	public static String RESULT_UNDEFINED = "N/A";
	public static String RESULT_OK = "Ok";
	public static String RESULT_WARNING = "Warning";
	public static String RESULT_NOK = "Not Ok";
	
	public Rule ( String licenseName ) {
		myLicenseName = licenseName;
	}

	public String getLicenseName() {
		return myLicenseName;
	}
	
	public String getRule( Dependency dependencyType ) {
		if ( dependencyType.getDependencyType().equalsIgnoreCase( Dependency.DEP_TYPE_STATIC_LINK ) ) {
			return staticRule;
		} else if ( dependencyType.getDependencyType().equalsIgnoreCase( Dependency.DEP_TYPE_DYNAMIC_LINK ) ) {
			return dynamicRule;
		}
		
		return rpcRule;
	}

	public void setRule( Dependency dependencyType, String rule ) {
		if ( dependencyType.getDependencyType().equalsIgnoreCase( Dependency.DEP_TYPE_STATIC_LINK ) ) {
			staticRule = rule;
		} else if ( dependencyType.getDependencyType().equalsIgnoreCase( Dependency.DEP_TYPE_DYNAMIC_LINK ) ) {
			dynamicRule = rule;
		} else {
			rpcRule = rule;
		}
	}
	
	private String staticRule = RESULT_UNDEFINED ;
	private String dynamicRule = RESULT_UNDEFINED ;
	private String rpcRule = RESULT_UNDEFINED;

	private String myLicenseName = "";
}

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

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class LicenseRules {
	
	public LicenseRules( String licenseName ) {
		myLicenseName = licenseName;
		rules = new TreeMap();
	}

	public void setRule( String licenseName, Rule rule ) {
		rules.put( licenseName, rule );
	}

	public Rule getRule( String licenseName ) {
		Rule r = (Rule)rules.get( licenseName );
		if ( r != null )
			return r;
		else
			return new Rule( licenseName );
	}

	public String getLicenseName() {
		return myLicenseName;
	}
	
	public Rule [] getAllRules() {		
		Collection col = rules.values();
		Rule[] rulesArray = new Rule[col.size()];
		col.toArray(rulesArray);
		return rulesArray;
	}
	
	private Map rules = null;
	private String myLicenseName = "";
}

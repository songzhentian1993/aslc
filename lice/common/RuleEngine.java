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
package lice.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

import lice.licenses.LicenseChecker;
import lice.licenses.LicenseRules;
import lice.licenses.Rule;
import lice.objects.Dependency;

public class RuleEngine {

	public static LicenseRules getRules ( String licenseName ) {
		readRules();
		for (int i = 0; i < licenseRules.length; i++) {
			if ( licenseRules[i].getLicenseName().equalsIgnoreCase( licenseName ) ) {
				return licenseRules[i];
			}
		}
		return null;
	}
	
	public static LicenseRules [] readRules() {

		if ( licenseRules != null ) return licenseRules;

		String [] licenseNames = LicenseChecker.getLicenseNames();
		licenseRules = new LicenseRules[ licenseNames.length ];
		
		TreeMap tm = readExistingRules();
		
		LicenseRules rule = null;
		for (int i = 0; i < licenseNames.length; i++) {
			rule = (LicenseRules)tm.get( licenseNames[i] );
			if ( rule == null ) {
				rule = new LicenseRules( licenseNames[i] );
				Rule r = new Rule(licenseNames[i]);
				r.setRule( new Dependency( null, Dependency.DEP_TYPE_STATIC_LINK ), Rule.RESULT_OK );
				r.setRule( new Dependency( null, Dependency.DEP_TYPE_DYNAMIC_LINK ), Rule.RESULT_OK );
				r.setRule( new Dependency( null, Dependency.DEP_TYPE_RPC ), Rule.RESULT_OK );
				rule.setRule( licenseNames[i], r ); // license is always OK with itself
			}			
			licenseRules[i] = rule;
		}
		
		return licenseRules;
	}

	private static TreeMap readExistingRules() {
		TreeMap tm = new TreeMap();

		File dir = new File( RULES_DIR );
		
		File [] f = dir.listFiles();
		for (int i = 0; i < f.length; i++) {
			if ( f[i].isFile() ) {
				//System.out.println( "File name: " + f[i].getName() );
				LicenseRules ruleContainer = new LicenseRules( f[i].getName() );

				String filename = f[i].getAbsolutePath();
				BufferedReader inputData = null;
				File inputFile = null;
				try {
					  inputFile = new File( filename );
					  FileReader in = new FileReader(inputFile);
					  inputData = new BufferedReader(in);
				}
				catch (Exception e){
					System.err.println("File input error");
					e.printStackTrace();
					return tm;
				}		
					  
				try {
					String line = "";
					while ( (line = inputData.readLine()) != null) {
						//System.out.println("Line: " + line);

						Rule r = new Rule( line.substring(0, line.indexOf(':')));
						String ruleString = line.substring( line.indexOf(':') + 1, line.length());
						String [] rules = ruleString.split(" ");
						//System.out.println(rules[0] + rules[1] + rules[2]);
						r.setRule( new Dependency( null, Dependency.DEP_TYPE_STATIC_LINK ), rules[0] );
						r.setRule( new Dependency( null, Dependency.DEP_TYPE_DYNAMIC_LINK ), rules[1] );
						r.setRule( new Dependency( null, Dependency.DEP_TYPE_RPC ), rules[2] );
						ruleContainer.setRule( r.getLicenseName(), r );
						tm.put( ruleContainer.getLicenseName(), ruleContainer );
					}		
				}
				catch (Exception e){
					System.err.println("String parse error");
					try {
						inputData.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					return tm;
				}
						
				try {
					inputData.close();
				} catch (IOException e) {
					e.printStackTrace();
				}		
			}
		}
		
		return tm;
	}

	public static void saveRules( LicenseRules [] licenseRules ) {
		for (int i = 0; i < licenseRules.length; i++) {
			LicenseRules lic = licenseRules[i];
			Rule [] rules = lic.getAllRules();
			
			BufferedWriter out = null;
			if ( rules.length > 0 ) {
				try {
					out = new BufferedWriter(new FileWriter(RULES_DIR + lic.getLicenseName() ));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			for (int j = 0; j < rules.length; j++) {
				Rule r = rules[j];
		        try {
					out.write(
							r.getLicenseName() + ":" + 
							r.getRule( new Dependency( null, Dependency.DEP_TYPE_STATIC_LINK ) ) + " " + 
							r.getRule( new Dependency( null, Dependency.DEP_TYPE_DYNAMIC_LINK ) ) + " " +
							r.getRule( new Dependency( null, Dependency.DEP_TYPE_RPC ) ) + "\n"
							);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
	        try {
	        	if ( out != null ) {
	        		out.close();
	        	}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static LicenseRules [] licenseRules;
    private static String RULES_DIR = "./rules/";

}

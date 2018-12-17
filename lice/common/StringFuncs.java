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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author timot
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StringFuncs {

		public static String readFileForLicenseAnalysis( String fileName, boolean isSourceFile ) {

		StringBuffer strbuf = new StringBuffer("");
		FileReader fr = null;
		try {
			fr = new FileReader( fileName );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Read first 100 lines
		
		BufferedReader buf = new BufferedReader( fr );
		int counter = 0;
		String s = "";
		try {
			while ( (s = buf.readLine()) != null) {
				s = s.trim();
				strbuf.append(s + "\n");
				counter++;
				if ( counter > 99 ) break;
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String sos = strbuf.toString();
		if ( isSourceFile )
			sos = removeCommentMarks( sos ); 

		try {
			fr.close();
			buf.close();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		return sos;
	}

		public static String readAll( String fileName ) {

			StringBuffer strbuf = new StringBuffer("");
			FileReader fr = null;
			try {
				fr = new FileReader( fileName );
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			BufferedReader buf = new BufferedReader( fr );
			String s = "";
			try {
				while ( (s = buf.readLine()) != null) {
					s = s.trim();
					strbuf.append(s + "\n");
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String sos = strbuf.toString();
			
			return sos;
		}
	
		
	private static String removeCommentMarks( String all ) {
		all = all.replaceAll("/", "");
		all = all.replaceAll("\\*", "");
		all = all.replaceAll("\\.", "");
		all = all.replaceAll("#", "");
		//all = all.replaceAll("\\\\", "");
		
		return all;
	}
		
/*	public static String findCommentedRegions( String all ) {
		
		Pattern pattern = Pattern.compile(regexp);
		Matcher matcher = pattern.matcher(all);


		while (matcher.find()) { 
			//System.out.println(matcher.group());
			String s = matcher.group(); 
			buf.append( s );
		}
		
		return buf.toString();
	} */
		
	public static int findMatchingString ( String regexp, String all ) {
//        regexp = "(?m)^.*" + regexp + ".*$";

		
		matchStr = "";
        		
        Pattern pattern = Pattern.compile(regexp, Pattern.MULTILINE);
        matcher = pattern.matcher(all);
        
        if (matcher.find()) {         	
        	//matchStr = matcher.group();
        	
        	//int start = matcher.start();
        	//int end = matcher.end();        	
        	//System.out.println("Match "+ start + " " + end );
        	//System.out.println( matchStr );
        	
        	//Pattern extraCheck = Pattern.compile( ".license." );
        	//matcher = extraCheck.matcher( all );
        	//if ( matcher.find() ) {
        		//System.out.println("Löytyi! " + matcher.start() + " " + matcher.end() );
        	//	return 2;
        	//}
        	
        	return 1;
        } 
		
        return 0;
	}

	public static String addPathToFileName( String fileName, String sourcePath ) {

		if (fileName.length() < 1 ) return null;
		
		if ( !sourcePath.endsWith("/")) {
			sourcePath += "/";
		}
		
		fileName = fileName.replaceAll("/\\./", "/");
		
		if ( fileName.charAt(0) != '/') {
			
			if ( fileName.startsWith("./") ) {
				fileName = fileName.substring(2);
				fileName = sourcePath + fileName;
				return fileName;
			}
			
			if ( fileName.startsWith("../")) {
				String tempPath = sourcePath;
				
				while ( fileName.startsWith("../") ) {
					fileName = fileName.substring(3);
					tempPath = tempPath.substring(0, tempPath.lastIndexOf('/'));
					tempPath = tempPath.substring(0, tempPath.lastIndexOf('/') + 1);
				}
				
				fileName = tempPath + fileName;
				
			} else {
				fileName = sourcePath + fileName;				
			}
			//System.out.println("Adding path: " + sourcePath);
		}

		
		
		return fileName;
	}

	
	private static Matcher matcher = null;
	private static String matchStr = "";
}

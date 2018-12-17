package lice.common;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class DebugHelper {

	public static Vector<String> allSourceFileNames( String directoryName ) {
		Vector<String> buf = new Vector<String>();
		File directory = new File(directoryName);
		File [] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			if ( files[i].isDirectory() ) {
				buf.addAll( allSourceFileNames( files[i].getPath() ) );
			} else {
				String fileName = files[i].getName();
				if ( CommonFuctions.isSourceFile(fileName) ) {
					try {
						buf.add( files[i].getCanonicalPath() );
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		return buf;
	}
}

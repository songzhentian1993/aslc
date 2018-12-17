package lice.analyzator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import lice.common.StringFuncs;
import lice.licenses.LicenseTemplate;
import lice.objects.RemoteProcedureCallInfo;
import lice.objects.TargetFile;

public class RPCAnalyzer {

	public static TargetFile findRPC( TargetFile f ) {
		
		if ( f.getFileName().endsWith(".h")) return f;
		
		String all;
		StringBuffer strbuf = new StringBuffer("");
		FileReader fr = null;
		try {
			fr = new FileReader( f.getFileName() );
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
		
		all = sos.toLowerCase();
		
		String regexp = "";

//		for (Iterator iter = licenses.iterator(); iter.hasNext();) {
//			LicenseTemplate lic = (LicenseTemplate) iter.next();
			regexp = "init_plugin";
			regexp = regexp.toLowerCase();
			
			int numberOfMatches = StringFuncs.findMatchingString( regexp, all ); 
			if ( numberOfMatches > 0 ) {
				RemoteProcedureCallInfo info = new RemoteProcedureCallInfo();
				info.setRpcStatus(RemoteProcedureCallInfo.RPC_IMPLEMENT);
				info.setRpcId("GAIM_PLUGIN");
				f.setRpcInfo(info);
			}
			regexp = "gaim_plugins_find_with_id";
			//regexp = "gaim_plugin_load";
			regexp = regexp.toLowerCase();
			
			numberOfMatches = StringFuncs.findMatchingString( regexp, all ); 
			if ( numberOfMatches > 0 ) {
				RemoteProcedureCallInfo info = new RemoteProcedureCallInfo();
				info.setRpcStatus(RemoteProcedureCallInfo.RPC_CALL);
				info.setRpcId("GAIM_PLUGIN");
				f.setRpcInfo(info);
			}
			
//		} 
		return f;
	}
}

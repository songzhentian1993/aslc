package lice.objects;

public class RemoteProcedureCallInfo {

	public static String RPC_NONE = "none";
	public static String RPC_CALL = "call";
	public static String RPC_IMPLEMENT = "implement";
	
	
	public String getRpcId() {
		return rpcId;
	}
	
	public void setRpcId(String rpcId) {
		this.rpcId = rpcId;
	}
	
	public String getRpcStatus() {
		return rpcStatus;
	}

	public void setRpcStatus(String rpcStatus) {
		this.rpcStatus = rpcStatus;
	}

	private String rpcStatus = RPC_NONE;
	private String rpcId = "";

}

package simulator.tunnel.signalling;

import java.util.HashMap;
import java.util.Map;

import simulator.tunnel.network.UDPSocketInfo;

public class SIPUsersMap {

	private Map<String, UDPSocketInfo> mUsersAddressesMap;
	
	public SIPUsersMap() {
		mUsersAddressesMap = new HashMap<String, UDPSocketInfo>();
	}
	
	public void putSipUser(String user, UDPSocketInfo socketInfo) {
		mUsersAddressesMap.put(user, socketInfo);
	}
	
	public void removeSipUser(String user) {
		mUsersAddressesMap.remove(user);
	}
	
	public int getSipUsersCount() {
		return mUsersAddressesMap.size();
	}
	
	public UDPSocketInfo getSocketInfoForUser(String user) {
		return mUsersAddressesMap.get(user);
	}
}

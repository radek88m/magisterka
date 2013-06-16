package simulator.tunnel.network;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class UDPSocketInfo {

	private InetAddress mIPAddress;
	private int mPort;
	
	public UDPSocketInfo(InetAddress address, int port){
		mIPAddress = address;
		mPort = port;
	}
	
	public UDPSocketInfo(String ipAddress, int port) throws UnknownHostException {
		mIPAddress = InetAddress.getByName(ipAddress);
		mPort = port;
	}
	
	public InetAddress getAddress() {
		return mIPAddress;
	}
	
	public int getPort() {
		return mPort;
	}
	
	@Override
	public String toString() {
		return mIPAddress+":"+mPort;
	}
	
	@Override
	public boolean equals(Object obj) {
		UDPSocketInfo other = (UDPSocketInfo) obj;
		if(mIPAddress.equals(other.getAddress())
				&& mPort == other.getPort()) {
			return true;
		}
		return false;
	}
}

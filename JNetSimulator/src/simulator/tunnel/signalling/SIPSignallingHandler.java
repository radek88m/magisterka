package simulator.tunnel.signalling;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Scanner;

public class SIPSignallingHandler implements IIncomingSipMessageHandler {

	private SIPTunnel mTunnel;
	private SIPTunnelConfig mConfig;
	
	private String mOriginSipUser;
	
	private String mOriginIP;
	private InetAddress mOriginAddress;
	private int mOriginPort;
	
	
	SIPSignallingHandler(SIPTunnel tunnel, SIPTunnelConfig config) {
		mTunnel = tunnel;
		mConfig = config;
	}
	
	public boolean handleOriginPacket(String msgBuff,
			DatagramPacket packet) {
		
		mOriginIP = packet.getAddress().toString();
		mOriginAddress = packet.getAddress();
		mOriginPort = packet.getPort();
		
		SipMessageScanner scanner = new SipMessageScanner(msgBuff);
		
		mOriginSipUser = scanner.getSipUser();
		
		handleIncomingPacket(msgBuff, packet);
				
		return true;
	}
	

	private void handleIncomingPacket(String msgBuff, DatagramPacket inputPacket) {
		
		InetAddress destAddress;
		int destPort;
		
		if(mOriginAddress.equals(inputPacket.getAddress()) && mOriginPort == inputPacket.getPort()) {
			destAddress = mConfig.getSipServerAddress();
			destPort = mConfig.getSipServerPort();
		} else {
			// Packet from server, send it to client
			destAddress = mOriginAddress;
			destPort = mOriginPort;
		}
		
		DatagramPacket outPacket = new DatagramPacket(inputPacket.getData(), inputPacket.getData().length,
				destAddress, destPort);
		
		mTunnel.sendPacket(outPacket);
	}

	@Override
	public boolean onIncomingPacket(SIPTunnel tunnel, String msgBuff,
			DatagramPacket packet) {
		
		SipMessageScanner scanner = new SipMessageScanner(msgBuff);
		if(mOriginSipUser.equals(scanner.getSipUser())) {
			handleIncomingPacket(msgBuff, packet);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public long timeSinceLastHandledMessage() {
		// TODO Auto-generated method stub
		return 0;
	}
}

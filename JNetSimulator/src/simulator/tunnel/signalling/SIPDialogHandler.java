package simulator.tunnel.signalling;

import java.net.DatagramPacket;

import simulator.tunnel.mediastream.TunnelStreamManager;
import simulator.tunnel.network.UDPSocketInfo;
import simulator.tunnel.signalling.SIPMessageScanner.SipHeader;
import simulator.tunnel.signalling.SIPMessageScanner.SipMethod;

public class SIPDialogHandler implements ISIPIncomingMessageHandler {

	private SIPTunnel mTunnel;
	private SIPTunnelConfig mConfig;
		
	private String mDialogCallID;
	
	private long mLastMessageTime;
	
	private boolean isStartedBySipServer = false;
	
	private SIPInviteSessionHandler mInviteHandler;
	
	SIPDialogHandler(SIPTunnel tunnel, SIPTunnelConfig config) {
		mTunnel = tunnel;
		mConfig = config;
	}
	
	public boolean handleOriginPacket(String msgBuff,
			DatagramPacket packet) {
		
		UDPSocketInfo info = new UDPSocketInfo(packet.getAddress(), packet.getPort());
		
		SIPMessageScanner scanner = new SIPMessageScanner(msgBuff);
		
		if(mConfig.getSipServerSocketInfo().equals(info)) {
			// Dialog started by sip server
			sendToUserDestination(scanner, msgBuff, packet);
			isStartedBySipServer = true;
		} else {
			String sipUser = scanner.getUserFromHeader(SipHeader.CONTACT);
			if(scanner.isSipRequest()) {
				mTunnel.getUsersMap().putSipUser(sipUser, info);
			}
			sendToSipServer(msgBuff, packet);
			isStartedBySipServer = false;
		}
		
		mDialogCallID = scanner.getHeaderValue(SipHeader.CALL_ID);		
		
		// Check if we have INVITE
		if(scanner.isMethod(SipMethod.INVITE)) {
			TunnelStreamManager manager = mTunnel.getTunnelStreamManager();
			mInviteHandler = new SIPInviteSessionHandler(mConfig, manager);
			mInviteHandler.handleMessage(msgBuff, packet);
		}

		mLastMessageTime = System.currentTimeMillis();
				
		return true;
	}
	
	private void sendToUserDestination(SIPMessageScanner scanner, String msgBuff,
			DatagramPacket packet) {
		if(mInviteHandler != null) mInviteHandler.handleMessage(mDialogCallID, packet);
		String user = scanner.getUserFromHeader(SipHeader.TO);		
		UDPSocketInfo userDest = mTunnel.getUsersMap().getSocketInfoForUser(user);
		if(userDest == null) return;
		sendToDestination(userDest, msgBuff, packet);
	}

	
	private void sendToSipServer(String msgBuff, DatagramPacket inputPacket) {
		DatagramPacket outPacket = new DatagramPacket(
				inputPacket.getData(), 
				inputPacket.getLength(),
				mConfig.getSipServerAddress(), 
				mConfig.getSipServerPort());
		
		mTunnel.sendPacket(outPacket);
	}
	
	private void sendToDestination(UDPSocketInfo destination, 
			String msgBuff, DatagramPacket inputPacket) {
		
		DatagramPacket outPacket = new DatagramPacket(
				inputPacket.getData(), 
				inputPacket.getLength(),
				destination.getAddress(), 
				destination.getPort());
		
		mTunnel.sendPacket(outPacket);
	}
	

	private void dispatchIncomingPacket(String msgBuff, DatagramPacket inputPacket) {

		UDPSocketInfo info = new UDPSocketInfo(inputPacket.getAddress(), 
				inputPacket.getPort());
		
		SIPMessageScanner scanner = new SIPMessageScanner(msgBuff);
		
		if(mConfig.getSipServerSocketInfo().equals(info)) {
			try {
				if(isStartedBySipServer) {
					sendToUserDestination(scanner, msgBuff, inputPacket);
				} else {
					UDPSocketInfo addressFromVia = scanner.getDestAddressFromVia();
					sendToDestination(addressFromVia, msgBuff, inputPacket);
				}
			} catch (Exception e) {
			}
		} else {
			sendToSipServer(msgBuff, inputPacket);
		}
				
		mLastMessageTime = System.currentTimeMillis();
	}

	@Override
	public boolean onIncomingPacket(SIPTunnel tunnel, String msgBuff,
			DatagramPacket packet) {
		
		if(msgBuff == null) return false;
		
		SIPMessageScanner scanner = new SIPMessageScanner(msgBuff);
		String callId = scanner.getHeaderValue(SipHeader.CALL_ID);
		if(mDialogCallID.equals(callId)) {
			dispatchIncomingPacket(msgBuff, packet);
			return true;
		}
		
		return false;
	}

	@Override
	public long timeSinceLastHandledMessage() {
		return mLastMessageTime;
	}
}

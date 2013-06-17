package simulator.tunnel.signalling;

import java.math.BigInteger;
import java.net.DatagramPacket;
import java.security.SecureRandom;

import simulator.tunnel.mediastream.TunnelStreamManager;
import simulator.tunnel.network.UDPSocketInfo;
import simulator.tunnel.signalling.SIPMessageScanner.SipHeader;
import simulator.tunnel.signalling.SIPMessageScanner.SipMethod;

public class SIPDialogHandler implements ISIPIncomingMessageHandler {
	
	private static final int BRANCH_ID_LEN = 130;

	private SIPTunnel mTunnel;
	private SIPTunnelConfig mConfig;
		
	private String mDialogCallID;
	
	private String mUniqueBranchID;
	
	private long mLastMessageTime;
	
	private boolean isStartedBySipServer = false;
	
	private SIPInviteSessionHandler mInviteHandler;
	
	SIPDialogHandler(SIPTunnel tunnel, SIPTunnelConfig config) {
		mTunnel = tunnel;
		mConfig = config;
	}
	
	private String generateUniqueString(int len) {
		SecureRandom random = new SecureRandom();
		return new BigInteger(len, random).toString(32);
	}
	
	public boolean handleOriginPacket(String msgBuff,
			DatagramPacket packet) {
		
		UDPSocketInfo info = new UDPSocketInfo(packet.getAddress(), packet.getPort());
		
		SIPMessageScanner scanner = new SIPMessageScanner(msgBuff);
		
		mDialogCallID = scanner.getHeaderValue(SipHeader.CALL_ID);		
		
		mUniqueBranchID = generateUniqueString(BRANCH_ID_LEN);
		
		if(mConfig.getSipServerSocketInfo().equals(info)) {
			// Dialog started by sip server
			isStartedBySipServer = true;
			sendToUserDestination(scanner, packet);
		} else {
			isStartedBySipServer = false;
			String sipUser = scanner.getUserFromHeader(SipHeader.CONTACT);
			if(scanner.isSipRequest()) {
				mTunnel.getUsersMap().putSipUser(sipUser, info);
			}
			sendToSipServer(scanner, packet);
		}		
		
		// Check if we have INVITE
		if(scanner.isMethod(SipMethod.INVITE)) {
			TunnelStreamManager manager = mTunnel.getTunnelStreamManager();
			mInviteHandler = new SIPInviteSessionHandler(mConfig, manager);
			mInviteHandler.handleMessage(msgBuff, packet);
		}

		mLastMessageTime = System.currentTimeMillis();
				
		return true;
	}
	
	private void sendToUserDestination(SIPMessageScanner scanner, DatagramPacket packet) {
		//if(mInviteHandler != null) mInviteHandler.handleMessage(mDialogCallID, packet);
		
		String user = scanner.getUserFromHeader(SipHeader.TO);		
				
		UDPSocketInfo userDest = mTunnel.getUsersMap().getSocketInfoForUser(user);
		
		if(userDest == null) return;
		
		sendToDestination(scanner, userDest, packet);
	}

	
	private void sendToSipServer(SIPMessageScanner scanner, DatagramPacket inputPacket) {
		
//		if(isStartedBySipServer) {
//			SIPMessageScanner scanner = new SIPMessageScanner(msgBuff);
//			scanner.replaceVia(inputPacket, mConfig.getSipServerDomain(),  mConfig.getSipServerPort());
//		}
				
		byte[] data = scanner.handleViaHeader(inputPacket, mConfig.getLocalTunnelIPAddress(), 
				mConfig.getLocalTunnelSipPort(), mUniqueBranchID);
		
		DatagramPacket outPacket = new DatagramPacket(
				data, 
				data.length,
				mConfig.getSipServerAddress(), 
				mConfig.getSipServerPort());
		
		mTunnel.sendPacket(outPacket);
	}
	
	private void sendToDestination(SIPMessageScanner scanner, UDPSocketInfo destination, DatagramPacket inputPacket) {
		
		byte[] data = scanner.handleViaHeader(inputPacket, mConfig.getLocalTunnelIPAddress(), 
				mConfig.getLocalTunnelSipPort(), mUniqueBranchID);
		
		DatagramPacket outPacket = new DatagramPacket(
				data, 
				data.length,
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
					sendToUserDestination(scanner, inputPacket);
				} else {
					UDPSocketInfo addressFromVia = scanner.getDestAddressFromVia(mUniqueBranchID);
					sendToDestination(scanner, addressFromVia, inputPacket);
				}
			} catch (Exception e) {
			}
		} else {
			sendToSipServer(scanner, inputPacket);
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

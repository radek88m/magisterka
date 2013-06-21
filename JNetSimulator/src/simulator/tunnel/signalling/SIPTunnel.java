package simulator.tunnel.signalling;

import java.net.DatagramPacket;
import java.util.ArrayList;
import simulator.tunnel.mediastream.TunnelStreamManager;
import simulator.tunnel.network.IOPacketDispatcher;
import simulator.tunnel.network.IOPacketDispatcher.IDispatcherHandler;

public class SIPTunnel implements IDispatcherHandler{
	
	private SIPTunnelConfig mConfig;

	private int mLocalPort;
	private IOPacketDispatcher mIOPacketDispatcher;	
	
	private TunnelStreamManager mStreamManager;
	
	private ArrayList<ISIPIncomingMessageHandler> mSipDialogHandlers;
	
	private SIPUsersMap mUsersMap;
	
	public SIPTunnel(SIPTunnelConfig config) {
		mConfig = config;
		mLocalPort = mConfig.getLocalTunnelSipPort();
		
		mSipDialogHandlers = new ArrayList<ISIPIncomingMessageHandler>();
		mUsersMap = new SIPUsersMap();
		
		mStreamManager = new TunnelStreamManager(mConfig);
	}
		
	public boolean startTunnel() {
		mIOPacketDispatcher = new IOPacketDispatcher(mLocalPort);
		mIOPacketDispatcher.registerHandler(this);
		mIOPacketDispatcher.start();
		return true;
	}
	
	public boolean stopTunnel() {
		mIOPacketDispatcher.unregisterHandler(this);
		mIOPacketDispatcher.stop();
		mIOPacketDispatcher = null;
		return true;
	}
	
	public boolean sendPacket(DatagramPacket packet) {	
		return mIOPacketDispatcher.sendPacket(packet);
	}
	
	public SIPUsersMap getUsersMap() {
		return mUsersMap;
	}
	
	public TunnelStreamManager getTunnelStreamManager() {
		return mStreamManager;
	}

	@Override
	public boolean onHandleIncomingPacket(IOPacketDispatcher dispatcher,
			DatagramPacket packet) {
		
		// Drop keep alive packets
		if(packet.getLength() <= 4) return false;
		
		String msgBuff = new String(packet.getData(), 0, packet.getLength());
		
		// Dispatch packet to handlers
		for(ISIPIncomingMessageHandler handler : mSipDialogHandlers) {
			if(handler.onIncomingPacket(this, msgBuff, packet)) {
				return true;
			}
		}		

//		long time = System.currentTimeMillis();
//		// Delete unused handlers
//		for(ISIPIncomingMessageHandler handler : mSipDialogHandlers) {
//			if((time - handler.timeSinceLastHandledMessage()) > 60000) {
//				mSipDialogHandlers.remove(handler);
//			}
//		}
		
		// Incoming packet not handled by any module, we have to create new handler
		SIPDialogHandler handler = new SIPDialogHandler(this, mConfig);
		
		// Check if we have a valid SIP message
		if(handler.handleOriginPacket(msgBuff, packet))
			mSipDialogHandlers.add(handler);
		
		return true;
	}

}

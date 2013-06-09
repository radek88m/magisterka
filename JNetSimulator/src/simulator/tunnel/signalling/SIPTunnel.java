package simulator.tunnel.signalling;

import java.net.DatagramPacket;
import java.util.ArrayList;

import simulator.tunnel.network.IOPacketDispatcher;
import simulator.tunnel.network.IOPacketDispatcher.IDispatcherHandler;

public class SIPTunnel implements IDispatcherHandler{
	
	private SIPTunnelConfig mConfig;

	private int mLocalPort;
	private IOPacketDispatcher mIOPacketDispatcher;	
	
	private ArrayList<IIncomingSipMessageHandler> mSipHandlers;
	
	public SIPTunnel(SIPTunnelConfig config) {
		mConfig = config;
		mLocalPort = mConfig.getLocalTunnelSipPort();
		
		mSipHandlers = new ArrayList<IIncomingSipMessageHandler>();
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

	@Override
	public boolean onHandleIncomingPacket(IOPacketDispatcher dispatcher,
			DatagramPacket packet) {
		
		String msgBuff = new String(packet.getData());
		
		// Dispatch packet to handlers
		for(IIncomingSipMessageHandler handler : mSipHandlers) {
			if(handler.onIncomingPacket(this, msgBuff, packet)) {
				return true;
			}
		}
		
		// Incoming packet not handled by any module, we have to create new handler
		SIPSignallingHandler handler = new SIPSignallingHandler(this, mConfig);
		
		// Check if we have a valid SIP message
		if(handler.handleOriginPacket(msgBuff, packet))
			mSipHandlers.add(handler);
		
		return true;
	}

}

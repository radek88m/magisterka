package simulator.tunnel.signalling;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Iterator;

import simulator.gui.logger.Logger;
import simulator.tunnel.mediastream.TunnelStreamManager;
import simulator.tunnel.network.IOPacketDispatcher;
import simulator.tunnel.network.IOPacketDispatcher.IDispatcherHandler;

public class SIPTunnel implements IDispatcherHandler{

	private Logger mLogger;
	
	private SIPTunnelConfig mConfig;

	private int mLocalPort;
	private IOPacketDispatcher mIOPacketDispatcher;	

	private TunnelStreamManager mStreamManager;

	private ArrayList<ISIPIncomingMessageHandler> mSipDialogHandlers;

	private SIPUsersMap mUsersMap;
	
	private boolean isRunning;

	public SIPTunnel(SIPTunnelConfig config, Logger logger) {
		mLogger = logger;
		mConfig = config;
		mLocalPort = mConfig.getLocalTunnelSipPort();

		mSipDialogHandlers = new ArrayList<ISIPIncomingMessageHandler>();
		mUsersMap = new SIPUsersMap();

		mStreamManager = new TunnelStreamManager(mConfig);
	}
	
	public void reloadConfig(SIPTunnelConfig config) {
		Logger logger = mConfig.traceSipMessage() ? mLogger : null;
		if(mIOPacketDispatcher != null) {
			mIOPacketDispatcher.setLogger(logger);
		}
	}
	
	public boolean startTunnel() {
		Logger logger = mConfig.traceSipMessage() ? mLogger : null;
		mIOPacketDispatcher = new IOPacketDispatcher(mLocalPort, logger);
		mIOPacketDispatcher.registerHandler(this);
		mIOPacketDispatcher.start();
		isRunning = true;
		return true;
	}

	public boolean stopTunnel() {
		mIOPacketDispatcher.unregisterHandler(this);
		mIOPacketDispatcher.stop();
		mIOPacketDispatcher = null;
		isRunning = false;
		return true;
	}
	
	public boolean isRunning() {
		return isRunning;
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

		// Delete unused handlers
		long time = System.currentTimeMillis();
		long timeoutTime = 60000; // 60 seconds
		for (Iterator<ISIPIncomingMessageHandler> iterator = mSipDialogHandlers.iterator(); iterator.hasNext();) {
			ISIPIncomingMessageHandler handler = iterator.next();
			if((time - handler.timeSinceLastHandledMessage()) > timeoutTime) {
				logMessage("Deleting dialog handler: " + handler);
				//mSipDialogHandlers.remove(handler);
			}
		}

		// Incoming packet not handled by any module, we have to create new handler
		SIPDialogHandler handler = new SIPDialogHandler(this, mConfig);

		// Check if we have a valid SIP message
		if(handler.handleOriginPacket(msgBuff, packet)) {
			logMessage("Adding new dialog handler: " + handler);
			mSipDialogHandlers.add(handler);
		}
		return true;
		
	}
	
	private void logMessage(String str) {
		if(mLogger != null)
			mLogger.println(SIPTunnel.class.getSimpleName().toString()+": "+str);
	}

	public Logger getLogger() {
		return mLogger;
	}
}

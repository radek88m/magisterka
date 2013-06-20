package simulator.tunnel.mediastream;

import java.net.DatagramPacket;

import simulator.tunnel.network.IOPacketDispatcher;
import simulator.tunnel.network.IOPacketDispatcher.IDispatcherHandler;

public class TunnelStream implements IDispatcherHandler {
	
	private int mLocalPort;
	private TunnelStreamSettings mSettings;
	private IOPacketDispatcher mIOPacketDispatcher;
	
	private TunnelStreamHandler mPartyA;
	private TunnelStreamHandler mPartyB;
	
	private boolean isRunning;
	
	public TunnelStream(int port, TunnelStreamSettings settings) {
		mLocalPort = port;
		mSettings = settings;
	}
	
	
	public boolean start() {
		mIOPacketDispatcher = new IOPacketDispatcher(mLocalPort);
		mIOPacketDispatcher.registerHandler(this);
		mIOPacketDispatcher.start();
		
		isRunning = true;
		
		return true;
	}
	
	public boolean stop() {
		
		isRunning = false;
		
		if(mPartyA != null) {
			mPartyA.stopProcessing();
			mPartyA = null;
		}
		if(mPartyB != null) {
			mPartyB.stopProcessing();
			mPartyB = null;
		}
		mIOPacketDispatcher.unregisterHandler(this);
		mIOPacketDispatcher.stop();
		mIOPacketDispatcher = null;
		return true;
	}
	
	public boolean isRunning() {
		return isRunning;
	}


	@Override
	public boolean onHandleIncomingPacket(IOPacketDispatcher dispatcher,
			DatagramPacket packet) {
		
		if(mPartyA == null) {
			mPartyA = new TunnelStreamHandler(mIOPacketDispatcher, packet, mSettings);
			return true;
		} 
		if (mPartyB == null) {
			
			if(mPartyA.isOriginPacket(packet)) {
				return true;
			}
			
			mPartyB = new TunnelStreamHandler(mIOPacketDispatcher, packet, mSettings);
			
			// Spiecie ze soba dwoch strumieni
			mPartyB.acquireDestination(mPartyA.getOriginPacket());
			mPartyA.acquireDestination(mPartyB.getOriginPacket());
			
			mPartyA.startProcessing();
			mPartyB.startProcessing();
			
			// Mamy obie strony rozmowy, mozemy przestac sluchac na porcie
			mIOPacketDispatcher.unregisterHandler(this);
			return true;
		} 
		
		return false;
	}


	public int getPort() {
		return mLocalPort;
	}

}

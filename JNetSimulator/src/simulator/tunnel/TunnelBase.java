package simulator.tunnel;

import java.net.DatagramPacket;

import simulator.tunnel.IOPacketDispatcher.IDispatcherHandler;

public class TunnelBase implements IDispatcherHandler {
	
	private int mLocalPort;
	private TunnelSettings mSettings;
	private IOPacketDispatcher mIOPacketDispatcher;
	
	private TunnelStreamHandler mPartyA;
	private TunnelStreamHandler mPartyB;
	
	public TunnelBase(int port, TunnelSettings settings) {
		mLocalPort = port;
		mSettings = settings;
	}
	
	
	public boolean start() {
		mIOPacketDispatcher = new IOPacketDispatcher(mLocalPort);
		mIOPacketDispatcher.registerHandler(this);
		mIOPacketDispatcher.start();
		return true;
	}
	
	public boolean stop() {
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


	@Override
	public boolean onHandleIncomingPacket(IOPacketDispatcher dispatcher,
			DatagramPacket packet) {
		
		if(mPartyA == null) {
			mPartyA = new TunnelStreamHandler(mIOPacketDispatcher, packet, mSettings);
			mPartyA.startProcessing();
			return true;
		} else if (mPartyB == null) {
			mPartyB = new TunnelStreamHandler(mIOPacketDispatcher, packet, mSettings);
			mPartyB.startProcessing();
			
			// Spiecie ze soba dwoch strumieni
			mPartyB.accuireDestination(mPartyA.getOriginPacket());
			mPartyA.accuireDestination(mPartyB.getOriginPacket());
			
			// Mamy obie strony rozmowy, mozemy przestac sluchac na porcie
			mIOPacketDispatcher.unregisterHandler(this);
			return true;
		} else {
			return false;
		}
	}

}

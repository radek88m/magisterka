package simulator.tunnel.mediastream;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;

import simulator.gui.logger.Logger;
import simulator.tunnel.network.IOPacketDispatcher;
import simulator.tunnel.network.IOPacketDispatcher.IDispatcherHandler;

public class TunnelStreamHandler implements IDispatcherHandler {

	private IOPacketDispatcher mDispatcher;

	private Logger mLogger;

	private DatagramPacket mOriginPacket;
	private String mOriginIP;
	private InetAddress mOriginInetAddress;
	private int mOriginPort;

	private String mDestIP;
	private InetAddress mDestAddress;
	private int mDestPort;

	private boolean hasDestination = false;

	private TunnelStreamSettings mSettings;

	private TunnelStreamManipulator mTunnelStreamManipulator;

	private ArrayList<MediaPacket> mIncomingPackets;
	private Object mLock = new Object();

	public TunnelStreamHandler(IOPacketDispatcher dispatcher, 
			DatagramPacket originPacket, TunnelStreamSettings settings, Logger logger) {
		mDispatcher = dispatcher;
		mOriginInetAddress = originPacket.getAddress();
		mOriginPort = originPacket.getPort();
		mOriginIP = mOriginInetAddress.toString().substring(1);
		byte[] mockData = new byte[1];
		mOriginPacket = new DatagramPacket(mockData, mockData.length, mOriginInetAddress, mOriginPort);

		mSettings = settings;

		mIncomingPackets = new ArrayList<MediaPacket>();
		mLogger = logger;
	}

	public DatagramPacket getOriginPacket(){
		return mOriginPacket;
	}

	public void acquireDestination(DatagramPacket destinationPacket) {
		mDestAddress = destinationPacket.getAddress();
		mDestPort = destinationPacket.getPort();		
		mDestIP = mDestAddress.toString().substring(1);

		hasDestination = true;
	}

	@Override
	public boolean onHandleIncomingPacket(IOPacketDispatcher dispatcher,
			DatagramPacket packet) {
		if(!hasDestination) return false;
		if(packet.getAddress().equals(mOriginInetAddress)
				&& packet.getPort() == mOriginPort) {
			synchronized(mLock) {
				MediaPacket mediaPacket = new MediaPacket(packet,
						System.currentTimeMillis());
				mIncomingPackets.add(mediaPacket);
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean startProcessing() {
		mDispatcher.registerHandler(this);
		mTunnelStreamManipulator = new TunnelStreamManipulator(this, mSettings);
		mTunnelStreamManipulator.start();
		logMessage("TunnelStreamManipulator started, destination: "+mDestAddress.toString()+":"+mDestPort);
		return true;
	}

	public boolean stopProcessing() {
		mDispatcher.unregisterHandler(this);
		mTunnelStreamManipulator.stopRunning();
		mTunnelStreamManipulator = null;
		logMessage("TunnelStreamManipulator stopped, destination: "+mDestAddress.toString()+":"+mDestPort);
		return true;
	}

	public MediaPacket requestPacketFromQueue() {
		synchronized(mLock) {
			if(mIncomingPackets.size() > 0) {
				MediaPacket mediaPacket = mIncomingPackets.get(0);
				mIncomingPackets.remove(mediaPacket);
				return mediaPacket;
			} 
		}
		return null;
	}

	public void sendToDestination(byte[] data) {
		if(!hasDestination) return;
		DatagramPacket packet = new DatagramPacket(data, 
				data.length, mDestAddress, mDestPort);
		mDispatcher.sendPacket(packet);
	}

	@Override
	public String toString() {
		String string = "From "+mOriginIP+":"+mDestPort+" to " +
				mDestIP+":"+mDestPort;
		return string;
	}

	public boolean isOriginPacket(DatagramPacket packet) {
		return (packet.getAddress().equals(mOriginInetAddress)
				&& packet.getPort() == mOriginPort);
	}

	private void logMessage(String str) {
		if(mLogger != null)
			mLogger.println(TunnelStreamHandler.class.getSimpleName().toString()+": "+str);
	}
}

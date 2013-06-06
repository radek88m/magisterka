package simulator.tunnel;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;

import simulator.tunnel.IOPacketDispatcher.IDispatcherHandler;

public class TunnelStreamHandler implements IDispatcherHandler {
	
	private IOPacketDispatcher mDispatcher;
	
	private DatagramPacket mOriginPacket;
	private String mOriginIP;
	private InetAddress mOriginInetAddress;
	private int mOriginPort;
	
	private String mDestIP;
	private InetAddress mDestAddress;
	private int mDestPort;
	
	private boolean hasDestination = false;
	
	private TunnelSettings mSettings;
	
	private TunnelStreamWorker mTunnelStreamWorker;
	
	private ArrayList<byte[]> mIncomingPackets;
	private Object mLock = new Object();
	
	public TunnelStreamHandler(IOPacketDispatcher dispatcher, 
			DatagramPacket originPacket, TunnelSettings settings) {
		mDispatcher = dispatcher;
		mOriginPacket = originPacket;
		mOriginInetAddress = originPacket.getAddress();
		mOriginPort = originPacket.getPort();
		mOriginIP = mOriginInetAddress.toString();
		
		mSettings = settings;
		
		mIncomingPackets = new ArrayList<byte[]>();
	}
	
	public DatagramPacket getOriginPacket(){
		return mOriginPacket;
	}
	
	public void accuireDestination(DatagramPacket destinationPacket) {
		mDestAddress = destinationPacket.getAddress();
		mDestPort = destinationPacket.getPort();		
		mDestIP = mDestAddress.toString();
		
		hasDestination = true;
	}
	
	@Override
	public boolean onHandleIncomingPacket(IOPacketDispatcher dispatcher,
			DatagramPacket packet) {
		if(!hasDestination) return false;
		if(packet.getAddress() == mOriginInetAddress 
				&& packet.getPort() == mOriginPort) {
			mIncomingPackets.add(packet.getData().clone());
			return true;
		} else {
			return false;
		}
	}
	
	public boolean startProcessing() {
		mDispatcher.registerHandler(this);
		mTunnelStreamWorker = new TunnelStreamWorker();
		mTunnelStreamWorker.start();
		return true;
	}
	
	public boolean stopProcessing() {
		mDispatcher.unregisterHandler(this);
		mTunnelStreamWorker.stopRunning();
		mTunnelStreamWorker = null;
		return true;
	}
	
	private void sendToDestination(byte[] data) {
		if(!hasDestination) return;
		DatagramPacket packet = new DatagramPacket(data, 
				data.length, mOriginInetAddress, mDestPort);
		mDispatcher.sendPacket(packet);
	}
	
	@Override
	public String toString() {
		String string = "From "+mOriginIP+":"+mDestPort+" to " +
				mDestIP+":"+mDestPort;
		return string;
	}
	
	private class TunnelStreamWorker extends Thread {
		
		private boolean isRunning = true;
		
		@Override
		public void run() {
			super.run();
			while(isRunning) {
				try {
					if(mIncomingPackets.size() > 0) {
						byte[] data = mIncomingPackets.get(0);
						sendToDestination(data);
						mIncomingPackets.remove(data);
					}
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void stopRunning() {
			isRunning = false;
		}
		
	}
}

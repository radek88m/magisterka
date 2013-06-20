package simulator.tunnel.mediastream;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;

import simulator.logger.Logger;
import simulator.tunnel.network.IOPacketDispatcher;
import simulator.tunnel.network.IOPacketDispatcher.IDispatcherHandler;

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

	private TunnelStreamSettings mSettings;

	private TunnelStreamWorker mTunnelStreamWorker;

	private ArrayList<byte[]> mIncomingPackets;
	private Object mLock = new Object();

	public TunnelStreamHandler(IOPacketDispatcher dispatcher, 
			DatagramPacket originPacket, TunnelStreamSettings settings) {
		mDispatcher = dispatcher;
		mOriginInetAddress = originPacket.getAddress();
		mOriginPort = originPacket.getPort();
		mOriginIP = mOriginInetAddress.toString().substring(1);
		byte[] mockData = new byte[1];
		mOriginPacket = new DatagramPacket(mockData, mockData.length, mOriginInetAddress, mOriginPort);

		mSettings = settings;

		mIncomingPackets = new ArrayList<byte[]>();
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
//				Logger.println("Przyjalem pakiet od: "+packet.getAddress().toString()+", len: "+packet.getLength());
				byte[] data = new byte[packet.getLength()];
				System.arraycopy(packet.getData(), 0, data, 0, packet.getLength());
				mIncomingPackets.add(data);
			}
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
				data.length, mDestAddress, mDestPort);
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
			Logger.println("Bede nakurwiac do: "+mDestAddress.toString()+":"+mDestPort);
			while(isRunning) {
				try {
					synchronized(mLock) {
						if(mIncomingPackets.size() > 0) {
							byte[] data = mIncomingPackets.get(0);
//							Logger.println("Nakurwiam pakiet do: "+mOriginInetAddress.toString()+", len: "+data.length);
							sendToDestination(data);
							mIncomingPackets.remove(data);
						} 
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

	public boolean isOriginPacket(DatagramPacket packet) {
		return (packet.getAddress().equals(mOriginInetAddress)
				&& packet.getPort() == mOriginPort);
	}
}

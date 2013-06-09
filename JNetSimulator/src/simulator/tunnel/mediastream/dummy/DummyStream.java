package simulator.tunnel.mediastream.dummy;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import simulator.logger.Logger;
import simulator.tunnel.mediastream.dummy.audio.AudioCodecMode;
import simulator.tunnel.network.UDPSocketAdapter;
import simulator.tunnel.network.UDPSocketAdapter.IUDPSocketAdapterListener;

public class DummyStream implements IUDPSocketAdapterListener {

	private UDPSocketAdapter mSocketAdapter;
	private int mLocalPort;
	private AudioCodecMode mMode;
	private ProducerThread mProducerThread;
	
	private String mDestIP;
	private InetAddress mDestAddress;
	private int mDestPort;
	
	public DummyStream(int localPort, AudioCodecMode mode) {
		mLocalPort = localPort;
		mMode = mode;
	}
	
	public void setRemoteDestination(String ipAddress, int port) {
		mDestIP = ipAddress;
		mDestPort = port;
		try {
			mDestAddress = InetAddress.getByName(ipAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public boolean start() {		
		mSocketAdapter = new UDPSocketAdapter(mLocalPort);
		mSocketAdapter.setReceiveListener(this);
		mSocketAdapter.openSocket();
		mSocketAdapter.startReceive();
		
		
		long timeInterval = mMode.getFramePtime() 
				* mMode.getFramesPerPacket();
		
		int bytes = mMode.getOutputBytesPerFrame()
				* mMode.getFramesPerPacket();
		
		mProducerThread = new ProducerThread();
		
		mProducerThread.setup(timeInterval, bytes);
		
		mProducerThread.start();
		return true;
	}
	
	public boolean stop() {
		mSocketAdapter.setReceiveListener(null);
		mSocketAdapter.stopReceive();
		mSocketAdapter.closeSocket();
		mSocketAdapter = null;
		mProducerThread.stopProducer();
		return true;
	}
	
	
	private class ProducerThread extends Thread {
		
		private boolean isRunning = true;
		private Object mLock = new Object();
		private long timeInterval;
		private int dataSize;
				
		void setup(long interval, int bytes){
			timeInterval = interval;
			dataSize = bytes;
		}
		
		@Override
		public void run() {
			super.run();
			while(isRunning) {
				try {
					byte[] sendBuff = new byte[dataSize];
					DatagramPacket packet = new DatagramPacket(sendBuff, sendBuff.length,
							mDestAddress, mDestPort);
					Logger.println("Sending "+dataSize+" to "+ mDestIP+":"+mDestPort);
					mSocketAdapter.sendData(packet);
					Thread.sleep(2000);//timeInterval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void stopProducer() {
			isRunning = false;
		}
	}
	
	@Override
	public void onPacketReceived(UDPSocketAdapter socketAdapter,
			DatagramPacket packet) {
		Logger.println("Received " + packet.getData().length + " bytes from "
			+packet.getAddress().toString()+":"+packet.getPort());
	}

}

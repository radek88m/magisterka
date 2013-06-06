package simulator;

import simulator.logger.Logger;


public class DummyStreamProducer {
	
	private UDPSocketAdapter mUDPSocketAdapter;
	private AudioCodecMode mMode;
	private ProducerThread mProdecerThread;
	
	public DummyStreamProducer(UDPSocketAdapter socketAdapter) {
		mUDPSocketAdapter = socketAdapter;
	}
	
	public void setSendCodecMode(AudioCodecMode mode) {
		mMode = mode;
	}
	
	public void setUDPSocketAdapter(UDPSocketAdapter adapter) {
		mUDPSocketAdapter = adapter;
	}
	
	public boolean start() {
		boolean val = true;
		
		long timeInterval = mMode.getFramePtime() 
				* mMode.getFramesPerPacket();
		
		int bytes = mMode.getOutputBytesPerFrame()
				* mMode.getFramesPerPacket();
		
		mProdecerThread = new ProducerThread(mUDPSocketAdapter);
		
		mProdecerThread.setup(timeInterval, bytes);
		
		mProdecerThread.start();
		return val;
	}
	
	public boolean stop() {
		boolean val = false;
		mProdecerThread.start();
		return val;
	}
	
	
	private class ProducerThread extends Thread {
		
		private UDPSocketAdapter mAdapter;
		private boolean isRunning = true;
		private Object mLock = new Object();
		private long timeInterval;
		private int dataSize;
		
		ProducerThread(UDPSocketAdapter adapter) {
			mAdapter = adapter;
		}
		
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
					Logger.println("Sending "+dataSize+" bytes of data");
					mAdapter.sendData(sendBuff);
					Thread.sleep(timeInterval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void stopProducer() {
			isRunning = false;
		}
	}
}

package simulator;


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
		mProdecerThread = new ProducerThread(mUDPSocketAdapter);
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
		
		ProducerThread(UDPSocketAdapter adapter) {
			mAdapter = adapter;
		}
		
		@Override
		public void run() {
			super.run();
			while(isRunning) {
				try {
					byte[] sendBuff = new byte[33];
					System.out.println("Sending 33 bytes of data");
					mAdapter.sendData(sendBuff);
					Thread.sleep(1000);
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

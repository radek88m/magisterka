package simulator.tunnel;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPSocketAdapter {
	
	public interface IUDPSocketAdapterListener {
		void onPacketReceived(UDPSocketAdapter socketAdapter, DatagramPacket packet);
	}
	
	private int mLocalPort;
	
	private IUDPSocketAdapterListener mSocketListener;
	
	private DatagramSocket mUDPSocket;
	private SocketOperationsThread mOperationsThread;
	
	public UDPSocketAdapter(int localPort) {
		mLocalPort = localPort;
	}
	
	public void setReceiveListener(IUDPSocketAdapterListener listener){
		mSocketListener = listener;
	}
	
	public boolean sendData(DatagramPacket packet) {
		if(mOperationsThread != null) {
			return mOperationsThread.sendData(packet);
		} else {
			return false;
		}
	}
	
	public boolean openSocket() {
		boolean val = false;
		try {
			mUDPSocket = new DatagramSocket(mLocalPort);
			mOperationsThread = new SocketOperationsThread(this, mUDPSocket, mSocketListener);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return val;
	}
	
	public boolean startReceive() {
		if(mOperationsThread != null) {
			mOperationsThread.start();
			return true;
		}
		return false;
	}
	
	public boolean stopReceive() {
		if(mOperationsThread != null) {
			mOperationsThread.stopReceiving();
			return true;
		}
		return false;
	}
	
	public boolean closeSocket() {
		stopReceive();
		if(mUDPSocket != null) {
			mUDPSocket.close();
			return true;
		}
		return false;
	}
	
	private class SocketOperationsThread extends Thread {
		
		private UDPSocketAdapter mUDPSocketAdapter;
		private DatagramSocket mSocket;
		private IUDPSocketAdapterListener mListener;
		private boolean isRunning  = true;
		private Object mLock = new Object();
		
		SocketOperationsThread(UDPSocketAdapter adapter, DatagramSocket socket, IUDPSocketAdapterListener listener) {
			mUDPSocketAdapter = adapter;
			mSocket = socket;
			mListener = listener;
		}
		
		@Override
		public void run() {
			super.run();
			
			byte[] recvBuffer = new byte[1024];
			DatagramPacket receivePacket = 
					new DatagramPacket(recvBuffer, recvBuffer.length);
			while(isRunning) {
				try {
					mSocket.receive(receivePacket);
					mListener.onPacketReceived(mUDPSocketAdapter, receivePacket);
				} catch (IOException e) {}
			}
		}
		
		public void stopReceiving() {
			isRunning = false;
		}
		
		public boolean sendData(DatagramPacket packet) {
			try {
				mSocket.send(packet);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}
}

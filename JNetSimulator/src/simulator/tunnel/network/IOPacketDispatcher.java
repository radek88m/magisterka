package simulator.tunnel.network;

import java.net.DatagramPacket;
import java.util.concurrent.CopyOnWriteArrayList;

import simulator.gui.logger.Logger;
import simulator.tunnel.network.UDPSocketAdapter.IUDPSocketAdapterListener;

public class IOPacketDispatcher {
	
	public interface IDispatcherHandler {
		boolean onHandleIncomingPacket(IOPacketDispatcher dispatcher, DatagramPacket packet);
	}
	
	private IUDPSocketAdapterListener mListener = new IUDPSocketAdapterListener() {
		
		@Override
		public void onPacketReceived(UDPSocketAdapter socketAdapter,
				DatagramPacket packet) {
			dispatchPacket(packet);
		}
	};
	
	private UDPSocketAdapter mSocketAdapter;
	private int mLocalPort;
	
	private boolean mTracePackets = false;
	
	private CopyOnWriteArrayList<IDispatcherHandler> mHandlers;
		
	public IOPacketDispatcher(int localPort, boolean trace) {
		mLocalPort = localPort;
		mTracePackets = trace;
		mHandlers = new CopyOnWriteArrayList<IDispatcherHandler>();
	}

	public boolean registerHandler(IDispatcherHandler handler) {
		return mHandlers.add(handler);
	}
	
	public boolean unregisterHandler(IDispatcherHandler handler) {
		return mHandlers.remove(handler);
	}
	
	public boolean start() {
		mSocketAdapter = new UDPSocketAdapter(mLocalPort);
		mSocketAdapter.setReceiveListener(mListener);
		mSocketAdapter.openSocket();
		mSocketAdapter.startReceive();
		return true;
	}
	
	public boolean stop() {
		mSocketAdapter.setReceiveListener(null);
		mSocketAdapter.stopReceive();
		mSocketAdapter.closeSocket();
		mSocketAdapter = null;
		return true;
	}
	
	protected void dispatchPacket(DatagramPacket packet) {
		logPacket("Received UDP packet from: ", packet);
		for(IDispatcherHandler handler : mHandlers){
			if(handler.onHandleIncomingPacket(this, packet))
				break;
		}
	}
	
	public boolean sendPacket(DatagramPacket packet) {
		if(mSocketAdapter != null) {
			logPacket("Sending UDP packet to: ",packet);
			return mSocketAdapter.sendData(packet);
		} else {
			return false;
		}
	}

	private void logPacket(String logMsg, DatagramPacket packet) {
		if(mTracePackets)
			Logger.println(logMsg + packet.getAddress() +":" 
					+ packet.getPort() +"("+packet.getLength()+ " bytes)\n" 
						+ new String(packet.getData(), 0, packet.getLength()));
	}
}

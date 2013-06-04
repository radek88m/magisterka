package simulator;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		UDPSocketAdapter adapter = new UDPSocketAdapter(666);
		adapter.setRemoteSendAddress("localhost", 666);
		
		DummyStreamProducer prod = new DummyStreamProducer(adapter);
		
		DummyStreamReceiver receiver = new DummyStreamReceiver();
		receiver.setUDPSocketAdapter(adapter);
		
		adapter.openSocket();
		adapter.startReceive();
		
		prod.setSendCodecMode(new GSMCodecMode());
		prod.start();
		
		
	}

}

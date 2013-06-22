package simulator.tunnel.signalling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.DatagramPacket;

import simulator.tunnel.network.UDPSocketInfo;

public class SIPMessageScanner {

	private static final String SIP_URI_TAG = "sip:";
	private static final String OUTBOUND_PROXY_TAG = ";ob";

	private static final String NEW_LINE_TAG = "\n";

	private static final String VIA_HEADER_TAG = "Via:";
	private static final String CONTACT_HEADER_TAG = "Contact:";
	private static final String TO_HEADER_TAG = "To:";
	private static final String FROM_HEADER_TAG = "From:";
	private static final String CALL_ID_HEADER_TAG = "Call-ID:";
	private static final String CSEQ_HEADER_TAG = "CSeq:";
	
	private static final String ROUTE_HEADER_TAG = "Route:";
	private static final String RECORD_ROUTE_HEADER_TAG = "Record-Route:";

	public enum SipHeader {
		VIA(VIA_HEADER_TAG),
		CONTACT(CONTACT_HEADER_TAG),
		TO(TO_HEADER_TAG),
		FROM(FROM_HEADER_TAG),
		CALL_ID(CALL_ID_HEADER_TAG),
		CSEQ(CSEQ_HEADER_TAG),
		METHOD("SIP/2.0") {
			@Override
			String parseHeaderValue(String inputBuff) {
				try {
					String line;
					BufferedReader reader = new BufferedReader(new StringReader(inputBuff));
					while((line = reader.readLine()) != null) {
						if(line.contains(mHeaderStr)) {
							String[] parts = line.split(" ");
							mValue = parts[0];
							break;
						}
					}
				} catch (IOException e) {
					return null;
				}

				return mValue;
			}
		},
		STATUS_CODE("SIP/2.0") {
			@Override
			String parseHeaderValue(String inputBuff) {
				try {
					String line;
					BufferedReader reader = new BufferedReader(new StringReader(inputBuff));
					while((line = reader.readLine()) != null) {
						if(line.contains(mHeaderStr)) {
							String[] parts = line.split(" ");
							mValue = parts[1];
							break;
						}
					}
				} catch (IOException e) {
					return null;
				}

				return mValue;
			}
		};

		String mHeaderStr;
		String mValue;

		SipHeader(String headerStr) {
			mHeaderStr = headerStr;
			mValue = null;
		}

		String parseHeaderValue(String inputBuff) {
			try {
				String line;
				BufferedReader reader = new BufferedReader(new StringReader(inputBuff));
				while((line = reader.readLine()) != null) {
					if(line.startsWith(mHeaderStr)) {
						mValue = line.substring(mHeaderStr.length()).trim();
						break;
					}
				}
			} catch (IOException e) {
				return null;
			}

			return mValue;
		}

		public String replaceHeaderValue(String mInputBuffer, String newValue) {
			if(mValue != null)
				return mInputBuffer.replace(mValue, newValue);
			else 
				return null;
		}
	}	

	public enum SipMethod {
		REGISTER("REGISTER"),
		SUBSCRIBE("SUBSCRIBE"),
		INVITE("INVITE"),
		UPDATE("UPDATE"),
		ACK("ACK"),
		BYE("BYE"),
		CANCEL("CANCEL"),
		OPTIONS("OPTIONS"),
		PRACK("PRACK"),
		NOTIFY("NOTIFY"),
		PUBLISH("PUBLISH"),
		INFO("INFO"),
		REFER("REFER"),
		MESSAGE("MESSAGE");

		String mMethodStr;

		SipMethod(String methodStr) {
			mMethodStr = methodStr;
		}

		boolean isMethod(String sipMessage) {
			SipHeader header = SipHeader.METHOD;
			String value = header.parseHeaderValue(sipMessage);
			return value.contains(mMethodStr);
		}
	}	

	String mInputBuffer;

	public SIPMessageScanner(String msgBuff) {
		mInputBuffer = msgBuff;
	}

	public boolean isValid() {
		return mInputBuffer.contains("SIP/2.0");		
	}

	public String getHeaderValue(SipHeader header) {
		return header.parseHeaderValue(mInputBuffer);
	}

	public boolean replaceHeaderValue(SipHeader header, String newValue) {
		return header.replaceHeaderValue(mInputBuffer, newValue) != null ? true : false;
	}

	public String getUserFromHeader(SipHeader header) {
		String value = header.parseHeaderValue(mInputBuffer);
		if(value != null) {
			int idx = value.indexOf(SIP_URI_TAG);
			String user = value.substring(idx + SIP_URI_TAG.length());
			user = user.substring(0, user.indexOf("@"));
			return user;
		}
		return null;
	}

	public boolean isMethod(SipMethod method) {
		return method.isMethod(mInputBuffer);
	}

	public boolean isSipRequest() {
		SipHeader header = SipHeader.METHOD;
		if(header.parseHeaderValue(mInputBuffer) == null) 
			return false;
		else 
			return true;
	}

	public UDPSocketInfo getDestAddressFromVia(String dropBranchID) throws Exception {
		String line;
		BufferedReader reader = new BufferedReader(new StringReader(mInputBuffer));
		while((line = reader.readLine()) != null) {
			if(line.startsWith(VIA_HEADER_TAG)) {
				String val = line.substring(VIA_HEADER_TAG.length()).trim();

				if(val.contains(dropBranchID)) continue;

				val = val.substring(val.indexOf(" "));
				val = val.substring(0, val.indexOf(";")).trim();

				String[] parts = val.split(":");
				UDPSocketInfo info = new UDPSocketInfo(parts[0], Integer.parseInt(parts[1]));
				return info;
			}
		}
		throw new Exception();
	}

	public String getDestAddressStringFromVia() throws Exception {
		SipHeader header = SipHeader.VIA;
		String val = header.parseHeaderValue(mInputBuffer);
		val = val.substring(val.indexOf(" "));
		val = val.substring(0, val.indexOf(";")).trim();

		return val;
	}

	public void replaceVia(DatagramPacket packet, String localTunnelIPAddress,
			int localTunnelSipPort) {
		try {
			String serverAddr = getDestAddressStringFromVia();
			String replaceStr = localTunnelIPAddress+":"+localTunnelSipPort;
			mInputBuffer = mInputBuffer.replace(serverAddr, replaceStr);
			byte[] bytes = mInputBuffer.getBytes();
			packet.setData(bytes, 0, bytes.length);
		} catch (Exception e) {
		}

	}

	private String putStringAt(String inStr, int pos, String str) {
		return inStr.substring(0,pos) + str + inStr.substring(pos);
	}
	
	public String handleViaHeader(String localTunnelIPAddress, int localTunnelSipPort,
			String mUniqueBranchID) {
		if(mInputBuffer.contains(mUniqueBranchID)) {
			return removeViaHeader(mUniqueBranchID);
		} else {
			return addViaHeader(localTunnelIPAddress,
					localTunnelSipPort, mUniqueBranchID);
		}
	}
	
	public String handleContactHeader(String localTunnelIPAddress, int localTunnelSipPort) {
		try {
			SipHeader header = SipHeader.CONTACT;
			String val = header.parseHeaderValue(mInputBuffer);
			String userAddress;
			userAddress = val.substring(val.indexOf("@")+1, val.indexOf(">"));			
			String replaceStr = localTunnelIPAddress+":"+localTunnelSipPort;
			replaceStr = val.replace(userAddress, replaceStr);
			return mInputBuffer.replace(val, replaceStr);
		} catch (Exception e) {
			return mInputBuffer;
		}
	}

	private String removeViaHeader(String mUniqueBranchID) {
		try {
			
			int idx = mInputBuffer.indexOf(VIA_HEADER_TAG);
			int lastViaIdx = mInputBuffer.lastIndexOf(VIA_HEADER_TAG);
			
			mInputBuffer = mInputBuffer.substring(0, idx) + mInputBuffer.substring(lastViaIdx);
			
		} catch (Exception e) {}
		return mInputBuffer;
	}

	public void addViaHeader(UDPSocketInfo addrInfo, 
			String mUniqueBranchID) {

		addViaHeader(addrInfo.getAddress().toString(),
				addrInfo.getPort(), mUniqueBranchID);
	}

	public String addViaHeader(String localTunnelIPAddress, int localTunnelSipPort,
			String mUniqueBranchID) {
		try {
			String replaceStr = localTunnelIPAddress+":"+localTunnelSipPort;

			String newVia = "Via: SIP/2.0/UDP " +  replaceStr + ";branch=" + mUniqueBranchID+"\n";

			int idx = mInputBuffer.indexOf(NEW_LINE_TAG) + NEW_LINE_TAG.length();
			mInputBuffer = putStringAt(mInputBuffer, idx, newVia);
		} catch (Exception e) {
		}
		return mInputBuffer;
	}

	public String replaceRouteHeader(DatagramPacket packet,
			String localTunnelIPAddress, int localTunnelSipPort) {
		
		try {
			mInputBuffer = mInputBuffer.replace(ROUTE_HEADER_TAG, RECORD_ROUTE_HEADER_TAG);
		} catch (Exception e) {
		}
		return mInputBuffer;
	}

	public boolean isByeResponse() {
		SipHeader header = SipHeader.CSEQ;
		String val = header.parseHeaderValue(mInputBuffer);
		
		return val.contains(SipMethod.BYE.mMethodStr);
	}
}

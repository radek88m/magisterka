package simulator.tunnel.signalling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;

import simulator.tunnel.network.UDPSocketInfo;

public class SIPMessageScanner {
	
	private static final String SIP_URI_TAG = "sip:";
	
	public enum SipHeader {
		VIA("Via:"),
		CONTACT("Contact:"),
		TO("To:"),
		FROM("From:"),
		CALL_ID("Call-ID:"),
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
		ACK("UPDATE"),
		BYE("UPDATE"),
		CANCEL("UPDATE"),
		OPTIONS("UPDATE"),
		PRACK("UPDATE"),
		NOTIFY("UPDATE"),
		PUBLISH("UPDATE"),
		INFO("UPDATE"),
		REFER("UPDATE"),
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

	public UDPSocketInfo getDestAddressFromVia() throws Exception {
		SipHeader header = SipHeader.VIA;
		String val = header.parseHeaderValue(mInputBuffer);
		val = val.substring(val.indexOf(" "));
		val = val.substring(0, val.indexOf(";")).trim();
		
		String[] parts = val.split(":");
		UDPSocketInfo info = new UDPSocketInfo(parts[0], Integer.parseInt(parts[1]));
		return info;
	}
}

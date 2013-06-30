package simulator.tunnel.signalling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class SDPHelper {
	
	public enum SDPAttribute {
		CONN("c="),
		MEDIA("m="),
		RTCP_PARAM("a=rtcp:");
		
		String mAttrStr;
		String mValue;
		
		SDPAttribute(String attrStr) {
			mAttrStr = attrStr;
			mValue = null;
		}
		
		String parseAttrValue(String inputBuff) {
			try {
				String line;
				BufferedReader reader = new BufferedReader(new StringReader(inputBuff));
				while((line = reader.readLine()) != null) {
					if(line.startsWith(mAttrStr)) {
						mValue = line.substring(mAttrStr.length()).trim();
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
	
	private String mSipMessage;
	
	public SDPHelper(String sipMessage) {
		mSipMessage = sipMessage;
	}
	
	public boolean hasSDP() {
		return mSipMessage.contains("application/sdp");
	}
	
	public String getConnectionIP() {
		SDPAttribute attr  =  SDPAttribute.CONN;		
		String val = attr.parseAttrValue(mSipMessage);
				
		String[] parts = val.split(" ");
		
		return parts[2];
	}

	public int getMediaPort() {
		SDPAttribute attr  =  SDPAttribute.MEDIA;		
		String val = attr.parseAttrValue(mSipMessage);
		
		String[] parts = val.split(" ");
		
		return Integer.parseInt(parts[1]);
	}
	
	public int getRTCPMediaPort() {
		SDPAttribute attr  =  SDPAttribute.RTCP_PARAM;		
		String val = attr.parseAttrValue(mSipMessage);
		
		String[] parts = val.split(" ");
		
		return Integer.parseInt(parts[0]);
	}
	
	public String getRTCPAddressIP() {
		SDPAttribute attr  =  SDPAttribute.RTCP_PARAM;		
		String val = attr.parseAttrValue(mSipMessage);
		
		String[] parts = val.split(" ");
		
		return parts[3];
	}

	public String replaceConnectionLine(String localTunnelIPAddress) {
		SDPAttribute attr  =  SDPAttribute.CONN;		
		String val = attr.parseAttrValue(mSipMessage);
		if(val == null) return mSipMessage;
		String replaceStr = val.replace(getConnectionIP(), localTunnelIPAddress);

		return mSipMessage.replace(val, replaceStr);
	}
	
	public String replaceMediaPort(int port) {
		SDPAttribute attr  =  SDPAttribute.MEDIA;		
		String val = attr.parseAttrValue(mSipMessage);
		if(val == null) return mSipMessage;
		String replaceStr = val.replace(""+getMediaPort(), ""+port);

		return mSipMessage.replace(val, replaceStr);
	}
	
	public String replaceRTCPMediaLine(String localTunnelIPAddress,
			int localMediaPort) {
		SDPAttribute attr  =  SDPAttribute.RTCP_PARAM;		
		String val = attr.parseAttrValue(mSipMessage);
		
		if(val == null) return mSipMessage;
		
		String replaceStr = val.replace(""+getRTCPMediaPort(), ""+localMediaPort);
		
		replaceStr = replaceStr.replace(getRTCPAddressIP(), localTunnelIPAddress);

		return mSipMessage.replace(val, replaceStr);
	}
	
}

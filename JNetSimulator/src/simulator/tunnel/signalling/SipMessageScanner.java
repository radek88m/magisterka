package simulator.tunnel.signalling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class SipMessageScanner {
	
	private static final String SIP_URI_TAG = "sip:";

	public enum SipHeader {
		VIA("Via:"),
		CONTACT("Contact:"),
		TO("To:"),
		FROM("From:"),
		METHOD("");
		
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
	
	String mInputBuffer;
	
	public SipMessageScanner(String msgBuff) {
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

	public String getSipUser() {
		SipHeader header = SipHeader.CONTACT;
		String value = header.parseHeaderValue(mInputBuffer);
		if(value != null) {
			int idx = value.indexOf(SIP_URI_TAG);
			String user = value.substring(idx + SIP_URI_TAG.length());
			user = user.substring(0, user.indexOf("@"));
			return user;
		}
		return null;
	}
}

package simulator.tunnel.mediastream;

public class TunnelStreamSettings {
	
	public int jitterSequenceVariance = 0;	    // Wariancja zmiany sekwencji pakietu, zakres w przedziale pakiet�w <0 , N>
	public int delay = 0;					    // Wymuszone opoznienie [ms]
	public int lossPercentage = 0;    			// Straty pakiet�w [%]
	public int maxBandwidth = 0;				// Maksymalna przepustowosc [kbps]
}

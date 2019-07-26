import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IAS {
	
	interface ISmartCardGenerateListener {
		void onSmartCardGenerated(SmartCard sc);
	}
	
	private String Xu;
	private String Xgw;
	private List<GateWay> listGW;
	private static IAS mInstance = null;
	
	private Map<String, String> userInfoMap;
	
	public static IAS getInstance() {
		if(mInstance == null) {
			mInstance = new IAS();
		}
		
		return mInstance;
	}
	
	public IAS() {
		listGW = new ArrayList<>();
		userInfoMap = new HashMap<String, String>();
	}
	
	
	// System Setup Phase
	public void systemSetupPhase(int numberGW) {
		
		// Select secret Xu for user
		Xu = "IAS-Xu";
		
		// Generate numberGW gateways
		for(int i = 1; i <= numberGW; i++) {
			listGW.add(new GateWay("IAS-GWID" + "-" + i, "IAS-PGW" + "-" + i));
		}
		
		// Select secret Xgw for user
		Xgw = "IAS-Xgw";
		
		
		String v = "";
		String w = "";
		
		for(GateWay gw: listGW) {
			
			// Concatenate Xgw to GWID and hash
			v = HashUtils.concatAndHashString(gw.getGWID(), Xgw);
			
			// Hash PGW
			w = HashUtils.concatAndHashString(gw.getPGW(), null);
			
			// Add < GWID, PGW, v, w > to GW 's memory
			gw.addToMessage(gw.getGWID());
			gw.addToMessage(gw.getPGW());
			gw.addToMessage(v);
			gw.addToMessage(w);
		}	
	}
	
	public void handleUserRegister(String mid, String mpw, ISmartCardGenerateListener listener) {
		// Select PU for user
		String PU = "User-PU" + "-" + mid;
		
		// x = h(MID || MPW), y = h(MID || Xu)
		String x = HashUtils.concatAndHashString(mid, mpw);
		String y = HashUtils.concatAndHashString(mid, Xu);
		
		
		// d = y XOR hash_concateX_MPW
		String hash_concateMPW_X = HashUtils.concatAndHashString(mpw, x);
		String d = HashUtils.XOR(y, hash_concateMPW_X);
		
		
		// e = PU XOR hash_concateY_MPW
		String hash_concateMPW_Y = HashUtils.concatAndHashString(mpw , y);	
		String e = HashUtils.XOR(PU, hash_concateMPW_Y);
		
		
		// z = h(PU || Xu)
		String z = HashUtils.concatAndHashString(PU, Xu);
		
		
		// g = z XOR concateMPW_X_Y
		String hash_concateMPW_X_Y = HashUtils.concatAndHashString(mpw, x, y);
		String g = HashUtils.XOR(z, hash_concateMPW_X_Y);
		
		
		SmartCard sc = new SmartCard(z, d, e, g);
		listener.onSmartCardGenerated(sc);
		
		
		// Save mid and pu of user to map
		userInfoMap.put(mid, PU);
	}
}

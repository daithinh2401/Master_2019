import java.util.ArrayList;
import java.util.List;

public class IAS {
	
	interface ISmartCardGenerateListener {
		void onSmartCardGenerated(SmartCard sc);
	}
	
	private String Xu;
	private String Xgw;
	private List<GateWay> listGW;
	private static IAS mInstance = null;
	
	public static IAS getInstance() {
		if(mInstance == null) {
			mInstance = new IAS();
		}
		
		return mInstance;
	}
	
	public IAS() {
		listGW = new ArrayList<>();
	}
	
	
	// System Setup Phase
	public void systemSetupPhase(int numberGW) {
		
		// Select secret Xu for user
		Xu = "IAS-Xu";
		
		// Generate 5 gateways
		for(int i = 1; i <= numberGW; i++) {
			listGW.add(new GateWay("IAS-GWID" + i, "IAS-PGW" + i));
		}
		
		// Select secret Xgw for user
		Xgw = "IAS-Xgw";
		
		// Concatenate Xgw to GWID
		for(GateWay gw: listGW) {
			String gwid = gw.getGWID();
			gwid = HashUtils.concatenate2Strings(gwid, Xgw);
			gw.setGWID(gwid);
		}
		
		
		// Hash of GWID
		String v = "";
		String w = "";
		
		for(GateWay gw: listGW) {
			v = HashUtils.getSHA(gw.getGWID());
			w = HashUtils.getSHA(gw.getPGW());
			
			// Add < GWID, PGW, v, w > to GW 's memory
			gw.addToMessage(gw.getGWID());
			gw.addToMessage(gw.getPGW());
			gw.addToMessage(v);
			gw.addToMessage(w);
		}	
	}
	
	
	public void handleUserRegister(String mid, String mpw, ISmartCardGenerateListener listener) {
		// Select PU for user
		String PU = "User-PU";
		
		// Concatenate
		String concateIdPw = HashUtils.concatenate2Strings(mid, mpw);
		String concateIdXu = HashUtils.concatenate2Strings(mid, Xu);
		

		// Hash
		String x = HashUtils.getSHA(concateIdPw);
		String y = HashUtils.getSHA(concateIdXu);
		
		
		// Concatenate x and MPW and hash
		String concateX_MPW = HashUtils.concatenate2Strings(x, mpw);
		String hash_concateX_MPW = HashUtils.getSHA(concateX_MPW);	
		// d = XOR y and hash_concateX_MPW
		String d = hash_concateX_MPW;
		
		
		// Concatenate y and MPW and hash 
		String concateY_MPW = HashUtils.concatenate2Strings(y, mpw);
		String hash_concateY_MPW = HashUtils.getSHA(concateY_MPW);
		// e = XOR PU and hash_concateY_MPW
		String e = hash_concateY_MPW;
		
		
		// Concatenate PU and Xu
		String concatePU_Xu = HashUtils.concatenate2Strings(PU, Xu);
		String z = HashUtils.getSHA(concatePU_Xu);
		
		// Concatenate MPW, x, y
		String concateMPW_X_Y = HashUtils.concatenate3Strings(mpw, x, y);
		
		// g = XOR z and concateMPW_X_Y
		String g = concateMPW_X_Y;
		
		
		SmartCard sc = new SmartCard(z, d, e, g);
		listener.onSmartCardGenerated(sc);
		
	}
}

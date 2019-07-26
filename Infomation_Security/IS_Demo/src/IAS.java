import java.sql.Timestamp;
import java.util.*;

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

	public List<GateWay> getListGW() {
		return listGW;
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
			gw.addToMessage(GateWay.MESSAGE_GWID, gw.getGWID());
			gw.addToMessage(GateWay.MESSAGE_PGW, gw.getPGW());
			gw.addToMessage(GateWay.MESSAGE_V_J, v);
			gw.addToMessage(GateWay.MESSAGE_W_J, w);
		}	
	}
	
	public void handleUserRegister(String PU, String mid, String mpw, ISmartCardGenerateListener listener) {
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
		
		
		SmartCard sc = new SmartCard(x, z, d, e, g);
		listener.onSmartCardGenerated(sc);
		
		
		// Save mid and pu of user to map
		userInfoMap.put(mid, PU);
	}

	public boolean authenticateStep4(Timestamp t1, Timestamp t2, String m1, String m2, String m3, String m4, String m5,
									 String m6, String m7, String m8, String PU, String PGW, String mid){


		// stage 4
		Date dateCurrent = new Date();
		Timestamp t_c = new Timestamp(dateCurrent.getTime());

		if (Math.abs(t_c.getTime() - t2.getTime()) < Main.DELTA_T){
			System.out.println("Verify t2: " + t2.getTime() + " t_c: " + t_c.getTime() + " success");
		}else {
			System.out.println("Verify t2: " + t2.getTime() + " t_c: " + t_c.getTime() + " fail");
			return false;
		}

		String w_j_1_star = HashUtils.concatAndHashString(PGW, Xgw);
		String gwid_j_star = HashUtils.getStringFromXOR(m5, HashUtils.concatAndHashString(w_j_1_star, String.valueOf(t2.getTime())));

		System.out.println("Verify gwid_j_star: " + gwid_j_star);
		System.out.println("Verify gwid: " + listGW.get(Main.GATE_WAY_INDEX_DEFAULT).getGWID());

		if (gwid_j_star.equals(listGW.get(Main.GATE_WAY_INDEX_DEFAULT).getGWID())){
			System.out.println("Verify gwid_j_star and gwid success: ");
		}else {
			System.out.println("Verify gwid_j_star and gwid fail: ");
		}


		// stage 5
		String z_i_1_star = HashUtils.concatAndHashString(PU, Xu);
		String mid_j_star = HashUtils.getStringFromXOR(m1, HashUtils.concatAndHashString(w_j_1_star, String.valueOf(t2.getTime())));

		if (mid_j_star.equals(mid)){
			System.out.println("Verify mid_j_star and mid success: ");
		}else {
			System.out.println("Verify mid_j_star and mid fail: ");
		}


		// stage 6
		String v_j_star = HashUtils.concatAndHashString(listGW.get(Main.GATE_WAY_INDEX_DEFAULT).getGWID(), Xgw);
		String k_j_star = HashUtils.XOR(m6, HashUtils.concatAndHashString(v_j_star, String.valueOf(t2.getTime())));
		String s_j_1_star = HashUtils.XOR(m7, HashUtils.concatAndHashString(v_j_star, w_j_1_star, String.valueOf(t2.getTime())));
		String m8_star = HashUtils.concatAndHashString(m5, m6, m7, k_j_star, s_j_1_star, PU, String.valueOf(t2.getTime()));

		if (m8.equals(m8_star)){
			System.out.println("Verify m8 and m8_star success: ");
		}else {
			System.out.println("Verify m8 and m8_star fail: ");
		}

		String y_i_star = HashUtils.concatAndHashString(mid, Xu);
		String k_i_star = HashUtils.XOR(m2, HashUtils.concatAndHashString(y_i_star, String.valueOf(t1.getTime())));
		String r_i_1_star = HashUtils.XOR(m3, HashUtils.concatAndHashString(y_i_star, z_i_1_star, String.valueOf(t1.getTime())));
		String m4_star = HashUtils.concatAndHashString(m1, m2, m3, k_i_star, r_i_1_star,
				getListGW().get(Main.GATE_WAY_INDEX_DEFAULT).getGWID(), String.valueOf(t1.getTime()));

		if (m4.equals(m4_star)){
			System.out.println("Verify m4 and m4_star success: ");
		}else {
			System.out.println("Verify m4 and m4_star fail: ");
		}


		//stage 7

		String nk_i = HashUtils.concatAndHashString(k_i_star, mid);
		String nk_j = HashUtils.concatAndHashString(k_j_star, getListGW().get(Main.GATE_WAY_INDEX_DEFAULT).getGWID());
		String pu_i_2 = HashUtils.concatAndHashString(PU, r_i_1_star);
		String pgw_j_2 = HashUtils.concatAndHashString(PGW, s_j_1_star);
		String z_i_2 = HashUtils.concatAndHashString(pu_i_2, Xu);
		String w_j_2_star = HashUtils.concatAndHashString(pgw_j_2, Xgw);


		// stage 8

		dateCurrent = new Date();
		Timestamp t3 = new Timestamp(dateCurrent.getTime());
		String m9 = HashUtils.XOR(nk_j, HashUtils.concatAndHashString(getListGW().get(Main.GATE_WAY_INDEX_DEFAULT).getGWID(),
				y_i_star, String.valueOf(t3.getTime())));
		String m10 = HashUtils.XOR(z_i_2, HashUtils.concatAndHashString(y_i_star, z_i_1_star, String.valueOf(t3.getTime())));
		String m11 = HashUtils.concatAndHashString(m9, m10, nk_i, nk_j, pu_i_2, String.valueOf(t1.getTime()),
				String.valueOf(t2.getTime()), String.valueOf(t3.getTime()), y_i_star);
		String m12 = HashUtils.XOR(nk_i, HashUtils.concatAndHashString(PU, v_j_star, String.valueOf(t3.getTime())));
		String m13 = HashUtils.XOR(w_j_2_star, HashUtils.concatAndHashString(v_j_star, w_j_1_star, String.valueOf(t3.getTime())));
		String m14 = HashUtils.concatAndHashString(m11, m12, nk_i, nk_j, pgw_j_2, String.valueOf(t1.getTime()),
				String.valueOf(t2.getTime()), String.valueOf(t3.getTime()), v_j_star);

		// update pu_1_i to pu_2_i - pgw_j_1 to pgw_j2

		return true;
	}
}

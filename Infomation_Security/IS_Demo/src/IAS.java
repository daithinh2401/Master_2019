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
	
	public List<GateWay> getListGW(){
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
		
		
		SmartCard sc = new SmartCard(x, z, d, e, g);
		listener.onSmartCardGenerated(sc);
		
		
		// Save mid and pu of user to map
		userInfoMap.put(mid, PU);
	}
	
	public void authenticateStep4(User user, Map<String, String> authenMap) {
		String t2_time = authenMap.get("t2_time");
		
		Timestamp tc = new Timestamp(System.currentTimeMillis());
		if(HashUtils.getAbs(tc.getTime(), Long.parseLong(t2_time)) < HashUtils.DELTA_TIME) {
			System.out.println("authenticateStep4(): Verify DELTA_TIME success");
			
			String gwid = listGW.get(0).getGWID();
			
			String pgw = authenMap.get("PGW");
			String M5 = authenMap.get("M5");
			
			// w_star = h(PGW , Xgw)
			String w_star = HashUtils.concatAndHashString(pgw, null);
			
			// GWID_star = xor (M5, h(w_star,t2))
			String hash1 = HashUtils.concatAndHashString(w_star, t2_time);
			String GWID_star = HashUtils.getStringFromXOR(M5 , hash1);
			
			GWID_star = GWID_star.substring(0, gwid.length());
			
			if(gwid.equals(GWID_star)) {
				System.out.println("authenticateStep4(): Verify GWID and GWID_star success !");
			} else {
				System.out.println("authenticateStep4(): Verify GWID and GWID_star failed !");
			}

			// stage 5
			String PU = userInfoMap.get(authenMap.get("mid"));
			String mid = authenMap.get("mid");
			String z_i_1_star = HashUtils.concatAndHashString(PU, Xu);
			String mid_j_star = HashUtils.getStringFromXOR(authenMap.get("M1"),
					HashUtils.concatAndHashString(z_i_1_star, authenMap.get("t1_time")));

			authenMap.put("z_i_1_star", z_i_1_star);

			if (mid_j_star.equals(mid)){
				System.out.println("Verify mid_j_star and mid success: " + mid);
			}else {
				System.out.println("Verify mid_j_star and mid fail: " + mid + " " + mid_j_star);
			}

			// stage 6
			String v_j_star = HashUtils.concatAndHashString(gwid, Xgw);
			String k_j_star = HashUtils.getStringFromXOR(authenMap.get("M6"), HashUtils.concatAndHashString(v_j_star, t2_time));
			String s_j_1_star = HashUtils.getStringFromXOR(authenMap.get("M7"),
					HashUtils.concatAndHashString(v_j_star, w_star, t2_time));
			String m8_star = HashUtils.concatAndHashString(authenMap.get("M5"), authenMap.get("M6"), authenMap.get("M7"),
					k_j_star, s_j_1_star, PU, t2_time);

			if (authenMap.get("M8").equals(m8_star)){
				System.out.println("Verify m8 and m8_star success: " + authenMap.get("M8"));
			}else {
				System.out.println("Verify m8 and m8_star fail: " + authenMap.get("M8") + " " + m8_star);
			}

			String y_i_star = HashUtils.concatAndHashString(mid, Xu);
			String k_i_star = HashUtils.getStringFromXOR(authenMap.get("M2"), HashUtils.concatAndHashString(y_i_star, authenMap.get("t1_time")));
			String r_i_1_star = HashUtils.getStringFromXOR(authenMap.get("M3"), HashUtils.concatAndHashString(y_i_star, z_i_1_star, authenMap.get("t1_time")));
			String m4_star = HashUtils.concatAndHashString(authenMap.get("M1"), authenMap.get("M2"), authenMap.get("M3"), k_i_star, r_i_1_star,
					gwid, authenMap.get("t1_time"));

			if (authenMap.get("M4").equals(m4_star)){
				System.out.println("Verify m4 and m4_star success: " + authenMap.get("M4"));
			}else {
				System.out.println("Verify m4 and m4_star fail: " + authenMap.get("M4") + " " + m4_star);
			}


			//stage 7

			String nk_i = HashUtils.concatAndHashString(k_i_star, mid);
			String nk_j = HashUtils.concatAndHashString(k_j_star, gwid);
			String pu_i_2 = HashUtils.concatAndHashString(PU, r_i_1_star);
			String pgw_j_2 = HashUtils.concatAndHashString(pgw, s_j_1_star);
			String z_i_2 = HashUtils.concatAndHashString(pu_i_2, Xu);
			String w_j_2_star = HashUtils.concatAndHashString(pgw_j_2, Xgw);

			// stage 8

			Timestamp t3 = new Timestamp(System.currentTimeMillis());
			String t3_time = String.valueOf(t3.getTime());
			String m9 = HashUtils.XOR(nk_j, HashUtils.concatAndHashString(gwid, y_i_star, t3_time));
			String m10 = HashUtils.XOR(z_i_2, HashUtils.concatAndHashString(y_i_star, z_i_1_star, t3_time));
			String m11 = HashUtils.concatAndHashString(m9, m10, nk_i, nk_j, pu_i_2, authenMap.get("t1_time"),
					authenMap.get("t2_time"), t3_time, y_i_star);
			String m12 = HashUtils.XOR(nk_i, HashUtils.concatAndHashString(PU, v_j_star, String.valueOf(t3.getTime())));
			String m13 = HashUtils.XOR(w_j_2_star, HashUtils.concatAndHashString(v_j_star, w_star, String.valueOf(t3.getTime())));
			String m14 = HashUtils.concatAndHashString(m11, m12, nk_i, nk_j, pgw_j_2, authenMap.get("t1_time"),
					authenMap.get("t2_time"), String.valueOf(t3.getTime()), v_j_star);

			// update pu_1_i to pu_2_i - pgw_j_1 to pgw_j2

			authenMap.put("y_i_star", y_i_star);
			authenMap.put("pu_i_1", PU);
			authenMap.put("pu_i_2", pu_i_2);
			authenMap.put("pgw_j_1", pgw);
			authenMap.put("pgw_j_2", pgw_j_2);
			authenMap.put("t3_time", t3_time);
			authenMap.put("M9", m9);
			authenMap.put("M10", m10);
			authenMap.put("M11", m11);
			authenMap.put("M12", m12);
			authenMap.put("M13", m13);
			authenMap.put("M14", m14);

			System.out.println("authenticateStep4(): Stage 4 - 5 - 6 - 7 - 8 success");

			GateWay gw = IAS.getInstance().getListGW().get(0);
			gw.authenticateStep5(user, authenMap);
		} else {
			System.out.println("authenticateStep4(): Verify DELTA_TIME success");
			return;
		}
		
		
	}
}

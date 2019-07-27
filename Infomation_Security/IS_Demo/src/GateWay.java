import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GateWay {

	private String GWID;
	private String PGW;
	private List<String> message;
	private Random random = new Random();
	
	public GateWay(String gwid, String pgw) {
		this.GWID 		= gwid;
		this.PGW 		= pgw;
		this.message 	= new ArrayList<>();
	}
	
	public String getGWID() {
		return GWID;
	}
	
	public void setGWID(String gwid) {
		GWID = gwid;
	}
	
	public String getPGW() {
		return PGW;
	}
	
	public void setPGW(String pgw) {
		PGW = pgw;
	}
	
	public void addToMessage(String str) {
		message.add(str);
	}
	
	public void authenticateStep3(User user, Map<String, String> authenMap) {
		String t1_time = authenMap.get("t1_time");
		String PU_star = authenMap.get("PU_star");
		
		Timestamp tc = new Timestamp(System.currentTimeMillis());
		if(HashUtils.getAbs(tc.getTime(), Long.parseLong(t1_time)) < HashUtils.DELTA_TIME) {
			System.out.println("authenticateStep3(): Verify DELTA_TIME success");
			
			int k = random.nextInt();
			int s = random.nextInt();
			
			String gwid = message.get(0);
			String pgw = message.get(1);
			String v = message.get(2);
			String w = message.get(3);		

			// Get current time t2
			Timestamp t2 = new Timestamp(System.currentTimeMillis());
			String t2_time = String.valueOf(t2.getTime());
			
			// M5 = xor(h(w, T2) , GWID)
			String hash1 = HashUtils.concatAndHashString(w, t2_time);
			String M5 = HashUtils.XOR(hash1, gwid);
			
			System.out.println("M5 = " + M5);
			System.out.println("hash1 = " + hash1);
			System.out.println("gwid = " + gwid);
			
			
			// M6 = xor(k , h(v,t2))
			String hash2 = HashUtils.concatAndHashString(v, t2_time);
			String M6 = HashUtils.XOR(String.valueOf(k), hash2);
			
			
			// M7 = xor(s , h(v, w, t2))
			String hash3 = HashUtils.concatAndHashString(v, w, t2_time);
			String M7 = HashUtils.XOR(String.valueOf(s), hash3);
			
			
			// M8 = xor(s , h(v, w, t2))
			String M8 = HashUtils.concatAndHashString(M5, M6, M7, String.valueOf(k), 
					String.valueOf(s), PU_star, t2_time);		
			
			
			authenMap.put("PGW", pgw);
			authenMap.put("M5", M5);
			authenMap.put("M6", M6);
			authenMap.put("M7", M7);
			authenMap.put("M8", M8);
			authenMap.put("t2_time", t2_time);
			authenMap.put("k", String.valueOf(k));
			authenMap.put("s", String.valueOf(s));
			
			System.out.println("authenticateStep3(): Done, send to IAS authenticate step 4");
			
			IAS.getInstance().authenticateStep4(user, authenMap);
			
		} else {
			System.out.println("authenticateStep3(): Verify DELTA_TIME failed");
			return;
		}
		
	}

	public void authenticateStep5(User userWantAuthen, Map<String, String> authenMap) {
		String t3_time = authenMap.get("t3_time");

		Timestamp tc = new Timestamp(System.currentTimeMillis());
		if(HashUtils.getAbs(tc.getTime(), Long.parseLong(t3_time)) < HashUtils.DELTA_TIME) {
			System.out.println("authenticateStep5(): Verify DELTA_TIME success");

			String gwid = message.get(0);
			String v = message.get(2);
			String w = message.get(3);
			String k = authenMap.get("k");
			String s = authenMap.get("s");
			String pu = authenMap.get("pu_i_1");
			String pgw = authenMap.get("pgw_j_1");

			String nk_i_star = HashUtils.getStringFromXOR(authenMap.get("M12"),
					HashUtils.concatAndHashString(pu, v, authenMap.get("t3_time")));
			String nk_j = HashUtils.concatAndHashString(k , gwid);
			String pgw_j_2 = HashUtils.concatAndHashString(pgw, s);
			String w_j_2_star = HashUtils.getStringFromXOR(authenMap.get("M13"),
					HashUtils.concatAndHashString(v, w));
			String m14_star = HashUtils.concatAndHashString(authenMap.get("M11"), authenMap.get("M12"), nk_i_star, nk_j, pgw_j_2,
					authenMap.get("t1_time"), authenMap.get("t2_time"), authenMap.get("t3_time"), v);

			if (m14_star.equals(authenMap.get("M14"))){
				System.out.println("Verify m14 and m14_star success: " + authenMap.get("M14"));
			}else {
				System.out.println("Verify m14 and m14_star fail: " + authenMap.get("M14") + " " + m14_star);
			}

			Timestamp t4 = new Timestamp(System.currentTimeMillis());
			String t4_time = String.valueOf(t4.getTime());
			String sk_u_i_gw_j = HashUtils.concatAndHashString(nk_i_star, nk_j);
			String m15 = HashUtils.concatAndHashString(pu, gwid, sk_u_i_gw_j, authenMap.get("t1_time"), authenMap.get("t2_time"),
					authenMap.get("t3_time"), t4_time);

			authenMap.put("t4_time", t4_time);
			authenMap.put("M15", m15);
			authenMap.put("gwid", gwid);

			userWantAuthen.authenticateStep6(authenMap);

		} else {
			System.out.println("authenticateStep5(): Verify DELTA_TIME failed");
			return;
		}

	}
}

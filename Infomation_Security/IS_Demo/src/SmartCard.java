import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SmartCard {

	private String x;
	private String z;
	private String d;
	private String e;
	private String g;
	private String b;
	private Random random = new Random();
	
	private Map<String, String> authenMap = new HashMap<>();
	
	public SmartCard(String x, String c, String d, String e, String g) {
		this.x = x;
		this.z = c;
		this.d = d;
		this.e = e;
		this.g = g;
	}
	
	public void setB(String b) {
		this.b = b;
	}
	
	public void changePassword(Map<String , String> verifyMap) {
		verifyMap = verifyOldPassword(verifyMap);
		
		if(verifyMap != null) {
			System.out.println("changePassword(): Verify success !");
			
			doChangePassword(verifyMap);
		}	
		
		return;
	}
	
	public Map<String , String> verifyOldPassword(Map<String , String> verifyMap) {
		
		// Get from map
		String id = verifyMap.get("id");
		String oldPw = verifyMap.get("oldPw");
		
		// a_star = getStringFromXOR(b , h(ID || PW))
		String hashIdPW = HashUtils.concatAndHashString(id, oldPw);
		String a_star = HashUtils.getStringFromXOR(this.b, hashIdPW);
		
		// MID_star = h(a || ID) - MPW_star = h(a || PW)
		String mid_star = HashUtils.concatAndHashString(a_star, id);
		String mpw_star = HashUtils.concatAndHashString(a_star, oldPw);
		
		// x_star = h(MID_star || MPW_star)
		String x_star = HashUtils.concatAndHashString(mid_star, mpw_star);
		
		// check x
		if(!this.x.equals(x_star)) {
			System.out.println("verifyOldPassword(): Failed, wrong password !");
			return null;
		}
		
		
		// Save to map
		verifyMap.put("a_star", a_star);
		verifyMap.put("x_star", x_star);
		verifyMap.put("mid_star", mid_star);
		verifyMap.put("mpw_star", mpw_star);
		
		
		return verifyMap;
	}
	
	public void doChangePassword(Map<String , String> verifyMap) {
		
		// Get from map
		String a_star = verifyMap.get("a_star");
		String x_star = verifyMap.get("x_star");
		String newPw = verifyMap.get("newPw");
		String mpw_star = verifyMap.get("mpw_star");
		String mid = verifyMap.get("MID");
		String id = verifyMap.get("id");
		
		// MPW_new = h(a_star || PWnew)
		String mpw_new = HashUtils.concatAndHashString(a_star, newPw);
	
		// y_star = xor(d , h(MPW || x_star))
		String hash1 = HashUtils.concatAndHashString(mpw_star, x_star);
		String y_star = HashUtils.getStringFromXOR(this.d, hash1);
			
		// PU_star = xor(e , h(MPW || y))
		String hash2 = HashUtils.concatAndHashString(mpw_star, y_star);
		String PU_star = HashUtils.getStringFromXOR(this.e, hash2);

		// z = xor(g , h(MPW || x || y))
		String hash3 = HashUtils.concatAndHashString(mpw_star, x_star, y_star);
		String z = HashUtils.getStringFromXOR(this.g, hash3);
		
		
		// x_new = h(MID || MPW_star)
		String x_new = HashUtils.concatAndHashString(mid, mpw_new);
		this.x = x_new;

		// d_new = xor(y , h(MPW || x || y))
		String hash4 = HashUtils.concatAndHashString(mpw_new, x_new);
		String d_new = HashUtils.XOR(y_star, hash4);
		this.d = d_new;

		// e_new = xor(PU_star , h(mpw_new || y_star))
		String hash5 = HashUtils.concatAndHashString(mpw_new, y_star);
		String e_new = HashUtils.XOR(PU_star, hash5);
		this.e = e_new;

		// g_new = xor(z , h(MPW || x || y))
		String hash6 = HashUtils.concatAndHashString(mpw_new, x_star, y_star);
		String g_new = HashUtils.XOR(z, hash6);
		this.g = g_new;		
		
		// b_new = xor(z , h(MPW || x || y))
		String hash7 = HashUtils.concatAndHashString(id, newPw);
		String b_new = HashUtils.XOR(a_star, hash7);
		this.b = b_new;
		
		System.out.println("doChangePassword(): Done !");
	}
	
	
	
	public boolean authenticateStep1(String id, String pw) {
		
		// a = b xor h(ID || PW)
		String hashIdPw = HashUtils.concatAndHashString(id, pw);
		String a = HashUtils.getStringFromXOR(this.b, hashIdPw);
		
		// MID = h(a || ID) - MPW = h(a || PW)
		String mid = HashUtils.concatAndHashString(a, id);
		String mpw = HashUtils.concatAndHashString(a, pw);
		
		// x_star = h(MID || MPW)
		String x_star = HashUtils.concatAndHashString(mid, mpw);
		
		boolean result;
		if(this.x.equals(x_star)) {
			result = true;
			System.out.println("authenticateStep1(): true - Go to next step");
		} else {
			result = false;
			System.out.println("authenticateStep1(): false - Failed, stop this process");
		}
		
		authenMap.put("x_star", x_star);
		authenMap.put("mid", mid);
		authenMap.put("mpw", mpw);
		
		return result;
	}
	
	public void authenticateStep2(User user) {
		int k = random.nextInt();
		int r = random.nextInt();
		
		String x_star = authenMap.get("x_star");
		String mid = authenMap.get("mid");
		String mpw = authenMap.get("mpw");
		
		// y_star = xor( d, h(MPW, x_star)
		String hash1 = HashUtils.concatAndHashString(mpw, x_star);
		String y_star = HashUtils.getStringFromXOR(this.d, hash1);
		
		// PU_star = xor(e , h(MPW, y_star))
		String hash2 = HashUtils.concatAndHashString(mpw, y_star);
		String PU_star = HashUtils.getStringFromXOR(this.e, hash2);		
		
		
		// z_star = xor(g , h(MPW, x_star, y_star))
		String hash3 = HashUtils.concatAndHashString(mpw, x_star, y_star);
		String z_star = HashUtils.getStringFromXOR(this.g, hash3);		
		
		// Current timestamp
		Timestamp t1 = new Timestamp(System.currentTimeMillis());
		String t1_time = String.valueOf(t1.getTime());
		
		// M1 = xor(h(z_star, T1), MID)
		String hash4 = HashUtils.concatAndHashString(z_star, t1_time);
		String M1 = HashUtils.XOR(hash4, mid);
		
		
		// M2 = xor(K, h(y_star, T1))
		String hash5 = HashUtils.concatAndHashString(y_star, t1_time);
		String M2 = HashUtils.XOR(String.valueOf(k), hash5);		
		
		
		// M3 = xor(r, h(y_star, z_star, T1))
		String hash6 = HashUtils.concatAndHashString(y_star, z_star, t1_time);
		String M3 = HashUtils.XOR(String.valueOf(r), hash6);
		
		
		// M4 = h(M1, M2, M3, k, r, GWID, T1)
		GateWay gw = IAS.getInstance().getListGW().get(0);
		String gwid = gw.getGWID();
		String M4 = HashUtils.concatAndHashString(M1, M2, M3, String.valueOf(k), 
				String.valueOf(r), gwid, t1_time);
		
		
		authenMap.put("PU_star", PU_star);
		authenMap.put("M1", M1);
		authenMap.put("M2", M2);
		authenMap.put("M3", M3);
		authenMap.put("M4", M4);
		authenMap.put("t1_time", t1_time);
		authenMap.put("k", String.valueOf(k));
		authenMap.put("r", String.valueOf(r));
		System.out.println("authenticateStep2(): Done, send to GW verify");
		
		gw.authenticateStep3(user, authenMap);
		
	}
	
}

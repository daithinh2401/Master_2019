import java.util.Map;

public class SmartCard {

	private String x;
	private String z;
	private String d;
	private String e;
	private String g;
	private String b;
	
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
		
		return result;
	}
	
	public void authenticateStep2() {
		
	}
	
}

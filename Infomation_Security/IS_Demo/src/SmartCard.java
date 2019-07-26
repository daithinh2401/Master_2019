import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SmartCard {

	private String x;
	private String c;
	private String d;
	private String e;
	private String g;
	private String b;
	
	public SmartCard(String x, String c, String d, String e, String g) {
		this.x = x;
		this.c = c;
		this.d = d;
		this.e = e;
		this.g = g;
	}
	
	public void setB(String b) {
		this.b = b;
	}
	
	public void changePassword(String id, String oldPw, String newPw) {
		
		Map<String , String> verifyMap = new HashMap<>();
		verifyMap.put("id", id);
		verifyMap.put("oldPw", oldPw);
		verifyMap.put("newPw", newPw);
		
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
		verifyMap.put("mid_star", mid_star);
		verifyMap.put("x_star", x_star);
		
		return verifyMap;
	}
	
	public void doChangePassword(Map<String , String> verifyMap) {
		
		// Get from map
		String a_star = verifyMap.get("a_star");
		String id = verifyMap.get("id");
		String mid = verifyMap.get("mid");
		String newPw = verifyMap.get("newPw");
		String x_star = verifyMap.get("x_star");
		
		
		// MPW_new = h(a_star || PWnew)
		String mpw_new = HashUtils.concatAndHashString(a_star, newPw);
		
		// y = xor(d , h(MPW || x_star))
		String hashMPW_X = HashUtils.concatAndHashString(mpw_new, x_star);
		String y_star = HashUtils.getStringFromXOR(this.d, hashMPW_X);
		
		// PU_star = xor(e , h(MPW || y))
		String hashMPW_Y = HashUtils.concatAndHashString(mpw_new, y_star);
		String PU_star = HashUtils.getStringFromXOR(this.e, hashMPW_Y);
		
		// z = xor(g , h(MPW || x || y))
		String hashMPW_X_Y = HashUtils.concatAndHashString(mpw_new, x_star, y_star);
		String z = HashUtils.getStringFromXOR(this.g, hashMPW_X_Y);
		
		
		// x_new = h(MID || MPW_star)
		String x_new = HashUtils.concatAndHashString(mid, mpw_new);
		this.x = x_new;
		
		
		// d_new = xor(y , h(MPW || x || y))
		String hash_newpw_x = HashUtils.concatAndHashString(mpw_new, x_new);
		String d_new = HashUtils.XOR(y_star, hash_newpw_x);
		this.d = d_new;
		
		
		// e_new = xor(PU_star , h(mpw_new || y_star))
		String hash_mpw_new_y = HashUtils.concatAndHashString(mpw_new, y_star);
		String e_new = HashUtils.XOR(PU_star, hash_mpw_new_y);
		this.e = e_new;
		
		
		// g_new = xor(z , h(MPW || x || y))
		String hash_newpw_x_y = HashUtils.concatAndHashString(mpw_new, x_star, y_star);
		String g_new = HashUtils.XOR(z, hash_newpw_x_y);
		this.g = g_new;
		
		
		// b_new = xor(z , h(MPW || x || y))
		String hash_id_newpw = HashUtils.concatAndHashString(id, newPw);
		String b_new = HashUtils.XOR(a_star, hash_id_newpw);
		this.b = b_new;
		
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

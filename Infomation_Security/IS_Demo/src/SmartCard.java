
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

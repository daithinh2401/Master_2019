import java.sql.Timestamp;
import java.util.Date;

public class SmartCard {

	private String x;
	private String c;
	private String d;
	private String e;
	private String g;
	private String b;

	private String x_star;
	private String mpw;
	private String mid;

	String pu_i_1_star;
	String m1;
	String m2;
	String m3;
	String m4;
	Timestamp t1;
	
	public SmartCard(String x, String c, String d, String e, String g) {
		this.x = x;
		this.c = c;
		this.d = d;
		this.e = e;
		this.g = g;
	}

    public String getX_star() {
        return x_star;
    }

    public void setB(String b) {
		this.b = b;
	}
	
	public boolean authenticateStep1(String id, String pw) {
		
		// a = b xor h(ID || PW)
		String hashIdPw = HashUtils.concatAndHashString(id, pw);
		String a = HashUtils.getStringFromXOR(this.b, hashIdPw);
		
		// MID = h(a || ID) - MPW = h(a || PW)
		mid = HashUtils.concatAndHashString(a, id);
        mpw = HashUtils.concatAndHashString(a, pw);
		
		// x_star = h(MID || MPW)
		x_star = HashUtils.concatAndHashString(mid, mpw);
		
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
        int k_i = HashUtils.randomIntegerUnsigned();
        int r_i_1 = HashUtils.randomIntegerUnsigned();

        String y_star = HashUtils.XOR(d, HashUtils.concatAndHashString(mpw, x_star));
        pu_i_1_star = HashUtils.XOR(e, HashUtils.concatAndHashString(mpw, y_star));
        String z_i_1_star = HashUtils.XOR(g, HashUtils.concatAndHashString(mpw, x_star, y_star));

        Date currentDate = new Date();
        t1 = new Timestamp(currentDate.getTime());
        m1 = HashUtils.XOR(HashUtils.concatAndHashString(z_i_1_star, String.valueOf(t1.getTime())), mid);
        m2 = HashUtils.XOR(k_i + "", HashUtils.concatAndHashString(y_star, String.valueOf(t1.getTime())));
        m3 = HashUtils.XOR(r_i_1 + "", HashUtils.concatAndHashString(y_star, z_i_1_star, String.valueOf(t1.getTime())));
        m4 = HashUtils.concatAndHashString(m1, m2, m3, k_i + "", r_i_1 + "", IAS.getInstance().getListGW().get(Main.GATE_WAY_INDEX_DEFAULT).getGWID(), String.valueOf(t1.getTime()));

        System.out.println("authenticateStep2(): success - t1: " + t1.getTime() + " m1: " + m1 + " m2: " + m2 + " m3: " + m3 + " m4: " + m4);
	}
	
}

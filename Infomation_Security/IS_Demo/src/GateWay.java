import java.sql.Timestamp;
import java.util.*;

public class GateWay {

	public static final String MESSAGE_V_J = "v_j";
	public static final String MESSAGE_GWID = "gwid";
	public static final String MESSAGE_PGW = "pgw";
	public static final String MESSAGE_W_J = "w_j";

	private String GWID;
	private String PGW;
	private Map<String, String> message;

	String m5;
	String m6;
	String m7;
	String m8;

	Timestamp t_2;
	
	public GateWay(String gwid, String pgw) {
		this.GWID 		= gwid;
		this.PGW 		= pgw;
		this.message 	= new HashMap<>();
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
	
	public void addToMessage(String key, String value) {
		message.put(key, value);
	}

	public boolean authenticateStep3(Timestamp t1, String m1, String m2, String m3, String m4, String PU){

		Date dateCurrent = new Date();
		Timestamp t_c = new Timestamp(dateCurrent.getTime());

		if (Math.abs(t_c.getTime() - t1.getTime()) < Main.DELTA_T){
			System.out.println("Verify t1: " + t1.getTime() + " t_c: " + t_c.getTime() + " success");
		}else {
			System.out.println("Verify t1: " + t1.getTime() + " t_c: " + t_c.getTime() + " fail");
			return false;
		}

		int k_j = HashUtils.randomIntegerUnsigned();
		int s_j_1 = HashUtils.randomIntegerUnsigned();

		dateCurrent = new Date();
		t_2 = new Timestamp(dateCurrent.getTime());

		m5 = HashUtils.XOR(HashUtils.concatAndHashString(message.get(MESSAGE_W_J), String.valueOf(t_2.getTime())), message.get(MESSAGE_GWID));
		m6 = HashUtils.XOR(k_j + "", HashUtils.concatAndHashString(message.get(MESSAGE_V_J), String.valueOf(t_2.getTime())));
		m7 = HashUtils.XOR(s_j_1 + "", HashUtils.concatAndHashString(message.get(MESSAGE_V_J), message.get(MESSAGE_W_J), String.valueOf(t_2.getTime())));
		m8 = HashUtils.concatAndHashString(m5, m6, m7, k_j + "", s_j_1 + "", PU, String.valueOf(t_2.getTime()));

		System.out.println("authenticateStep3(): success - t2: " + t_2.getTime() + " m5: " + m5 + " m6: " + m6 + " m7: " + m7 + " m8: " + m8);
		return true;
	}
}

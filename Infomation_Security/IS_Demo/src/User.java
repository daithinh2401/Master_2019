import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class User {

	private String ID;
	private String PW;
	private String MID;
	private String MPW;
	private int a;
	private Random random = new Random();
	
	private SmartCard mSmartCard;
	
	public User(String id, String pw) {
		this.ID = id;
		this.PW = pw;
		this.a = random.nextInt();
	}
	
	public SmartCard getSmartCard() {
		return mSmartCard;
	}
	
	public void registerUser() {
		
		// Concatenate ID and PW with random number a
		String concatId = HashUtils.concat2Strings(a + "", ID);
		String concatPw = HashUtils.concat2Strings(a + "", PW);
		
		
		// Hash ID and PW to MID and MPW
		MID = HashUtils.getSHA(concatId);
		MPW = HashUtils.getSHA(concatPw);
		
		
		// Send MID and MPW to IAS to handle
		IAS.getInstance().handleUserRegister(MID, MPW, new IAS.ISmartCardGenerateListener() {
			
			@Override
			public void onSmartCardGenerated(SmartCard sc) {
				
				// Concat and hash id, pw
				String hashIDPW = HashUtils.concatAndHashString(ID, PW);
				
				// b = a XOR hashIDPW
				String b = HashUtils.XOR(String.valueOf(a), hashIDPW);
				
				sc.setB(b);
			
				mSmartCard = sc;
				

				System.out.println("registerUser(): Done !");
			}
		});
	}
	
	public void authenticate(String id, String pw) {
		
		boolean step1Result = mSmartCard.authenticateStep1(id, pw);
		
		if(step1Result) {
			mSmartCard.authenticateStep2(this);
		}
	}


	public void authenticateStep6(Map<String, String> authenMap) {
		String t4_time = authenMap.get("t4_time");

		Timestamp tc = new Timestamp(System.currentTimeMillis());
		if(HashUtils.getAbs(tc.getTime(), Long.parseLong(t4_time)) < HashUtils.DELTA_TIME) {
			System.out.println("authenticateStep6(): Verify DELTA_TIME success");

			String k = authenMap.get("k");
			String gwid = authenMap.get("gwid");
			String y_i_star = authenMap.get("y_i_star");
			String pu_i_1 = authenMap.get("pu_i_2");
			String r_1_i = authenMap.get("r");
			String z_i_1_star = authenMap.get("z_i_1_star");
			String nk_j_star = HashUtils.getStringFromXOR(authenMap.get("M9"), HashUtils.concatAndHashString(gwid, y_i_star, authenMap.get("t3_time")));
			String nk_i = HashUtils.concatAndHashString(k, MID);
			String pu_i_2 = HashUtils.concatAndHashString(pu_i_1, r_1_i);
			String z_i_2_star = HashUtils.getStringFromXOR(authenMap.get("M10"), HashUtils.concatAndHashString(y_i_star,z_i_1_star , authenMap.get("t3_time")));
			String m11_star = HashUtils.concatAndHashString(authenMap.get("M9"), authenMap.get("M10"), nk_i, nk_j_star, pu_i_2,
					authenMap.get("t1_time"), authenMap.get("t2_time"), authenMap.get("t3_time"), y_i_star);
			if (m11_star.equals(authenMap.get("M11"))){
				System.out.println("Verify m11 and m11_star success: " + authenMap.get("M11"));
			}else {
				System.out.println("Verify m11 and m11_star success: " + authenMap.get("M11") + " " + m11_star);
			}

			String sk_u_i_gw_j = HashUtils.concatAndHashString(HashUtils.getStringFromXOR(nk_i, nk_j_star),null);
			String m15_star = HashUtils.concatAndHashString(pu_i_1, gwid, sk_u_i_gw_j, authenMap.get("t1_time"),
					authenMap.get("t2_time"), authenMap.get("t3_time"), y_i_star);
			if (m15_star.equals(authenMap.get("M15"))){
				System.out.println("Verify m15 and m15_star success: " + authenMap.get("M15"));
			}else {
				System.out.println("Verify m15 and m15_star success: " + authenMap.get("M15") + " " + m15_star);
			}

		} else {
			System.out.println("authenticateStep6(): Verify DELTA_TIME failed");
			return;
		}

	}
	
	public void changePassword(String id, String oldPw, String newPw) {
		
		Map<String, String> verifyMap = new HashMap<>();
		
		verifyMap.put("a", String.valueOf(this.a));
		verifyMap.put("MID", MID);
		verifyMap.put("MPW", MPW);
		verifyMap.put("id", id);
		verifyMap.put("oldPw", oldPw);
		verifyMap.put("newPw", newPw);
		
		mSmartCard.changePassword(verifyMap);
	}
	
	
}

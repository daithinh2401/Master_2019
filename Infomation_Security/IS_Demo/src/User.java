import java.util.Random;

public class User {

	private String ID;
	private String PW;
	private String MID;
	private String MPW;
	private int a;

	String PU;

	private SmartCard mSmartCard;
	
	public User(String id, String pw) {
		this.ID = id;
		this.PW = pw;
		this.a = HashUtils.randomIntegerUnsigned();
	}
	
	public SmartCard getSmartCard() {
		return mSmartCard;
	}

	public String getMID() {
		return MID;
	}

	public void registerUser() {
		
		// Concatenate ID and PW with random number a
		String concatId = HashUtils.concat2Strings(a + "", ID);
		String concatPw = HashUtils.concat2Strings(a + "", PW);
		
		
		// Hash ID and PW to MID and MPW
		MID = HashUtils.getSHA(concatId);
		MPW = HashUtils.getSHA(concatPw);
		
		
		// Send MID and MPW to IAS to handle

		PU = "User-PU" + "-" + MID;

		IAS.getInstance().handleUserRegister(PU, MID, MPW, new IAS.ISmartCardGenerateListener() {
			
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
		
		if(!step1Result || mSmartCard.getX_star() == null || mSmartCard.getX_star().equals("")) {
			return;
		}

		mSmartCard.authenticateStep2();
		
	}
	
}

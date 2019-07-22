import java.util.Random;

public class User {

	private String ID;
	private String PW;
	private String MID;
	private String MPW;
	private int a;
	private Random random = new Random();
	
	public User(String id, String pw) {
		this.ID = id;
		this.PW = pw;
		this.a = random.nextInt();
	}
	
	public void registerUser() {
		
		// Concatenate ID and PW with random number a
		ID = HashUtils.concatenate2Strings(a + "", ID);
		PW = HashUtils.concatenate2Strings(a + "", PW);
		
		
		// Hash ID and PW to MID and MPW
		MID = HashUtils.getSHA(ID);
		MPW = HashUtils.getSHA(PW);
		
		
		// Send MID and MPW to IAS to handle
		IAS.getInstance().handleUserRegister(MID, MPW, new IAS.ISmartCardGenerateListener() {
			
			@Override
			public void onSmartCardGenerated(SmartCard sc) {
				
				String concateIDPW = HashUtils.concatenate2Strings(ID, PW);
				String hashIDPW = HashUtils.getSHA(concateIDPW);
				
				// b = a XOR hashIDPW
				String b = hashIDPW;
				
				sc.setB(b);
				System.out.println("registerUser(): " + b);
				
			}
		});
	}
	
}

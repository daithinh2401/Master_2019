
public class Main {

	
	public static void main(String[] args) {
		IAS ias = IAS.getInstance();
		
		// setup 5 gateway
		ias.systemSetupPhase(5);
		
		User user = new User("abc", "123");
		user.registerUser();
		
		user.authenticate("abc", "123");
		
		user.changePassword("abc", "123", "456");
		
		// re-authenticate with new password
		user.authenticate("abc", "456");
	}
	
}
